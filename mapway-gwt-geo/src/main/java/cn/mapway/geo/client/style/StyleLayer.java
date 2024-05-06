package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;
import lombok.Data;

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
        borderStyle = new BorderStyle();
        fillStyle = new FillStyle();
    }
}
