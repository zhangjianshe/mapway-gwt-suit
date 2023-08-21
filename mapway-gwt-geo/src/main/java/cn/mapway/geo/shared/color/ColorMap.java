package cn.mapway.geo.shared.color;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * 颜色映射表
 */
public class ColorMap implements Serializable, IsSerializable {
    double start;
    double end;
    String name;
    byte[] rgba;
}
