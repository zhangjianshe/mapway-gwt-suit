package cn.mapway.geo.shared.color;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static cn.mapway.geo.shared.color.ColorTableType.COLOR_TYPE_SINGLE_BAND_PSEUDO_COLOR;


/**
 * 颜色表操作
 * 1. 单波段伪彩色
 */
@Data
public class ColorTable implements Serializable, IsSerializable {
    private static int defaultColor = 0xFFFFFF00;
    /**
     * 颜色表类型 ColorTableType
     */
    Integer colorTableType;

    /**
     * 值 映射到颜色的 方法 唯一值映射  范围映射
     */
    Integer colorMapType;

    /**
     * 改颜色表是否为归一化颜色表
     */
    Boolean normalize;

    List<ColorMap> colorMaps;


    /**
     * 缺省构造一个  单波段为彩色 单一值的颜色表
     */
    public ColorTable() {
        normalize = false;
        colorTableType = COLOR_TYPE_SINGLE_BAND_PSEUDO_COLOR.code;
        colorMapType = ColorMapType.COLOR_MAP_TYPE_UNIQUE.code;
        colorMaps = new ArrayList<>();
    }

    public void add(ColorMap colorMap) {
        colorMaps.add(colorMap);
    }

    public void clear() {
        colorMaps.clear();
    }

    /**
     * 将 double 映射到某个颜色
     *
     * @param value
     * @return
     */
    public int mapColor(double value) {
        if (colorMaps == null) {
            return defaultColor;
        }

        //单波段伪彩色
        if (colorTableType.equals(COLOR_TYPE_SINGLE_BAND_PSEUDO_COLOR.code)) {
            ColorMapType mapType = ColorMapType.valueOfCode(colorMapType);
            switch (mapType) {
                case COLOR_MAP_TYPE_RANGE:
                    //范围映射
                    for (ColorMap colorMap : colorMaps) {
                        if (value >= colorMap.start && value < colorMap.end) {
                            return colorMap.rgba;
                        }
                    }
                    return defaultColor;
                case COLOR_MAP_TYPE_UNIQUE:
                    for (ColorMap colorMap : colorMaps) {
                        if (Math.abs(value - colorMap.start) < 0.001) {
                            return colorMap.rgba;
                        }
                    }
                default:
                    return defaultColor;
            }
        }
        return defaultColor;
    }

}
