package cn.mapway.ui.server.font;

import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZipUtil
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class ZipUtil {
    private final static int BUFFER_SIZE = 4096;



    public static void zip(File[] files, String zipFile) throws IOException {
        File target = new File(zipFile);
        Files.createDirIfNoExists(target.getParentFile());

        BufferedInputStream origin;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            byte[] data = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++) {
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    String pathName = files[i].getName();
                    ZipEntry entry = new ZipEntry(pathName.substring(pathName.lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }
            }
        } finally {
            out.close();
        }
    }

    public static void unzip(File zipFile, String location) throws IOException {
        unzip(zipFile.getAbsolutePath(), location);
    }

    public static void unzip(String zipFile, String location) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(new File(location), zipEntry, false);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            log.info("write file {} with size {}", zipEntry.getName(), zipEntry.getSize());
            zipEntry = zis.getNextEntry();

        }
        zis.closeEntry();
        zis.close();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry, boolean flattenPath) throws IOException {
        String name = zipEntry.getName();
        if (flattenPath) {
            name = Files.getName(name);
        }
        File destFile = new File(destinationDir, name);

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }

    public static String listInfo(File file) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table cellpadding='4'>");
        ZipInputStream zis = new ZipInputStream(Streams.fileIn(file));
        ZipEntry zipEntry = null;
        int index = 1;
        try {
            zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory()) {
                    sb.append("<tr><td>" + (index++) + "</td><td>目录</td><td>").append(zipEntry.getName()).append("</td><td></td></tr>");
                } else {
                    sb.append("<tr><td>" + (index++) + "</td><td>文件</td><td>").append(zipEntry.getName()).append("</td><td align='right'>" + Strings.formatSizeForReadBy1024(zipEntry.getSize()) + "</td></tr>");
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            return "解析压缩文件出错了:" + e.getMessage();
        }
        sb.append("</table>");
        return sb.toString();
    }

}
