package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * SymbolStyle
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Data
public class SymbolStyle implements Serializable, IsSerializable {
    /**
     * 填充颜色
     */
    String fillColor;
    /**
     * 是否填充
     */
    Boolean fill;
    /**
     * 填充透明度
     */
    Float fillOpacity;

    /**
     * 是否显示边框
     */
    Boolean stroke;
    /**
     * 边框颜色
     */
    String color;
    /**
     * 边框宽度
     */
    Float width;
    /**
     * 边框的透明度
     */
    Float opacity;

    public SymbolStyle() {
        stroke = true;
        opacity = 1.0f;
        width = 1.0f;
        color = "#ffff00";

        fill = false;
        fillColor = "#ff0000";
        fillOpacity = 0.5f;
    }
}
