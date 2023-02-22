package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * MapStyle
 * 描述地图的样式
 *
 * @author zhang
 */
@Data
public class MapStyle implements Serializable, IsSerializable {
    public final static Integer STYLE_TYPE_SIMPLE = 0;
    public final static Integer STYLE_TYPE_RULE = 1;
    /**
     * 样式规则类型 缺省是简单样式
     */
    Integer styleType = STYLE_TYPE_SIMPLE;
    /**
     * 描述样式图层
     */
    List<StyleLayer> styles;

    public MapStyle() {
        styles = new ArrayList<>();
    }

}
