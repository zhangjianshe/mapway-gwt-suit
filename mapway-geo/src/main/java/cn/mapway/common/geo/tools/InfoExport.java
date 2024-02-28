package cn.mapway.common.geo.tools;


import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;

/**
 * InfoExport
 * 打印有关信息
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */

public class InfoExport {
    GlobalMercator globalMercator = new GlobalMercator(256);

    public static void main(String[] args) {
        InfoExport app = new InfoExport();
        app.run();
    }

    public static String formatFileSize(long fileS) {

        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = String.format("%12.2f B", (double) fileS);
        } else if (fileS < 1048576) {
            fileSizeString = String.format("%12.2fKB", (double) fileS / 1024);
        } else if (fileS < 1073741824) {
            fileSizeString = String.format("%12.2fMB", (double) fileS / 1048576);
        } else if (fileS < 1073741824L * 1024) {
            fileSizeString = String.format("%12.2fGB", (double) fileS / 1073741824);
        } else if (fileS < 1073741824L * 1024 * 1024) {
            fileSizeString = String.format("%12.2fTB", (double) fileS / (1073741824L * 1024));
        } else {
            fileSizeString = String.format("%12.2fPB", (double) fileS / (1073741824L * 1024 * 1024));
        }
        return fileSizeString;
    }

    void log(String line) {
        System.out.println(line);
    }

    private void run() {
        log("=======================地理有关的基础信息===========================");
        log("\r\n");
        log("地球半径 " + globalMercator.EARTH_RADIUS + "米");
        log("地球赤道周长 " + Math.PI * 2 * globalMercator.EARTH_RADIUS + "米");
        log("瓦片级别\t瓦片行(列)数\t分辨率(米/像素)\t编号\t中国区瓦片数量 行*列\t合计瓦片\t.S文件数\t最大TABLE数量 \t磁盘空间");
        Box china = new Box(73.55, 3.85, 135.1, 53.57);
        for (int zoom = 0; zoom < 25; zoom++) {
            Point leftBottom = globalMercator.lngLatToTile(china.getXmin(), china.getYmin(), zoom);
            Point rightTop = globalMercator.lngLatToTile(china.getXmax(), china.getXmin(), zoom);
            long col = (long) (rightTop.getX() - leftBottom.getX() + 1);
            long row = (long) (rightTop.getY() - leftBottom.getY() + 1);

            long tableRow = ((long) rightTop.getX()) / 256 - ((long) leftBottom.getX()) / 256 + 1;
            long tableCol = ((long) rightTop.getY()) / 256 - ((long) leftBottom.getY()) / 256 + 1;
            long disk = (col * row * 2 * 1024 * 1024);
            String diskStr = formatFileSize(disk);
            String line = String.format("%-5d\t% 9d\t% 12.2f米\t%c\t% 8d*%-8d \t% 15d\t% 12d\t% 12d\t%s",
                    zoom, (long) Math.pow(2, zoom), globalMercator.resolution(zoom),
                    (char) ('A' + Math.max(9, zoom)),
                    row, col, row * col,
                    tableRow * tableCol,
                    (row * col) / (tableRow * tableCol),
                    diskStr
            );
            log(line);
        }

    }

}
