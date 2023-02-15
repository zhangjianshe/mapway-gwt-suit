package cn.mapway.ui.server.mvc;

import cn.mapway.ui.client.mvc.IModule;
import cn.mapway.ui.client.mvc.ModuleFactory;
import cn.mapway.ui.client.mvc.ModuleMarker;
import cn.mapway.ui.client.resource.MapwayResource;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;


/**
 * 管理系统模块类，自动搜索IModule类,生成一个代理类,使用此代理类实例化模块,模块的生成是单例模式 +.
 *
 * @author zhangjianshe
 */
@Slf4j
public class ModuleFactoryGenerator extends Generator {

    private static final String DEFAULT_ICON = "MapwayResource.INSTANCE.module()";
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
        final String genPackageName = "cn.mapway.ui.client.mvc";

        // 代理类名称
        final String genClassName = "ModuleFactoryImpl";


        // 代码生成器工厂类
        ClassSourceFileComposerFactory composer =
                new ClassSourceFileComposerFactory(genPackageName, genClassName);

        if (hasGenerator) {
            log.info("GWT 生成一个模块工厂类{} 已经生成了", typeName);
            return composer.getCreatedClassName();
        }

        TypeOracle oracle = context.getTypeOracle();

        // 需要查找的管理模块
        JClassType instantiableType = oracle.findType(IModule.class.getName());

        // 需要管理的模块集合
        List<JClassType> clazzes = new ArrayList<JClassType>();


        // 查找所有的类，并统计出需要管理的类
        for (JClassType classType : oracle.getTypes()) {
            if (!classType.equals(instantiableType) && classType.isAssignableTo(instantiableType)) {
                clazzes.add(classType);
            }
        }
        // 代理类继承需要代理的接口
        composer.addImplementedInterface(ModuleFactory.class.getCanonicalName());
        // 代理类要引用的类包
        composer.addImport("cn.mapway.ui.client.mvc.*");
        composer.addImport(MapwayResource.class.getCanonicalName());
        composer.addImport("com.google.gwt.resources.client.ImageResource");
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.addImport(List.class.getCanonicalName());
        composer.addImport(Map.class.getCanonicalName());
        composer.addImport(HashMap.class.getCanonicalName());
        composer.addImport(ArrayList.class.getCanonicalName());
        composer.addImport(Collections.class.getCanonicalName());
        composer.addImport(Comparator.class.getCanonicalName());

        // 创建一个源代码生成器对象
        PrintWriter printWriter = context.tryCreate(logger, genPackageName, genClassName);
        StringBuilder moduleListInitCodes = new StringBuilder();
        StringBuilder moduleListCreateCodes = new StringBuilder();
        StringBuilder classBodyCodes = new StringBuilder();

        if (printWriter != null) {
            // 源代码生成器
            SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);


            classBodyCodes.append("\r\n   private static ImageResource DEFAULT_ICON=" + DEFAULT_ICON + ";");

            printResourceClass(clazzes, classBodyCodes);
            // 输出代码方法
            printFactoryMethod(clazzes, moduleListInitCodes, moduleListCreateCodes);

