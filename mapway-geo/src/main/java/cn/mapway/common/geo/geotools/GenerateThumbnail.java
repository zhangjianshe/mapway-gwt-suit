package cn.mapway.common.geo.geotools;

import cn.mapway.common.geo.gdal.GdalUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
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
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.nutz.img.Images;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.ContrastMethod;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
        // 用 gdal读取tif 将tif转换为BufferedImage
        BufferedImage imageImage = readImage(imageUrl.getAbsolutePath());
        imageImage = Thumbnails.of(imageImage)
                .size(width, height).asBufferedImage();

        BufferedImage labelImage = Thumbnails.of(labelUrl).size(width, height).asBufferedImage();
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
                bi = readImage(rasterFile.getAbsolutePath());
//                bi = Images.read(rasterFile);
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
                CoordinateReferenceSystem decode = CRS.decode("EPSG:3857");
                mapArea = new ReferencedEnvelope(0,height,0,height, decode);
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
        BufferedImage bi = readImage(rasterFile.getAbsolutePath());
//        BufferedImage bi = Images.read(rasterFile);
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


    public static BufferedImage readImage(String imageUrl){
        Dataset open = gdal.Open(imageUrl, gdalconst.GA_ReadOnly);
        int rasterCount = open.getRasterCount();
        BufferedImage bufferedImage = new BufferedImage(open.getRasterXSize(), open.getRasterYSize(), BufferedImage.TYPE_3BYTE_BGR);
        if(rasterCount >=3 ){
            // 取前三个波段
            int rasterXSize = open.getRasterXSize();
            int rasterYSize = open.getRasterYSize();
            byte[] bytes = readPixelValues(open.GetRasterBand(1), 0.01, 0.99);
            byte[] bytes1 = readPixelValues(open.GetRasterBand(2), 0.01, 0.99);
            byte[] bytes2 = readPixelValues(open.GetRasterBand(3), 0.01, 0.99);
            for(int x = 0; x < rasterXSize; x++){
                for(int y = 0; y < rasterYSize; y++){
                    byte r = bytes[x + y * rasterXSize];
                    byte g = bytes1[x + y * rasterXSize];
                    byte b = bytes2[x + y * rasterXSize];
                    int value = ((255 & 0xFF) << 24) |
                            ((r & 0xFF) << 16) |
                            ((g & 0xFF) << 8)  |
                            ((b & 0xFF) << 0);
                    bufferedImage.setRGB(x, y, value);
                }
            }
        } else {
            // 取单波段
            byte[] bytes = readPixelValues(open.GetRasterBand(1), 0.01, 0.99);
            for(int x = 0; x < open.getRasterXSize(); x++){
                for(int y = 0; y < open.getRasterYSize(); y++){
                    byte color = bytes[x + y * open.getRasterXSize()];
                    int value = ((255 & 0xFF) << 24) |
                            ((color & 0xFF) << 16) |
                            ((color & 0xFF) << 8)  |
                            ((color & 0xFF) << 0);

                    bufferedImage.setRGB(x, y, value);
                }
            }
        }
        return bufferedImage;
    }

    public static byte[] readPixelValues(Band band) {
        return readPixelValues(band, band.getDataType(),0.0, 0.0);
    }

    public static byte[] readPixelValues(Band band, Double min, Double max) {
        return readPixelValues(band, band.getDataType(),min, max);
    }

    public static byte[] readPixelValues(Band band, int dataType, Double min, Double max) {
        if(dataType == gdalconst.GDT_Byte){
            byte[] data = new byte[band.getXSize() * band.getYSize()];
            band.ReadRaster(0, 0, band.getXSize(), band.getYSize(), dataType, data);
            return data;
        } else if (dataType == gdalconst.GDT_Int16 || dataType == gdalconst.GDT_UInt16){
            short[] data = new short[band.getXSize() * band.getYSize()];
            band.ReadRaster(0, 0, band.getXSize(), band.getYSize(), dataType, data);
            Double[] values = new Double[2];

            // int[] 转换为 float[]
            float[] floats = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                floats[i] = (float) data[i];
            }
            return stretch(floats, min, max);
        } else if(dataType == gdalconst.GDT_Int32 || dataType == gdalconst.GDT_UInt32){
            int[] data = new int[band.getXSize() * band.getYSize()];
            band.ReadRaster(0, 0, band.getXSize(), band.getYSize(), dataType, data);
            // int[] 转换为 float[]
            float[] floats = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                floats[i] = (float) data[i];
            }
            return stretch(floats, min, max);
        } else if(dataType == gdalconst.GDT_Float32){
            float[] data = new float[band.getXSize() * band.getYSize()];
            band.ReadRaster(0, 0, band.getXSize(), band.getYSize(), dataType, data);
            return stretch(data, min, max);
        } else if(dataType == gdalconst.GDT_Float64){
            double[] data = new double[band.getXSize() * band.getYSize()];
            band.ReadRaster(0, 0, band.getXSize(), band.getYSize(), dataType, data);
            // double[] 转换为 float[]
            float[] floats = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                floats[i] = (float) data[i];
            }
            return stretch(floats, min, max);
        }
        return null;
    }

    public static byte[] stretch(float[] data, Double min, Double max) {
        byte[] result = new byte[data.length];
        float[] clone = data.clone();
        Arrays.sort(clone);
        int length = clone.length;
        int minIndex = 0;
        int maxIndex = clone.length - 1;
        if(min != null && max != null){
            minIndex = (int) (length * min);
            maxIndex = (int) (length * max);
        }
        float minValue = clone[minIndex];
        float maxValue = clone[maxIndex];
        float width = maxValue - minValue;
        for (int i = 0; i < data.length; i++) {
            float value = data[i];
            float offset = data[i] - minValue;
            if(value > maxValue){
                result[i] = (byte) 255;
            } else if(value < minValue){
                result[i] = (byte) 0;
            } else {
                result[i] = (byte) (255 * (offset / width));
            }
        }
        return result;
    }


    public static void main(String[] args) throws IOException {
        GdalUtil.init();

        String imageUrl = "F:\\data\\cis\\labels\\sample_189\\image\\43257.tif";
        String outUrl = "F:\\data\\cis\\labels\\sample_189\\t.webp";

        File image1File = new File(imageUrl);
        emptyLabel(image1File, "webp", 0.9, new File(outUrl));
    }
}
