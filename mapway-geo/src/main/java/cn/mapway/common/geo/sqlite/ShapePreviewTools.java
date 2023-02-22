package cn.mapway.common.geo.sqlite;

import cn.mapway.common.geo.gdal.GdalUtil;
import cn.mapway.geo.shared.vector.Box;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.gdal;
import org.gdal.ogr.*;
import org.nutz.img.Images;
import org.nutz.lang.Files;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * SqliteMapTools
 * 基于.S文件格式的操作
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class ShapePreviewTools {

    private DataSource worldDataSource;

    public static void main(String[] args) {
        ShapePreviewTools tools = new ShapePreviewTools();
        tools.initialize();
        Double[] area=new Double[1];
        byte[] bytes = previewShapeFile(tools.worldDataSource,area);
        Files.write("e:\\test.png", bytes);
    }

    /**
     * 预览shape文件 输出一张预览图
     *
     * @param shapeFile
     * @return
     */
    public static byte[] previewShapeFile(DataSource shapeFile, Double[] area) {
        Layer layer = shapeFile.GetLayer(0);
        layer.ResetReading();
        Feature feature = layer.GetNextFeature();
        double[] extend = layer.GetExtent();

        int width = 800;
        int height = 300;
        if (extend == null || extend.length < 4) {
            BufferedImage image = Images.createText("空的shape文件不能预览SHAPE");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Images.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } else {
            Box box = new Box();
            box.setValue(extend[0], extend[2], extend[1], extend[3]);
            Box target = new Box();
            target.setValue(0, 0, width, box.height() * ((width * 1.0f) / box.width()));
            height = (int) target.height();
            target.inflate(-5, -5);
            double xscale = target.width() / box.width();
            double yscale = target.height() / box.height();

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.setColor(Color.white);
            graphics.fillRect(0, 0, width, height);

            MapStyle style = new MapStyle();
            style.setFill(true);
            style.setFillColor(Color.GRAY);
            style.setShowName(false);
            style.setBorderColor(Color.DARK_GRAY);
            graphics.setColor(style.getBorderColor());

            Graphics2D g2d = (Graphics2D) graphics;
            BasicStroke basicStroke = new BasicStroke(0.1f);
            g2d.setStroke(basicStroke);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawRect((int) target.xmin, (int) target.ymin, (int) target.width(), (int) target.height());
            double areaValue = 0.0;
            while (feature != null) {
                Geometry geometry = feature.GetGeomFieldRef(0);
                areaValue += geometry.GetArea();
                drawGeometry(graphics, box, target, xscale, yscale, geometry, style);
                feature = layer.GetNextFeature();
            }
            if (area != null && area.length > 0) {
                area[0] = areaValue;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Images.write(bufferedImage, "png", outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 绘制一个几何形体
     *
     * @param graphics
     * @param sourceBox
     * @param xscale
     * @param yscale
     * @param geometry
     * @param style
     */
    private static void drawGeometry(Graphics graphics, Box sourceBox, Box targetBox, double xscale, double yscale, Geometry geometry, MapStyle style) {

        if (geometry.GetGeometryType() == ogrConstants.wkbMultiPolygon ||
                geometry.GetGeometryType() == ogrConstants.wkbMultiPolygonZM ||
                geometry.GetGeometryType() == ogrConstants.wkbMultiPolygonM ||
                geometry.GetGeometryType() == ogrConstants.wkbMultiPolygon25D ||
                geometry.GetGeometryType() == ogrConstants.wkbPolygon25D ||
                geometry.GetGeometryType() == ogrConstants.wkbCurvePolygonZ
        ) {
            int geometryCount = geometry.GetGeometryCount();
            for (int index = 0; index < geometryCount; index++) {
                Geometry polygon = geometry.GetGeometryRef(index);
                drawPolygon(graphics, sourceBox, targetBox, xscale, yscale, polygon, style);
            }
        } else if (geometry.GetGeometryType() == ogrConstants.wkbPolygon) {
            drawPolygon(graphics, sourceBox, targetBox, xscale, yscale, geometry, style);
        } else if (geometry.GetGeometryType() == ogrConstants.wkbLineString) {
            drawLine(graphics, sourceBox, targetBox, xscale, yscale, geometry, style);
        } else if (geometry.GetGeometryType() == ogrConstants.wkbMultiLineString) {
            int geometryCount = geometry.GetGeometryCount();
            for (int index = 0; index < geometryCount; index++) {
                Geometry line = geometry.GetGeometryRef(index);
                drawLine(graphics, sourceBox, targetBox, xscale, yscale, line, style);
            }
        } else if (geometry.GetGeometryType() == ogrConstants.wkbPoint) {
            drawPoint(graphics, sourceBox, targetBox, xscale, yscale, geometry, style);
        } else if (geometry.GetGeometryType() == ogrConstants.wkbMultiPoint) {
            int geometryCount = geometry.GetGeometryCount();
            for (int index = 0; index < geometryCount; index++) {
                Geometry line = geometry.GetGeometryRef(index);
                drawPoint(graphics, sourceBox, targetBox, xscale, yscale, line, style);
            }
        } else {
            log.error("geometry type {}", Long.toHexString(geometry.GetGeometryType()));
        }
    }

    private static void drawPoint(Graphics graphics, Box sourceBox, Box targetBox, double xscale, double yscale, Geometry geometry, MapStyle style) {

    }

    private static void drawLine(Graphics graphics, Box sourceBox, Box targetBox, double xscale, double yscale, Geometry geometry, MapStyle style) {
    }

    private static void drawPolygon(Graphics graphics, Box sourceBox, Box targetBox, double xscale, double yscale, Geometry geometry, MapStyle style) {
        int gcount = geometry.GetGeometryCount();
        for (int i = 0; i < gcount; i++) {
            Geometry line = geometry.GetGeometryRef(i);
            line.CloseRings();
            Polygon p = new Polygon();
            for (int nc = 0; nc < line.GetPointCount(); nc++) {
                double[] pt = line.GetPoint(nc);
                transform(sourceBox, targetBox, xscale, yscale, pt);
                p.addPoint((int) pt[0], (int) pt[1]);
            }
            graphics.setColor(style.fillColor);
            graphics.fillPolygon(p);
        }
    }

    private static void transform(Box sourceBox, Box targetBox, double xscale, double yscale, double[] pt) {
        pt[0] = xscale * (pt[0] - sourceBox.xmin) + targetBox.xmin;
        pt[1] = targetBox.height() - (yscale * (pt[1] - sourceBox.ymin)) + targetBox.ymin;
    }

    /**
     * 初始化之前做的事情
     */
    public void initialize() {
        String world = "C:\\data\\share\\polygon\\xian_polygon.shp";
        GdalUtil.init();
        gdal.SetConfigOption("SHAPE_RESTORE_SHX", "YES");
        String strdrivername = "ESRI Shapefile";
        Driver odriver = ogr.GetDriverByName(strdrivername);

        log.info("world info @ {}", world);
        worldDataSource = odriver.Open(world);

    }
}
