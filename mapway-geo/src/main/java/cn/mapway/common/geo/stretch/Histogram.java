package cn.mapway.common.geo.stretch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Histogram {

    public static final int DEFAULT_BINS = 256;
    List<HistogramNode> nodes = null;
    int bins;
    double min;
    double max;
    double step;

    Set<Double> noValueSet;

    public Histogram(int bins, double min, double max, Set<Double> noValueSet){
        this.bins = bins;
        this.min = min;
        this.max = max;
        this.step = (max - min) / bins;
        nodes = new ArrayList<>(bins);
        double width = max - min;
        double step = width / bins;
        this.noValueSet = noValueSet;
        for(int i = 0; i < bins; i++){
            HistogramNode node = new HistogramNode();
            node.min = min + i * step;
            node.max = min + (i + 1) * step;
            node.brightness = (node.min + node.max) / 2;
            nodes.add(node);
        }
    }

    public void add(double value){
        double offset = value - min;
        int index = (int) (offset / step);
        if(index > bins - 1){
            index = bins - 1;
        }
        if(index >= 0 && index < bins){
            HistogramNode node = nodes.get(index);
            node.count++;
        }
    }

    public void calculatePct(int total){
        for(HistogramNode node: nodes){
            if(node.count == 0){
                node.pct = 0;
            } else {
                node.pct = (node.count * 1.0) / total;
            }
        }
    }

    public void calculateAccumulation(){
        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            HistogramNode node = nodes.get(i);
            if(i == 0){
                node.accumulation = node.count;
            } else {
                node.accumulation = nodes.get(i - 1).accumulation + node.count;
            }
        }
    }

    public double getHistogramNodeByMinPct(double minPct){
        double sum = 0;
        // 正向遍历
        for(HistogramNode node: nodes){
            sum += node.pct;
            if(sum >= minPct){
                return  node.min;
            }
        }
        return 0;
    }

    public double getHistogramNodeByMaxPct(double maxPct){
        double sum = 0;
        // 逆向遍历
        maxPct = 1.0 - maxPct;
        int size = nodes.size();
        for(int i = size - 1; i >= 0; i--){
            HistogramNode node = nodes.get(i);
            sum += node.pct;
            if(sum >= maxPct){
                return node.max;
            }
        }
        return 0;
    }

    public void pixel(double totalPixels) {
        for (HistogramNode node : nodes) {
            double pixel = node.accumulation * 255 / totalPixels;
            // pixel 转换为byte
            byte v = (byte)((int)pixel & 0xFF);
//            byte v1 = (byte) pixel;
            node.pixel = v;
        }
    }

    public byte getEqualizedPixel(double value) {
        if(noValueSet != null && noValueSet.contains(value)){
            return 0;
        }
        double offset = value - min;
        int index = (int) (offset / step);
        if(index > bins - 1){
            index = bins - 1;
        }
        if(index >= 0 && index < bins){
            return nodes.get(index).pixel;
        }
        return 0;
    }

    public byte getEqualizedPixel(int value) {
        if(noValueSet != null && noValueSet.contains((double)value)){
            return 0;
        }
        double offset = value - min;
        int index = (int) (offset / step);
        if(index > bins - 1){
            index = bins - 1;
        }
        if(index >= 0 && index < bins){
            return nodes.get(index).pixel;
        }
        return 0;
    }
}
