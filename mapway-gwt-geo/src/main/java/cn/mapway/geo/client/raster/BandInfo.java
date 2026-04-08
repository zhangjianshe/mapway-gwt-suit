package cn.mapway.geo.client.raster;

import cn.mapway.geo.shared.color.ColorMap;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * BandInfo
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */


@Data
public class BandInfo implements Serializable, IsSerializable {

    /**
     * special start from 0,1,2...
     */
    public int index;
    public int dataType;
    public Double maxValue;
    public Double minValue;
    public Double calMinValue;
    public Double calMaxValue;

    /**
     * 是否进行 Gamma矫正
     * default is false
     */
    public boolean enableGamma = false;

    /**
     * Gamma矫正
     */
    public Double gammaMin;
    public Double gamma;
    public Double gammaMax;

    /**
     * 输出 最小值 拉伸的目标 [0-255]
     */
    public int outputMin = 0;
    /**
     * 输出 最大值 拉伸的目标 [0-255]
     */
    public int outputMax = 255;


    public Double[] noValues;

    public Map<String, String> metadata;

    /**
     * 影像中的颜色表 IndexRGB
     */
    public List<ColorMap> colorMaps;
    /**
     * 金字塔信息
     */
    public String[] overviews;

    /**
     * 波段名称
     * 比如 映像数据 映像分类
     */
    public String name;

    /**
     * 像素处理分为２个步骤
     * 1. 检查是不是需要拉伸
     * 　　byte uint8 有没有设定
     * 　　其他类型　必须拉伸
     * 2.是否需要Gamma 矫正取决与用户设定
     *
     * @param v
     * @return
     */
    public double calValue(boolean mustLashen, double v) {
        double v1=v;
        if (mustLashen || outputMin>0 || outputMax<255) {
            double min = this.getCalMinValue(); // 即 Mean - 2*StdDev
            double max = this.getCalMaxValue(); // 即 Mean + 2*StdDev
            double outputExtend=outputMax-outputMin;
            // 2. 执行线性拉伸计算
            double result =outputMin+ ((v - min) / (max - min)) * outputExtend;

            // 3. 边界检查 (Clamping)
            if (result < outputMin) result = outputMin;
            if (result > outputMax) result = outputMax;

            v1 = result;
        }
        if(enableGamma)
        {
            return Math.pow((v1 - gammaMin) / (gammaMax - gammaMin), gamma);
        }
        else {
            return v1;
        }
    }

    public void check() {
        if (gammaMax == null) {
            gammaMax = maxValue;
        }
        if (gammaMin == null) {
            gammaMin = minValue;
        }
        if (gamma == null) {
            gamma = 1.0;
        }
    }

    public double getValueExtend() {
        return maxValue - minValue;
    }

    public double getCalValueExtend() {
        return calMaxValue - calMinValue;
    }
}
