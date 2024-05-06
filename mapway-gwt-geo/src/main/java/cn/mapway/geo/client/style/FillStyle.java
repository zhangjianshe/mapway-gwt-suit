package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;

import java.io.Serializable;

/**
 * FillStyle
 * 填充样式
 *
 * @author zhang
 */
@JsType
public class FillStyle implements Serializable, IsSerializable {
    public final static int FILL_TYPE_COLOR = 0;
    public final static int FILL_TYPE_PATTERN = 1;
    /**
     * 填充类型
     */
    public int type =0;
    /**
     * 填充颜色
     */
    public String color;

    /**
     * 填充模板
     */
    public String pattern;

    /**
     * 填充的透明度
     */
    public double opacity;
}
