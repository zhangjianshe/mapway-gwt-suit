package cn.mapway.spring.processor;


import cn.mapway.spring.processor.module.AllModules;
import cn.mapway.ui.server.code.RpcEntry;
import cn.mapway.ui.server.code.RpcProxy;
import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.File;
import java.util.*;

@AutoService(Processor.class)
public class ClientRpcStubGenerator extends AbstractProcessor {
    private final List<String> exceptionStacks = Collections.synchronizedList(new ArrayList<>());


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportedAnnotationTypes = new HashSet<String>();
        supportedAnnotationTypes.add(RpcPackage.class.getName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        try {
            processImpl(annotations, roundEnv);
        } catch (RuntimeException e) {
            // We don't allow exceptions of any kind to propagate to the compiler
            exceptionStacks.add(e.getMessage());
            fatalError(e.getMessage());
        }

        return true;
    }


    private void processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("Processing " + annotations.size() );
        processAnnotations(annotations, roundEnv);
    }

    /**
     * 读取接口信息 生成代码
     *
     * @param annotations
     * @param roundEnv
     */
    private void processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RpcPackage.class);
        for (Element e : elements) {
            //处理每一个 代理接口
            PackageElement p = (PackageElement) e;
            processPackage(p, roundEnv);
        }
    }

    private String getPackageName(TypeElement element) {
        String qualifiedName = element.getQualifiedName().toString();
        int position = qualifiedName.lastIndexOf('.');
        if (position > 0) {
            return qualifiedName.substring(0, position);
        }
        return qualifiedName;
    }


    private void processPackage(PackageElement packageElement, RoundEnvironment roundEnv) {

        AnnotationHolder rpcPackage = AnnotationHolder.createFromElement(packageElement, RpcPackage.class);

        //检查包下面的各个类 是否是Controller类
        List<? extends Element> enclosedElements = packageElement.getEnclosedElements();
        if (Lang.isEmpty(enclosedElements)) {
            return;
        }
        List<Element> exportClazz = new ArrayList<Element>();
        for (Element enclosedElement : enclosedElements) {
            System.out.println("checking " + enclosedElement.getSimpleName() + "");
            if (enclosedElement.getKind().equals(ElementKind.CLASS)) {
                if (isController(rpcPackage, enclosedElement)) {
                    System.out.println(enclosedElement.getSimpleName() + " is a controller");
                    //输出
                    exportClazz.add(enclosedElement);
                }
            }
        }
        String outputPath;
        // 优先选择用户编译时的配置 通过MAVEN或者 命令行
        outputPath = processingEnv.getOptions().get("RPC_OUT_PATH");
        if (Strings.isBlank(outputPath)) {
            //如果没有配置 则选择 注解配置
            outputPath = rpcPackage.getString("localPath");
        }
        if (Strings.isBlank(outputPath)) {
            //如果注解也没有配置 使用当前目录
            outputPath = rpcPackage.getString("localPath");
        }

        String packageName = rpcPackage.getString("packageName");
        if (Strings.isBlank(packageName)) {
            packageName = packageElement.getQualifiedName().toString();
        }

        if (rpcPackage.getBoolean("merge")) {
            //将所有的API统一 输出到一个类里
            String interfaceName = rpcPackage.getString("name");
            if (Strings.isBlank(interfaceName)) {
                interfaceName = Strings.upperFirst(packageElement.getSimpleName().toString()) + "Client";
            }
            exportTo(rpcPackage, outputPath, packageName, interfaceName, exportClazz);
        } else {
            for (Element e : exportClazz) {
                String interfaceName = "I" + Strings.upperFirst(e.getSimpleName().toString()) + "Client";
                exportTo(rpcPackage,outputPath, packageName, interfaceName, Lang.list(e));
            }
        }

        //输出所有接口中用到的模型 module存储的包路径为 packageName+".module"

    }

    private void exportTo(AnnotationHolder rpcPackageHolder, String outputPath, String packageName, String interfaceName, List<Element> list) {
        System.out.println("<-------------- " + outputPath + "------------->");

        TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(interfaceName);
        typeSpecBuilder.addModifiers(Modifier.PUBLIC);

        //TODO  检查是否已经有之前的记录了 可以从之前的记录中国获取该值
        if(rpcPackageHolder.getBoolean("merge")) {
            AnnotationSpec.Builder rpcInterfaceAnnotationBuilder = AnnotationSpec.builder(RpcProxy.class);
            rpcInterfaceAnnotationBuilder.addMember("url", "$S", "");
            rpcInterfaceAnnotationBuilder.addMember("packageName", "$S", rpcPackageHolder.getString("proxyPackage"));
            rpcInterfaceAnnotationBuilder.addMember("className", "$S", rpcPackageHolder.getString("proxyName"));
            typeSpecBuilder.addAnnotation(rpcInterfaceAnnotationBuilder.build());
        }
        else
        {
            AnnotationSpec.Builder rpcInterfaceAnnotationBuilder = AnnotationSpec.builder(RpcProxy.class);
            rpcInterfaceAnnotationBuilder.addMember("url", "$S", "");
            rpcInterfaceAnnotationBuilder.addMember("packageName", "$S", "");
            rpcInterfaceAnnotationBuilder.addMember("className", "$S", "");
            typeSpecBuilder.addAnnotation(rpcInterfaceAnnotationBuilder.build());
        }


        List<String> allMethods = new ArrayList<String>();
        for (Element e : list) {
            processClazz(allMethods, typeSpecBuilder, e);
        }

        //输出所有解析的模型定义
        System.out.println(AllModules.getInstance().toString());

        try {
            JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build())
                    .build();
            if (Strings.isBlank(outputPath)) {
                javaFile.writeTo(processingEnv.getFiler());

            } else {
                javaFile.writeTo(new File(outputPath));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processClazz(List<String> allMethods, TypeSpec.Builder typeSpecBuilder, Element e) {

        String rootPath = "";
        String rootMethod = "GET";
        String acceptContent = "text/plain";

        AnnotationHolder rootMapping = AnnotationHolder.createFromElement(e, RequestMapping.class);
        List<String> paths = rootMapping.getStrings("value");
        List<VariableElement> methods = rootMapping.getEnums("method");
        if (paths.size() > 0) {
            rootPath = paths.get(0);
        }
        if (methods.size() > 0) {
            rootMethod = methods.get(0).getSimpleName().toString();
        }

        AnnotationHolder rootRestController = AnnotationHolder.createFromElement(e, RestController.class);
        if (rootRestController.isPresent()) {
            acceptContent = "application/json";
        }


        for (Element enclosedElement : e.getEnclosedElements()) {
            if (enclosedElement.getKind().equals(ElementKind.METHOD) && enclosedElement.getModifiers().contains(Modifier.PUBLIC)) {
                String name = enclosedElement.getSimpleName().toString();
                String method = rootMethod;
                String content = acceptContent;
                String path = rootPath;

                AnnotationHolder getMapping = AnnotationHolder.createFromElement(enclosedElement, GetMapping.class);
                AnnotationHolder postMapping = AnnotationHolder.createFromElement(enclosedElement, PostMapping.class);
                AnnotationHolder requestMapping = AnnotationHolder.createFromElement(enclosedElement, RequestMapping.class);
                AnnotationHolder responseBody = AnnotationHolder.createFromElement(enclosedElement, ResponseBody.class);
                if (responseBody.isPresent()) {
                    content = "application/json";
                }
                if (getMapping.isPresent()) {
                    method = "GET";
                    List<String> _paths = getMapping.getStrings("value");
                    if (_paths.size() > 0) {
                        path = path + _paths.get(0);
                    }
                } else if (postMapping.isPresent()) {
                    method = "POST";
                    List<String> _paths = postMapping.getStrings("value");
                    if (_paths.size() > 0) {
                        path = path + _paths.get(0);
                    }
                } else if (requestMapping.isPresent()) {

                    List<VariableElement> _methods = requestMapping.getEnums("method");
                    if (_methods.size() > 0) {
                        method = _methods.get(0).getSimpleName().toString();
                    }
                    List<String> _paths = requestMapping.getStrings("value");
                    if (_paths.size() > 0) {
                        path = path + _paths.get(0);
                    }
                } else {
                    //有错误
                    continue;
                }

                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(name);

                //处理参数
                ExecutableElement executableElement = (ExecutableElement) enclosedElement;

                //处理返回值 返回值可能是一个 模板类 Result<Abc<Time, String>>
                // 针对里面的所有有非 基本类型的类型 创建相应的 模型类
                // 并用 新创建的模型类填充返回值
                processReturnValueType(methodBuilder, executableElement.getReturnType());

                List<? extends VariableElement> parameters = executableElement.getParameters();
                VariableElement bodyElement = null;
                for (int i = 0; i < parameters.size(); i++) {
                    VariableElement variableElement = parameters.get(i);

                    AnnotationHolder requestBody = AnnotationHolder.createFromElement(variableElement, RequestBody.class);
                    if (requestBody.isPresent()) {
                        bodyElement = variableElement;
                        continue;
                    }
                    String pname = variableElement.getSimpleName().toString();

                    AnnotationHolder pathVariable = AnnotationHolder.createFromElement(variableElement, PathVariable.class);
                    if (pathVariable.isPresent()) {
                        if (pathVariable.getString("name").length() > 0) {
                            pname = pathVariable.getString("name");
                        }
                    }
                    methodBuilder.addParameter(TypeName.get(variableElement.asType()), pname);
                }
                if (bodyElement != null) {
                    String pname = bodyElement.getSimpleName().toString();
                    AnnotationHolder pathVariable = AnnotationHolder.createFromElement(bodyElement, PathVariable.class);
                    if (pathVariable.isPresent()) {
                        if (pathVariable.getString("name").length() > 0) {
                            pname = pathVariable.getString("name");
                        }
                    }
                    methodBuilder.addParameter(TypeName.get(bodyElement.asType()), pname);
                }

                AnnotationSpec.Builder rpcEntryBuilder = AnnotationSpec.builder(RpcEntry.class);
                rpcEntryBuilder.addMember("path", "$S", path);
                rpcEntryBuilder.addMember("method", "$S", method);
                rpcEntryBuilder.addMember("contentType", "$S", content);
                methodBuilder.addAnnotation(rpcEntryBuilder.build());
                methodBuilder.addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);

                typeSpecBuilder.addMethod(methodBuilder.build());
            }
        }

    }

    /**
     * 处理返回值类类型
     * 比较复杂的操作
     * @param methodBuilder
     * @param returnType
     */
    private void processReturnValueType(MethodSpec.Builder methodBuilder, TypeMirror returnType) {
        TypeName typeName = TypeName.get(returnType);
        methodBuilder.returns(typeName);
        TypeMirrorVisitor visitor=new TypeMirrorVisitor(processingEnv);

//        TypeElement listElement = processingEnv.getElementUtils().getTypeElement("java.util.List");
//        TypeElement str=processingEnv.getElementUtils().getTypeElement("java.lang.String");
//
//        DeclaredType declaredType = processingEnv.getTypeUtils().getDeclaredType(listElement, str.asType());
//        System.out.println(declaredType.toString());
        visitor.parse(returnType);

    }


    private boolean isController(AnnotationHolder rpcPackage, Element element) {

        boolean hasController = MoreElements.isAnnotationPresent(element, Controller.class);
        boolean hasRestController = MoreElements.isAnnotationPresent(element, RestController.class);

        if (!(hasController || hasRestController)) {
            return false;
        }

        List<DeclaredType> includes = rpcPackage.getClasses("includes");
        for (DeclaredType clazz : includes) {
            if (clazz.asElement().getSimpleName().toString().equals(element.getSimpleName().toString())) {
                return true;
            }
        }
        List<DeclaredType> excludes = rpcPackage.getClasses("excludes");
        for (DeclaredType clazz : excludes) {
            if (clazz.asElement().getSimpleName().toString().equals(element.getSimpleName().toString())) {
                return false;
            }
        }
        return true;
    }

    private void log(String msg) {
        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
        }
    }

    private void warning(String msg, Element element, AnnotationMirror annotation) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg, element, annotation);
    }

    private void error(String msg, Element element, AnnotationMirror annotation) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, element, annotation);
    }

    private void fatalError(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: " + msg);
    }
}