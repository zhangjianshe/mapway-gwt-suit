package cn.mapway.common.geo.geotools;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.nutz.img.Images;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @Author baoshuaiZealot@163.com  2023/7/12
 */
@Slf4j
public class GenerateThumbnail {

    private static StyleFactory sf = CommonFactoryFinder.getStyleFactory();

    private static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    private static Style redStyle = createPolygonStyle(Color.RED, 2.0f,null, 0.0f);

    private static Style rgbStyle = createRGBStyle(1, 2, 3);

    public static void generateRaster(File imageUrl, File labelUrl, File thumbnailUrl, int width, int height) throws IOException {
        BufferedImage imageImage = Thumbnails.of(imageUrl).size(width, height).asBufferedImage();
        // 若波段数大于3 则只取前三个波段
        if(imageImage.getRaster().getNumBands() > 3){
            BufferedImage bufferedImage = new BufferedImage(imageImage.getWidth(), imageImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(imageImage, 0, 0, null);
            imageImage = bufferedImage;
        }
        BufferedImage labelImage = Thumbnails.of(labelUrl).size(width, height).asBufferedImage();

        Color color = new Color(255, 0, 0);
        BufferedImage cannyImg = new Canny().getCannyImg(labelImage, imageImage);
        thumbnailUrl.getParentFile().mkdirs();
        Thumbnails.of(cannyImg)
                .scale(1.0)
                .outputFormat("webp")
                .outputQuality(0.9)
                .toFile(thumbnailUrl);
    }

    /**
     * 生成缩略图
     * @param imageUrl  栅格底图文件
     * @param labelUrl  栅格标注文件
     * @param thumbnailUrl 缩略图文件
     * @throws IOException
     */
    public static void generateRaster(File imageUrl, File labelUrl, File thumbnailUrl) throws IOException {
        generateRaster(imageUrl, labelUrl, thumbnailUrl, 256, 256);
    }

    /**
     * 导出缩略图
     * @param rasterFile 栅格底图文件
     * @param geojsons   矢量geojson信息
     * @param splitSize  切片大小
     * @param exportUrl  导出路径, 导出路径最后不要带有后缀
     * @return
     */
    // 根据传入的矢量生成tif,  矢量转栅格
    public static boolean generateVector(File rasterFile, String geojsons, int splitSize, File exportUrl, boolean gcsFlag) throws IOException {
        return generateVector(rasterFile, geojsons, splitSize, exportUrl, 0.9, "webp", gcsFlag);
    }

    public static boolean generateVector(File rasterFile, String geojsons, int splitSize, File exportUrl, double quality, String imageFormat, boolean gcsFlag) throws IOException {
        final MapContent map = new MapContent();
        try{
            ReferencedEnvelope mapArea = null;
            BufferedImage bi = null;
            FeatureCollection featureCollection = GeojsonUtils.parseFeatures(geojsons);
            if(featureCollection.isEmpty()){
                return emptyLabel(rasterFile, imageFormat, quality, exportUrl);
            }
            if(gcsFlag){
                AbstractGridFormat format = GridFormatFinder.findFormat(rasterFile);
                // this is a bit hacky but does make more geotiffs work
                Hints hints = new Hints();
                if (format instanceof GeoTiffFormat) {
                    hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
                }
                AbstractGridCoverage2DReader reader = format.getReader(rasterFile, hints);

                if(rgbStyle == null){
                    rgbStyle = createRGBStyle(1, 2, 3);
                }
                Style rasterStyle = rgbStyle;
                // Set up a MapContent with the two layers
                org.geotools.map.Layer rasterLayer = new GridReaderLayer(reader, rasterStyle);
                map.addLayer(rasterLayer);

                // 获得输出范围
                mapArea = rasterLayer.getBounds();
                // 初始化输出图像
                bi = new BufferedImage(splitSize, splitSize, BufferedImage.TYPE_INT_ARGB);
            } else {
                bi = Images.read(rasterFile);
                int height = bi.getHeight();
                // 行列号坐标的使用
                // 处理y轴倒转
                try(FeatureIterator features = featureCollection.features()){
                    while(features.hasNext()){
                        SimpleFeature next = (SimpleFeature)features.next();
                        Geometry geometry = (Geometry)next.getDefaultGeometry();
                        int numGeometries = geometry.getNumGeometries();
                        for (int i = 0; i < numGeometries; i++) {
                            Geometry geometryN = geometry.getGeometryN(i);
                            Coordinate[] coordinates = geometryN.getCoordinates();
                            for (Coordinate coordinate : coordinates) {
                                double x = coordinate.x;
                                double y = coordinate.y;

                                coordinate.setX(x);
                                coordinate.setY(height -y);
                            }
                        }
                    }
                }

                mapArea = new ReferencedEnvelope(0,height,0,height, DefaultGeographicCRS.WGS84);
                bi = Thumbnails.of(bi).width(splitSize).height(splitSize).asBufferedImage();
            }

            if(redStyle == null){
                redStyle = createPolygonStyle(Color.YELLOW,  2.0f,null, 0.0f);
            }
            Style shpStyle = redStyle;
            org.geotools.map.Layer shpLayer = new FeatureLayer(featureCollection, shpStyle);
            map.addLayer(shpLayer);

            // 初始化渲染器
            StreamingRenderer sr = new StreamingRenderer();

            sr.setMapContent(map);
            Graphics g = bi.getGraphics();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Rectangle rect = new Rectangle(0, 0, splitSize, splitSize);
            // 绘制地图
            sr.paint((Graphics2D) g, rect, mapArea);

            File parentFile = exportUrl.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            int subIndex = exportUrl.getName().lastIndexOf(".");
            if(subIndex != -1){
                exportUrl = new File(exportUrl.getParentFile(), exportUrl.getName().substring(0, subIndex));
            }

            //将BufferedImage变量写入文件中。
            Thumbnails.of(bi).scale(1.0)
                    .outputFormat(imageFormat)
                    .outputQuality(quality)
                    .toFile(exportUrl);
        }catch (Exception e){
            log.error("exportThumbnailTile error", e);
            try{
                return emptyLabel(rasterFile, imageFormat, quality, exportUrl);
            } catch (Exception ex){
                log.error("exportThumbnailTile error", ex);
                throw ex;
            }
        } finally {
            map.dispose();
        }
        return true;
    }

    private static boolean emptyLabel(File rasterFile, String imageFormat, double quality, File exportUrl) throws IOException {
        BufferedImage bi = Images.read(rasterFile);
        File parentFile = exportUrl.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        int subIndex = exportUrl.getName().lastIndexOf(".");
        if(subIndex != -1){
            exportUrl = new File(exportUrl.getParentFile(), exportUrl.getName().substring(0, subIndex));
        }
        Thumbnails.of(bi).scale(1.0)
                .outputFormat(imageFormat)
                .outputQuality(quality)
                .toFile(exportUrl);
        return true;
    }

    private static Style createRGBStyle(int ...channelNum) {
        SelectedChannelType[] sct = new SelectedChannelType[channelNum.length];
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
        for (int i = 0; i < 3; i++) {
            sct[i] = sf.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
        }
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct[0], sct[1], sct[2]);
        sym.setChannelSelection(sel);
        return SLD.wrapSymbolizers(sym);
    }

