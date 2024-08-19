package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * ChartItemStyle
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ChartItemStyle {
    public String color;
    public String shadowColor;
    public String borderType;
    public float opacity;
    public double borderRadius;
    public String borderColor;
    public double borderWidth;
    @JsOverlay
    public final ChartItemStyle set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
        return this;
    }
}
