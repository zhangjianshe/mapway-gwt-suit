package cn.mapway.echart.client;

import elemental2.core.JsArray;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import java.util.List;

/**
 * ChartSerial
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ChartSerial {
    public String name;
    public String type;
    public JsArray<Object> data;
    public ChartItemStyle itemStyle;
    public String barWidth;
    public String barCategoryGap;
    public boolean showBackground;
    public String stack;
    public ChartBackgroundStyle backgroundStyle;
    public ChartLabelStyle label;
    public ChartLabelStyle axisLabel;
    public SplitLine splitLine;
    public float startAngle;
    public float endAngle;
    public float min;
    public float max;
    public int splitNumber;
    public AxisLineStyle axisLine;
    public Tick axisTick;
    public SerialDetail detail;

    @JsOverlay
    public final static ChartSerial create(String name, String type, List<Object> data) {
        ChartSerial serial = new ChartSerial();
        serial.build(name, type, data);
        return serial;
    }

    @JsOverlay
    public final void build(String name, String type, List<Object> data) {
        this.name = name;
        this.type = type;
        this.data = new JsArray<>();
        for (Object d : data) {
            this.data.push(d);
        }
    }

    @JsOverlay
    public final void setShowBackground(boolean showBackground) {
        this.showBackground = showBackground;
    }

    @JsOverlay
    public final ChartItemStyle getItemStyle() {
        if (itemStyle == null) {
            itemStyle = new ChartItemStyle();
        }
        return itemStyle;
    }

    @JsOverlay
    public final ChartBackgroundStyle getBackgroundStyle() {
        if (backgroundStyle == null) {
            backgroundStyle = new ChartBackgroundStyle();
        }
        return backgroundStyle;
    }

    @JsOverlay
    public final ChartLabelStyle getLabelStyle() {
        if (label == null) {
            label = new ChartLabelStyle();
        }
        return label;
    }

    @JsOverlay
    public final SerialDetail getDetail() {
        if (detail == null) {
            detail = new SerialDetail();
        }
        return detail;
    }

    @JsOverlay
    public final ChartLabelStyle getAxisLabel() {
        if (axisLabel == null) {
            axisLabel = new ChartLabelStyle();
        }
        return axisLabel;
    }

    @JsOverlay
    public final SplitLine getSplitLine() {
        if (splitLine == null) {
            splitLine = new SplitLine();
        }
        return splitLine;
    }

    @JsOverlay
    public final AxisLineStyle getAxisLineStyle() {
        if (axisLine == null) {
            axisLine = new AxisLineStyle();
        }
        return axisLine;
    }

    @JsOverlay
    public final Tick getAxisTick() {
        if (axisTick == null) {
            axisTick = new Tick();
        }
        return axisTick;
    }

    @JsOverlay
    public final void bar(String name, List<Object> data) {
        build(name, "bar", data);
    }

    @JsOverlay
    public final void gauge(String name, List<Object> data) {
        build(name, "gauge", data);
    }
}
