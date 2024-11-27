package cn.mapway.server;

import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class MyScans {


    /**
     * 读取二进制资源
     *
     * @param packageName the package name
     * @param resName     the res name
     * @return byte [ ]
     */
    public static byte[] readBinResource(String packageName, String resName) {
        String src = packageName.replace('.', '/');
        if (src.length() > 0) {
            src += "/";
        }
        src += resName;

        // package name

        String pacakgeName = "";
        String fileName = "";
        if (src.length() > 0) {
            int pos = src.lastIndexOf('/');

            if (pos > 0) {
                pacakgeName = src.substring(0, pos);
                fileName = src.substring(pos + 1);
            } else {
                pacakgeName = "";
                resName = src;
            }

            // 读取资源
            byte[] bytes;
            try {
                bytes = innerReadResource(pacakgeName, fileName);
                return bytes;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return new byte[0];
        }
        return new byte[0];
    }

    /**
     * 读取资源内容
     *
     * @param packageName the package name
     * @param resName     the res name
     * @return string
     */
    public static String readResource(String packageName, String resName) {
        String src = packageName.replace('.', '/');
        if (!resName.startsWith("/")) {
            if (src.length() > 0) {
                src += "/";
            }
        }
        src += resName;

        // package name

        String pacakgeName = "";
        String fileName = "";
        if (src.length() > 0) {
            int pos = src.lastIndexOf('/');

            if (pos > 0) {
                pacakgeName = src.substring(0, pos);
                fileName = src.substring(pos + 1);
            } else {
                pacakgeName = "";
                resName = src;
            }

            // 读取资源
            byte[] bytes;
            try {
                bytes = innerReadResource(pacakgeName, fileName);
                String data = new String(bytes, StandardCharsets.UTF_8);
                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return "";
        }
        return "";
    }

    /**
     * 正规化资源读取
     *
     * @param packageName 包名
     * @param fileName    文件名
     * @return
     * @throws IOException
     */
    private static byte[] innerReadResource(String packageName, String fileName) throws IOException {

        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        log.debug(packageName + "," + fileName);


        URL url = MyScans.class.getClassLoader().getResource(packageName);

        if (url == null) {
            System.out.println("sorry cannot find resource :" + packageName + "/" + fileName);
            return "".getBytes();
        }
        // 得到协议的名称
        String protocol = url.getProtocol();
        // 如果是以文件的形式保存在服务器上
        if ("file".equals(protocol)) {
            // 获取包的物理路径
            String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
            filePath += File.separator + fileName;
            return Files.readBytes(filePath);
        } else if ("jar".equals(protocol)) {
            // 如果是jar包文件
            // 定义一个JarFile
            JarFile jar;

            // 获取jar
            jar = ((JarURLConnection) url.openConnection()).getJarFile();

            // 从此jar包 得到一个枚举类
            Enumeration<JarEntry> entries = jar.entries();
            // 同样的进行循环迭代
            while (entries.hasMoreElements()) {
                // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(fileName)) {
                    InputStream input = jar.getInputStream(entry);
                    return Streams.readBytesAndClose(input);
                }
            }
        }
        return new byte[0];
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pack the pack
     * @return classes
     */
    public static Set<Class<?>> getClasses(String pack) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            // log
                                            // .error("添加用户自定义视图类错误 找不到此类的.class文件");
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName the package name
     * @param packagePath the package path
     * @param recursive   the recursive
     * @param classes     the classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                        final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    // classes.add(Class.forName(packageName + '.' + className));
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader()
                            .loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }
}
