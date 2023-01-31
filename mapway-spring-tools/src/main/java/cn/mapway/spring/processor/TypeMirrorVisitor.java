package cn.mapway.spring.processor;

import cn.mapway.spring.processor.module.AllModules;
import cn.mapway.spring.processor.module.ApiModuleDefine;
import cn.mapway.spring.processor.module.FieldDefine;
import cn.mapway.spring.processor.module.ModulePara;
import com.squareup.javapoet.*;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.SimpleTypeVisitor8;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类型解析器
 */
public class TypeMirrorVisitor extends SimpleTypeVisitor8<TypeName,String> {
    Log log = Logs.getLog(TypeMirrorVisitor.class);
    ProcessingEnvironment env;
    public TypeMirrorVisitor(ProcessingEnvironment environment)
    {
            env=environment;
    }

    /**
     * 将一种类型 转化为另一种类型
     * 在解析的过程中 懑举出所有的类型列表
     *
     * @param packageName
     * @param typeMirror
     * @return
     */
    public TypeName parse(String packageName, TypeMirror typeMirror)
    {
        return typeMirror.accept(this,packageName);
    }

    @Override
    public TypeName visitArray(ArrayType t, String packageName) {
        TypeMirror com= t.getComponentType();
        TypeName transMirror = parse(packageName, com);
        return ArrayTypeName.of(transMirror);
    }

    public List<? extends Element> findAllFields(TypeElement typeElement)
    {
        return typeElement.getEnclosedElements().stream().filter(e-> {
            return e.getKind().equals(ElementKind.FIELD)
                    && (!e.getModifiers().contains(Modifier.STATIC));
        }).collect(Collectors.toList());
    }
    String getQName(Element element)
    {
        PackageElement pkg = env.getElementUtils().getPackageOf(element);
        return pkg.getQualifiedName()+"."+element.getSimpleName();
    }

    /**
     * 处理类似 Result<Time1<Nest1<Integer>>>,Time2>
     * @param t
     * @param packageName
     * @return
     */
    @Override
    public TypeName visitDeclared(DeclaredType t,  String packageName) {

        TypeName outerName=TypeName.get(t);

        Element element = t.asElement();
        if(element.getKind().equals(ElementKind.CLASS))
        {
            String qname=getQName(element);
            if(!isSystem(qname))
            {
                outerName= handleModule(packageName,false,qname,element);
            }
        }
        else if(element.getKind().equals(ElementKind.INTERFACE))
        {
            String qname=getQName(element);
            if(!isSystem(qname)) {
                outerName = handleModule(packageName, true, qname, element);
            }
        }

        // outName is  ApiResult<T>
        // returnType would be ApiResult<GoData>
        List<? extends TypeMirror> typeArguments = t.getTypeArguments();
        List<TypeName> typeArguments2=new ArrayList<>();
        for(TypeMirror arg : typeArguments)
        {
            typeArguments2.add(parse(packageName, arg));
        }
        if(Lang.isNotEmpty(typeArguments2))
        {
            TypeName[] types = typeArguments2.toArray(new TypeName[typeArguments2.size()]);
            String name=AllModules.getSimpleName(outerName.toString());
            // remove args
            int pos=name.indexOf("<");
            if(pos>0)
            {
                name=name.substring(0,pos);
            }
            outerName=ParameterizedTypeName.get(ClassName.get(packageName,name), types);
        }

       return outerName;
    }


    /**
     * 判断是否是GWT系统 可以处理的类
     * @param qname
     * @return
     */
    private boolean isSystem(String qname) {
        if(qname.startsWith("java.lang"))
        {
            return true;
        }
        List<String> omittedClasses = Lang.list(String.class.getCanonicalName(),
                Date.class.getCanonicalName(),
                java.sql.Date.class.getCanonicalName(),
                java.io.Serializable.class.getCanonicalName()
        );

        return omittedClasses.contains(qname);
    }
    boolean isObject(String qname) {
        return "java.lang.Object".equals(qname);
    }



