package cn.mapway.geo.client.style;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;
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
@JsType
public class MapStyle implements Serializable, IsSerializable {
    public final static int STYLE_TYPE_SIMPLE = 0;
    public final static int STYLE_TYPE_RULE = 1;
    /**
     * 样式规则类型 缺省是简单样式
     */
    public int styleType ;
    /**
     * 描述样式图层
     */
    public  StyleLayer[] styles;

}
