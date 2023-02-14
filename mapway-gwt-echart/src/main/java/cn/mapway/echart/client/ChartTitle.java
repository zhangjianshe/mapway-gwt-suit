package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

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
}