    /**
     *  像全局 模型列表中添加模型定义
     *
     *
     * @param packageName
     * @param qname       cn.test.client.rpc.module.Result
     * @param element     Restlt<Time1<Integer>> extends Parent
     * @return
     */
    private TypeName handleModule(String packageName, boolean isInterface, String qname, Element element) {

        ApiModuleDefine module = AllModules.getInstance().getModule(qname);
        if(module==null)
        {
            //首先添加到库中 禁止接下来的操作出现循环
            module=new ApiModuleDefine(qname);
            AllModules.getInstance().put(module);

            //转换后的基类
            TypeName translationSuperMirror=null;
            //处理父类型
            TypeMirror superclass = ((TypeElement) element).getSuperclass();
            if(!(isSystem(superclass.toString()) || isObject(superclass.toString())))
            {
                translationSuperMirror=parse(packageName, superclass);
            }

            //处理实现的接口
            List<? extends TypeMirror> interfaces = ((TypeElement) element).getInterfaces();
            for(int i=0; i<interfaces.size(); i++)
            {
                module.translateImpls.add(parse(packageName, interfaces.get(i)));
            }


            //处理泛型参数信息  Time1<Integer>
            List<? extends TypeParameterElement> typeParameters = ((TypeElement) element).getTypeParameters();
            List<TypeName> translateParameters = new ArrayList<>();

            if(typeParameters.size()>0) {
                    // Time1<Integer>
                for (TypeParameterElement typeParameter : typeParameters) {
                    ModulePara modulePara = new ModulePara(typeParameter.getSimpleName().toString());
                    // Result<T extends A & B,C super Number，K>
                    //        =============== ==============  =
                    TypeName translatePara= typeParameter.asType().accept(new SimpleTypeVisitor6<TypeName,String>(){
                        @Override
                        public TypeName visitTypeVariable(TypeVariable t, String pname) {

                            List<TypeName> up=t.getUpperBound().accept(new SimpleTypeVisitor6<List<TypeName>,String>(){

                                // A & B
                                @Override
                                public List<TypeName> visitIntersection(IntersectionType t, String pname) {
                                    List<TypeName> translateup=new ArrayList<TypeName>();
                                    for(TypeMirror v : t.getBounds())
                                    {
                                        if(isObject(v.toString()))
                                        {
                                            continue;
                                        }
                                        modulePara.upBound.add(v.toString());
                                        TypeName parse = parse(pname, v);
                                        translateup.add(parse);
                                    }
                                    return translateup;
                                }
                                // K
                                @Override
                                public List<TypeName> visitDeclared(DeclaredType t, String pname) {
                                    if(!isObject(t.toString())) {
                                        modulePara.upBound.add(t.toString());
                                        return Lang.list(parse(pname, t));
                                    }
                                    return new ArrayList<>();
                                }
                            },packageName);


                            TypeName low=t.getLowerBound().accept(new SimpleTypeVisitor6<TypeName,String>(){
                                @Override
                                public TypeName visitDeclared(DeclaredType t, String pname) {
                                    modulePara.lowerBound.add(t.toString());
                                    return parse(packageName, t);
                                }

                            },packageName);

                            if(Lang.isNotEmpty(up))
                            {
                                // extends
                                TypeName[] types = up.toArray(new TypeName[up.size()]);
                                return TypeVariableName.get(typeParameter.getSimpleName().toString(),types);
                            }
                            else if(Lang.isNotEmpty(low))
                            {
                                return TypeVariableName.get(typeParameter.getSimpleName().toString(),low);
                            }
                            return TypeVariableName.get(typeParameter.getSimpleName().toString());
                        }
                    },null);
                    translateParameters.add(translatePara);
                    module.parameters.add(modulePara);
                }
            }

            if(!isInterface) {
                module.isInterface=false;
                //字段信息 需要获取
                List<VariableElement> fields = (List<VariableElement>) findAllFields((TypeElement) element);
                for (VariableElement field : fields) {
                    FieldDefine fieldDefine = new FieldDefine();

                    fieldDefine.name = field.getSimpleName().toString();
                    fieldDefine.qTypeName = getQName(field);
                    fieldDefine.tType = parse(packageName, field.asType());
                    module.fields.add(fieldDefine);
                }
            }
            else{
                //接口 社么都不做
                module.isInterface=true;
            }

            String name=element.getSimpleName().toString();

            TypeName[] args=translateParameters.toArray(new TypeName[translateParameters.size()]);
            module.packageName=packageName;
            if(args.length>0) {
                module.translateName = ParameterizedTypeName.get(ClassName.get(packageName, name), args);
            }
            else {
                module.translateName = ClassName.get(packageName, name);
            }

            if(translationSuperMirror!=null) {
                module.translateSuper=translationSuperMirror;
            }
        }
        return module.translateName;
    }

    @Override
    public TypeName visitExecutable(ExecutableType t,  String packageName) {
        log.infof("ExecutableType {}",t.toString());
        return  TypeName.get(t);
    }


    @Override
    public TypeName visitPrimitive(PrimitiveType t,  String packageName) {
        return TypeName.get(t).box();
    }

    @Override
    public TypeName visitTypeVariable(TypeVariable t,  String packageName) {
        log.infof("TypeVariable {}",t.toString());
        return TypeName.get(t);
    }

    @Override
    public TypeName visitWildcard(WildcardType t,  String packageName) {
        log.infof("WildcardType {}",t.toString());
        return TypeName.get(t);
    }
}
