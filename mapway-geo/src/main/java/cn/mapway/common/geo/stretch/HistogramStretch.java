package cn.mapway.common.geo.stretch;

import java.util.HashSet;
import java.util.Set;

public class HistogramStretch {

    public static Histogram getHistogram(double[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, int bins){
        if(data == null || data.length == 0){
            return null;
        }
        Set<Double> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue);
            }
        }
        if(min == null || max == null){
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;
            for(double d: data){
                if(!noValueSet.contains(d)){
                    if(d < min){
                        min = d;
                    }
                    if(d > max){
                        max = d;
                    }
                }
            }
        }
        Histogram histogram = new Histogram(bins, min, max);
        for(double d: data){
            if(!noValueSet.contains(d)){
                histogram.add(d);
            }
        }
        histogram.calculatePct(data.length);
        return histogram;
    }

    public static Histogram getHistogram(int[] data, Double[] noValues, Double min, Double max, Double minPct, Double maxPct, int bins){
        if(data == null || data.length == 0){
            return null;
        }
        Set<Double> noValueSet = new HashSet<>();
        if(noValues != null){
            for(Double noValue: noValues){
                noValueSet.add(noValue);
            }
        }
        if(min == null || max == null){
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;
            for(double d: data){
                if(!noValueSet.contains(d)){
                    if(d < min){
                        min = d;
                    }
                    if(d > max){
                        max =  d;
                    }
                }
            }
        }
        Histogram histogram = new Histogram(bins, min, max);
        for(double d: data){
            if(!noValueSet.contains(d)){
                histogram.add(d);
            }
        }
        histogram.calculatePct(data.length);
        return histogram;
    }

}
