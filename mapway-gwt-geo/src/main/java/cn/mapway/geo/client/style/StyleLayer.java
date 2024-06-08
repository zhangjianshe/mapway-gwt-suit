package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;

import java.io.Serializable;

/**
 * StyleLayer
 * 描述样式表中的一个层
 *
 * @author zhang
 */
@JsType
public class StyleLayer implements Serializable, IsSerializable {

    /**
     * 简单样式
     */
    public final static int STYLE_TYPE_SIMPLE = 0;
    static FillStyle FILL_STYLE;
    static BorderStyle BORDER_STYLE;
    /**
     * 样式类型
     */
    public int type = STYLE_TYPE_SIMPLE;
    /**
     * 边框的样式
     */
    public BorderStyle borderStyle;
    /**
     * 填充样式
     */
    public FillStyle fillStyle;
    public StyleLayer() {
        borderStyle = getDefaultBorderStyle();
        fillStyle = getDefaultFillStyle();
    }

    private synchronized FillStyle getDefaultFillStyle() {
        if (FILL_STYLE == null) {
            FILL_STYLE = new FillStyle();
            FILL_STYLE.opacity = 0.5;
            FILL_STYLE.color = "#0000FF";
            FILL_STYLE.type = 0;
            FILL_STYLE.pattern = "";
        }
        return FILL_STYLE;
    }

    private synchronized BorderStyle getDefaultBorderStyle() {
        if (BORDER_STYLE == null) {
            BORDER_STYLE = new BorderStyle();
            BORDER_STYLE.width = 1;
            BORDER_STYLE.opacity = 1;
            BORDER_STYLE.color = "#000000";
            BORDER_STYLE.type = 0;
            BORDER_STYLE.pattern = "";
        }
        return BORDER_STYLE;
    }
}