    public static Style createPolygonStyle(Color outlineColor, float strokeWidth,  Color fillColor, float opacity) {
        org.geotools.styling.Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(strokeWidth));
        Fill fill = Fill.NULL;
        if (fillColor != null) {
            fill = sf.createFill(ff.literal(fillColor), ff.literal(opacity));
        }
        return SLD.wrapSymbolizers(sf.createPolygonSymbolizer(stroke, fill, null));
    }

    public static void main(String[] args) throws IOException {
        String imageUrl = "D:\\数据\\t\\Q_208_95_835_382___15360___8192.jpg";
        String labelUrl = "D:\\数据\\t\\Q_208_95_835_382___15360___8192.tif";
        File thumbnailUrl = new File("D:\\数据\\t\\0_1.webp");
        int width = 256;
        int height = 256;

        BufferedImage imageImage = Thumbnails.of(imageUrl).size(width, height).asBufferedImage();
        BufferedImage labelImage = Thumbnails.of(labelUrl).size(width, height).asBufferedImage();

        BufferedImage cannyImg = new Canny().getCannyImg(labelImage, imageImage);
        thumbnailUrl.getParentFile().mkdirs();
        Thumbnails.of(cannyImg)
                .scale(1.0)
                .outputFormat("webp")
                .outputQuality(0.9)
                .toFile(thumbnailUrl);
    }
}
