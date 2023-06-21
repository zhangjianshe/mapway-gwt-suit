package cn.mapway.ui.server.font;

import cn.mapway.ui.server.font.model.FontFile;
import cn.mapway.ui.server.font.model.FontItem;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebFontImport
 * 导入 iconfont.cn 下载的图标集
 *
 * @author zhang
 */
public class WebFontImport {
    public static void main(String[] args) throws FileNotFoundException {

        String zipLocation = getFromEnviroment("FONT_ZIP_LOCATION", "D:\\data\\download.zip");
        String path = Files.getParent(zipLocation);
        String tempPath = path + File.separator + "temp";
        Files.createDirIfNoExists(tempPath);
        Files.deleteDir(new File(tempPath));

        String fontPath = "";
        try {
            ZipUtil.unzip(new File(zipLocation), tempPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] files = Files.lsDir(new File(tempPath), null);
        for (File file : files) {
            if (file.getName().startsWith("font_")) {
                fontPath = file.getAbsolutePath();
                break;
            }
        }
        String jsonFile = fontPath + File.separator + "iconfont.json";

        FontFile fontFile = Json.fromJson(FontFile.class, Files.read(jsonFile));
        StringBuilder sb = new StringBuilder();
        StringBuilder mapInit = new StringBuilder();
        for (int i = 0; i < fontFile.glyphs.size(); i++) {
            FontItem item = fontFile.glyphs.get(i);
            sb.append("\tpublic final static String " + item.toName() + " = \"" + item.unicode + "\";\n");
            mapInit.append("\tunicodes.put(\"" + item.toName() + "\", Fonts." + item.toName() + ");\r\n");

        }
        String packageName = "cn.mapway.ui.client.fonts";
        String template = readTemplate("(Fonts\\.txt)$");
        Map<String, Object> mapper = new HashMap<>();

        mapper.put("__FONT_LIST__", sb.toString());
        mapper.put("__PACKAGE_NAME__", packageName);
        //unicodes.put("console", Fonts.CONSOLE);

        mapper.put("__FONT_MAP_INIT__", mapInit.toString());
        String data = Strings.replaceBy(template, mapper);
        writeTo(packageName, "Fonts.java", data);

        //拷贝字体文件到 资源目录
        //当前目录 File

        File target = new File("mapway-gwt-common/src/main/resources/META-INF/public-web-resources/webfonts");
        Files.copy(new File(fontPath + File.separator + "iconfont.woff2"), new File(target.getAbsolutePath() + File.separator + "iconfont.woff2"));
    }

    private static String getFromEnviroment(String fontZipLocation, String defaultValue) {
        String s1 = System.getenv().get(fontZipLocation);
        if (Strings.isNotBlank(s1)) {
            return s1;
        }
        return defaultValue;
    }


    public static void writeTo(String packageName, String fileName, String content) {
        String path = packageName.replaceAll("\\.", "/");

        File file = new File("mapway-gwt-common/src/main/java/" + path + File.separator + fileName);
        Files.write(file, content);
    }


    /**
     * 读取模板
     * Fonts.txt
     *
     * @param resourceName
     * @return
     */
    public static String readTemplate(String resourceName) {
        try {
            List<NutResource> resources = Scans.me().scan("mapway-gwt-common/src/main/resources/", resourceName);
            if (resources.size() > 0) {
                String data = Streams.readAndClose(resources.get(0).getReader());
                return data;
            } else {
                System.out.println("没有找到资源" + resourceName);
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
