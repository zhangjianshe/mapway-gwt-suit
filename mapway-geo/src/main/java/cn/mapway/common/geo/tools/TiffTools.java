package cn.mapway.common.geo.tools;


import cn.mapway.biz.core.BizResult;
import cn.mapway.common.geo.gdal.GdalUtil;
import cn.mapway.common.geo.gdal.WebMercator;
import cn.mapway.common.geo.sfile.TileNo;
import cn.mapway.common.geo.tools.parser.GF1Parser;
import cn.mapway.common.geo.tools.parser.ISatelliteExtractor;
import cn.mapway.geo.client.raster.*;
import cn.mapway.geo.shared.color.ColorMap;
import cn.mapway.geo.shared.color.ColorTable;
import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;
import cn.mapway.geo.shared.vector.Rect;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.util.Colors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.gdalconst.gdalconstJNI;
import org.gdal.ogr.GeomTransformer;
import org.gdal.ogr.Geometry;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.json.Json;
import org.nutz.lang.*;
import org.nutz.lang.random.R;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

import static cn.mapway.geo.shared.GeoConstant.*;
import static org.gdal.gdalconst.gdalconstConstants.*;
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

    private final int defaultColor = 0x808080FF;

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

        TileNo tileNo = WebMercator.tileNoFromWgs84(39.975, 111.29, 18);
        System.out.println(tileNo);

        // http://localhost:7100/api/v1/map3/50cc3696b12053bf6aa92a3997c3dd1248a468b94f72a65363e3663c8f95100f/15/26782/13539.png
        //http://localhost:7100/api/v1/map3/552f5dbb55dc8e2f42812c713f6f95339a5cdd33affce45c8555bd4c5fc55b86/15/25833/12852.png
        filePath = "/data/personal/1/成果展示/基于无人机图像的分类/ndvi.tif";
        filePath = "/data/personal/1/成果展示/S_842_387_3371_1549.tif";
        filePath = "/data/personal/1/test/R_398_196_1595_785.tif";
        filePath = "/data/personal/1/bhg/guoyuan_ndvi_20240910.tif";
        filePath = "/mnt/cangling/devdata/personal/1/O_53_27_214_109.tif";
        filePath = "/mnt/cangling/devdata/personal/1/tianye_sentinel_20240606.tif";
        //17/101104/56083.png
        long tilex = 94;
        long tiley = 45;
        int zoom = 7;
        GdalUtil.init();
        GdalUtil.setPAM(true, "/data/pam");
        TiffTools tiffTools = new TiffTools();

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

            @Override
            public boolean resetCache(String sha256) {
                return false;
            }
        });
        //System.out.println("INFO image extend " + md5File.getBox().toString());
        //System.out.println("INFO image size " + md5File.width + " " + md5File.height);
        md5File.getBandInfos().get(0).enableGamma = false;
        ColorTable colorTable = new ColorTable();
        colorTable.setDefaultTable(true);
        if (md5File.getBandInfos().get(0).getColorMaps() != null) {
            colorTable.setColorMaps(md5File.getBandInfos().get(0).getColorMaps());
        }
        byte[] bytes = tiffTools.extractFromSource(md5File, tilex, tiley, zoom, colorTable);
        if (bytes == null) {
            System.out.println("gen error");
        } else {
            Files.write("/data/tmp/123.png", bytes);
            System.out.println("gen success");
        }
    }

    public static synchronized SpatialReference getWgs84Reference() {
        if (srfwgs84 == null) {
            srfwgs84 = new SpatialReference();
            srfwgs84.ImportFromEPSG(4326);
            srfwgs84.SetAxisMappingStrategy(OAMS_TRADITIONAL_GIS_ORDER);
        }
        return srfwgs84;
    }

    public static byte[] generateImagePreview(ImageInfo imageInfo, int targetWidth, ColorTable colorTable) {
        Dataset dataset = null;
        try {
            dataset = gdal.Open(imageInfo.location, gdalconstConstants.GA_ReadOnly);
            int rasterWidth = dataset.getRasterXSize();
            int rasterHeight = dataset.getRasterYSize();
            int targetHeight = (int) Math.ceil((double) (rasterHeight * targetWidth) / (double) rasterWidth);
            Rect source = new Rect(0.0F, 0.0F, rasterWidth, rasterHeight);
            Rect target = new Rect(0.0F, 0.0F, targetWidth, targetHeight);
            Dataset previewDataset = getMemoryDriver().Create("", targetWidth, targetHeight,
                    4, gdalconstConstants.GDT_Byte);
            List<BandData> sourceBandList = new ArrayList();
            List<Band> targetBandList = new ArrayList();
            targetBandList.add(previewDataset.GetRasterBand(1));
            targetBandList.add(previewDataset.GetRasterBand(2));
            targetBandList.add(previewDataset.GetRasterBand(3));
            processBandInfo(sourceBandList, imageInfo.bandInfos, imageInfo.chanelData, dataset);
            BaseTileExtractor extractor = new BaseTileExtractor();
            extractor.setColorTable(colorTable);
            byte[] transparentBand = extractor.getBand(dataset.GetRasterCount() == 1,
                    new Size(targetWidth, targetHeight), target, source, sourceBandList, targetBandList);
            previewDataset.GetRasterBand(4).WriteRaster(0, 0, targetWidth, targetHeight, transparentBand);
            previewDataset.FlushCache();
            String tempPath = "/var/ibcache/preview/temp";
            Files.createDirIfNoExists(tempPath);
            String targetFileName = tempPath + "/" + R.UU16() + ".png";
            Dataset targetDataset = getPngDriver().CreateCopy(targetFileName, previewDataset);
            targetDataset.FlushCache();
            dataset.Close();
            byte[] image = Files.readBytes(targetFileName);
            Files.deleteFile(new File(targetFileName));
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    /**
     * 根据经纬度坐标 查询映像中的数值
     *
     * @param filePath
     * @param lat
     * @param lng
     * @return
     */
    public static BizResult<List<Double>> readPixelValues(String filePath, double lat, double lng) {
        Dataset dataset = gdal.Open(filePath, gdalconstConstants.GA_ReadOnly);
        int rasterCount = dataset.getRasterCount();
        int rasterXSize = dataset.getRasterXSize();
        int rasterYSize = dataset.getRasterYSize();


        if (Strings.isBlank(dataset.GetProjection())) {
            return BizResult.error(500, "目标文件没有设置坐标参考");
        }

        SpatialReference imageReference = new SpatialReference();
        imageReference.ImportFromWkt(dataset.GetProjection());
        imageReference.SetAxisMappingStrategy(OAMS_TRADITIONAL_GIS_ORDER);
        SpatialReference wgs84 = getWgs84Reference();

        CoordinateTransformation coordinateTransformation = CoordinateTransformation.CreateCoordinateTransformation(wgs84, imageReference);
        double[] coord = coordinateTransformation.TransformPoint(lng, lat);
        Point pixelCoord = BaseTileExtractor.imageSpaceToSourceSpace(dataset.GetGeoTransform(), new Point(coord[0], coord[1]));

        List<Double> data = new ArrayList<>(rasterCount);
        if (
                pixelCoord.x < 0 || pixelCoord.x >= rasterXSize
                        || pixelCoord.y < 0 || pixelCoord.y >= rasterYSize
        ) {
            log.warn("location is out of image extends {} {}", lat, lng);
            for (int i = 0; i < rasterCount; i++) {
                data.add(0.0);
            }
            return BizResult.success(data);
        } else {

            for (int i = 1; i <= rasterCount; i++) {
                Band band = dataset.GetRasterBand(i);
                int dataType = band.getDataType();
                data.add(readPixelValue(band, dataType, pixelCoord.getXAsInt(), pixelCoord.getYAsInt()));
            }
        }
        return BizResult.success(data);
    }

    private static double readPixelValue(Band band, int dataType, int x, int y) {
        double[] doubleValue = {0};
        band.ReadRaster(x, y, 1, 1, 1, 1, gdalconstConstants.GDT_Float64, doubleValue);
        return doubleValue[0];
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

    public static boolean calRasterPreview(String sourceFileName, String targetFileName, int targetWidth, List<BandInfo> bandInfos, ChanelData chanelData, ColorTable colorTable, ProgressCallback callback) {
        Dataset dataset = null;
        boolean success = false;
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

            byte[] transparentBand = extractor.getBand(dataset.GetRasterCount() == 1, new Size(targetWidth, targetHeight),
                    target, source, sourceBandList, targetBandList);
            previewDataset.GetRasterBand(4).WriteRaster(0, 0, targetWidth, targetHeight, transparentBand);
            previewDataset.FlushCache();
            //输出到指定的路径
            Dataset targetDataset = getPngDriver().CreateCopy(targetFileName, previewDataset);
            targetDataset.FlushCache();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataset != null) {
                dataset = null;
            }
        }
        return success;
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
        //System.out.println("[INFO AFFINE TRANSFORM] " + Arrays.toString(adfGeoTransform));
        Point leftBottom = BaseTileExtractor.rasterSpaceToImageSpace(adfGeoTransform, new Point(0, dataset.GetRasterYSize()));
        Point rightTop = BaseTileExtractor.rasterSpaceToImageSpace(adfGeoTransform, new Point(dataset.getRasterXSize(), 0));
        //System.out.println("[INFO IMAGE RANGE] " + leftBottom + " " + rightTop);

        info.box = new Box();
        info.setSourceBox(new Box());
        Box box = info.box;
        String projectionWkt = dataset.GetProjection();
        String linerUnit = "";
        String angleUnit = "";
        double linerScale = 1.0;
        double angleScale = 1.0;
        if (Strings.isNotBlank(projectionWkt)) {
            SpatialReference spatialReference = new SpatialReference();

            spatialReference.ImportFromWkt(projectionWkt);
            spatialReference.SetAxisMappingStrategy(OAMS_TRADITIONAL_GIS_ORDER);

            SpatialReference wgs84Reference = new SpatialReference();
            wgs84Reference.ImportFromEPSG(4326);
            wgs84Reference.SetAxisMappingStrategy(OAMS_TRADITIONAL_GIS_ORDER);

            linerUnit = spatialReference.GetLinearUnitsName();
            linerScale = spatialReference.GetLinearUnits();
            angleUnit = spatialReference.GetAngularUnitsName();
            angleScale = spatialReference.GetAngularUnits();
            //System.out.println("[INFO] units " + linerUnit + " " + linerScale + " " + angleUnit + " " + angleScale);

            CoordinateTransformation coordinateTransformation = CoordinateTransformation.CreateCoordinateTransformation(spatialReference, wgs84Reference);
            double[] doubles1 = coordinateTransformation.TransformPoint(leftBottom.getX(), leftBottom.getY());
            double[] doubles2 = coordinateTransformation.TransformPoint(rightTop.getX(), rightTop.getY());

            box.setValue(doubles1[0], doubles1[1], doubles2[0], doubles2[1]);
            //System.out.println("[INFO PROJECTION] " + box);

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
                info.setSrid(spatialReference.AutoIdentifyEPSG());
                info.setProjection(spatialReference.ExportToPrettyWkt());
            }
        } else {
            info.setSrid(SRID_NULL);
            info.setProjection("");
        }

        if (info.getSrid() == SRID_WGS84) {
            info.setLat(box.center().getY());
            info.setLng(box.center().getX());
            Point resolution = resolution(adfGeoTransform);

            Point resolutionMi = new Point(
                    resolution.getX() * linerScale,
                    resolution.getY() * linerScale
            );

            int zoom = zoomByWgs84Resolution(resolutionMi.getX());
            info.setMaxZoom(22);
            info.setMinZoom(3);
            info.setResolution((int) resolutionMi.getX() * 10);
            info.getSourceBox().copyFrom(box);
        } else if (info.getSrid() == SRID_CGCS2000) {
            info.setLat(box.center().getY());
            info.setLng(box.center().getX());
            Point resolution = resolution(adfGeoTransform);
            //计算分辨率 需要转换为米 如果原单位是米 那么分辨率也是米 单位
            // TODO 需要根据参考系的单位进行换算
            // Point resolutionMi = wgs84Resolution(box.center(), resolution);
            Point resolutionMi = new Point(
                    resolution.getX() * linerScale,
                    resolution.getY() * linerScale
            );

            int zoom = zoomByWgs84Resolution(resolutionMi.getX());
            info.setMaxZoom(22);
            info.setMinZoom(3);
            info.setResolution((int) (resolutionMi.getX() * 10));
            info.getSourceBox().copyFrom(box);
        } else if (info.getSrid() == SRID_WEB_MERCATO) {

            info.setLat(box.center().getY());
            info.setLng(box.center().getX());

            //每个像素所占的经度
            double lngPerPixel = box.width() / info.getWidth();
            double resolution = lngPerPixel * (2 * Math.PI * GlobalMercator.get().EARTH_RADIUS) / 360;
            int zoom = GlobalMercator.get().zoomForPixelSize(resolution);

            info.setMaxZoom(22);
            info.setMinZoom(3);
            info.setResolution((int) (resolution * 10));
        } else if (info.getSrid() > 0) {
            info.setLat(box.center().getY());
            info.setLng(box.center().getX());

            //每个像素所占的经度
            double lngPerPixel = box.width() / info.getWidth();
            double resolution = lngPerPixel * (2 * Math.PI * GlobalMercator.get().EARTH_RADIUS) / 360;
            int zoom = GlobalMercator.get().zoomForPixelSize(resolution);

            info.setMaxZoom(22);
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
        return new Point(adfGeoTransform[1], Math.abs(adfGeoTransform[5]));
    }

    public static Point wgs84Resolution(Point location, Point resolution) {
        GlobalMercator globalMercator = new GlobalMercator(256);
        double temp = resolution.getX() * Math.PI * 2 * globalMercator.EARTH_RADIUS / 360.0;
        return new Point(temp, temp);
    }

    /**
     * 提取出 RGB通道信息
     *
     * @param sourceBandList
     * @param bandInfos
     * @param chanelData
     * @param dataset
     */
    private static void processBandInfo(List<BandData> sourceBandList,
                                        List<BandInfo> bandInfos, ChanelData chanelData, Dataset dataset) {
        //原始影像的波段数
        int bandCount = dataset.GetRasterCount();

        if (chanelData.redChanel > bandCount || chanelData.redChanel < 1) {
            chanelData.redChanel = 1;
        }
        for (int i = 0; i < bandInfos.size(); i++) {
            BandInfo bandInfo = bandInfos.get(i);
            if (bandInfos.get(i).index + 1 == chanelData.redChanel) {
                sourceBandList.add(new BandData(dataset.GetRasterBand(chanelData.redChanel), bandInfo));
                break;
            }
        }

        if (chanelData.greenChanel > bandCount || chanelData.greenChanel < 1) {
            chanelData.greenChanel = 1;
        }
        for (int i = 0; i < bandInfos.size(); i++) {
            BandInfo bandInfo = bandInfos.get(i);
            if (bandInfos.get(i).index + 1 == chanelData.greenChanel) {
                sourceBandList.add(new BandData(dataset.GetRasterBand(chanelData.greenChanel), bandInfo));
                break;
            }
        }

        if (chanelData.blueChanel > bandCount || chanelData.blueChanel < 1) {
            chanelData.blueChanel = 1;
        }
        for (int i = 0; i < bandInfos.size(); i++) {
            BandInfo bandInfo = bandInfos.get(i);
            if (bandInfos.get(i).index + 1 == chanelData.blueChanel) {
                sourceBandList.add(new BandData(dataset.GetRasterBand(chanelData.blueChanel), bandInfo));
                break;
            }
        }

    }

    /**
     * 从 GDAL 虚拟文件系统读取数据并清理
     * 适配 GDAL 3.12.0 封装
     *
     * @param vsiPath 虚拟路径，例如 "/vsimem/tile_123.png"
     * @return 字节数组
     */
    public static byte[] vsimemReadAndClean(String vsiPath) {
        try {
            // 1. 获取内存文件 Buffer
            // 对应源码中的: public static byte[] GetMemFileBuffer(String utf8_path)
            // 注意：此方法专门用于 /vsimem/ 路径
            byte[] buffer = gdal.GetMemFileBuffer(vsiPath);

            if (buffer == null || buffer.length == 0) {
                log.warn("VSI 内存文件读取为空或路径无效: {}", vsiPath);
                return null;
            }

            return buffer;
        } catch (Exception e) {
            log.error("读取 VSI 内存文件异常: {}", vsiPath, e);
            return null;
        } finally {
            // 2. 释放内存（极其重要！）
            // 对应源码中的: public static int Unlink(String utf8_path)
            // 相当于执行 'rm'，如果不 unlink，/vsimem/ 会持续占用 JVM 堆外内存
            int result = gdal.Unlink(vsiPath);
            if (result != 0) {
                log.debug("VSI Unlink 路径: {}, 结果码: {}", vsiPath, result);
            }
        }
    }

    private boolean isPixelTransparent(double pixel, BandInfo bandInfo) {
        Double[] noValues = bandInfo.noValues;
        if (noValues != null && noValues.length > 0) {
            for (int t = 0; t < noValues.length; t++) {
                if (Math.abs(pixel - noValues[t]) < 0.0000001) {
                    return true;
                }
            }
            return false;
        }
        return Math.abs(pixel) < 0.0000001;
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
        String fileName = "/dev/shm/" + R.UU16() + ".png";
        return new File(fileName);
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

    /* *//**
     * 从原始影像中输出瓦片
     *
     * @param imageInfo
     * @param tileX
     * @param tileY
     * @param zoom
     * @return
     *//*
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
    }*/

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
     * 从源影像提取指定瓦片数据并渲染为 PNG 字节流
     */
    public byte[] extractFromSource(ImageInfo imageInfo, long tilex, long tiley, int zoom, ColorTable colorTable) {
        Dataset srcDataset = null;
        Dataset memDataset = null;
        Dataset outDataset = null;
        Dataset renderDataset = null;
        try {
            srcDataset = gdal.Open(imageInfo.location, gdalconstConstants.GA_ReadOnly);
            if (srcDataset == null) return null;

            // 1. 计算瓦片在 EPSG:3857 下的边界 (minX, minY, maxX, maxY)
            // 使用 GlobalMercator 或你项目中的 WebMercator 工具类
            double[] bounds3857 = GlobalMercator.getTileBounds3857(tilex, tiley, zoom);

            // 检查tile是否落在srcDataset内部
            double[] extent4 = new double[4];
            srcDataset.GetExtentWGS84LongLat(extent4);
            Box imgBox = new Box(extent4[0], extent4[2], extent4[1], extent4[3]);
            // 将你的瓦片 3857 边界转回经纬度 (使用你的 GlobalMercator 工具)
            Box tileLonLat = GlobalMercator.get().tileLngLatBounds(tilex, tiley, zoom);
            // tileLonLat 通常返回 [minLon, minLat, maxLon, maxLat]

            // 进行相交判断
            boolean isIntersect = imgBox.intersect(tileLonLat);
            Point center = imgBox.center();
            if (!isIntersect && !tileLonLat.contain(center)) {
                return null; // 或者返回透明图片，避免浪费 CPU 进行 Warp
            }

            // 2. 构造 Warp 选项
            // 使用 Warp 可以自动处理：坐标转换、切片裁剪、重采样
            Vector<String> options = new Vector<>();
            options.add("-t_srs");
            options.add("EPSG:3857");
            options.add("-te"); // 设置输出范围 [xmin ymin xmax ymax]
            options.add(String.valueOf(bounds3857[0]));
            options.add(String.valueOf(bounds3857[1]));
            options.add(String.valueOf(bounds3857[2]));
            options.add(String.valueOf(bounds3857[3]));
            options.add("-ts");
            options.add("256");
            options.add("256"); // 输出分辨率
            options.add("-dstnodata");
            options.add("0");
            options.add("-r");
            options.add("bilinear"); // 重采样方式：双线性插值
            options.add("-of");
            options.add("MEM");     // 直接输出到内存驱动
            ChanelData chanelData = imageInfo.getChanelData();

            if (chanelData == null) {
                chanelData = new ChanelData();
            }
            if (srcDataset.GetRasterCount() < 1) {
                return null;
            } else if (srcDataset.GetRasterCount() == 1) {
                options.add("-srcband");
                options.add(String.valueOf(1));
            } else if (srcDataset.GetRasterCount() == 2) {
                options.add("-srcband");
                options.add(String.valueOf(1));
                options.add("-srcband");
                options.add(String.valueOf(2));
            } else if (srcDataset.GetRasterCount() == 3) {
                options.add("-srcband");
                options.add(String.valueOf(1));
                options.add("-srcband");
                options.add(String.valueOf(2));
                options.add("-srcband");
                options.add(String.valueOf(3));
            } else {

                options.add("-srcband");
                options.add(String.valueOf(chanelData.getRedChanel())); // Red
                options.add("-srcband");
                options.add(String.valueOf(chanelData.getGreenChanel())); // Green
                options.add("-srcband");
                options.add(String.valueOf(chanelData.getBlueChanel())); // Blue
            }

            WarpOptions warpOptions = new WarpOptions(options);

            // 执行 Warp：将源数据裁剪并重投影到内存 Dataset 中
            // 注意：这里仍然保留原始位深（如 Float32 或 UInt16），方便后续色彩拉伸
            memDataset = gdal.Warp("", new Dataset[]{srcDataset}, warpOptions);
            if (memDataset == null) return null;
            // 检查 Warp 后的波段数是否符合预期
            if (memDataset.getRasterCount() < 1) {
                log.error("Warp 结果波段数为0");
                return null;
            }


            // 3. 应用色彩映射与拉伸 (Rendering)
            // 创建一个 RGBA 的 8-bit 内存数据集用于存储渲染结果
            renderDataset = getMemoryDriver().Create("", 256, 256, 4, gdalconstConstants.GDT_Byte);

            // 遍历波段进行拉伸处理 (结合你已有的 BandInfo 逻辑)
            applyColorScaling(memDataset, renderDataset, imageInfo, colorTable);

            // 4. 将结果转为 PNG 字节流
            String tmpPath = "/vsimem/tile_" + R.UU16() + ".png"; // 使用 GDAL 虚拟文件系统
            outDataset = getPngDriver().CreateCopy(tmpPath, renderDataset);
            outDataset.FlushCache();

            byte[] pngBytes = vsimemReadAndClean(tmpPath);
            return pngBytes;

        } catch (Exception e) {
            log.error("切片提取失败: {}", e.getMessage());
            return null;
        } finally {
            // 必须显式 delete，否则会内存泄漏
            if (srcDataset != null) srcDataset.delete();
            if (memDataset != null) memDataset.delete();
            // 记得加上这个，因为 renderDataset 是通过 Create 创建的内存对象
            if (renderDataset != null) renderDataset.delete();
            if (outDataset != null) outDataset.delete();
        }
    }

    /**
     * @param value 0xRRGGBBAA
     * @return
     */
    public int translateColor(ColorTable colorTable, double value) {
        if (colorTable == null) {
            return defaultColor;
        }
        return colorTable.mapColor(value);
    }

    public void applyColorScaling(Dataset memDataset, Dataset renderDataset, ImageInfo imageInfo, ColorTable colorTable) {
        int width = 256;
        int height = 256;
        Rect targetRect = new Rect(0, 0, width, height);

        // 获取透明度通道 Buffer
        byte[] transparentBand = new byte[width * height];

        // 获取波段配置
        ChanelData chanelData = imageInfo.getChanelData();
        if (chanelData == null) chanelData = new ChanelData();


        // 因为 Warp 时我们只提取了 1~3 个波段，所以 memDataset 里的索引始终从 1 开始
        int renderBandCount = memDataset.getRasterCount();
        if (renderBandCount <= 2) {
            //只有一个波段　需要利用颜色表进行颜色映射
            Band srcBand = memDataset.GetRasterBand(1);
            int originalBandIndex = getOriginalIndex(1, chanelData, renderBandCount);
            BandInfo info = imageInfo.findBand(originalBandIndex);

            BandData bandData = new BandData(srcBand, info);
            ByteBuffer sourceData = readSourceDataNoTranslate(
                    transparentBand, bandData,
                    0, 0, width, height,
                    0, 0, width, height
            );

            ByteBuffer[] sourceBuffer = new ByteBuffer[3];
            sourceBuffer[0] = ByteBuffer.allocateDirect(width * height);
            sourceBuffer[1] = ByteBuffer.allocateDirect(width * height);
            sourceBuffer[2] = ByteBuffer.allocateDirect(width * height);
            int dt = srcBand.getDataType();
            //循环处理每一个像素
            sourceData.order(ByteOrder.nativeOrder());

            /*if (info.enableGamma) {
                if (info.gammaMax == null || info.gammaMin == null || info.gamma == null) {
                    // gamma 矫正参数未设置, 根据当前小区域计算
                    calculateGamma(info, sourceData, dt, targetRect);
                }
            }*/

            info.check();
            FloatBuffer floatBuffer = sourceData.asFloatBuffer();
            DoubleBuffer doubleBuffer = sourceData.asDoubleBuffer();
            IntBuffer intBuffer = sourceData.asIntBuffer();
            ShortBuffer shortBuffer = sourceData.asShortBuffer();

            //循环目标区域 [0-78][0-32]
            for (int row = 0; row < targetRect.getHeightAsInt(); row++) {
                for (int col = 0; col < targetRect.getWidthAsInt(); col++) {
                    //目标像素位置 用于读取经过GDAL采样后的影像数组
                    int location = row * targetRect.getWidthAsInt() + col;

                    int tilePosition = row * width + col;
                    double pixelValue = 0;
                    if (dt == GDT_Byte || dt == GDT_Int8) {
                        pixelValue = ((int) sourceData.get(location)) & 0xFF;

                    } else if (dt == GDT_Int16) {
                        pixelValue = shortBuffer.get(location);
                    } else if (dt == gdalconstConstants.GDT_UInt16) {
                        int v = shortBuffer.get(location) & 0xFFFF;
                        pixelValue = v;
                    } else if (dt == gdalconstConstants.GDT_Int32) {
                        pixelValue = intBuffer.get(location);
                    } else if (dt == GDT_UInt32) {
                        long unsignedLong = Integer.toUnsignedLong(intBuffer.get(location));
                        pixelValue = unsignedLong;
                    } else if (dt == gdalconstConstants.GDT_Float32) {
                        pixelValue = floatBuffer.get(location);
                    } else if (dt == gdalconstConstants.GDT_Float64) {
                        pixelValue = doubleBuffer.get(location);
                    }

                    int rgba;
                    boolean isNoValue = false;
                    Double[] noValues = bandData.getInfo().getNoValues();
                    if (noValues != null) {
                        for (int i = 0; i < noValues.length; i++) {
                            if (noValues[i] != null && noValues[i].intValue() == pixelValue) {
                                isNoValue = true;
                                break;
                            }
                        }
                    }

                    if (isNoValue) {
                        sourceBuffer[0].put(tilePosition, (byte) 0xFF);
                        sourceBuffer[1].put(tilePosition, (byte) 0xFF);
                        sourceBuffer[2].put(tilePosition, (byte) 0xFF);
                        transparentBand[tilePosition] = (byte) 0xFF;
                        continue;
                    }
                    if (info.enableGamma) {
                        // 采用Gamma矫正算法
                        //  value  [outputMin,outputMax]
                        double value = info.calValue(pixelValue);
                        if (colorTable == null) {
                            int v = (int) value;
                            rgba = Colors.fromColorInt(v, v, v, 0xFF);
                        } else if (colorTable.getDefaultTable() != null && colorTable.getDefaultTable()) {
                            //缺省的颜色表
                            int v = (int) value;
                            rgba = Colors.fromColorInt(v, v, v, 0xFF);
                        } else {
                            if (colorTable != null && colorTable.getNormalize() != null && colorTable.getNormalize()) {
                                //用户设置了归一化调色板
                                pixelValue = normalizePixel(bandData, value);
                                rgba = translateColor(colorTable, pixelValue);
                            } else {
                                long valueLong = Math.round(value);
                                rgba = translateColor(colorTable, valueLong);
                            }
                        }
                    } else if (colorTable != null) {
                        //颜色表为缺省的　首先使用
                        if (colorTable.getDefaultTable() != null && colorTable.getDefaultTable()) {

                            if (bandData.getInfo().colorMaps != null) {
                                //用户设定了自己的颜色表　就用用户的颜色表渲染
                                rgba = translateImageColorTable(bandData.getInfo().colorMaps, pixelValue);
                            } else {
                                //如果没有设定,直接用像素值
                                int v = (int) pixelValue;
                                rgba = Colors.fromColorInt(v, v, v, 0xFF);
                            }
                        } else {
                            //不是缺省的颜色表
                            //用户自定义了颜色表
                            if (colorTable.getNormalize() != null && colorTable.getNormalize()) {
                                //颜色表是归一化颜色表 0.0-1.0  范围之外的颜色通通为透明
                                // 讲数据中的颜色进行归一化处理
                                pixelValue = normalizePixel(bandData, pixelValue);

                                if (pixelValue < 0.0 || pixelValue > 1.0) {
                                    rgba = 0xFFFFFF00;
                                } else {
                                    rgba = translateColor(colorTable, pixelValue);
                                }

                            } else {
                                //颜色表不是归一化颜色表 使用颜色表进行转换
                                rgba = translateColor(colorTable, pixelValue);
                            }
                        }
                    } else {
                        //没有条色斑　原始数据
                        int v = (int) pixelValue;
                        rgba = Colors.fromColorInt(v, v, v, 0xFF);
                    }

                    sourceBuffer[0].put(tilePosition, (byte) (Colors.r(rgba) & 0xFF));
                    sourceBuffer[1].put(tilePosition, (byte) (Colors.g(rgba) & 0xFF));
                    sourceBuffer[2].put(tilePosition, (byte) (Colors.b(rgba) & 0xFF));
                    transparentBand[tilePosition] = (byte) (Colors.a(rgba) & 0xFF);
                }
            }

            for (int i = 0; i < 3; i++) {
                Band targetBand = renderDataset.GetRasterBand(i + 1);
                targetBand.WriteRaster_Direct(0, 0,
                        width, height, sourceBuffer[i]);
            }

            // 处理 Alpha 通道 (第 4 波段)
            renderDataset.GetRasterBand(4).WriteRaster(0, 0, width, height, transparentBand);
        } else {

            for (int i = 1; i <= Math.min(renderBandCount, 3); i++) {
                Band srcBand = memDataset.GetRasterBand(i);
                Band destBand = renderDataset.GetRasterBand(i);

                // 获取原始波段的配置信息 (用于 Gamma 和 NoData 处理)
                // 注意：这里需要根据你 Warp 时的顺序找回原来的 BandInfo
                int originalBandIndex = getOriginalIndex(i, chanelData, renderBandCount);
                BandInfo info = imageInfo.findBand(originalBandIndex);

                BandData bandData = new BandData(srcBand, info);

                // 调用你修改后的 readAndTranslateToBytes256
                // 现在的参数非常简单：0,0 开始，长宽都是 256
                ByteBuffer renderedBuffer = readAndTranslateToBytes256(
                        transparentBand, bandData,
                        0, 0, width, height,
                        0, 0, width, height,
                        width, height);

                destBand.WriteRaster_Direct(0, 0, width, height, renderedBuffer);

            }
            // 处理 Alpha 通道 (第 4 波段)
            renderDataset.GetRasterBand(4).WriteRaster(0, 0, width, height, transparentBand);
        }
    }

    private int translateImageColorTable(List<ColorMap> colorMaps, double pixelValue) {
        //System.out.println("pixel value: " + pixelValue);
        if (colorMaps == null) {
            return defaultColor;
        }
        for (ColorMap colorMap : colorMaps) {
            if (colorMap.getStart() == pixelValue) {
                return colorMap.getRgba();
            }
        }
        //透明颜色
        return 0;
    }

    /**
     * 像素值归一化处理   less 0 || great 1 -> is transparent  set it Value to -1
     *
     * @param sourceBand
     * @param pixelValue
     * @return
     */
    private double normalizePixel(BandData sourceBand, double pixelValue) {
        double total = sourceBand.getInfo().getMaxValue() - sourceBand.getInfo().getMinValue();
        double value = pixelValue - sourceBand.getInfo().getMinValue();
        if (Math.abs(total) < 0.0000001) {
            //颜色数据差别娇小 无法显示 设为-1
            return -1.0;
        }
        return Math.abs(value / total);
    }

    public synchronized ByteBuffer getTargetBuffer(int w, int h) {
        return ByteBuffer.allocateDirect(w * h);
    }

    public synchronized ByteBuffer getSourceBuffer(int w, int h) {
        return ByteBuffer.allocateDirect(w * h * 8);
    }

    public ByteBuffer readSourceDataNoTranslate(byte[] transparentBand, BandData sourceBandData,
                                                int sourceX, int sourceY,
                                                int sourceWidth, int sourceHeight,
                                                int targetX, int targetY,
                                                int targetWidth, int targetHeight
    ) {
        Band sourceBand = sourceBandData.getBand();
        ByteBuffer source = ByteBuffer.allocateDirect(targetWidth * targetHeight * 8);
        int dt = sourceBand.GetRasterDataType();
        source.position(0);
        sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight, targetWidth, targetHeight, dt, source);
        return source;
    }

    public ByteBuffer readAndTranslateToBytes256(byte[] transparentBand, BandData sourceBandData,
                                                 int sourceX, int sourceY,
                                                 int sourceWidth, int sourceHeight,
                                                 int targetX, int targetY,
                                                 int targetWidth, int targetHeight,
                                                 int canvasWidth, int canvasHeight
    ) {
        Band sourceBand = sourceBandData.getBand();
        int dt = sourceBand.GetRasterDataType();
        BandInfo info = sourceBandData.info;

        // 获取缓冲区（建议根据 DataType 动态分配大小，这里沿用你的 getSourceBuffer）
        ByteBuffer target = getTargetBuffer(canvasWidth, canvasHeight);
        ByteBuffer source = getSourceBuffer(targetWidth, targetHeight);
        source.order(ByteOrder.nativeOrder());
        target.position(0);
        source.position(0);

        // 1. 直接读取原始数据到 DirectBuffer
        sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight,
                targetWidth, targetHeight, dt, source);

        // 2. 动态计算 Gamma（针对当前瓦片统计信息）
        if (info.enableGamma && (info.gammaMax == null || info.gammaMin == null)) {
            calculateGamma(info, source, dt, new Rect().setValue(0, 0, targetWidth, targetHeight));
        }
        info.check();

        int totalPixels = targetWidth * targetHeight;

        // 3. 根据不同数据类型进行解析与像素映射
        if (dt == gdalconstConstants.GDT_Byte || dt == gdalconstConstants.GDT_Int8) {
            for (int i = 0; i < totalPixels; i++) {
                double pixel = (source.get(i) & 0xFF);
                processPixel(i, pixel, info, transparentBand, target);
            }
        } else if (dt == gdalconstConstants.GDT_Int16 || dt == gdalconstConstants.GDT_UInt16) {
            ShortBuffer sb = source.asShortBuffer();
            for (int i = 0; i < totalPixels; i++) {
                // 注意 UInt16 的符号位处理
                double pixel = (dt == gdalconstConstants.GDT_UInt16) ? (sb.get(i) & 0xFFFF) : sb.get(i);
                processPixel(i, pixel, info, transparentBand, target);
            }
        } else if (dt == gdalconstConstants.GDT_Int32 || dt == gdalconstConstants.GDT_UInt32) {
            IntBuffer ib = source.asIntBuffer();
            for (int i = 0; i < totalPixels; i++) {
                // 注意 UInt32 的符号位处理
                double pixel = (dt == gdalconstConstants.GDT_UInt32) ? (ib.get(i) & 0xFFFFFFFFL) : ib.get(i);
                processPixel(i, pixel, info, transparentBand, target);
            }
        } else if (dt == gdalconstConstants.GDT_Float32) {
            FloatBuffer fb = source.asFloatBuffer();
            for (int i = 0; i < totalPixels; i++) {
                double pixel = fb.get(i);
                processPixel(i, pixel, info, transparentBand, target);
            }
        } else if (dt == gdalconstConstants.GDT_Float64) {
            DoubleBuffer db = source.asDoubleBuffer();
            for (int i = 0; i < totalPixels; i++) {
                double pixel = db.get(i);
                processPixel(i, pixel, info, transparentBand, target);
            }
        } else {
            log.error("未处理的 GDAL 数据类型: {}", dt);
        }

        return target;
    }

    /**
     * 像素处理核心逻辑：透明度检查 + 色彩映射
     */
    private void processPixel(int index, double pixel, BandInfo info, byte[] transparentBand, ByteBuffer target) {
        // 透明度处理
        if (isPixelTransparent(pixel, info)) {
            transparentBand[index] = (byte) 0x00;
        } else {
            // 如果之前没有被标记为透明，则设为不透明
            // 注意：如果是多波段处理，逻辑上只要有一个波段有值，通常就不透明，或者取交集
            //if (transparentBand[index] != (byte) 0x00) {
            transparentBand[index] = (byte) 0xFF;
            //}
        }

        // 映射到 0-255 并存入目标 Buffer
        double val = info.calValue(pixel);
        target.put(index, (byte) ((int) val & 0xFF));
    }

    private void calculateGamma(BandInfo info, ByteBuffer sourceData, int dt, Rect targetRect) {
        // 遍历 sourceData, 计算最大最小值
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (int row = 0; row < targetRect.getHeightAsInt(); row++) {
            for (int col = 0; col < targetRect.getWidthAsInt(); col++) {
                int location = row * targetRect.getWidthAsInt() + col;
                double pixelValue = 0;
                if (dt == GDT_Byte || dt == GDT_Int8) {
                    pixelValue = sourceData.get(location) & 0xFF;
                } else if (dt == GDT_Int16 || dt == gdalconstConstants.GDT_UInt16) {
                    pixelValue = sourceData.asShortBuffer().get(location) & 0xFFFF;
                } else if (dt == gdalconstConstants.GDT_Int32 || dt == gdalconstConstants.GDT_UInt32) {
                    pixelValue = sourceData.asIntBuffer().get(location);
                } else if (dt == gdalconstConstants.GDT_Float32) {
                    pixelValue = sourceData.asFloatBuffer().get(location);
                } else if (dt == gdalconstConstants.GDT_Float64) {
                    pixelValue = sourceData.asDoubleBuffer().get(location);
                }
                if (pixelValue > max) {
                    max = pixelValue;
                }
                if (pixelValue < min) {
                    min = pixelValue;
                }
            }
        }
        sourceData.position(0);
        // 进行线性拉伸, 计算直方图
        int[] histogram = new int[256];
        for (int row = 0; row < targetRect.getHeightAsInt(); row++) {
            for (int col = 0; col < targetRect.getWidthAsInt(); col++) {
                int location = row * targetRect.getWidthAsInt() + col;
                double pixelValue = 0;
                if (dt == GDT_Byte || dt == GDT_Int8) {
                    pixelValue = sourceData.get(location) & 0xFF;
                } else if (dt == GDT_Int16 || dt == gdalconstConstants.GDT_UInt16) {
                    pixelValue = sourceData.asShortBuffer().get(location) & 0xFFFF;
                } else if (dt == gdalconstConstants.GDT_Int32 || dt == gdalconstConstants.GDT_UInt32) {
                    pixelValue = sourceData.asIntBuffer().get(location);
                } else if (dt == gdalconstConstants.GDT_Float32) {
                    pixelValue = sourceData.asFloatBuffer().get(location);
                } else if (dt == gdalconstConstants.GDT_Float64) {
                    pixelValue = sourceData.asDoubleBuffer().get(location);
                }
                if (max != min) {
                    double value = (pixelValue - min) / (max - min);
                    int key = (int) (value * 255);
                    histogram[key]++;
                }
            }
        }
        sourceData.position(0);

        // 计算平均亮度
        int totalBrightness = 0;
        int totalPixels = 0;
        for (int i = 0; i < histogram.length; i++) {
            totalBrightness += i * histogram[i];
            totalPixels += histogram[i];
        }
        double averageBrightness = totalBrightness / totalPixels;

        // 使用对数函数将平均亮度映射到伽马值
        double deviation = Math.log(averageBrightness) / Math.log(127);
        double gamma = 1.0;
//        if (deviation < 1) {
//            gamma = 1 - deviation;
//        } else if (deviation > 1) {
//            gamma = 1 + deviation;
//        }
        // 或者直接 gamma = 1 / deviation
        gamma = 1 / deviation;

        info.setGammaMax(max);
        info.setGammaMin(min);
        info.setGamma(gamma);
        // 需要将 sourceData 置为初始位置
        sourceData.position(0);
    }

    /**
     * 辅助方法：映射 Warp 后的索引到原始索引
     */
    private int getOriginalIndex(int warpIdx, ChanelData data, int count) {
        if (count == 1) return 1;
        if (count == 2) return warpIdx;
        if (warpIdx == 1) return data.getRedChanel();
        if (warpIdx == 2) return data.getGreenChanel();
        return data.getBlueChanel();
    }

}