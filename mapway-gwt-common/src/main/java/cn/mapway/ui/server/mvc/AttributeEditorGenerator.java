package cn.mapway.ui.server.mvc;

import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditorFactory;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * 搜集属性编辑器 创建一个工厂
 */
@Slf4j
public class AttributeEditorGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        // 代理类名称
        final String genPackageName = "cn.mapway.ui.client.mvc.attribute.editor";
        final String genClassName = "AttributeEditorFactoryImpl";


        // 代码生成器工厂类
        ClassSourceFileComposerFactory composer =
                new ClassSourceFileComposerFactory(genPackageName, genClassName);


        TypeOracle oracle = context.getTypeOracle();

        // 需要查找的管理模块
        JClassType instantiableType = oracle.findType(IAttributeEditor.class.getName());

        // 需要管理的模块集合
        List<JClassType> clazzes = new ArrayList<JClassType>();


        // 查找所有的类，并统计出需要管理的类
        for (JClassType classType : oracle.getTypes()) {
            if (!classType.equals(instantiableType) && classType.isAssignableTo(instantiableType)) {
                clazzes.add(classType);
            }
        }
        // 代理类继承需要代理的接口
        composer.addImplementedInterface(IAttributeEditorFactory.class.getCanonicalName());
        // 代理类要引用的类包
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.addImport(Map.class.getCanonicalName());
        composer.addImport(HashMap.class.getCanonicalName());

        // 创建一个源代码生成器对象
        PrintWriter printWriter = context.tryCreate(logger, genPackageName, genClassName);

        StringBuilder createCodes = new StringBuilder();

        if (printWriter != null) {
            // 源代码生成器
            SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);
            createCodesList(clazzes, createCodes);
            String fileContent = readTemplate();
            fileContent = replaceAll(fileContent, createCodes.toString());
            // 写入磁盘
            sourceWriter.print(fileContent);
            sourceWriter.commit(logger);
        }
        return composer.getCreatedClassName();
    }

    /**
     * //         else if("".equals(code))
     * //         {
     * //             return new XXXXEDITOR();
     * //         }
     *
     * @param clazzes
     * @param createCodes
     */

    private void createCodesList(List<JClassType> clazzes, StringBuilder createCodes) {
        Set<String> processed = new HashSet<>();
        for (JClassType clazzType : clazzes) {
            AttributeEditor attributeEditor = clazzType.getAnnotation(AttributeEditor.class);
            if (attributeEditor == null) {
                log.error(clazzType.getName() + " is not marker as AttributeEditor");
                continue;
            }

            String code = attributeEditor.value();
            if (code == null || code.length() == 0) {
                log.error(clazzType.getName() + " Marker AttributeEditor's code is null");
                continue;
            }

            if (processed.contains(code)) {
                log.error(clazzType.getName() + "  AttributeEditor's code is duplicate");
                continue;
            }

            createCodes.append("\r\n else if(code.equals(\"" + code + "\"))");
            createCodes.append("\r\n {");
            createCodes.append("\r\n   return new " + clazzType.getQualifiedSourceName() + "();");
            createCodes.append("\r\n }");
            log.info("generate the editor create code " + clazzType.getName());
            processed.add(code);
        }
    }

    private String readTemplate() {
        String fileName = "FakeIAttributeEditorFactoryImpl.txt";
        InputStream inputStream = this.getClass().getResourceAsStream(fileName);
        assert inputStream != null;
        return Lang.readAll(Streams.utf8r(inputStream));
    }

    private String replaceAll(String source, String createCodes) {
        Map<String, Object> mapper = new HashMap<String, Object>();
        mapper.put("//ATTRIBUTE_EDITOR_CREATOR_LIST", createCodes);

        String data = Strings.replaceBy(source, mapper);
        return data;
    }
}
