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

    public double calValue(double v) {
            if (Objects.equals(gammaMax, gammaMin)) {
                return v;
            }
            if (v < gammaMin) {
                v = gammaMin;
            }
            if (v > gammaMax) {
                v = gammaMax;
            }
            return outputMin + (outputMax - outputMin) * Math.pow((v - gammaMin) / (gammaMax - gammaMin), gamma);
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
