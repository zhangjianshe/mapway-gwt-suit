package cn.mapway.common.geo.stretch;

import cn.mapway.common.geo.gdal.GdalUtil;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.gdal.gdalconst.gdalconstConstants.GDT_Byte;

public class HistogramStretch {

    public static void main(String[] args) {
        File srcFile = new File("F:\\data\\cis\\labels\\sample_145\\image\\15623.tif");
        File destFile = new File("F:\\data\\temp\\2.tif");
        System.setProperty("org.geotools.referencing.forceXY", "true");

        GdalUtil.init();
        Dataset srcDataset = gdal.Open(srcFile.getAbsolutePath(), gdalconst.GA_ReadOnly);
        if (srcDataset == null) {
            System.out.println("打开文件失败");
            return;
        }
        // 创建新的图像
        BufferedImage destImage = new BufferedImage(srcDataset.getRasterXSize(), srcDataset.getRasterYSize(), BufferedImage.TYPE_3BYTE_BGR);
        int r = 1, g = 2, b = 3;
        HistogramStretch histogramStretch = new HistogramStretch();
        byte[] stretchR = histogramStretch.stretch(srcDataset, r, 0.0, 1.0, 256);
        byte[] stretchG = histogramStretch.stretch(srcDataset, g, 0.0, 1.0, 256);
        byte[] stretchB = histogramStretch.stretch(srcDataset, b, 0.0, 1.0, 256);
        for (int y = 0; y < srcDataset.getRasterYSize(); y++) {
            for (int x = 0; x < srcDataset.getRasterXSize(); x++) {
                int index = y * srcDataset.getRasterXSize() + x;
                int rgb = 0xFF000000 | ((stretchR[index] & 0xFF) << 16) | ((stretchG[index] & 0xFF) << 8) | (stretchB[index] & 0xFF);
                destImage.setRGB(x, y, rgb);
            }
        }
        try {
            ImageIO.write(destImage, "jpg", destFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public byte[] stretch(Dataset srcDataset, int bandIndex, double minPct, double maxPct, int bins) {
        Band band = srcDataset.GetRasterBand(bandIndex);
        int sizeX = band.getXSize();
        int sizeY = band.getYSize();


        int dt = band.getDataType();
        if (dt == GDT_Byte || dt == gdalconstConstants.GDT_Int16 || dt == gdalconstConstants.GDT_UInt16
                || dt == gdalconstConstants.GDT_UInt32 || dt == gdalconstConstants.GDT_Int32) {
            int[] bufR = new int[sizeX * sizeY];
            band.ReadRaster(0, 0, sizeX, sizeY, bufR);
            Double[] noValues = new Double[10];
            band.GetNoDataValue(noValues);
            return histogramStretch(bufR, noValues, null, null, minPct, maxPct, bins);
        } else {
            double[] bufR = new double[sizeX * sizeY];
            Double[] noValues = new Double[10];
            band.GetNoDataValue(noValues);
            return histogramStretch(bufR, noValues, null, null, minPct, maxPct, bins);
        }
    }

    public static byte[] histogramStretch(double[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, Integer bins) {
        if(bins == null){
            bins = 256;
        }
        Histogram histogram = HistogramStretchUtils.getHistogram(data, noValues, min, max, minPct, maxPct, bins);
        double[] minMax = new double[2];
        minMax[0] = histogram.getHistogramNodeByMinPct(minPct);
        minMax[1] = histogram.getHistogramNodeByMaxPct(maxPct);
        // 计算累积直方图
        histogram.calculateAccumulation();
        // 计算直方图均衡化后的像素值
        double totalPixels = data.length * 1.0;
        histogram.pixel(totalPixels);
        // 创建新的均衡化图像
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            if(minMax[0] <= data[i] && data[i] <= minMax[1]){
                result[i] = histogram.getEqualizedPixel(data[i]);
            } else {
                result[i] = 0;
            }
        }
        return result;
    }

    public static byte[] histogramStretch(int[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, Integer bins) {
        if(bins == null){
            bins = 256;
        }
        Histogram histogram = HistogramStretchUtils.getHistogram(data, noValues, min, max, minPct, maxPct, bins);
        double[] minMax = new double[2];
        minMax[0] = histogram.getHistogramNodeByMinPct(minPct);
        minMax[1] = histogram.getHistogramNodeByMaxPct(maxPct);

        // 计算累积直方图
        histogram.calculateAccumulation();
        // 计算直方图均衡化后的像素值
        double totalPixels = data.length * 1.0;
        histogram.pixel(totalPixels);
        // 创建新的均衡化图像
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            if(minMax[0] <= data[i] && data[i] <= minMax[1]){
                result[i] = histogram.getEqualizedPixel(data[i]);
            } else {
                result[i] = 0;
            }
        }
        return result;
    }

}
