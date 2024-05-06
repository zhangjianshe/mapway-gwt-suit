package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;

import java.io.Serializable;

/**
 * BorderStyle
 * 边框样式
 *
 * @author zhang
 */
@JsType
public class BorderStyle implements Serializable, IsSerializable {
    public final static Integer BORDER_TYPE_SOLID = 0;
    /**
     * 边框的颜色
     */
    public String color;

    /**
     * 边框宽度
     */
    public int width;

    /**
     * 边框类型
     */
    public int type;

    /**
     * 边框的模式
     */
    public String pattern;

    /**
     * 边框透明度 0-1
     * 0 不透明
     */
    public double opacity;
}
