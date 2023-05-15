package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * LineStyle
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class LineStyle {
    public String color;
    public float width;
    public String type;
    public float dashOffset;
    public String cap;
    public String join;
    public float miterLimit;
    public float opacity;
    public float shadowOffsetX;
    public float shadowOffsetY;
    public String shadowColor;
    public float shadowBlur;

    @JsOverlay
    public final static LineStyle create() {
        LineStyle lineStyle = new LineStyle();
        return lineStyle;
    }

    @JsOverlay
    public final void set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
    }
}
