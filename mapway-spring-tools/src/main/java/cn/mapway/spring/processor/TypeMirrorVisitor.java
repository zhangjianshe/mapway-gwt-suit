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
public class TypeMirrorVisitor extends SimpleTypeVisitor8<Void,Void> {
    Log log = Logs.getLog(TypeMirrorVisitor.class);
    List<TypeMirror> components = new ArrayList<TypeMirror>();
    ProcessingEnvironment env;
    public TypeMirrorVisitor(ProcessingEnvironment environment)
    {
            env=environment;
    }
    public void parse(TypeMirror typeMirror)
    {
        typeMirror.accept(this,null);
    }

    @Override
    public Void visitArray(ArrayType t, Void unused) {
        TypeMirror com= t.getComponentType();
        log.infof("Array {}",com.toString());
        return null;
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
    public Void visitDeclared(DeclaredType t, Void unused) {
        Element element = t.asElement();
        if(element.getKind().equals(ElementKind.CLASS))
        {
            String qname=getQName(element);
            boolean isSystm=isSystem(qname);
            if(!isSystm)
            {
                handleModule(qname,element);
            }
        }
        List<? extends TypeMirror> typeArguments = t.getTypeArguments();
        for(TypeMirror arg : typeArguments)
        {
            parse(arg);
        }
        return null;
    }

    /**
     * 判断是否是GWT系统 可以处理的类
     * @param qname
     * @return
     */
    private boolean isSystem(String qname) {
        List<String> omittedClasses = Lang.list(String.class.getCanonicalName(),
                Date.class.getCanonicalName(), java.sql.Date.class.getCanonicalName());

        return omittedClasses.contains(qname);
    }

    /**
     *  像全局 模型列表中添加模型定义
     * @param qname
     * @param element
     */
    private void handleModule(String qname, Element element) {
        ApiModuleDefine module = AllModules.getInstance().getModule(qname);
        if(module==null)
        {
            //首先添加到库中 禁止接下来的操作出现循环
            module=new ApiModuleDefine(qname);
            AllModules.getInstance().put(module);

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
                                        modulePara.upBound.add(v.toString());
                                        parse(v);
                                    }
                                    return null;
                                }
                                // K
                                @Override
                                public Void visitDeclared(DeclaredType t, Void unused) {
                                    modulePara.upBound.add(t.toString());
                                    parse(t);
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
                String fieldName = field.getSimpleName().toString();
                String typeName = field.asType().toString();
                FieldDefine fieldDefine = new FieldDefine( typeName,fieldName);
                module.fields.add(fieldDefine);
            }
        }
    }

    @Override
    public Void visitExecutable(ExecutableType t, Void unused) {
        log.infof("ExecutableType {}",t.toString());
        return null;
    }

    @Override
    public Void visitPrimitive(PrimitiveType t, Void unused) {

        log.infof("PrimitiveType {}",t.toString());
        return null;
    }

    @Override
    public Void visitTypeVariable(TypeVariable t, Void unused) {
        log.infof("TypeVariable {}",t.toString());
        return null;
    }

    @Override
    public Void visitWildcard(WildcardType t, Void unused) {
        log.infof("WildcardType {}",t.toString());
        return null;
    }
}
