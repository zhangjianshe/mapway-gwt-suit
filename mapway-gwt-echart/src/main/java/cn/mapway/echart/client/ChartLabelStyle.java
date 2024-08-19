package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * ChartLabelStyle
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ChartLabelStyle {
    public boolean show;
    public String position;
    public float distance;
    public String color;

    @JsOverlay
    public final ChartLabelStyle setShow(boolean show) {
        this.show = show;
        return this;
    }
    @JsOverlay
    public final ChartLabelStyle setColor(String color) {
        this.color = color;
        return this;
    }
    @JsOverlay
    public final ChartLabelStyle setDistance(float distance) {
        this.distance = distance;
        return this;
    }
    /**
     * 设置图标位置
     *
     * @param position {@link LabelPosition}
     */
    @JsOverlay
    public final ChartLabelStyle setPosition(String position) {
        this.position = position;
        return this;
    }
    @JsOverlay
    public final ChartLabelStyle set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
        return this;
    }
}
