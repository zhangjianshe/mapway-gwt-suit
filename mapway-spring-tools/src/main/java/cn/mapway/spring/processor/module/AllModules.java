package cn.mapway.spring.processor.module;

import com.squareup.javapoet.*;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 包含系统接口中使用到的所有 模型类
 */
public class AllModules {

    private static final AllModules INSTANCE = new AllModules();

    public static AllModules getInstance() {
        return INSTANCE;
    }

    private final Map<String, ApiModuleDefine> modules = new HashMap<String, ApiModuleDefine>();

    public ApiModuleDefine getModule(String qname) {
        return modules.get(qname);
    }

    public void put(ApiModuleDefine define) {
        modules.put(define.qname, define);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (ApiModuleDefine define : modules.values()) {
            sb.append("\r\n");
            sb.append(define.toString());
        }
        return sb.toString();
    }


    /**
     * 输出所有的模型到 文件
     *
     * @param outputPath
     */
    public void emitToPath(String outputPath) {

        for (ApiModuleDefine define : modules.values()) {
            if (define.translateName == null) {
                continue;
            }
            TypeSpec.Builder tb;
            if (define.translateName instanceof ParameterizedTypeName) {
                ParameterizedTypeName p = (ParameterizedTypeName) define.translateName;
                if (define.isInterface) {

                    tb = TypeSpec.interfaceBuilder(p.rawType.simpleName());
                } else {
                    tb = TypeSpec.classBuilder(p.rawType.simpleName());
                }

                for (TypeName arg : p.typeArguments) {
                    if (arg instanceof TypeVariableName) {
                        tb.addTypeVariable((TypeVariableName) arg);
                    }
                }
            } else {
                String name = getSimpleName(define.translateName.toString());
                if (define.isInterface) {
                    tb = TypeSpec.interfaceBuilder(name);
                } else {
                    tb = TypeSpec.classBuilder(name);
                }
            }

            if (define.translateSuper != null) {
                tb.superclass(define.translateSuper);
            }
            if (Lang.isNotEmpty(define.translateImpls)) {
                tb.addSuperinterfaces(define.translateImpls);
            }

            tb.addModifiers(Modifier.PUBLIC);
            AnnotationSpec.Builder jsTypeBuild = AnnotationSpec.builder(JsType.class);
            jsTypeBuild.addMember("isNative", "$L", "true");
            jsTypeBuild.addMember("namespace", "$T.GLOBAL", JsPackage.class);
            jsTypeBuild.addMember("name", "$S", "Object");
            tb.addAnnotation(jsTypeBuild.build());

            for (FieldDefine fieldDefine : define.fields) {
                // Integer Float Byte Long 都转化为Double存储
                TypeName storeTypeName;
                String toNumberType = "";
                if (fieldDefine.tType.isPrimitive() || fieldDefine.tType.isBoxedPrimitive()) {
                    TypeName temp = fieldDefine.tType.unbox();
                    if (temp.equals(TypeName.FLOAT)) {
                        toNumberType = "float";
                        storeTypeName = TypeName.DOUBLE.box();
                    } else if (temp.equals(TypeName.INT)) {
                        toNumberType = "int";
                        storeTypeName = TypeName.DOUBLE.box();
                    } else if (temp.equals(TypeName.LONG)) {
                        toNumberType = "long";
                        storeTypeName = TypeName.DOUBLE.box();
                    } else if (temp.equals(TypeName.BYTE)) {
                        toNumberType = "byte";
                        storeTypeName = TypeName.DOUBLE.box();
                    } else if (temp.equals(TypeName.SHORT)) {
                        toNumberType = "short";
                        storeTypeName = TypeName.DOUBLE.box();
                    } else {
                        storeTypeName = fieldDefine.tType;
                    }
                } else {
                    storeTypeName = fieldDefine.tType;
                }

                FieldSpec.Builder fb = FieldSpec.builder(storeTypeName, fieldDefine.name, Modifier.PRIVATE);
                tb.addField(fb.build());
                //字段处理 增加另外的处理

                MethodSpec.Builder methodGetterBuilder = MethodSpec.methodBuilder("get" + Strings.upperFirst(fieldDefine.name));
                methodGetterBuilder.returns(fieldDefine.tType);
                methodGetterBuilder.addModifiers(Modifier.FINAL, Modifier.PUBLIC);
                if (Strings.isBlank(toNumberType)) {
                    methodGetterBuilder.addCode("\treturn this.$L;", fieldDefine.name);
                } else {

                    methodGetterBuilder.addCode("\treturn this.$L==null?null:this.$L.$LValue();", fieldDefine.name,fieldDefine.name,toNumberType);
                }
                AnnotationSpec.Builder jsOverlay1 = AnnotationSpec.builder(JsOverlay.class);
                methodGetterBuilder.addAnnotation(jsOverlay1.build());

                tb.addMethod(methodGetterBuilder.build());

                MethodSpec.Builder methodSetterBuilder = MethodSpec.methodBuilder("set" + Strings.upperFirst(fieldDefine.name));
                methodSetterBuilder.addParameter(fieldDefine.tType, "value");
                methodSetterBuilder.returns(TypeName.VOID);
                methodSetterBuilder.addModifiers(Modifier.FINAL, Modifier.PUBLIC);
                if (Strings.isBlank(toNumberType)) {
                    methodSetterBuilder.addCode("\t this.$L=value;", fieldDefine.name);
                }
                else{
                    methodSetterBuilder.addCode("\t this.$L=(value==null)?null:value.doubleValue();", fieldDefine.name);
                }


                AnnotationSpec.Builder jsOverlay = AnnotationSpec.builder(JsOverlay.class);
                methodSetterBuilder.addAnnotation(jsOverlay.build());
                tb.addMethod(methodSetterBuilder.build());

            }
            JavaFile javaFile = JavaFile.builder(define.packageName, tb.build()).build();
            try {
                javaFile.writeTo(new File(outputPath));
                System.out.println("Written to " + outputPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getSimpleName(String name) {
        if (Strings.isBlank(name)) {
            return "";
        }
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            return name.substring(pos + 1);
        }
        return name;
    }
}
