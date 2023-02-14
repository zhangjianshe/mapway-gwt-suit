package cn.mapway.echart.client;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

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
}
