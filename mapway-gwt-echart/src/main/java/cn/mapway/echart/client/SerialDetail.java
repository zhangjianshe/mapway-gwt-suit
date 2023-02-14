package cn.mapway.echart.client;

import elemental2.core.JsArray;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * SerialDetail
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SerialDetail {
    public boolean valueAnimation;
    public String color;
    public Object formatter;
    public JsArray offsetCenter;
    @JsOverlay
    public final SerialDetail setFormatterString(String formater)
    {
        this.formatter=formater;
        return this;
    }
    @JsOverlay
    public final SerialDetail setFormatter(IFormatter formatter)
    {
        this.formatter=formatter;
        return this;
    }
    @JsOverlay
    public final SerialDetail setOffsetCenter(String dx, String dy)
    {
        this.offsetCenter=new JsArray(dx, dy);
        return this;
    }
}
