package cn.mapway.geo.client.raster;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

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
    public Double[] noValues;

    public double getValueExtend() {
        return maxValue - minValue;
    }

    public double getCalValueExtend() {
        return calMaxValue - calMinValue;
    }
}
