package cn.mapway.spring.processor;

import cn.mapway.spring.processor.module.AllModules;
import cn.mapway.spring.processor.module.ApiModuleDefine;
import cn.mapway.spring.processor.module.FieldDefine;
import cn.mapway.spring.processor.module.ModulePara;
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
public class TypeMirrorVisitor extends SimpleTypeVisitor8<TypeMirror,Void> {
    Log log = Logs.getLog(TypeMirrorVisitor.class);
    ProcessingEnvironment env;
    public TypeMirrorVisitor(ProcessingEnvironment environment)
    {
            env=environment;
    }

    /**
     * 将一种类型 转化为另一种类型
     * 在解析的过程中 懑举出所有的类型列表
     * @param typeMirror
     * @return
     */
    public TypeMirror parse(TypeMirror typeMirror)
    {
        return typeMirror.accept(this,null);
    }

    @Override
    public TypeMirror visitArray(ArrayType t, Void unused) {
        TypeMirror com= t.getComponentType();
        TypeMirror transMirror = parse(com);
        return env.getTypeUtils().getArrayType(transMirror);
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
    @Override
    public TypeMirror visitDeclared(DeclaredType t, Void unused) {

        Element element = t.asElement();
        if(element.getKind().equals(ElementKind.CLASS))
        {
            String qname=getQName(element);
            boolean isSystm=isSystem(qname);
            if(!isSystm)
            {
                element= handleModule(qname,element);
            }
        }
        List<? extends TypeMirror> typeArguments = t.getTypeArguments();
        List< TypeMirror> typeArguments2=new ArrayList<>();
        for(TypeMirror arg : typeArguments)
        {
            typeArguments2.add(parse(arg));
        }
        TypeMirror[] args=typeArguments2.toArray(new TypeMirror[typeArguments.size()]);
        // 返回转换后的类Mirror
       return env.getTypeUtils().getDeclaredType((TypeElement) element,args);
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
                Date.class.getCanonicalName(), java.sql.Date.class.getCanonicalName());

        return omittedClasses.contains(qname);
    }
    boolean isObject(String qname) {
        return "java.lang.Object".equals(qname);
    }
    /**
     *  像全局 模型列表中添加模型定义
     * @param qname
     * @param element
     * @return
     */
    private Element handleModule(String qname, Element element) {
        ApiModuleDefine module = AllModules.getInstance().getModule(qname);
        if(module==null)
        {
            //首先添加到库中 禁止接下来的操作出现循环
            module=new ApiModuleDefine(qname);
            AllModules.getInstance().put(module);

            //处理父类型
            TypeMirror superclass = ((TypeElement) element).getSuperclass();
            if(!(isSystem(superclass.toString()) || isObject(superclass.toString())))
            {
                parse(superclass);
            }

            //处理泛型参数信息
            List<? extends TypeParameterElement> typeParameters = ((TypeElement) element).getTypeParameters();
            if(typeParameters.size()>0) {
                for (TypeParameterElement typeParameter : typeParameters) {
                    ModulePara modulePara = new ModulePara(typeParameter.getSimpleName().toString());
                    // Result<T extends A & B,C super Number，K>
                    // T
                    typeParameter.asType().accept(new SimpleTypeVisitor6<Void,Void>(){
                        @Override
                        public Void visitTypeVariable(TypeVariable t, Void unused) {

                            t.getUpperBound().accept(new SimpleTypeVisitor6<Void,Void>(){

                                // A & B
                                @Override
                                public Void visitIntersection(IntersectionType t, Void unused) {
                                    for(TypeMirror v : t.getBounds())
                                    {
                                        if(isObject(v.toString()))
                                        {
                                            continue;
                                        }
                                        modulePara.upBound.add(v.toString());
                                        parse(v);
                                    }
                                    return null;
                                }
                                // K
                                @Override
                                public Void visitDeclared(DeclaredType t, Void unused) {
                                    if(!isObject(t.toString())) {
                                        modulePara.upBound.add(t.toString());
                                        parse(t);
                                    }
                                    return null;
                                }
                            },null);

                            t.getLowerBound().accept(new SimpleTypeVisitor6<Void,Void>(){
                                @Override
                                public Void visitDeclared(DeclaredType t, Void unused) {
                                    modulePara.lowerBound.add(t.toString());
                                    parse(t);
                                    return null;
                                }

                            },null);
                            return null;
                        }
                    },null);

                    module.parameters.add(modulePara);
                }
            }

            //字段信息 需要获取
            List<VariableElement> fields= (List<VariableElement>) findAllFields((TypeElement) element);
            for(VariableElement field : fields)
            {
                FieldDefine fieldDefine = new FieldDefine();

                fieldDefine.name=field.getSimpleName().toString();
                fieldDefine.qTypeName=getQName(field);
                fieldDefine.tType=parse(field.asType());

                module.fields.add(fieldDefine);
            }
        }
        return element;
    }

    @Override
    public TypeMirror visitExecutable(ExecutableType t, Void unused) {
        log.infof("ExecutableType {}",t.toString());
        return env.getTypeUtils().getNullType();
    }

    @Override
    public TypeMirror visitPrimitive(PrimitiveType t, Void unused) {
        //基本类型不变
        return t;
    }

    @Override
    public TypeMirror visitTypeVariable(TypeVariable t, Void unused) {
        log.infof("TypeVariable {}",t.toString());
        return env.getTypeUtils().getNullType();
    }

    @Override
    public TypeMirror visitWildcard(WildcardType t, Void unused) {
        log.infof("WildcardType {}",t.toString());
        return env.getTypeUtils().getNullType();
    }
}
