package cn.mapway.geo.shared.color;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * 颜色映射表
 */
@Data
public class ColorMap implements Serializable, IsSerializable {
    double start;
    double end;
    String name;
    /**
     * color is 0xRRGGBBAA
     */
    int rgba;

    public ColorMap uniqueMap(double value, int rgba) {
        start = value;
        this.rgba = rgba;
        return this;
    }

    public ColorMap rangeMap(double start, double end, int rgba) {
        this.start = start;
        this.end = end;
        this.rgba = rgba;
        return this;
    }

}
