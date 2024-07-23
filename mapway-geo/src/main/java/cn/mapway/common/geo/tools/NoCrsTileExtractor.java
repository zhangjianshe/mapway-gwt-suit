package cn.mapway.common.geo.tools;

import cn.mapway.common.geo.gdal.GdalUtil;
import cn.mapway.geo.client.raster.*;
import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;
import cn.mapway.geo.shared.vector.Rect;
import cn.mapway.ui.client.mvc.Size;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.ogr.Geometry;
import org.gdal.osr.SpatialReference;
import org.nutz.lang.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cn.mapway.common.geo.tools.TiffTools.getMemoryDriver;
import static cn.mapway.common.geo.tools.TiffTools.getPngDriver;
import static cn.mapway.geo.shared.GeoConstant.SRID_WEB_MERCATO;

@Slf4j
public class NoCrsTileExtractor extends BaseTileExtractor implements ITileExtractor {

    GlobalMercator globalMercator = GlobalMercator.get();


    public boolean extractTileToTarget(ImageInfo imageInfo, long x, long y, int width, int height, Dataset sourceDataset, Dataset targetDataset) {
        // 高度 宽度
        Rect targetRect = new Rect(0, 0, 256, 256);

        Rect sourceRect = new Rect(x, y,  width, height) ;


        // 构造三个波段进行处理
        List<BandData> sourceBandList = new ArrayList<>(3);
        List<Band> targetBandList = new ArrayList<>(3);

        boolean singleBand=processBands(imageInfo, sourceDataset, targetDataset, sourceBandList, targetBandList);
        byte[] transparentBand = getBand(singleBand, new Size(256,256), targetRect, sourceRect, sourceBandList, targetBandList);
        if (targetDataset.getRasterCount() == 4) {
            targetDataset.GetRasterBand(4).WriteRaster(0, 0, 256, 256, transparentBand);
        }

        //写完了一个tile 直接输出到 .S文件中去吧
        targetDataset.FlushCache();
        return true;
    }

    @Override
    public boolean extractTileToTarget(ImageInfo imageInfo, long tileX, long tileY, int zoom, Dataset sourceDataset, Dataset targetDataset) {
        //瓦片宽度和高度 一般为256个像素
        int tileSize = globalMercator.tileSize;

        // 将 瓦片对应的WebMercator坐标范围 转化为影像参考系下的坐标范围 也就是参考系变换
        SpatialReference baseSr = new SpatialReference();
        baseSr.ImportFromEPSG(3857);

        SpatialReference sourceSr = new SpatialReference();
        if (imageInfo.getProjection() == null || imageInfo.getProjection().isEmpty()) {
            log.error("imageInfo.getProjection() is null");
            if(imageInfo.getSrid()==SRID_WEB_MERCATO)
            {
                sourceSr.ImportFromEPSG(3857);
            }
            else {
                return false;
            }
        }
        else {
            sourceSr.ImportFromWkt(imageInfo.getProjection());
        }

        //下面的算法 计算影像和瓦片相交的区域
        // targetRect 就是目标瓦片的像素空间
        // sourceRect 就是影像的像素空间
        // 计算方法如下
        // 基准坐标使用 3857 参考系
        // 1.将影像的像素空间转换为 3857 参考系  (0,0,width,height)->(mercatorXmin,mercatorYmin,mercatorXmax,mercatorYmax)
        // 2.将 tileExtend 转换为 3857 参考系  (tileX,tileY,zoom)->(mercatorXmin,mercatorYmin,mercatorXmax,mercatorYmax)
        // 3.计算两个矩形的交集
        // 4.将交集转换为 影像的像素空间 和 瓦片的像素空间
        // 5.             sourceRect      targetRect


        // 1.栅格坐标转化为 影像参考系下的坐标 sourceSize 利用影像中的仿射变换6个参数
        Point leftBottom = new Point(0, imageInfo.getHeight());
        leftBottom = rasterSpaceToImageSpace(imageInfo.getGeoTransform(), leftBottom);
        Point topRight = new Point(imageInfo.getWidth(), 0);
        topRight = rasterSpaceToImageSpace(imageInfo.getGeoTransform(), topRight);
        Box sourceSize = new Box(leftBottom.x, leftBottom.y, topRight.x, topRight.y);
        // 2. 将 sourceSize转化为3857基准参考坐标
        Geometry pt0 = TiffTools.toGeometry(sourceSize, sourceSr);
        Geometry rasterInBase = TiffTools.toTargetGeometry(pt0, baseSr);
        // 3. tile范围转化为3857下的坐标
        Box tileInBaseSr = globalMercator.tileBoundsWebMercator(tileX, tileY, zoom);
        Geometry tileInBase = TiffTools.toGeometry(tileInBaseSr, baseSr);

        // 4.查看 rasterInBaseSr 和 tileInBaseSr 是否有交集
        Box crossBox;

        if (tileInBase.Contains(rasterInBase)) {
            //瓦片包含影像全部
            crossBox = TiffTools.fromGeometry(rasterInBase);
        } else if (rasterInBase.Contains(tileInBase)) {
            //影像包含了瓦片 这个应该是多数情况
            crossBox = TiffTools.fromGeometry(tileInBase);
        } else if (rasterInBase.Intersect(tileInBase)) {
            //相交
            Geometry intersection = rasterInBase.Intersection(tileInBase);
            crossBox = TiffTools.fromGeometry(intersection);
        } else {
            //啥也不是
            // log.info("no cross");
            return false;
        }


        //5 计算 rasterInBaseSr 和 tileInBaseSr的交集 单位是 m

        //6.计算交集 在tile下的范围 targetRect
        Rect targetRect = crossBoxInTileRect(crossBox, tileX, tileY, zoom);
        //7 计算交集在 raster下的范围 sourceRect
        Rect sourceRect = crossBoxInRasterRect(crossBox, imageInfo, baseSr, sourceSr);


        //准备波段数据
        //如果原始波段数 大于3  只取前三个波段进行输出
        //如果原始波段数 小于3  用最后一个波段填充其余的波段
        // 构造三个波段进行处理
        List<BandData> sourceBandList = new ArrayList<>(3);
        List<Band> targetBandList = new ArrayList<>(3);

        boolean singleBand=processBands(imageInfo, sourceDataset, targetDataset, sourceBandList, targetBandList);
        byte[] transparentBand = getBand(singleBand, new Size(256,256), targetRect, sourceRect, sourceBandList, targetBandList);
        if (targetDataset.getRasterCount() == 4) {
            targetDataset.GetRasterBand(4).WriteRaster(0, 0, 256, 256, transparentBand);
        }

        //写完了一个tile 直接输出到 .S文件中去吧
        targetDataset.FlushCache();
        return true;
    }

