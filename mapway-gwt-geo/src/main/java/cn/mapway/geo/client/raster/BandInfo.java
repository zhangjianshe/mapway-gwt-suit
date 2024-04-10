package cn.mapway.geo.client.raster;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * BandInfo
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */


@Data
public class BandInfo implements Serializable, IsSerializable {

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

    public double getValueExtend() {
        return maxValue - minValue;
    }

    public double getCalValueExtend() {
        return calMaxValue - calMinValue;
    }
}
