package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class DataItem {
    public double value;
    public String name;
    @JsOverlay
    public final DataItem val(double value)
    {
        this.value = value;
        return this;
    }
    @JsOverlay
    public final DataItem name(String name)
    {
        this.name = name;
        return this;
    }
    @JsOverlay
    public final DataItem set(String name,double v)
    {
        this.name = name;
        this.value=v;
        return this;
    }
}
