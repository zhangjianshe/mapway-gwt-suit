package cn.mapway.common.geo.tools;

import cn.mapway.geo.client.raster.*;
import cn.mapway.geo.shared.color.ColorTable;
import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.nutz.json.Json;
import org.nutz.lang.Files;

import java.util.HashMap;
import java.util.List;

public class ReadTileTest {

    private static Driver driverMemory;

    private static Driver driverPng;

    public static void main(String[] args) {
        gdal.AllRegister();
        String path = "F:\\data\\16bit_image\\GF1_PMS1_E80.9_N38.3_20220207_L1A0006276500-PAN1.tiff";
        String outPath = "F:\\data\\8.png";

        // 目标区域
        int startX = 0;
        int startY = 0;
        int enxX = 512;
        int endY = 400;
        // 先实验一个小的区域
        Dataset dataset = gdal.Open(path);
//        ImageInfo imageInfo1 = TiffTools.extractImageInformation(dataset);
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
        for (BandInfo bandInfo: bandInfos){
            bandInfo.enableGamma = true;
        }

        // 输出图像大小
        int width = Math.abs(enxX - startX);
        int height = Math.abs(endY - startY);

        // 找到最长边
        int maxSide = Math.max(width, height);
        int round = (int) Math.round(maxSide * 1.3);
        if(round > imageInfo.width || round > imageInfo.height){
            round = (int) Math.min(imageInfo.width, imageInfo.height);
        }
        int tileX = startX - (round - width) / 2;
        int tileY = startY - (round - height) / 2;

        // 若tileX或tileY小于0，则将其置为0
        tileX = Math.max(0, tileX);
        tileY = Math.max(0, tileY);
        int tileXEnd = tileX + round;
        int tileYEnd = tileY + round;

        if(tileXEnd > imageInfo.width){
            tileXEnd = (int) imageInfo.width;
            tileX = tileXEnd - round;
        }
        if(tileYEnd > imageInfo.height){
            tileYEnd = (int) imageInfo.height;
            tileY = tileYEnd - round;
        }

        // 若tileXEnd或tileYEnd大于imageInfo.width或imageInfo.height，则将其置为imageInfo.width或imageInfo.height
        tileXEnd = Math.min(tileXEnd, (int) imageInfo.width);
        tileYEnd = Math.min(tileYEnd, (int) imageInfo.height);



        // 根据目标区域计算出一个正方型, 这个正方形要不能超过图像的大小
        Dataset memoryDataset = getMemoryDriver().Create("", 256, 256, 4, gdalconstConstants.GDT_Byte);
        NoCrsTileExtractor extractor = new NoCrsTileExtractor();
        boolean b = extractor.extractTileToTarget(imageInfo, tileX, tileY, tileXEnd-tileX, tileYEnd-tileY, dataset, memoryDataset);

        memoryDataset.FlushCache();
        Dataset targetDataset = getPngDriver().CreateCopy(outPath, memoryDataset);
        targetDataset.FlushCache();
        targetDataset.Close();

        //  log.info("extract Tile {} ({} {} {})  用时{}毫秒", imageInfo.location, tileX, tileY, zoom, stopwatch.getDuration());
        byte[] data = Files.readBytes(outPath);

    }


    public static synchronized Driver getMemoryDriver() {
        if (driverMemory == null) {
            driverMemory = gdal.GetDriverByName("MEM");
        }
        return driverMemory;
    }

    public static synchronized Driver getPngDriver() {
        if (driverPng == null) {
            driverPng = gdal.GetDriverByName("PNG");
        }
        return driverPng;
    }
}
