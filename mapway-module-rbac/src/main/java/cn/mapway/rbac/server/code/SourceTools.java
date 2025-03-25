package cn.mapway.rbac.server.code;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * SourceTools
 * 源代码工具
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class SourceTools {
    public static void main(String[] args) {
        SourceTools tools = new SourceTools();
        String packageBean = "cn.mapway.rbac.shared.rpc";
        String packageService = "cn.mapway.rbac.server.service";
        ArrayList<String> methods = Lang.list(
                "queryUserOrg","queryUserRoleResource"
        );
        methods.stream().forEach(n -> {
            tools.gen(n, packageBean, packageService);
        });
        methods.stream().forEach(n -> {
            tools.genMethod(n, packageBean, packageService);
        });
        methods.stream().forEach(n -> {
            tools.genImpl(n);
        });

        methods.stream().forEach(n -> {
            tools.genApi(n);
        });

    }

    private void genMethod(String methodName, String packageBean, String packageService) {
        String beanName = Strings.upperFirst(methodName);
        String method = ("RpcResult<" + beanName + "Response> " + methodName + "(" + beanName + "Request request);");
        System.out.println(method);

        String source = "mapway-module-rbac/src/main/java/cn/mapway/rbac/shared/servlet/IRbacServer.java";
        File file = null;
        try {
            file = ResourceUtils.getFile(source);
            String content = Files.read(file);
            String insertPoint = "///CODE_GEN_INSERT_POINT///";
            String replace = insertPoint + "\r\n\t" + method + "\r\n";
            content = content.replaceFirst(insertPoint, replace);
            Files.write(file, content);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        //异步接口   void login(UserLoginRequest request, AsyncCallback<RpcResult<CurrentUserResponse>> async);
         source = "mapway-module-rbac/src/main/java/cn/mapway/rbac/shared/servlet/IRbacServerAsync.java";
       String asyncMethod="void "+methodName+"("+beanName+"Request request, AsyncCallback<RpcResult<"+beanName+"Response>> async);";
        try {
            file = ResourceUtils.getFile(source);
            String content = Files.read(file);
            String insertPoint = "///CODE_GEN_INSERT_POINT///";
            String replace = insertPoint + "\r\n\t" + asyncMethod + "\r\n";
            content = content.replaceFirst(insertPoint, replace);
            Files.write(file, content);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * 根据方法生成 一个
     * Executor的所有类和方法
     *
     * @param methodName
     */
    private void gen(String methodName, String beanPackage, String executorPackage) {

        String basePath = "mapway-module-rbac/src/main/java/";

        String beanName = Strings.upperFirst(methodName) + "Request";
        genBean(basePath, beanName, beanPackage);
        beanName = Strings.upperFirst(methodName) + "Response";
        genBean(basePath, beanName, beanPackage);

        beanName = Strings.upperFirst(methodName);
        basePath = "mapway-module-rbac/src/main/java/";
        genExecutor(basePath, beanName, beanPackage, executorPackage);

    }

    private void genApi(String queryName) {
        String template = readTemplate("code/controller.txt");
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("__name__", queryName);
        mapper.put("__NAME__", Strings.upperFirst(queryName));

        String data = Strings.replaceBy(template, mapper);
        System.out.println(data);
    }

    private void  genImpl(String queryUserVolumn) {
        String template = readTemplate("code/serverlet.txt");
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("__name__", queryUserVolumn);
        mapper.put("__NAME__", Strings.upperFirst(queryUserVolumn));

        String data = Strings.replaceBy(template, mapper);
        System.out.println(data);

        String source ="mapway-module-rbac/src/main/java/cn/mapway/rbac/server/servlet/RbacServlet.java";

        try {
            File file = ResourceUtils.getFile(source);
            String content = Files.read(file);
            String insertPoint = "///CODE_GEN_INSERT_POINT///";
            String replace = insertPoint + "\r\n\t" + data + "\r\n";
            content = content.replaceFirst(insertPoint, replace);
            Files.write(file, content);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //servlet implements
    }


    private void genExecutor(String basePath, String beanName, String beanPackage, String servicePackage) {
        String template = readTemplate("code/executor.txt");
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("__PACKAGE__", servicePackage);
        mapper.put("__BEANPACKAGE__", beanPackage);
        mapper.put("__NAME__", beanName);
        String data = Strings.replaceBy(template, mapper);
        writeTo(basePath, servicePackage, beanName + "Executor.java", data);
    }


    private void genBean(String basePath, String beanName, String packageName) {
        String template = readTemplate("code/bean.txt");
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("__PACKAGE__", packageName);
        mapper.put("__NAME__", beanName);
        String data = Strings.replaceBy(template, mapper);


        writeTo(basePath, packageName, beanName + ".java", data);

    }

    /**
     * @param basePath    "ai-server-web/src/main/java/"
     * @param packageName
     * @param fileName
     * @param content
     */
    private void writeTo(String basePath, String packageName, String fileName, String content) {
        String path = packageName.replaceAll("\\.", "/");
        try {
            File file = ResourceUtils.getFile(basePath + path + "/" + fileName);
            if (!file.exists()) {
                Files.write(file, content);
            } else {
                //     log.warn("file {} exist", file.getAbsolutePath());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取模板
     *
     * @param resourceName
     * @return
     */
    private String readTemplate(String resourceName) {
        try {
            URL url = Resources.getResource(resourceName);
            return Streams.readAndClose(Streams.utf8r(url.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
