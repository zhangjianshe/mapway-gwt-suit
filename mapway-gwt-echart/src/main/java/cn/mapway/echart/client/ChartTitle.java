package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * ChartTitle
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ChartTitle {
    public String text;

    @JsOverlay
    public final static ChartTitle create(String title) {
        ChartTitle chartTitle = new ChartTitle();
        chartTitle.text = title;
        return chartTitle;
    }
    @JsOverlay
    public final ChartTitle set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
        return this;
    }
}
