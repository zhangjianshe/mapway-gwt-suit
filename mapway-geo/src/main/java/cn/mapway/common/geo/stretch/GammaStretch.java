package cn.mapway.common.geo.stretch;

import java.util.*;

import static cn.mapway.common.geo.stretch.Histogram.DEFAULT_BINS;

public class GammaStretch {


    /**
     * Gamma stretch byte [ ].
     * @param data      数据
     * @param noValues  无效值
     * @param min       最小值
     * @param max       最大值
     * @param minPct    最小百分比
     * @param maxPct    最大百分比
     * @param gamma     伽马值
     * @return
     */
    public static byte[] gammaStretch(double[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        List<Histogram> histograms = new ArrayList<>();
        double[] minMax = LinearStretch.getMinMax(data, noValues, minPct, maxPct, histograms);
        min = minMax[0];
        max = minMax[1];
        if(gamma == null){
            if(histograms.isEmpty()){
                Histogram histogram = HistogramStretchUtils.getHistogram(data, noValues, minMax[0], minMax[1], minPct, maxPct, DEFAULT_BINS);
                histograms.add(histogram);
            }
            gamma = calculateGamma(histograms.get(0));
        }
        Set<Double> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue);
            }
        }
        byte[] result = new byte[data.length];
        for(int i = 0; i < data.length; i++){
            double d = data[i];
            if(noValueSet.contains(d)){
                result[i] = 0;
            } else {
                double linearPixel = clip(d, gamma, min, max);
                byte v=(byte)((int)linearPixel & 0xFF);
                result[i] = v;
            }
        }
        return result;
    }

    public static byte[] gammaStretch(float[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        double[] doubleData = new double[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return gammaStretch(doubleData, noValues, min, max, minPct, maxPct, gamma);
    }

    public static byte[] gammaStretch(int[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        Set<Integer> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue.intValue());
            }
        }

        List<Histogram> histograms = new ArrayList<>();
        // 将float数组转换为double数组

        int[] minMax = LinearStretch.getMinMax(data, noValues, minPct, maxPct, histograms);
        min = (double) minMax[0];
        max = (double) minMax[1];
        if(gamma == null){
            if(histograms.isEmpty()){
                Histogram histogram = HistogramStretchUtils.getHistogram(data, noValues, (double) minMax[0], (double) minMax[1], minPct, maxPct, DEFAULT_BINS);
                histograms.add(histogram);
            }
            gamma = calculateGamma(histograms.get(0));
        }

        byte[] result = new byte[data.length];
        for(int i = 0; i < data.length; i++){
            int d = data[i];
            if(noValueSet.contains(d)){
                result[i] = 0;
            } else {
                double linearPixel = clip(d, min, max, gamma);
                byte v=(byte)((int)linearPixel & 0xFF);
                result[i] = v;
            }
        }
        return result;
    }


    public static byte[] gammaStretch(short[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        int[] doubleData = new int[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return gammaStretch(doubleData, noValues, min, max, minPct, maxPct, gamma);
    }

    public static byte[] gammaStretch(byte[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        int[] doubleData = new int[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return gammaStretch(doubleData, noValues, min, max, minPct, maxPct, gamma);
    }

    /**
     * Gamma correction the pixel,by the way with linenarly extraction
     *
     * @param pixelValue
     * @param gammaMin
     * @param gammaMax
     * @param gamma      0.1-6
     * @return 0-1.0
     */
    private static double clip(double pixelValue, Double gammaMin, Double gammaMax, Double gamma) {
        if (pixelValue <= gammaMin) {
            pixelValue = gammaMin;
        }
        if (pixelValue >= gammaMax) {
            pixelValue = gammaMax;
        }
        double value = Math.pow((pixelValue - gammaMin) / (gammaMax - gammaMin), gamma);
        return value * 255;
    }

    private static double calculateGamma(Histogram histogram){
        // 计算平均亮度
        long totalBrightness = 0;
        long totalPixels = 0;
        int i = 0;
        for (HistogramNode node : histogram.nodes) {
            totalBrightness += i * node.count;
            totalPixels += node.count;
            i++;
        }
        double averageBrightness = totalBrightness / totalPixels;

        // 使用对数函数将平均亮度映射到伽马值
        double deviation = Math.log(averageBrightness) / Math.log((double) histogram.nodes.size() / 2);
        if(deviation == 0){
            deviation = 0.01;
        }
        return 1.0 / deviation;
    }
}
