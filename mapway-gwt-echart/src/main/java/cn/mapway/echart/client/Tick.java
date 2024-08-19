package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * SplitLine
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Tick {
    public float distance;
    public int splitNumber;
    public LineStyle lineStyle;

    @JsOverlay
    public final LineStyle getLineStyle() {
        if (lineStyle == null) {
            lineStyle = LineStyle.create();
        }
        return lineStyle;
    }

    @JsOverlay
    public final Tick setDistance(float distance) {
        this.distance = distance;
        return this;
    }

    @JsOverlay
    public final Tick setSplitNumber(int splitNumber) {
        this.splitNumber = splitNumber;
        return this;
    }

    @JsOverlay
    public final Tick set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
        return this;
    }

}