            String fileContent = readTemplate();
            fileContent = replaceAll(fileContent, genPackageName, genClassName, moduleListInitCodes.toString(), moduleListCreateCodes.toString(), classBodyCodes.toString());
            // 写入磁盘
            sourceWriter.print(fileContent);
            sourceWriter.commit(logger);
        }

        log.info("GWT 生成一个模块工厂类{}", typeName);
        // hasGenerator=true;
        // 返回生成的代理对象类名称
        return composer.getCreatedClassName();
    }

    /**
     * 读取模板文件
     *
     */
    private String readTemplate() {
        String fileName = "ModuleFactoryTemplate.txt";
        InputStream inputStream = this.getClass().getResourceAsStream(fileName);
        assert inputStream != null;
        return Lang.readAll(Streams.utf8r(inputStream));
    }

    /**
     * __PACKAGE_NAME__
     * __CLASS_NAME__
     * __MODULE_CREATE_LIST__
     * //if(moduleCode.equals("toolbar")){
     * //    return new ToolbarModule();
     * //}
     * __MODULE_INIT_LIST__
     * // ModuleInfo moduleInfo=new ModuleInfo(null,null,null,false,null,null,true);
     * // temp.add(moduleInfo);
     * // addModule(moduleInfo);
     *
     * @param source
     * @return
     */
    private String replaceAll(String source, String packageName, String className, String initList, String createList, String classBody) {
        Map<String, Object> mapper = new HashMap<String, Object>();
        mapper.put("__PACKAGE_NAME__", packageName);
        mapper.put("__CLASS_NAME__", className);
        mapper.put("__MODULE_CREATE_LIST__", createList);
        mapper.put("__MODULE_INIT_LIST__", initList);
        mapper.put("__CLASS_BODY__", classBody);
        String data = Strings.replaceBy(source, mapper);
        return data;
    }

    /**
     * @param classType
     * @param iconName
     * @return
     */
    private String copyIcon(JClassType classType, String iconName) {

        if (Strings.isBlank(iconName) || "icon.png".equals(iconName)) {
            return "";
        }
        String packageName = classType.getPackage().getName().replaceAll("\\.", "/");
        String filename = packageName + "/" + iconName;

        String projectBase = System.getProperty("project.base");

        File f = Files.findFile(filename);

        if (f != null) {
            String path = f.getAbsolutePath().replaceAll("\\\\", "/");
            return extractClassName(path);
        } else {
            String filePath = projectBase + "/src/main/java/" + filename;

            File f1 = new File(filePath);
            if (f1.exists()) {
                String path = f1.getAbsolutePath().replaceAll("\\\\", "/");
              //  log.info("*** find module {} icon {}", classType.getName(), iconName);
                return extractClassName(path);
            } else {
              //  log.warn("*** module {} 's configuration icon {} can not found", classType.getName(), iconName);
                return "";
            }
        }
    }

    private String extractClassName(String abstractPath) {
        String match = "src/main/java/";
        int index = abstractPath.indexOf(match);
        return abstractPath.substring(index + match.length());
    }

    /**
     * @param iconName
     * @return
     */
    private String translateResourceFromPathToObject(String iconName) {

        if (Strings.isBlank(iconName)) {
            return "MapwayResource.INSTANCE.module()";
        }
        if (items.get(iconName) == null) {
            return "MapwayResource.INSTANCE.module()";
        }
        return "ModuleInfoResourceImpl.INSTANCE." + genResourceName(iconName) + "()";
    }

    /**
     * 构造资源类
     *
     * @param clazzes
     * @param sourceWriter
     */
    private void printResourceClass(List<JClassType> clazzes, StringBuilder sourceWriter) {
        sourceWriter.append("\r\n public static interface ModuleInfoResourceImpl extends com.google.gwt.resources.client.ClientBundle {");
        sourceWriter.append("\r\n   public static ModuleInfoResourceImpl INSTANCE= GWT.create(ModuleInfoResourceImpl.class);");
        // items contain   className->imagePath(may be empty)
        items.clear();
        for (JClassType t : clazzes) {
            if (t.isAbstract()) {
                continue;
            }
            ServerModuleInfo item = findModuleName(t);
            if (item == null) {
                continue;
            }
            if (Strings.isBlank(item.getIconString())
                    || Strings.equals(item.getIconString(), DEFAULT_ICON)
                    || Strings.equals(item.getIconString(), "icon.png")
            ) {
                items.put(t.getQualifiedSourceName(), "");
                continue;
            }
            items.put(t.getQualifiedSourceName(), copyIcon(t, item.getIconString()));
        }
        sourceWriter.append("\r\n");
        for (String key : items.keySet()) {
            String path = items.get(key);

            if (Strings.isBlank(path)) {
                continue;
            }
            sourceWriter.append("\t@Source(\"" + path + "\")\r\n");
            sourceWriter.append("\tImageResource " + genResourceName(key) + "();\r\n");
        }

        sourceWriter.append("}\r\n");
    }

    private String genResourceName(String key) {
        return "i" + Lang.md5(key).substring(6, 12);
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
        intiCodes.append("ModuleInfo moduleInfo;\r\n");
        for (JClassType classType : clazzes) {
            if (classType.isAbstract()) {
                continue;
            }
            ServerModuleInfo item = findModuleName(classType);
            if (item == null) {
                continue;
            }

            // ModuleInfo moduleInfo=new ModuleInfo(null,null,null,false,null,null,true);
            // temp.add(moduleInfo);
            // addModule(moduleInfo);
            if (Strings.isBlank(items.get(classType.getQualifiedSourceName()))) {
                intiCodes.append(" moduleInfo= new ModuleInfo(\"" + item.name + "\",\"" + item.code + "\",\""
                        + item.summary + "\"," + (item.isPublic ? "true" : "false") + ",DEFAULT_ICON,\""
                        + item.hash + "\"," + item.isVisible + ",\"" + item.group + "\",\"" + item.unicode + "\");\r\n");
            } else {
                intiCodes.append("moduleInfo=new ModuleInfo(\"" + item.name + "\",\"" + item.code + "\",\""
                        + item.summary + "\"," + (item.isPublic ? "true" : "false") + ",ModuleInfoResourceImpl.INSTANCE."
                        + genResourceName(classType.getQualifiedSourceName())
                        + "(),\"" + item.hash + "\"," + item.isVisible + ",\"" + item.group + "\",\"" + item.unicode + "\");\r\n");
            }
            intiCodes.append(String.format("moduleInfo.order=%s;\r\n", item.order));
            intiCodes.append(String.format("moduleInfo.parent=\"%s\";\r\n", item.parent));

            for (String tag : item.tags) {
                if (Strings.isNotBlank(tag)) {
                    intiCodes.append(String.format("moduleInfo.tags.add(\"%s\");\r\n", tag));
                }
            }
            intiCodes.append("modulesFlat.add(moduleInfo);\r\n");
            createCodes.append(String.format(" if(moduleCode.equals(\"%s\")){ return new %s();}\r\n", item.code, classType.getQualifiedSourceName()));

        }
    }

    /**
     * Find module name.
     *
     * @param classType the class type
     * @return the module item
     */
    private ServerModuleInfo findModuleName(JClassType classType) {
        ModuleMarker marker = classType.getAnnotation(ModuleMarker.class);
        if (marker == null) {
            return null;
        }
        String modulecode = null;
        String modulename = null;
        String summary = "";
        boolean isPublic = false;
        boolean isVisible = true;
        String group = "";

        modulecode = marker.value();
        modulename = marker.name();
        isPublic = marker.isPublic();
        summary = marker.summary();
        isVisible = marker.visible();
        group = marker.group() == null ? "" : marker.group();

        if (modulecode == null || modulecode.length() == 0) {
            modulecode = classType.getSimpleSourceName();
        }
        if (modulename == null || modulename.length() == 0) {
            modulename = modulecode;
        }

        ServerModuleInfo item = new ServerModuleInfo("", "", "", false, marker.icon(), "", false);
        log.info("module " + modulename + "\t" + group);
        item.code = modulecode;
        item.name = modulename;
        item.isPublic = isPublic;
        item.summary = summary;
        item.group = group;
        String md5 = Lang.md5(item.code);
        item.hash = md5.substring(md5.length() - 6);
        item.isVisible = isVisible;
        item.order = marker.order();
        item.unicode = marker.unicode();
        //新增的行为
        item.parent = marker.parent();
        item.setTags(marker.tags());
        return item;
    }

}