    /**
     * 将瓦片的像素空间转换为影像的栅格空间
     *
     * @param crossBox
     * @param imageInfo
     * @return
     */
    private Rect crossBoxInRasterRect(Box crossBox, ImageInfo imageInfo, SpatialReference baseSr, SpatialReference sourceSr) {
        // 1. 将 3857基准参考坐标 转化 sourceSize
        Geometry crossGeometry = TiffTools.toGeometry(crossBox, baseSr);
        Geometry sourceGeometry = TiffTools.toTargetGeometry(crossGeometry, sourceSr);
        Box crossInSource = TiffTools.fromGeometry(sourceGeometry);


        Point leftBottom = new Point(crossInSource.xmin, crossInSource.ymin);
        leftBottom = imageSpaceToSourceSpace(imageInfo.getGeoTransform(), leftBottom);
        Point topRight = new Point(crossInSource.xmax, crossInSource.ymax);
        topRight = imageSpaceToSourceSpace(imageInfo.getGeoTransform(), topRight);
        if (leftBottom.x < 0) {
            leftBottom.x = 0;
        }
        if (leftBottom.y > imageInfo.getHeight()) {
            leftBottom.y = imageInfo.getHeight();
        }
        if (topRight.x > imageInfo.getWidth()) {
            topRight.x = imageInfo.getWidth();
        }
        if (topRight.y < 0) {
            topRight.y = 0;
        }

        Rect sourceRect = new Rect((int) Math.floor(leftBottom.x), (int) Math.floor(topRight.y),
                (int) Math.ceil(topRight.x - leftBottom.x),
                (int) Math.ceil(leftBottom.y - topRight.y));


        return sourceRect;
    }

    /**
     * 计算交集在tile下的范围
     *
     * @param crossBox
     * @param tileX
     * @param tileY
     * @param zoom
     * @return
     */
    private Rect crossBoxInTileRect(Box crossBox, long tileX, long tileY, int zoom) {
        //坐标都是 3857坐标 需要转化为 tile的像素坐标
        Point leftBottom = globalMercator.meterToTile(crossBox.xmin, crossBox.ymin, tileX, tileY, zoom);
        Point rightTop = globalMercator.meterToTile(crossBox.xmax, crossBox.ymax, tileX, tileY, zoom);
        return new Rect((int) Math.floor(leftBottom.x), (int) Math.floor(rightTop.y), (int) Math.ceil(rightTop.x - leftBottom.x), (int) Math.ceil(leftBottom.y - rightTop.y));
    }

