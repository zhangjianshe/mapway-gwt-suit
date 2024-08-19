package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * ChartBackgroundStyle
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ChartBackgroundStyle {
    public String color;
    @JsOverlay
    public final ChartBackgroundStyle set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
        return this;
    }
}
