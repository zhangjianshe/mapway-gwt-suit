package cn.mapway.common.geo.stretch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.mapway.common.geo.stretch.Histogram.DEFAULT_BINS;

public class LinearStretch {

    public static double[] getMinMax(double[] data, Double[] noValues, Double minPct, Double maxPct, List<Histogram> out){
        Set<Double> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue);
            }
        }
        double[] minMax = new double[2];
        minMax[0] = Double.MAX_VALUE;
        minMax[1] = Double.MIN_VALUE;
        for(double d: data){
            if(!noValueSet.contains(d)){
                if(d < minMax[0]){
                    minMax[0] = d;
                }
                if(d > minMax[1]){
                    minMax[1] = d;
                }
            }
        }
        if(minPct == null && maxPct == null){
            return minMax;
        }

        if(minPct == null || minPct < 0.0 || minPct > 1.0){
            minPct = 0.0;
        } else if(maxPct == null || maxPct < 0.0 || maxPct > 1.0){
            maxPct = 1.0;
        }
        Histogram histogram = HistogramStretch.getHistogram(data, noValues, minMax[0], minMax[1], minPct, maxPct, DEFAULT_BINS);
        minMax[0] = histogram.getHistogramNodeByMinPct(minPct);
        minMax[1] = histogram.getHistogramNodeByMaxPct(maxPct);
        if(out != null){
            out.add(histogram);
        }
        return minMax;
    }

    public static int[] getMinMax(int[] data, Double[] noValues, Double minPct, Double maxPct, List<Histogram> out){
        Set<Integer> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue.intValue());
            }
        }

        int[] minMax = new int[2];
        minMax[0] = Integer.MAX_VALUE;
        minMax[1] = Integer.MIN_VALUE;
        for(int d: data){
            if(!noValueSet.contains(d)){
                if(d < minMax[0]){
                    minMax[0] = d;
                }
                if(d > minMax[1]){
                    minMax[1] = d;
                }
            }
        }
        if(minPct == null && maxPct == null){
            return minMax;
        } else if(minPct == null || minPct < 0.0 || minPct > 1.0){
            minPct = 0.0;
        } else if(maxPct == null || maxPct < 0.0 || maxPct > 1.0){
            maxPct = 1.0;
        }
        Histogram histogram = HistogramStretch.getHistogram(data, noValues, (double)minMax[0], (double)minMax[1], minPct, maxPct, DEFAULT_BINS);
        minMax[0] = (int)histogram.getHistogramNodeByMinPct(minPct);
        minMax[1] = (int)histogram.getHistogramNodeByMaxPct(maxPct);
        if(out != null){
            out.add(histogram);
        }
        return minMax;
    }

    /**
     * linear stretch byte [ ].
     * @param data      数据
     * @param noValues    无效值
     * @param min       最小值
     * @param max       最大值
     * @param minPct    最小百分比
     * @param maxPct    最大百分比
     * @return
     */
    public static byte[] linearStretch(double[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, List<Histogram> out){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        Set<Double> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue);
            }
        }
        if(minPct != null || maxPct != null){
            double[] minMax = getMinMax(data, noValues, minPct, maxPct, out);
            min = minMax[0];
            max = minMax[1];
        }
        byte[] result = new byte[data.length];
        for(int i = 0; i < data.length; i++){
            double d = data[i];
            if(noValueSet.contains(d)){
                result[i] = 0;
            } else {
                double linearPixel = linear(data[i], min, max);
                byte v=(byte)((int)linearPixel & 0xFF);
                result[i] = v;
            }
        }
        return result;
    }

    public static byte[] linearStretch(float[] data, Double[] noData, Double min, Double max, Double minPct,
                                       Double maxPct, List<Histogram> out){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        double[] doubleData = new double[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return linearStretch(doubleData, noData, min, max, minPct, maxPct, out);
    }

    public static byte[] linearStretch(int[] data, Double[] noValues, Double min, Double max, Double minPct,
                                       Double maxPct, List<Histogram> out){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        Set<Integer> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue.intValue());
            }
        }

        // 将float数组转换为double数组
        if(minPct != null || maxPct != null){
            int[] minMax = getMinMax(data, noValues, minPct, maxPct, out);
            min = (double) minMax[0];
            max = (double) minMax[1];
        }



        byte[] result = new byte[data.length];
        for(int i = 0; i < data.length; i++){
            int d = data[i];
            if(noValueSet.contains(d)){
                result[i] = 0;
            } else {
                double linearPixel = linear(d, min, max);
                byte v=(byte)((int)linearPixel & 0xFF);
                result[i] = v;
            }
        }
        return result;
    }


    public static byte[] linearStretch(short[] data, Double[] noData, Double min, Double max, Double minPct, Double maxPct){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        int[] doubleData = new int[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return linearStretch(doubleData, noData, min, max, minPct, maxPct, null);
    }

    public static byte[] linearStretch(byte[] data, Double[] noData, Double min, Double max, Double minPct, Double maxPct){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        int[] doubleData = new int[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return linearStretch(doubleData, noData, min, max, minPct, maxPct, null);
    }

    private static double linear(double pixel, Double minValue, Double maxValue) {
        if (Math.abs(maxValue - minValue) < 0.0000001) {
            return 1 * 255;
        } else {
            return ((pixel - minValue) / (maxValue - minValue)) * 255;
        }
    }

}
