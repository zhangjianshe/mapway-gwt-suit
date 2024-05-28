package cn.mapway.common.geo.tools;


import cn.mapway.biz.core.BizResult;
import cn.mapway.common.geo.tools.parser.GF1Parser;
import cn.mapway.common.geo.tools.parser.ISatelliteExtractor;
import cn.mapway.geo.client.raster.*;
import cn.mapway.geo.shared.color.ColorMap;
import cn.mapway.geo.shared.color.ColorTable;
import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;
import cn.mapway.geo.shared.vector.Rect;
import cn.mapway.ui.client.mvc.Size;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.gdalconst.gdalconstJNI;
import org.gdal.ogr.GeomTransformer;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.ogrConstants;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static cn.mapway.geo.shared.GeoConstant.*;
import static org.gdal.ogr.ogrConstants.wkbLinearRing;
import static org.gdal.ogr.ogrConstants.wkbPolygon;
import static org.gdal.osr.osrConstants.OAMS_TRADITIONAL_GIS_ORDER;

/**
 * TiffTools
 * tiff格式影像处理工具
 * 每一次从影像中切片 都会进行计算，为了简化这种操作,我们每次计算完成后 就将结果存入 mongoDB中,下次再查找 就不用每次都去计算图像信息了
 * 怎么标识一个图像？ 我们是计算这副图像的md5值进行唯一索引
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class TiffTools {

    private static final List<ISatelliteExtractor> satelliteExtractorList;
    static CoordinateReferenceSystem decode4326 = null;
    static CoordinateReferenceSystem decode3857 = null;
    static MathTransform transform3857To4326 = null;
    private static FilePool globalFilePool;
    private static Driver driverPng;
    private static Driver driverMemory;
    private static SpatialReference srfwgs84;
    private static SpatialReference srfwebMercator;

    static {
        satelliteExtractorList = new ArrayList<>();
        satelliteExtractorList.add(new GF1Parser());
        createTransform();
    }

    public TiffTools() {

    }

    /**
     * PNG Driver
     *
     * @return
     */
    public static synchronized Driver getPngDriver() {
        if (driverPng == null) {
            driverPng = gdal.GetDriverByName("PNG");
        }
        return driverPng;
    }

    /**
     * PNG Driver
     *
     * @return
     */
    public static synchronized Driver getMemoryDriver() {
        if (driverMemory == null) {
            driverMemory = gdal.GetDriverByName("MEM");
        }
        return driverMemory;
    }

    public static void main(String[] args) {

        String filePath;

        filePath = "/data/personal/1/Q_183_100_735_402.tif";
        long tilex = 11776;
        long tiley = 6432;
        int zoom = 14;
        gdal.AllRegister();
        TiffTools tiffTools = new TiffTools();


        File infoFile = new File(filePath + ".info");
        ImageInfo md5File;
        String sha256 = tiffTools.imageSha256(filePath);
        md5File = tiffTools.extractImageInformation(sha256, filePath, new IImagePreviewProvider() {
            @Override
            public byte[] read(String sha256) {
                return new byte[0];
            }

            @Override
            public boolean write(String sha256, byte[] image) {
                return true;
            }
        });


        byte[] bytes = tiffTools.extractFromSource(md5File, tilex, tiley, zoom, new ColorTable());
        Files.write("/data/tmp/123.png", bytes);
    }

    public static synchronized SpatialReference getWgs84Reference() {
        if (srfwgs84 == null) {
            srfwgs84 = new SpatialReference();
            srfwgs84.ImportFromEPSG(4326);
            srfwgs84.SetAxisMappingStrategy(OAMS_TRADITIONAL_GIS_ORDER);
        }
        return srfwgs84;
    }

    public static synchronized SpatialReference getWebMercatorReference() {
        if (srfwebMercator == null) {
            srfwebMercator = new SpatialReference();
            srfwebMercator.ImportFromEPSG(3857);
            srfwebMercator.SetAxisMappingStrategy(OAMS_TRADITIONAL_GIS_ORDER);
        }
        return srfwebMercator;
    }

    public static Geometry toTargetGeometry(Geometry pt0, SpatialReference targetSpatialReference) {
        CoordinateTransformation coordinateTransformation = new CoordinateTransformation(pt0.GetSpatialReference(), targetSpatialReference);
        GeomTransformer geomTransformer = new GeomTransformer(coordinateTransformation);
        return geomTransformer.Transform(pt0);
    }

    public static Box fromGeometry(Geometry pt1) {
        double[] x = new double[4];
        pt1.GetEnvelope(x);
        return new Box(x[0], x[2], x[1], x[3]);
    }

    public static Geometry toGeometry(Box box, SpatialReference spatialReference) {
        Geometry geometry = new Geometry(wkbPolygon);
        if (spatialReference != null) {
            geometry.AssignSpatialReference(spatialReference);
        } else {
            geometry.AssignSpatialReference(getWgs84Reference());
        }

        Geometry ring = new Geometry(wkbLinearRing);
        ring.AddPoint(box.xmin, box.ymax);
        ring.AddPoint(box.xmax, box.ymax);
        ring.AddPoint(box.xmax, box.ymin);
        ring.AddPoint(box.xmin, box.ymin);
        ring.CloseRings();
        geometry.AddGeometry(ring);
        return geometry;
    }

    public static Geometry toWgs84(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry.GetSpatialReference() == null) {
            return null;
        }
        CoordinateTransformation coordinateTransformation = new CoordinateTransformation(geometry.GetSpatialReference(), getWgs84Reference());
        GeomTransformer geomTransformer = new GeomTransformer(coordinateTransformation);
        return geomTransformer.Transform(geometry);
    }

    public static BizResult<List<Double>> readPixelValues(String filePath, double lat, double lng) {
        Dataset dataset = gdal.Open(filePath, gdalconstConstants.GA_ReadOnly);
        int rasterCount = dataset.getRasterCount();
        int rasterXSize = dataset.getRasterXSize();
        int rasterYSize = dataset.getRasterYSize();

        SpatialReference imageSpatialRef = dataset.GetSpatialRef();
        if (imageSpatialRef == null) {
            return BizResult.error(500, "目标文件没有设置坐标参考");
        }

        double[] affineCoff = dataset.GetGeoTransform();
        if (affineCoff == null) {
            return BizResult.error(500, "目标文件没有放射变换参数");
        }
        Geometry geoLocation = new Geometry(ogrConstants.wkbPoint);
        geoLocation.SetPoint(0, lng, lat);
        geoLocation.AssignSpatialReference(getWgs84Reference());
        geoLocation.TransformTo(imageSpatialRef);

        Point pt = new Point(geoLocation.GetX(), geoLocation.GetY());
        Point pixelLocation = BaseTileExtractor.imageSpaceToSourceSpace(affineCoff, pt);
        // now location is in image spatialReference
        log.info("read pixel {} {} -> {} {} -> {} {}", lng, lat, pt.x, pt.y, pixelLocation.x, pixelLocation.y);

        List<Double> data = new ArrayList<>(rasterCount);
        if (
                pixelLocation.x < 0 || pixelLocation.x >= rasterXSize
                        || pixelLocation.y < 0 || pixelLocation.y >= rasterYSize
        ) {
            log.warn("location is out of image extends");
            for (int i = 0; i < rasterCount; i++) {
                data.add(0.0);
            }
            return BizResult.success(data);
        } else {

            for (int i = 1; i <= rasterCount; i++) {
                Band band = dataset.GetRasterBand(i);
                int dataType = band.getDataType();
                data.add(readPixelValue(band, dataType, pixelLocation.getXAsInt(), pixelLocation.getYAsInt()));
            }
            log.info("data is {}", Json.toJson(data, JsonFormat.compact()));
        }
        return BizResult.success(data);
    }

    private static double readPixelValue(Band band, int dataType, int x, int y) {
        int[] intValue = {0};
        short[] shortValue = {0};
        byte[] byteValue = {0};
        double[] doubleValue = {0};
        float[] floatValue = {0};

        if (dataType == gdalconstConstants.GDT_Byte) {
            band.ReadRaster(x, y, 1, 1, 1, 1, dataType, byteValue);
            return byteValue[0];
        } else if (dataType == gdalconstConstants.GDT_Int16 || dataType == gdalconstConstants.GDT_UInt16) {
            band.ReadRaster(x, y, 1, 1, 1, 1, dataType, shortValue);
            return shortValue[0];

        } else if (dataType == gdalconstConstants.GDT_Int32 || dataType == gdalconstConstants.GDT_UInt32) {
            band.ReadRaster(x, y, 1, 1, 1, 1, dataType, intValue);
            return intValue[0];
        } else if (dataType == gdalconstConstants.GDT_Float32) {
            band.ReadRaster(x, y, 1, 1, 1, 1, dataType, floatValue);
            return floatValue[0];
        } else if (dataType == gdalconstConstants.GDT_Float64) {
            band.ReadRaster(x, y, 1, 1, 1, 1, dataType, doubleValue);
            return doubleValue[0];
        } else {
            log.warn("不能读取数值类型 {}", dataType);
            return 0;
        }
    }

    private static void createTransform() {
        try {
            decode4326 = CRS.decode("EPSG:4326");
            decode3857 = CRS.decode("EPSG:3857");
            transform3857To4326 = CRS.findMathTransform(decode3857, decode4326);
        } catch (Exception e) {
        }
    }

    /**
     * 计算图像的直方图
     *
     * @param location
     * @param bandInfo
     * @param bucketSize please input 256
     * @param callback   progress report
     */
    public static int[] calRasterHistogram(String location, BandInfo bandInfo, int bucketSize, ProgressCallback callback) {
        Dataset dataset = gdal.Open(location, gdalconstConstants.GA_ReadOnly);
        Band band = dataset.GetRasterBand(bandInfo.index + 1);
        int[] buckets = new int[bucketSize];
        band.GetHistogram(bandInfo.minValue, bandInfo.maxValue, buckets, false, true, callback);
        // dataset.Close();
        return buckets;
    }

    /**
     * 计算图像的金字塔
     *
     * @param location
     * @param callback progress report
     */
    public static String calRasterOverview(String location, ProgressCallback callback) {
        Dataset dataset = null;
        try {
            dataset = gdal.Open(location, gdalconstConstants.GA_ReadOnly);
            dataset.BuildOverviews(new int[]{2, 4, 8, 16, 32, 64, 128, 256, 512}, callback);
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (dataset != null) {
                //   dataset.Close();
            }
        }
        return "";
    }

    /**
     * 计算影像的预览图
     *
     * @param sourceFileName 输入tiff
     * @param targetFileName 输出 png
     * @param targetWidth    目标宽度
     * @param bandInfos      波段信息
     * @param chanelData     显示波段
     * @param callback
     */

    public static void calRasterPreview(String sourceFileName, String targetFileName, int targetWidth, List<BandInfo> bandInfos, ChanelData chanelData, ColorTable colorTable, ProgressCallback callback) {
        Dataset dataset = null;
        try {
            dataset = gdal.Open(sourceFileName, gdalconstConstants.GA_ReadOnly);
            //读取数据 生成预览图
            int rasterWidth = dataset.getRasterXSize();
            int rasterHeight = dataset.getRasterYSize();
            // 目标大小
            int targetHeight = (int) Math.ceil(rasterHeight * targetWidth / (double) rasterWidth);
            Rect source = new Rect(0, 0, rasterWidth, rasterHeight);
            Rect target = new Rect(0, 0, targetWidth, targetHeight);
            Dataset previewDataset = getMemoryDriver().Create("", targetWidth, targetHeight, 4, gdalconstConstants.GDT_Byte);

            List<BandData> sourceBandList = new ArrayList<>();
            List<Band> targetBandList = new ArrayList<>();

            targetBandList.add(previewDataset.GetRasterBand(1));
            targetBandList.add(previewDataset.GetRasterBand(2));
            targetBandList.add(previewDataset.GetRasterBand(3));

            processBandInfo(sourceBandList, bandInfos, chanelData, dataset);

            BaseTileExtractor extractor = new BaseTileExtractor();
            extractor.setColorTable(colorTable);

            byte[] transparentBand= extractor.getBand(dataset.GetRasterCount()==1, new Size(targetWidth, targetHeight),
                    source, target,sourceBandList, targetBandList);
            previewDataset.GetRasterBand(4).WriteRaster(0, 0, targetWidth, targetHeight, transparentBand);
            previewDataset.FlushCache();
            //输出到指定的路径
            Dataset targetDataset = getPngDriver().CreateCopy(targetFileName, previewDataset);
            targetDataset.FlushCache();
            targetDataset = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataset != null) {
                dataset = null;
            }
        }
    }

    public static String[] extractBandOverview(Band band) {
        // overviews
        int overviewCount = band.GetOverviewCount();
        String[] overviews = new String[overviewCount];
        for (int overviewIndex = 0; overviewIndex < overviewCount; overviewIndex++) {
            // GetOverview start with 0,1,2
            Band overviewBand = band.GetOverview(overviewIndex);
            String overview = overviewBand.GetXSize() + "x" + overviewBand.GetYSize();
            overviews[overviewIndex] = overview;
        }
        return overviews;
    }

    private static int colorFrom(int r, int g, int b, int a) {
        int color = (0xFF & r) << 3 * 8;
        color |= (0xFF & g) << 2 * 8;
        color |= (0xFF & b) << 8;
        color |= (0xFF & a);
        return color;
    }

    /**
     * 解析一个影像的坐标和影像信息
     *
     * @param dataset
     * @return
     */
    public static ImageInfo extractImageInformation(Dataset dataset) {
        ImageInfo info = new ImageInfo();
        info.setBands(dataset.getRasterCount());
        info.setWidth(dataset.GetRasterXSize());
        info.setHeight(dataset.GetRasterYSize());


        for (int i = 0; i < dataset.GetRasterCount(); i++) {
            Band band = dataset.GetRasterBand(i + 1);
            BandInfo bandInfo = new BandInfo();
            bandInfo.setIndex(i);
            bandInfo.setDataType(band.GetRasterDataType());

            org.gdal.gdal.ColorTable colorTable = band.GetColorTable();
            if (colorTable != null) {
                List<ColorMap> colorMaps = new ArrayList<>();

                for (int index = 0; index < colorTable.GetCount(); index++) {
                    Color color = colorTable.GetColorEntry(index);
                    if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                        continue;
                    }
                    ColorMap colorMap = new ColorMap();
                    colorMap.setName(String.valueOf(index));
                    colorMap.setStart(index);
                    colorMap.setEnd(index);
                    colorMap.setRgba(colorFrom(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                    colorMaps.add(colorMap);
                }

                bandInfo.colorMaps = colorMaps;
            } else {
                bandInfo.colorMaps = null;
            }
            bandInfo.overviews = extractBandOverview(band);
            bandInfo.metadata = new HashMap<>();

            String wavelength = band.GetMetadataItem(MetadataEnum.META_KEY_WAVELENGTH.getKey());
            if (StringUtils.isNotEmpty(wavelength)) {
                bandInfo.metadata.put(MetadataEnum.META_KEY_WAVELENGTH.getKey(), wavelength);
                String wavelength_units = band.GetMetadataItem(MetadataEnum.META_KEY_WAVELENGTH_UNITS.getKey());
                if (StringUtils.isEmpty(wavelength_units)) {
                    MetadataValue value = MetadataEnum.META_KEY_WAVELENGTH_UNITS.getValues()[0];
                    wavelength_units = value.getKey();
                }
                bandInfo.metadata.put(MetadataEnum.META_KEY_WAVELENGTH_UNITS.getKey(), wavelength_units);
            }

            calValueExtends(bandInfo, band);
            Double[] noValue = new Double[10];
            band.GetNoDataValue(noValue);
            int count = 0;
            for (int j = 0; j < 10; j++) {
                if (noValue[j] == null) {
                    count = j;
                    break;
                }
            }
            if (count == 0) {
                //用户没有设定 noValue 值 我们缺省将 0 设为 空值
                Double[] noValue1 = new Double[0];
                bandInfo.setNoValues(noValue1);
            } else {
                Double[] noValue1 = new Double[count];
                System.arraycopy(noValue, 0, noValue1, 0, count);
                bandInfo.setNoValues(noValue1);
            }
            info.getBandInfos().add(bandInfo);
        }

        //处理band对应的RGB颜色
        if (info.getBandInfos().size() == 1) {
            info.setChanelData(new ChanelData(1, 1, 1));
            //单波段影像 处理 空值问题 如果 保证 0作为空值处理
            Double[] noValue = info.getBandInfos().get(0).getNoValues();
            boolean found = false;
            for (int i = 0; i < noValue.length; i++) {
                if (Math.abs(noValue[i]) < 0.001) {
                    found = true;
                    break;
                }
            }
            log.info("source no value is found {} {}", found, Json.toJson(noValue));
            if (!found) {
                Double[] noValue1 = new Double[noValue.length + 1];
                noValue1[0] = 0.0;
                System.arraycopy(noValue, 0, noValue1, 1, noValue1.length - 1);
                log.info("source no value add   {}", found, Json.toJson(noValue1));
                info.getBandInfos().get(0).setNoValues(noValue1);
            }
        } else if (info.getBandInfos().size() == 2) {
            info.setChanelData(new ChanelData(1, 2, 1));
        }
        if (info.getBandInfos().size() == 3) {
            info.setChanelData(new ChanelData());
        } else if (info.getBandInfos().size() == 4) {
            info.setChanelData(new ChanelData(3, 2, 1));
        }
        if (info.getBandInfos().size() > 4) {
            info.setChanelData(new ChanelData(4, 3, 2));
        }


        //根据坐标系不同 我们计算影像的范围
        double[] adfGeoTransform = new double[6];
        dataset.GetGeoTransform(adfGeoTransform);
        info.geoTransform = adfGeoTransform;
        double originX = adfGeoTransform[0];
        double originY = adfGeoTransform[3];
        double pixelSizeX = adfGeoTransform[1];
        double pixelSizeY = (adfGeoTransform[5] > 0) ? adfGeoTransform[5] : -adfGeoTransform[5];
        //上面的变换 解决了像素空间 到 定义的坐标系之间的变换
        // 我们获取信息 需要将坐标系转换为地理空间坐标
        // 这一步比较复杂 我们要确定 影像的坐标系统，并计算影像的坐标范围
        // 我们支支持 三种坐标系统  WGS84>EPSG:4326 WEB Mercato>EPSG:3875 (3857) 0没有坐标系统设定
        SpatialReference spatialReference = dataset.GetSpatialRef();
        if (spatialReference != null) {
            if (spatialReference.IsProjected() > 0) {
                //投影的都认为是 WEB墨卡托 有点过分？再改吧
                info.setSrid(SRID_WEB_MERCATO);
                info.setProjection(spatialReference.ExportToPrettyWkt());
            } else if (spatialReference.IsGeographic() > 0) {
                //不是投影坐标系
                String geogcs = spatialReference.GetAttrValue("GEOGCS");
                String srname = spatialReference.GetName().toLowerCase();
                geogcs = geogcs.toLowerCase();
                if (
                        srname.startsWith("gcs_cgcs_2000") || srname.startsWith("cgcs")
                                || (geogcs.contains("2000") && geogcs.contains("china"))
                ) {
                    //中国的坐标系
                    info.setSrid(SRID_CGCS2000);
                    info.setProjection(spatialReference.ExportToPrettyWkt());
                } else if (geogcs.contains("wgs")) {
                    info.setSrid(SRID_WGS84);
                    info.setProjection(spatialReference.ExportToPrettyWkt());
                } else if (geogcs.contains("3875")) {
                    // 3875 ESRI
                    info.setSrid(SRID_WEB_MERCATO);
                    info.setProjection(spatialReference.ExportToPrettyWkt());
                } else {
                    log.info("不能处理的坐标系统{}", spatialReference.GetName());
                    info.setSrid(SRID_NULL);
                    info.setProjection(spatialReference.ExportToPrettyWkt());
                }
            }
        } else {
            if (originX >= -180 && originX <= 180 && originY >= -90 && originY <= 90) {
                log.info("图像上没有坐标参考系,但是我们猜测这是一个 WGS84坐标系");
                info.setSrid(SRID_WGS84);
            } else {
                log.info("图像上没有坐标参考系,但是我们猜测这是一个 WEB_MERCATO坐标系");
                info.setSrid(SRID_WEB_MERCATO);
            }
        }
        info.box = new Box();
        info.setSourceBox(new Box());
        Box box = info.box;
        if (info.getSrid() == SRID_WGS84) {
            box.setValue(originX, originY - pixelSizeY * dataset.GetRasterYSize(), originX + dataset.GetRasterXSize() * pixelSizeX, originY);
            info.setLat(box.center().getY());
            info.setLng(box.center().getX());
            Point resolution = resolution(adfGeoTransform);
            Point resolutionMi = wgs84Resolution(box.center(), resolution);
            int zoom = zoomByWgs84Resolution(resolutionMi.getX());
            info.setMaxZoom(zoom);
            info.setMinZoom(3);
            info.setResolution((int) resolutionMi.getX() * 10);
            info.getSourceBox().copyFrom(box);
        } else if (info.getSrid() == SRID_CGCS2000) {
            box.setValue(originX, originY - pixelSizeY * dataset.GetRasterYSize(), originX + dataset.GetRasterXSize() * pixelSizeX, originY);
            info.setLat(box.center().getY());
            info.setLat(box.center().getY());
            info.setLng(box.center().getX());
            Point resolution = resolution(adfGeoTransform);
            Point resolutionMi = wgs84Resolution(box.center(), resolution);
            int zoom = zoomByWgs84Resolution(resolutionMi.getX());
            info.setMaxZoom(zoom);
            info.setMinZoom(3);
            info.setResolution((int) (resolutionMi.getX() * 10));
            info.getSourceBox().copyFrom(box);
        } else if (info.getSrid() == SRID_WEB_MERCATO) {

            // 图像四个角 分别对应坐标参考系下的四个坐标

            Point pt0 = pixelToLocation(adfGeoTransform, 0, 0);
            Point pt1 = pixelToLocation(adfGeoTransform, info.getWidth(), info.getHeight());

            Box extendBox = new Box(pt0.getX(), pt1.getY(), pt1.getX(), pt0.getY());
            info.getSourceBox().copyFrom(extendBox);
            if (spatialReference == null) {
                spatialReference = getWebMercatorReference();
            }
            Geometry extend = toGeometry(extendBox, spatialReference);
            Geometry wgs84Extend = toWgs84(extend);
            double[] doubles = new double[4];
            wgs84Extend.GetEnvelope(doubles);
            log.info("extend:{}", Json.toJson(doubles));
            //print "minX: %d, minY: %d, maxX: %d, maxY: %d" %(env[0],env[2],env[1],env[3])
            // ymin ymax xmin xmax
            //  0    1   2    3
            box.setValue(doubles[0], doubles[2], doubles[1], doubles[3]);
            info.setLat(box.center().getY());
            info.setLng(box.center().getX());

            //每个像素所占的经度
            double lngPerPixel = box.width() / info.getWidth();
            double resolution = lngPerPixel * (2 * Math.PI * GlobalMercator.get().EARTH_RADIUS) / 360;
            int zoom = GlobalMercator.get().zoomForPixelSize(resolution);
            info.setMaxZoom(zoom);
            info.setMinZoom(3);
            info.setResolution((int) (resolution * 10));
        } else {
            //没有坐标
            // 1. 设置范围 3857的坐标转换为4326的坐标
            if (transform3857To4326 != null) {
                GeometryFactory geometryFactory = new GeometryFactory();
                org.locationtech.jts.geom.Point pointMin = geometryFactory.createPoint(new Coordinate(0, 0));
                org.locationtech.jts.geom.Point pointMax = geometryFactory.createPoint(new Coordinate(info.getWidth(), info.getHeight()));
                // 找到 pointMin 和 pointMax 中的minx miny maxx maxy

                double[] doubles = new double[4];
                doubles[0] = pointMin.getX() > pointMax.getX() ? pointMax.getX() : pointMin.getX();
                doubles[1] = pointMin.getX() > pointMax.getX() ? pointMin.getX() : pointMax.getX();
                doubles[2] = pointMin.getY() > pointMax.getY() ? pointMax.getY() : pointMin.getY();
                doubles[3] = pointMin.getY() > pointMax.getY() ? pointMin.getY() : pointMax.getY();
                box.setValue(doubles[0], doubles[2], doubles[1], doubles[3]);
            } else {
                box.setValue(-180, -90, 180, 90);
            }

            // 2. 设置GeoTransform为像素坐标
            // 像素坐标左上角是0,0 右下角是 width, height;
            // 3857坐标系下的坐标 左上角是-20037508.342789244, 20037508.342789244 右下角是20037508.342789244, -20037508.342789244
            // 计算GeoTransform
            double[] geoTransform = new double[6];
            geoTransform[0] = 0;
            geoTransform[1] = 1;
            geoTransform[2] = 0;
            geoTransform[3] = 0;
            geoTransform[4] = 0;
            geoTransform[5] = -1;
            info.geoTransform = geoTransform;
            info.setLat(0);
            info.setLng(0);
            double lngPerPixel = box.width() / info.getWidth();
            double resolution = lngPerPixel * (2 * Math.PI * GlobalMercator.get().EARTH_RADIUS) / 360;
            int zoom = GlobalMercator.get().zoomForPixelSize(resolution);
            info.setMaxZoom(zoom);
            info.setMinZoom(3);
            info.setResolution(0);
            info.getSourceBox().copyTo(box);
        }
        info.setDataTime(Times.format("yyyy-MM-dd HH:mm:ss", new Date()));
        return info;
    }

    /**
     * 统计波段的数据内容
     *
     * @param bandInfo
     * @param band
     */
    private static void calValueExtends(BandInfo bandInfo, Band band) {
        double[] min = new double[1];
        double[] max = new double[1];
        double[] mean = new double[1];
        double[] stddev = new double[1];
        // 这里修改了, 若统计过则不再次统计
        //todo 统计会很耗费时间 ,修改逻辑 增加统计反馈
        band.GetStatistics(1, 1, min, max, mean, stddev);
        bandInfo.setMinValue(min[0]);
        bandInfo.setMaxValue(max[0]);
        bandInfo.setCalMaxValue(mean[0] + 2.0 * stddev[0]);
        bandInfo.setCalMinValue(mean[0] - 2.0 * stddev[0]);
        // default disable gamma correction
        bandInfo.setGammaMin(min[0]);
        bandInfo.setGammaMax(max[0]);
        bandInfo.setGamma(0.5);
        bandInfo.setEnableGamma(false);
    }

    /**
     * 根据影像的分辨率 m 找到最合适的输出级别
     *
     * @param xMi
     * @return
     */
    private static int zoomByWgs84Resolution(double xMi) {
        GlobalMercator globalMercator = new GlobalMercator(256);
        return globalMercator.zoomForPixelSize(xMi);
    }

    /**
     * 像素坐标 转化为 坐标参考系下的坐标  进行仿射变换
     * * Xp = padfTransform[0] + P*padfTransform[1] + L*padfTransform[2];
     * * Yp = padfTransform[3] + P*padfTransform[4] + L*padfTransform[5];
     *
     * @param adfGeoTransform
     * @param pixelX
     * @param pixelY
     * @return
     */
    public static Point pixelToLocation(double[] adfGeoTransform, long pixelX, long pixelY) {
        double xLocation = adfGeoTransform[0] + pixelX * adfGeoTransform[1] + pixelY * adfGeoTransform[2];
        double yLocation = adfGeoTransform[3] + pixelX * adfGeoTransform[4] + pixelY * adfGeoTransform[5];
        return new Point(xLocation, yLocation);
    }

    /**
     * 目标单位 像素分辨率
     * WGS84 度
     * WEB MOCATOR mi
     *
     * @param adfGeoTransform
     * @return
     */
    public static Point resolution(double[] adfGeoTransform) {
        return new Point(adfGeoTransform[1], adfGeoTransform[5]);
    }

    public static Point wgs84Resolution(Point location, Point resolution) {
        GlobalMercator globalMercator = new GlobalMercator(256);
        double temp = resolution.getX() * Math.PI * 2 * globalMercator.EARTH_RADIUS / 360.0;
        return new Point(temp, temp);
    }

    /**
     * 提取出 REG通道信息
     *
     * @param sourceBandList
     * @param bandInfos
     * @param chanelData
     * @param dataset
     */
    private static void processBandInfo(List<BandData> sourceBandList,
                                        List<BandInfo> bandInfos, ChanelData chanelData, Dataset dataset) {
        int bandCount = dataset.GetRasterCount();
        if (chanelData.redChanel > bandCount || chanelData.redChanel < 1) {
            chanelData.redChanel = 1;
        }
        BandInfo redBandInfo = null;
        for (int i = 0; i < bandInfos.size(); i++) {
            BandInfo bandInfo = bandInfos.get(i);
            if (bandInfo.index + 1 == chanelData.redChanel) {
                redBandInfo = bandInfo;
                break;
            }
        }
        if (redBandInfo != null) {
            BandData bandData = new BandData(dataset.GetRasterBand(chanelData.redChanel), redBandInfo);
            sourceBandList.add(bandData);
        }
    }

    private void printDataType() {
        log.info(" GDT_Unknown {}", gdalconstJNI.GDT_Unknown_get());
        log.info(" GDT_Byte {}", gdalconstJNI.GDT_Byte_get());
        log.info(" GDT_UInt16 {}", gdalconstJNI.GDT_UInt16_get());
        log.info(" GDT_Int16 {}", gdalconstJNI.GDT_Int16_get());
        log.info(" GDT_UInt32 {}", gdalconstJNI.GDT_UInt32_get());
        log.info(" GDT_Int32 {}", gdalconstJNI.GDT_Int32_get());
        log.info(" GDT_Float32 {}", gdalconstJNI.GDT_Float32_get());
        log.info(" GDT_Float64 {}", gdalconstJNI.GDT_Float64_get());
        log.info(" GDT_CInt16 {}", gdalconstJNI.GDT_CInt16_get());
        log.info(" GDT_CInt32 {}", gdalconstJNI.GDT_CInt32_get());
        log.info(" GDT_CFloat32 {}", gdalconstJNI.GDT_CFloat32_get());
        log.info(" GDT_CFloat64 {}", gdalconstJNI.GDT_CFloat64_get());
        log.info(" GDT_TypeCount {}", gdalconstJNI.GDT_TypeCount_get());
    }

    private FilePool getGlobalFilePool() {
        if (globalFilePool == null) {
            globalFilePool = NutFilePool.getOrCreatePool("~/temp/ai", 10000);
        }
        return globalFilePool;
    }

    /**
     * 临时的png文件
     *
     * @return
     */
    public synchronized File tempFile() {
        FilePool pool = getGlobalFilePool();
        return pool.createFile(".png");
    }

    public String imageSha256(File file, Long userId, IProgressNotify notify) {
        if (file == null || !file.exists()) {
            return Lang.sha256("");
        }
        //这一版简化对影像的操作了
        //直接取file的绝对路径 进行哈希计算
        if (notify != null) {
            notify.notify(userId, MQTT_TOPIC_TYPE_DIR_INDEX, 0, 0, "解析完成", 100);
        }
        try (InputStream ins = Streams.fileIn(file)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            long total = file.length();
            long processed = 0;
            byte[] bs = new byte[32 * 1024];
            int len = 0;
            while ((len = ins.read(bs)) != -1) {
                md.update(bs, 0, len);
                processed += len;
                if (notify != null) {
                    if (total > 0) {
                        String message =
                                Strings.formatSizeForReadBy1024(processed) + "/" + Strings.formatSizeForReadBy1024(total);
                        notify.notify(userId, MQTT_TOPIC_TYPE_DIR_INDEX, 0, 0, message, (int) (100.0 * processed / total));
                    }
                }
            }


            byte[] hashBytes = md.digest();
            if (notify != null) {
                if (total > 0) {
                    String message = Strings.formatSizeForReadBy1024(total) + "/" + Strings.formatSizeForReadBy1024(total);
                    notify.notify(userId, MQTT_TOPIC_TYPE_DIR_INDEX, 0, 0, message, 100);
                }
            }
            return Lang.fixedHexString(hashBytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 计算影像的 SHA256
     *
     * @param imageAbsoluteFilename
     * @return
     */
    public String imageSha256(String imageAbsoluteFilename) {
        if (Strings.isBlank(imageAbsoluteFilename)) {
            return Lang.sha256("");
        }
        return Lang.sha256(imageAbsoluteFilename);
    }

    /**
     * 读取影像的信息
     * 有sha256值 就去数据库中查找 没找到 计算 找到 返回
     * 无Msha256值 计算
     *
     * @param imageAbsoluteFilename
     * @return
     */
    public ImageInfo extractImageInformation(String sha256, String imageAbsoluteFilename, IImagePreviewProvider previewProvider) {
        // sha256  必须传入 我们就是不计算了
        //需要重新计算
        Dataset dataset = gdal.Open(imageAbsoluteFilename, gdalconstConstants.GA_ReadOnly);
        ImageInfo info = extractImageInformation(dataset);


        //获取了影像的基本信息 然后我们还要根据影像的类型区获取传感器信息
        //比如 GF1 GF2 GF6 Sentianl等系列 主要是从 .xml文件中去读取
        extractSatelliteInfo(imageAbsoluteFilename, info);


        info.name = Files.getMajorName(imageAbsoluteFilename);
        info.setLocation(imageAbsoluteFilename);
        File f = new File(imageAbsoluteFilename);
        info.setFileSize(f.length());
        info.setSha256(sha256);

        //  previewImage(dataset, info, previewProvider);

        return info;
    }

    /**
     * 根据配置的解析列表进行猜测 元数据文件信息
     *
     * @param imageAbsoluteFilename
     * @param info
     */
    private void extractSatelliteInfo(String imageAbsoluteFilename, ImageInfo info) {
        for (ISatelliteExtractor extractor : satelliteExtractorList) {
            if (extractor.extract(imageAbsoluteFilename, info)) {
                return;
            }
        }
    }

    /**
     * 构建图像的预览信息
     *
     * @param sourceDataset
     * @param imageInfo
     * @param previewProvider
     */
    public byte[] previewImage(Dataset sourceDataset, ImageInfo imageInfo, IImagePreviewProvider previewProvider) {
        if (previewProvider == null || sourceDataset == null) {
            log.warn("传入预览接口为空");
            return null;
        }
        Stopwatch stopwatch = Stopwatch.begin();
        File temp = tempFile();
        String targetPngFileName = temp.getAbsolutePath();
        int ysize = (int) Math.ceil(imageInfo.height * (512.0d / imageInfo.width));

        Dataset memoryDataset = getMemoryDriver().Create("", 512, ysize, 4, gdalconstConstants.GDT_Byte);
        BaseTileExtractor extractor = new BaseTileExtractor();
        boolean b = extractor.preview(imageInfo, sourceDataset, memoryDataset, 512, ysize);
        byte[] data = null;
        if (b) {
            memoryDataset.FlushCache();
            Dataset targetDataset = getPngDriver().CreateCopy(targetPngFileName, memoryDataset);
            targetDataset.FlushCache();
            // targetDataset.Close();
            stopwatch.stop();
            log.info("extract Tile {}   用时{}毫秒", imageInfo.location, stopwatch.getDuration());
            data = Files.readBytes(targetPngFileName);
            previewProvider.write(imageInfo.getSha256(), data);
        } else {
            stopwatch.stop();
            log.error("preview Tile error {}   用时{}毫秒", imageInfo.location, stopwatch.getDuration());
        }
        //删除临时文件
        temp.delete();
        return data;
    }

    /**
     * 从原始影像中输出瓦片
     *
     * @param imageInfo
     * @param tileX
     * @param tileY
     * @param zoom
     * @return
     */
    public byte[] extractFromSource(ImageInfo imageInfo, long tileX, long tileY, int zoom, ColorTable colorTable) {
        if (imageInfo == null || !Files.isFile(new File(imageInfo.location))) {
            log.error("ImageInfo 描述的影像 不是一个合法的文件{}", Json.toJson(imageInfo));
            return null;
        }
        byte[] outData = null;

        if (imageInfo.getSrid() == SRID_WGS84) {
            WGS84TileExtractor extractor = new WGS84TileExtractor();
            extractor.setColorTable(colorTable);
            outData = extract(extractor, imageInfo, tileX, tileY, zoom);
        } else if (imageInfo.getSrid() == SRID_CGCS2000) {
            CGCS2000TileExtractor extractor = new CGCS2000TileExtractor();
            extractor.setColorTable(colorTable);
            outData = extract(extractor, imageInfo, tileX, tileY, zoom);
        } else if (imageInfo.getSrid() == SRID_WEB_MERCATO) {
            WebMercatorTileExtractor extractor = new WebMercatorTileExtractor();
            extractor.setColorTable(colorTable);
            outData = extract(extractor, imageInfo, tileX, tileY, zoom);
        } else {
            log.error("程序员抓紧实现 对 {}的影像进行解读", imageInfo.getSrid());
            return null;
        }
        return outData;
    }

    private byte[] extract(ITileExtractor extractor, ImageInfo imageInfo, long tileX, long tileY, int zoom) {
        Stopwatch stopwatch = Stopwatch.begin();
        Dataset sourceDataset = gdal.Open(imageInfo.location, gdalconstConstants.GA_ReadOnly);
        File temp = tempFile();
        String targetPngFileName = temp.getAbsolutePath();

        Dataset memoryDataset = getMemoryDriver().Create("", 256, 256, 4, gdalconstConstants.GDT_Byte);
        boolean b = extractor.extractTileToTarget(imageInfo, tileX, tileY, zoom, sourceDataset, memoryDataset);
        if (b) {
            memoryDataset.FlushCache();
            Dataset targetDataset = getPngDriver().CreateCopy(targetPngFileName, memoryDataset);
            targetDataset.FlushCache();
            stopwatch.stop();
            // sourceDataset.Close();
            // targetDataset.Close();

            //  log.info("extract Tile {} ({} {} {})  用时{}毫秒", imageInfo.location, tileX, tileY, zoom, stopwatch.getDuration());
            byte[] data = Files.readBytes(targetPngFileName);
            temp.delete();
            return data;
        } else {
            stopwatch.stop();
            //sourceDataset.Close();
            //  log.error("extract Tile error {} ({} {} {})  用时{}毫秒", imageInfo.location, tileX, tileY, zoom, stopwatch.getDuration());
            temp.delete();
            return null;
        }
    }
}