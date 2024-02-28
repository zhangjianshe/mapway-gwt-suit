package cn.mapway.common.geo.sqlite;

import cn.mapway.common.geo.tools.GlobalMercator;
import cn.mapway.common.geo.tools.TileTools;
import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;
import lombok.extern.slf4j.Slf4j;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.ogrConstants;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * MapRender
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public abstract class MapRender {
    private final GlobalMercator globalMercator = new GlobalMercator(256);
    BufferedImage tileImage;
    Graphics2D g;
    int[] exports = {1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public MapRender() {
        tileImage = new BufferedImage(256, 256, TYPE_INT_RGB);
        g = tileImage.createGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * 初始化
     */
    public abstract void initialize();

    public void range(int start, int end) {
        for (int i = 0; i < exports.length; i++) {
            if (i >= start && i <= end) {
                exports[i] = 1;
            } else {
                exports[i] = 0;
            }
        }
    }


    public void export() {
        initialize();
        int maxLevel = Math.min(23, exports.length);
        for (int i = 0; i < maxLevel; i++) {
            exportLevel(getTileTools(), i);
        }
    }

    public abstract TileTools getTileTools();

    /**
     * 输出某一级别
     *
     * @param tileTools
     * @param zoomLevel
     */
    public void exportLevel(TileTools tileTools, int zoomLevel) {
        log.info("子类实现此方法输出莫伊级别的图像");
    }

    /**
     * 通用的绘制Tile过程
     *
     * @param tools
     * @param drawerList
     */
    protected void commonDrawer(TileTools tools, int zoom, IDrawTile... drawerList) {

        if (exports[zoom] == 0) {
            log.info("LEVEL {} 配置不输出", zoom);
            return;
        }
        long tileCount = (long) Math.pow(2, zoom);
        for (long tx = 0; tx < tileCount; tx++) {
            for (long ty = 0; ty < tileCount; ty++) {
                Stopwatch stopwatch = Stopwatch.begin();
                Geometry filter = tileFilter(tx, ty, zoom);
                //清空图形
                g.setClip(0, 0, 256, 256);
                g.setColor(getBackground(zoom));
                g.fillRect(0, 0, 256, 256);
                if (drawerList != null) {
                    for (IDrawTile drawer : drawerList) {
                        drawer.draw(g, tx, ty, zoom, filter);
                    }
                }
                //写入数据库
                writeToDb(tools, tileImage, tx, ty, zoom);
                stopwatch.stop();
                log.info("写入数据库{} {}/{} 用时{}", zoom, tx * tileCount + ty, tileCount * tileCount, (stopwatch.getDuration()) + "ms");
            }
        }
    }

    /**
     * 子类可以重载该方法 返回地图北京色
     *
     * @return
     */
    public Color getBackground(int zoom) {
        return Color.WHITE;
    }


    /**
     * zoom layer tx,ty bounds
     *
     * @param tx
     * @param ty
     * @param zoom
     * @return
     */
    Geometry tileFilter(long tx, long ty, int zoom) {
        Box box = globalMercator.tileBounds(tx, ty, zoom);
        Geometry filter = Geometry.CreateFromWkt(box.toWKT());
        return filter;
    }


    /**
     * 图像写入数据库
     *
     * @param tools
     * @param image
     * @param tx
     * @param ty
     * @param zoom
     */
    private void writeToDb(TileTools tools, BufferedImage image, long tx, long ty, int zoom) {
        long tileCount = (long) Math.pow(2, zoom);
        ByteArrayOutputStream o = new ByteArrayOutputStream(8 * 1024);
        try {
            ImageIO.write(image, "png", o);
            InputStream inputStream = Streams.wrap(o.toByteArray());
            tools.write(tx, tileCount - ty - 1, zoom, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 画布上绘制几何图形
     *
     * @param g
     * @param geometry
     * @param style
     */
    protected void drawGeometry(String name, Graphics2D g, double tx, double ty, Geometry geometry, int zoom, MapStyle style) {
        if (geometry.GetGeometryType() == ogrConstants.wkbMultiPolygon) {
            int geometryCount = geometry.GetGeometryCount();
            for (int index = 0; index < geometryCount; index++) {
                Geometry polygon = geometry.GetGeometryRef(index);
                if (polygon.GetGeometryType() != ogrConstants.wkbPolygon) {
                    log.error("geometry type {}", polygon.GetGeometryType());
                    continue;
                }
                drawPolygon(name, tx, ty, g, polygon, zoom, style);
            }
        } else if (geometry.GetGeometryType() == ogrConstants.wkbPolygon) {
            drawPolygon(name, tx, ty, g, geometry, zoom, style);
        } else {
            log.error("geometry type {}", geometry.GetGeometryType());
        }
    }

    protected void drawPolygon(String name, double tx, double ty, Graphics2D g, Geometry polygon, int zoom, MapStyle style) {
        int geometryCount = Math.min(1, polygon.GetGeometryCount());
        if (geometryCount > 1) {
            log.info("featuer {} {}", name, geometryCount);
        }
        Geometry center = polygon.Centroid();
        for (int index = 0; index < geometryCount; index++) {
            Geometry line = polygon.GetGeometryRef(index);
            if (line.GetGeometryType() != ogrConstants.wkbLineString) {
                log.error("subtype {}", line.GetGeometryType());
                continue;
            }
            int pointCount = line.GetPointCount();
            //坐标转化
            int[] xPoints = new int[pointCount];
            int[] yPoints = new int[pointCount];
            for (int j = 0; j < pointCount; j++) {
                double[] point = line.GetPoint(j);
               Point Point = globalMercator.geoToTile(point[0], point[1], tx, ty, zoom);
                xPoints[j] = (int) Math.floor(Point.getX());
                yPoints[j] = 256 - (int) Math.floor(Point.getY());
            }
            if (style.fill) {
                g.setColor(style.getFillColor());
                g.fillPolygon(xPoints, yPoints, pointCount);
            }
            g.setColor(style.getBorderColor());
            g.drawPolygon(xPoints, yPoints, pointCount);
            //输出中文标题
            if (style.isShowName() && Strings.isNotBlank(name)) {
                cn.mapway.geo.shared.vector.Point centerTile = globalMercator.geoToTile(center.GetX(), center.GetY(), tx, ty, zoom);
                g.setColor(style.fontColor);
                g.setFont(style.font);
                g.drawString(name, (float) centerTile.getX(), (float) (256 - centerTile.getY()));
            }

        }
    }
}
