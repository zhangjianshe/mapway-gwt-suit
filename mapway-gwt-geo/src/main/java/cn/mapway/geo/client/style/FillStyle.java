package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * FillStyle
 * 填充样式
 *
 * @author zhang
 */
@Data
public class FillStyle implements Serializable, IsSerializable {
    public final static Integer FILL_TYPE_COLOR = 0;
    public final static Integer FILL_TYPE_PATTERN = 1;
    /**
     * 填充类型
     */
    Integer type = FILL_TYPE_COLOR;
    /**
     * 填充颜色
     */
    String color = "#00FF00";

    /**
     * 填充模板
     */
    String pattern = "";

    /**
     * 填充的透明度
     */
    double opacity = 1.0d;
}
