package cn.mapway.common.geo.stretch;

public class GammaStretch {


    /**
     * Gamma stretch byte [ ].
     * @param data      数据
     * @param noData    无效值
     * @param min       最小值
     * @param max       最大值
     * @param minPct    最小百分比
     * @param maxPct    最大百分比
     * @param gamma     伽马值
     * @return
     */
    public static byte[] gammaStretch(double[] data, Double noData, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        if(gamma == null){
            gamma = 0.7;
        }
        if(minPct == null || maxPct == null){
            double[] minMax = LinearStretch.getMinMax(data, noData, minPct, maxPct);
            min = minMax[0];
            max = minMax[1];
        }
        byte[] result = new byte[data.length];
        for(int i = 0; i < data.length; i++){
            if(data[i] == noData){
                result[i] = 0;
            } else {
                double linearPixel = clip(data[i], gamma, min, max);
                byte v=(byte)((byte)linearPixel & 0xFF);
                result[i] = v;
            }
        }
        return result;
    }

    public static byte[] gammaStretch(float[] data, Double noData, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        double[] doubleData = new double[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return gammaStretch(doubleData, noData, min, max, minPct, maxPct, gamma);
    }

    public static byte[] gammaStretch(int[] data, Integer noData, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        if(gamma == null){
            gamma = 0.7;
        }
        // 将float数组转换为double数组
        if(minPct == null || maxPct == null){
            int[] minMax = LinearStretch.getMinMax(data, noData, minPct, maxPct);
            min = (double) minMax[0];
            max = (double) minMax[1];
        }
        byte[] result = new byte[data.length];
        for(int i = 0; i < data.length; i++){
            if(data[i] == noData){
                result[i] = 0;
            } else {
                double linearPixel = clip(data[i], min, max, gamma);
                byte v=(byte)((byte)linearPixel & 0xFF);
                result[i] = v;
            }
        }
        return result;
    }


    public static byte[] gammaStretch(short[] data, Integer noData, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        int[] doubleData = new int[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return gammaStretch(doubleData, noData, min, max, minPct, maxPct, gamma);
    }

    public static byte[] gammaStretch(byte[] data, Integer noData, Double min, Double max, Double minPct, Double maxPct, Double gamma){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        int[] doubleData = new int[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return gammaStretch(doubleData, noData, min, max, minPct, maxPct, gamma);
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
        return value;
    }
}
