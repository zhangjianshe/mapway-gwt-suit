package cn.mapway.common.geo.stretch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LinearStretch {

    public static double[] getMinMax(double[] data, Double[] noValues, Double minPct, Double maxPct){
        Set<Double> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue);
            }
        }
        double[] minMax = new double[2];
        if(minPct == null && maxPct == null){
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
        } else if(minPct == null || minPct < 0.0 || minPct > 1.0){
            minPct = 0.0;
        } else if(maxPct == null || maxPct < 0.0 || maxPct > 1.0){
            maxPct = 1.0;
        }
        data = Arrays.copyOf(data, data.length);
        if(noValues != null && noValues.length != 0){
            data = Arrays.stream(data).filter(d -> !noValueSet.contains(d)).toArray();
        }
        Arrays.sort(data);
        int minIndex = (int) (data.length * minPct);
        int maxIndex = (int) (data.length * maxPct);
        minMax[0] = data[minIndex];
        minMax[1] = data[maxIndex];
        return minMax;
    }

    public static int[] getMinMax(int[] data, Double[] noValues, Double minPct, Double maxPct){
        Set<Integer> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue.intValue());
            }
        }

        int[] minMax = new int[2];
        if(minPct == null && maxPct == null){
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
        } else if(minPct == null || minPct < 0.0 || minPct > 1.0){
            minPct = 0.0;
        } else if(maxPct == null || maxPct < 0.0 || maxPct > 1.0){
            maxPct = 1.0;
        }
        data = Arrays.copyOf(data, data.length);
        if(noValues != null && noValues.length != 0){
            data = Arrays.stream(data).filter(d -> !noValueSet.contains(d)).toArray();
        }
        Arrays.sort(data);
        int minIndex = (int) (data.length * minPct);
        int maxIndex = (int) (data.length * maxPct);
        minMax[0] = data[minIndex];
        minMax[1] = data[maxIndex];
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
    public static byte[] linearStretch(double[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        Set<Double> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue);
            }
        }
        if(minPct == null || maxPct == null){
            double[] minMax = getMinMax(data, noValues, minPct, maxPct);
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
                byte v=(byte)((byte)linearPixel & 0xFF);
                result[i] = v;
            }
        }
        return result;
    }

    public static byte[] linearStretch(float[] data, Double[] noData, Double min, Double max, Double minPct, Double maxPct){
        if(data == null || data.length == 0){
            return new byte[0];
        }
        // 将float数组转换为double数组
        double[] doubleData = new double[data.length];
        for(int i = 0; i < data.length; i++){
            doubleData[i] = data[i];
        }
        return linearStretch(doubleData, noData, min, max, minPct, maxPct);
    }

    public static byte[] linearStretch(int[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct){
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
        if(minPct == null || maxPct == null){
            int[] minMax = getMinMax(data, noValues, minPct, maxPct);
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
                byte v=(byte)((byte)linearPixel & 0xFF);
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
        return linearStretch(doubleData, noData, min, max, minPct, maxPct);
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
        return linearStretch(doubleData, noData, min, max, minPct, maxPct);
    }

    private static double linear(double pixel, Double minValue, Double maxValue) {
        if (Math.abs(maxValue - minValue) < 0.0000001) {
            return 1;
        } else {
            return (pixel - minValue) / (maxValue - minValue);
        }
    }

}
