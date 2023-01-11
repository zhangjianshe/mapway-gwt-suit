package cn.mapway.ui.server.mvc;

import cn.mapway.ui.client.mvc.decorator.IWindowDecorator;
import cn.mapway.ui.client.mvc.decorator.IWindowDecoratorFactory;
import cn.mapway.ui.client.mvc.decorator.WindowDecorator;
import cn.mapway.ui.client.mvc.decorator.WindowDecoratorInfo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 窗口管理器的工厂类
 *
 * @author zhangjianshe
 */
@Slf4j
public class WindowDecoratorFactoryGenerator extends Generator {

    /**
     * 是否已经生成了
     */
    private static final boolean hasGenerator = false;
    Map<String, String> items = new HashMap<>();

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        // 生成目标类信息
        // 生成代理类的package
        final String genPackageName = "cn.mapway.ui.client.mvc.decorator";

        // 代理类名称
        final String genClassName = "WindowDecoratorFactoryImpl";


        // 代码生成器工厂类
        ClassSourceFileComposerFactory composer =
                new ClassSourceFileComposerFactory(genPackageName, genClassName);

        if (hasGenerator) {
            log.info("GWT 生成一个DecoratorFactory{} 已经生成了", typeName);
            return composer.getCreatedClassName();
        }

        TypeOracle oracle = context.getTypeOracle();

        // 需要查找的管理模块
        JClassType instantiableType = oracle.findType(IWindowDecorator.class.getName());

        // 需要管理的模块集合
        List<JClassType> clazzes = new ArrayList<JClassType>();


        // 查找所有的类，并统计出需要管理的类
        for (JClassType classType : oracle.getTypes()) {
            if (!classType.equals(instantiableType) && classType.isAssignableTo(instantiableType)) {
                clazzes.add(classType);
            }
        }
        // 代理类继承需要代理的接口
        composer.addImplementedInterface(IWindowDecoratorFactory.class.getCanonicalName());
        // 代理类要引用的类包
        composer.addImport("cn.mapway.ui.client.mvc.decorator.*");
        composer.addImport("cn.mapway.ui.client.mvc.decorator.impl.*");

        composer.addImport(List.class.getCanonicalName());
        composer.addImport(ArrayList.class.getCanonicalName());

        // 创建一个源代码生成器对象
        PrintWriter printWriter = context.tryCreate(logger, genPackageName, genClassName);
        StringBuilder moduleListInitCodes = new StringBuilder();
        StringBuilder moduleListCreateCodes = new StringBuilder();

        if (printWriter != null) {
            // 源代码生成器
            SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);

            // 输出代码方法
            printFactoryMethod(clazzes, moduleListInitCodes, moduleListCreateCodes);

            String fileContent = readTemplate();
            fileContent = replaceAll(fileContent, genPackageName, genClassName, moduleListInitCodes.toString(), moduleListCreateCodes.toString());
            // 写入磁盘
            sourceWriter.print(fileContent);
            sourceWriter.commit(logger);
        }

        log.info("GWT 生成一个装饰器类{}", typeName);
        // hasGenerator=true;
        // 返回生成的代理对象类名称
        return composer.getCreatedClassName();
    }

    /**
     * 读取模板文件
     *
     * @return
     */
    private String readTemplate() {
        String fileName = "WindowDecoratorFactoryTemplate.txt";
        InputStream inputStream = this.getClass().getResourceAsStream(fileName);
        return Lang.readAll(Streams.utf8r(inputStream));
    }

    private String replaceAll(String source, String packageName, String className, String initList, String createList) {
        Map<String, Object> mapper = new HashMap<String, Object>();
        mapper.put("__PACKAGE_NAME__", packageName);
        mapper.put("__CLASS_NAME__", className);
        mapper.put("_INIT_DECORATORS__", initList);
        mapper.put("__CREATE_DECORATORS__", createList);
        String data = Strings.replaceBy(source, mapper);
        return data;
    }

    private String extractClassName(String abstractPath) {
        String match = "src/main/java/";
        int index = abstractPath.indexOf(match);
        return abstractPath.substring(index + match.length());
    }


    /**
     * 需要代理的模块集合
     *
     * @param clazzes
     * @param intiCodes
     * @param createCodes
     */
    private void printFactoryMethod(List<JClassType> clazzes, StringBuilder intiCodes, StringBuilder createCodes) {

        intiCodes.append("\r\n");
        for (JClassType classType : clazzes) {
            if (classType.isAbstract()) {
                continue;
            }
            WindowDecoratorInfo item = findDecorationInfo(classType);
            if (item == null) {
                continue;
            }
            String code = String.format("decorators.add(new WindowDecoratorInfo(\"%s\",\"%s\",\"%s\"));\r\n", item.name, item.unicodeIcon, item.summary);
            intiCodes.append(code);
            String createCode = String.format("if(decorator.equals(\"%s\")){return new %s();}\r\n", item.name, classType.getQualifiedSourceName());
            createCodes.append(createCode);

        }
    }

    /**
     * Find module name.
     *
     * @param classType the class type
     * @return the module item
     */
    private WindowDecoratorInfo findDecorationInfo(JClassType classType) {
        WindowDecorator marker = classType.getAnnotation(WindowDecorator.class);
        if (marker == null) {
            return null;
        }
        WindowDecoratorInfo info = new WindowDecoratorInfo();
        info.name = marker.value();
        info.summary = marker.summary();
        info.unicodeIcon = marker.iconUnicode();
        return info;
    }

}
