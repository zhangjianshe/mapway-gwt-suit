package cn.mapway.ui.server.code;

import cn.mapway.ui.client.rpc.JsonRpcBase;
import cn.mapway.ui.client.rpc.PathItemVisitor;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import elemental2.core.Global;
import elemental2.promise.Promise;
import org.nutz.lang.Strings;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static cn.mapway.ui.client.rpc.JsonRpcBase.JSON_CONTENT_TYPE;

/**
 * JSON RPC代码生成处理器
 */
@AutoService(Processor.class)
public class JsonRpcProcessor extends AbstractProcessor {
    private final List<String> exceptionStacks = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportedAnnotationTypes = new HashSet<String>();
        supportedAnnotationTypes.add(RpcProxy.class.getName());
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
        return false;
    }

    private void processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processAnnotations(annotations, roundEnv);
    }

    /**
     * 读取接口信息 生成代码
     *
     * @param annotations
     * @param roundEnv
     */
    private void processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RpcProxy.class);


        for (Element e : elements) {
            //处理每一个 代理接口
            processElement(e, roundEnv);
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

    private void processElement(Element e, RoundEnvironment roundEnv) {
        if (e.getKind() == ElementKind.INTERFACE) {
            TypeElement type = (TypeElement) e;
            RpcProxy rpcProxy = e.getAnnotation(RpcProxy.class);
            //TODO check environment variable
            String output=processingEnv.getOptions().get("RPC_IMPL_PATH");
            if(!rpcProxy.enabled()) {
                System.out.println("用户禁止了生成代理接口");
                return ;
            }
            String name = rpcProxy.className();
            String packageName = rpcProxy.packageName();
            String basePath = rpcProxy.url();
            if (Strings.isBlank(name)) {
                name = e.getSimpleName() + "Proxy";
            }
            if (Strings.isBlank(packageName)) {
                packageName = getPackageName(type);
            }
            TypeSpec.Builder proxyImplType = TypeSpec.classBuilder(name)
                    .addModifiers(Modifier.PUBLIC);
            proxyImplType.superclass(JsonRpcBase.class);

            //添加构造函数
            MethodSpec construct1 = MethodSpec.constructorBuilder().addParameter(String.class, "basePath")
                    .addStatement("setBasePath(basePath)").addModifiers(Modifier.PUBLIC).build();
            MethodSpec construct0 = MethodSpec.constructorBuilder()
                    .addStatement("this($S)",basePath).addModifiers(Modifier.PUBLIC).build();
            proxyImplType.addMethod(construct0);
            proxyImplType.addMethod(construct1);
            proxyImplType.addModifiers(Modifier.PUBLIC);

            //处理接口中的所有方法
            List<? extends Element> enclosedElements = type.getEnclosedElements();
            if (enclosedElements != null) {
                for (Element elementMethod : enclosedElements) {
                    if (elementMethod.getKind() == ElementKind.METHOD) {
                        processMethod(basePath, proxyImplType, type, elementMethod);
                    }
                }
            }


            JavaFile javaFile = JavaFile.builder(packageName, proxyImplType.build())
                    .build();

            if(Strings.isBlank(output)) {
                Filer filer = processingEnv.getFiler();
                try {
                    javaFile.writeTo(filer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else{
                try {
                    javaFile.writeTo(new File(output));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            log("Process finished " + packageName + "." + name + ".java");
        } else {
            log("Process Element " + e.getSimpleName() + " is not supported, because it is not interface ");
        }
    }

    private void processMethod(String basePath, TypeSpec.Builder proxyImplType, TypeElement type, Element elementMethod) {
        ExecutableElement executableElement = (ExecutableElement) elementMethod;
        String methodName = executableElement.getSimpleName().toString();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName);

        TypeMirror returnType = executableElement.getReturnType();
        ParameterizedTypeName returnTypeName =ParameterizedTypeName.get(ClassName.get(Promise.class),TypeName.get(returnType).box());
        builder.returns(returnTypeName);

        //处理参数
        List<? extends VariableElement> parameters = executableElement.getParameters();
        for (VariableElement parameter : parameters) {
            builder.addParameter(TypeName.get(parameter.asType()), parameter.getSimpleName().toString(),Modifier.FINAL);
        }
        builder.addModifiers(Modifier.PUBLIC);

        //处理代码快
        String path = "";
        RpcEntry rpcEntry = executableElement.getAnnotation(RpcEntry.class);
        if (rpcEntry != null) {
            path = rpcEntry.path();
        }
        if (path.length() == 0) {
            path = methodName;
        }
        path = path.trim();

        // path need remove  /// --> /
        path=path.replaceAll("[/]{2,}","/");

        if (path.startsWith("http") || path.startsWith("ws")) {
            //设置了绝对路径
            builder.addStatement("String url=$S", path);
        } else {
            builder.addStatement("String url=getBasePath()+$S", path);
        }


        if(path.indexOf("{")>0)
        {
            //Path中有需要替换的变量
            builder.addCode("\turl = patternFill(url, new $T(){\n", PathItemVisitor.class);
            builder.addCode("\t   @Override\n");
            builder.addCode("\t  public String onItem(String __name){\n");
            for (VariableElement parameter : parameters) {
                String parameterName=parameter.getSimpleName().toString();
                builder.addCode("\t       if(__name.equals($S)){\n",parameterName);
                builder.addCode("\t            return $L==null?$S:$L.toString();\n",parameterName,"null",parameterName);
                builder.addCode("\t       }\n");
            }
                builder.addCode("\t       return \"\";\n");
            builder.addCode("\t  }\n");
            builder.addCode("\t});\n");
        }


        String method = rpcEntry.method();
        builder.addStatement("String method=\"" + method + "\"");
        if (method.compareToIgnoreCase("GET") == 0 || parameters.size() == 0) {
            builder.addStatement("String body=\"\"");
        } else {
            builder.addStatement("String body=$T.JSON.stringify($L)", Global.class, parameters.get(parameters.size()-1).getSimpleName());
        }
        builder.addStatement("$T<String,String> headers = new $T<String,String>()", Map.class, HashMap.class);
        if (JSON_CONTENT_TYPE.equals(rpcEntry.contentType())) {
            builder.addStatement("headers.put($L,$L)", "HEAD_CONTENT_TYPE","JSON_CONTENT_TYPE");

        } else {
            builder.addStatement("headers.put($L,$S)", "HEAD_CONTENT_TYPE", rpcEntry.contentType());
        }
        if (rpcEntry.acceptType() != null && rpcEntry.acceptType().length() > 0) {
            builder.addStatement("headers.put($L,$S)", "HEAD_ACCEPT_TYPE", rpcEntry.acceptType());
        }

        builder.addStatement("return httpRequest(url,method,body,headers)");


        proxyImplType.addMethod(builder.build());
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
