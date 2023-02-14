package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * SplitLine
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SplitLine {
    public float distance;
    public float length;
    public LineStyle lineStyle;

    @JsOverlay
    public final LineStyle getLineStyle() {
        if (lineStyle == null) {
            lineStyle = LineStyle.create();
        }
        return lineStyle;
    }

    @JsOverlay
    public final SplitLine setDistance(float distance) {
        this.distance = distance;
        return this;
    }
    @JsOverlay
    public final SplitLine setLength(float length) {
        this.length = length;
        return this;
    }

}
