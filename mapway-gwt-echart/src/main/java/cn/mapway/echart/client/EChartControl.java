package cn.mapway.echart.client;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * EChartControl
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class EChartControl {
    public native final void setOption(ChartOption option);

    public native final void resize(ResizeOption resizeOption);

    public native final void clear();

    public native final void dispose();
}