    public static void main(String[] args) {
        GdalUtil.init();
        String url = "F:\\data\\personal\\1\\959\\舰船\\美国空军基地-基地1\\image\\label2.tif";
        Integer tileX = 7927;
        Integer tileY = 7927;
        Integer tileXEnd = 8183;
        Integer tileYEnd = 8183;
        TiffTools tiffTools = new TiffTools();
        Dataset dataset = gdal.Open(url, gdalconstConstants.GA_ReadOnly);
        ImageInfo info = TiffTools.extractImageInformation(dataset);

        Dataset memoryDataset = getMemoryDriver().Create("", 256, 256, 4, gdalconstConstants.GDT_Byte);
        ImageInfo imageInfo = createImageInfo(dataset);
        NoCrsTileExtractor extractor = new NoCrsTileExtractor();
        boolean b = extractor.extractTileToTarget(imageInfo, tileX, tileY, tileXEnd-tileX, tileYEnd-tileY, dataset, memoryDataset);
        if (b) {
            memoryDataset.FlushCache();

            File temp = tiffTools.tempFile();
            String targetPngFileName = temp.getAbsolutePath();
            Dataset targetDataset = getPngDriver().CreateCopy(targetPngFileName, memoryDataset);
            targetDataset.FlushCache();
           // targetDataset.Close();

            //  log.info("extract Tile {} ({} {} {})  用时{}毫秒", imageInfo.location, tileX, tileY, zoom, stopwatch.getDuration());
            byte[] data = Files.readBytes(targetPngFileName);
            temp.delete();
            Files.write("F:\\data\\temp\\test.png", data);
//            return data;
        } else {
            //  log.error("extract Tile error {} ({} {} {})  用时{}毫秒", imageInfo.location, tileX, tileY, zoom, stopwatch.getDuration());
//            temp.delete();
//            return null;
        }
    }

    private static ImageInfo createImageInfo(Dataset dataset){
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.width = dataset.getRasterXSize();
        imageInfo.height = dataset.getRasterYSize();
        imageInfo.setBands(dataset.getRasterCount());

        for (int i = 0; i < dataset.GetRasterCount(); i++) {
            Band band = dataset.GetRasterBand(i + 1);
            BandInfo bandInfo = new BandInfo();
            bandInfo.setIndex(i);
            bandInfo.setDataType(band.GetRasterDataType());
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
                Double[] noValue1 = new Double[1];
                noValue1[0] = 0.0;
                bandInfo.setNoValues(noValue1);
            } else {
                Double[] noValue1 = new Double[count];
                System.arraycopy(noValue, 0, noValue1, 0, count);
                bandInfo.setNoValues(noValue1);
            }
            imageInfo.getBandInfos().add(bandInfo);
        }

        //处理band对应的RGB颜色
        if (imageInfo.getBandInfos().size() == 1) {
            imageInfo.setChanelData(new ChanelData(1, 1, 1));
            //单波段影像 处理 空值问题 如果 保证 0作为空值处理
            Double[] noValue = imageInfo.getBandInfos().get(0).getNoValues();
            boolean found = false;
            for (int i = 0; i < noValue.length; i++) {
                if (Math.abs(noValue[i]) < 0.001) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Double[] noValue1 = new Double[noValue.length + 1];
                noValue1[0] = 0.0;
                System.arraycopy(noValue, 0, noValue1, 1, noValue1.length - 1);
                imageInfo.getBandInfos().get(0).setNoValues(noValue1);
            }
        } else if (imageInfo.getBandInfos().size() == 2) {
            imageInfo.setChanelData(new ChanelData(1, 2, 1));
        }
        if (imageInfo.getBandInfos().size() == 3) {
            imageInfo.setChanelData(new ChanelData());
        } else if (imageInfo.getBandInfos().size() == 4) {
            imageInfo.setChanelData(new ChanelData(3, 2, 1));
        }
        if (imageInfo.getBandInfos().size() > 4) {
            imageInfo.setChanelData(new ChanelData(4, 3, 2));
        }


        List<BandInfo> bandInfos = imageInfo.getBandInfos();
        boolean enableGamma = bandInfos.size() < 3;
        for (BandInfo bandInfo: bandInfos){
            bandInfo.enableGamma = enableGamma;
        }

        return imageInfo;
    }

}
