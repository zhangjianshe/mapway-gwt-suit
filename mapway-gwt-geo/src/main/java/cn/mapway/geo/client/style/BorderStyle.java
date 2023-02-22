package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * BorderStyle
 * 边框样式
 *
 * @author zhang
 */
@Data
public class BorderStyle implements Serializable, IsSerializable {
    public final static Integer BORDER_TYPE_SOLID = 0;
    /**
     * 边框的颜色
     */
    String color = "#ff0000";

    /**
     * 边框宽度
     */
    Integer width = 2;

    /**
     * 边框类型
     */
    Integer type = BORDER_TYPE_SOLID;

    /**
     * 边框的模式
     */
    String pattern = "";

    /**
     * 边框透明度 0-1
     * 0 不透明
     */
    double opacity = 0.4d;
}
