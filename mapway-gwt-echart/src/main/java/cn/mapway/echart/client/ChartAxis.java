package cn.mapway.echart.client;

import elemental2.core.JsArray;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * ChartAxis
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ChartAxis {
    public JsArray<String> data;
    public String type;
    public String name;
    public boolean scale;
    AxisLineStyle axisLine;

    @JsOverlay
    public final static ChartAxis create(String type, String name) {
        ChartAxis chartAxis = new ChartAxis();
        chartAxis.type = type;
        chartAxis.name = name;
        return chartAxis;
    }

    @JsOverlay
    public final static ChartAxis createValueAxis(String name) {
        ChartAxis chartAxis = new ChartAxis();
        chartAxis.type = "value";
        chartAxis.name = name;
        return chartAxis;
    }

    @JsOverlay
    public final static ChartAxis createCategoryAxis(String name) {
        ChartAxis chartAxis = new ChartAxis();
        chartAxis.type = "category";
        chartAxis.name = name;
        return chartAxis;
    }

    @JsOverlay
    public final static ChartAxis createCategoryAxisWidthData(String name, String... axis) {
        ChartAxis chartAxis = new ChartAxis();
        chartAxis.type = "category";
        chartAxis.name = name;
        chartAxis.data = new JsArray<>(axis);
        return chartAxis;
    }

    @JsOverlay
    public final AxisLineStyle getAxisLineStyle() {
        if (this.axisLine == null) {
            this.axisLine = AxisLineStyle.create();
        }
        return this.axisLine;
    }
}
