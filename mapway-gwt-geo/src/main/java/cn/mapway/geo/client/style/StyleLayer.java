package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * StyleLayer
 * 描述样式表中的一个层
 *
 * @author zhang
 */
@Data
public class StyleLayer implements Serializable, IsSerializable {

    /**
     * 简单样式
     */
    public final static int STYLE_TYPE_SIMPLE = 0;

    /**
     * 样式类型
     */
    int type = STYLE_TYPE_SIMPLE;
    /**
     * 边框的样式
     */
    BorderStyle borderStyle;
    /**
     * 填充样式
     */
    FillStyle fillStyle;

    public StyleLayer() {
        borderStyle = new BorderStyle();
        fillStyle = new FillStyle();
    }
}
