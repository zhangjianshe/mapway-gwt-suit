package cn.mapway.echart.client;

import elemental2.core.JsArray;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * ChartOption
 * 图标配置项 参见 https://echarts.apache.org/zh/option.html#title
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ChartOption {
    public ChartTitle title;
    public ChartAxis xAxis;
    public ChartAxis yAxis;
    public JsArray<ChartSerial> series;

    public ChartTooltip tooltip;

    public ChartLegend legend;

    public ChartGrid grid;

    @JsOverlay
    public final static ChartOption create(String title) {
        ChartOption option = new ChartOption();
        option.title = ChartTitle.create(title);
        option.series = new JsArray<ChartSerial>();
        return option;
    }

    @JsOverlay
    public final ChartTitle getTitle() {
        return title;
    }

    @JsOverlay
    public final JsArray<ChartSerial> getSeries() {
        return series;
    }

    @JsOverlay
    public final ChartAxis getXAxis() {
        return xAxis;
    }

    @JsOverlay
    public final ChartAxis getYAxis() {
        return yAxis;
    }

    @JsOverlay
    public final ChartOption setXAxis(String name, String type) {
        xAxis = ChartAxis.create(name, type);
        return this;
    }

    @JsOverlay
    public final ChartOption setXAxis(ChartAxis xAxis) {
        this.xAxis = xAxis;
        return this;
    }

    @JsOverlay
    public final ChartOption setYAxis(ChartAxis yAxis) {
        this.yAxis = yAxis;
        return this;
    }

    @JsOverlay
    public final ChartOption setXAxisCategory(String name, String[] data) {
        if (data == null || data.length == 0) {
            xAxis = ChartAxis.createCategoryAxis(name);
        } else {
            xAxis = ChartAxis.createCategoryAxisWidthData(name, data);
        }
        return this;
    }

    @JsOverlay
    public final ChartOption setXAxisValue(String name) {
        xAxis = ChartAxis.createValueAxis(name);
        return this;
    }

    @JsOverlay
    public final ChartOption setYAxisValue(String name) {
        yAxis = ChartAxis.createValueAxis(name);
        return this;
    }

    @JsOverlay
    public final ChartOption addSerial(ChartSerial serial) {
        series.push(serial);
        return this;
    }

    @JsOverlay
    public final ChartTooltip getTooltip() {
        if (tooltip == null) {
            tooltip = ChartTooltip.create();
        }
        return tooltip;
    }

    @JsOverlay
    public final ChartOption setLegend(ChartLegend legend) {
        this.legend = legend;
        return this;
    }

    @JsOverlay
    public final ChartLegend getLegend() {
        return legend;
    }

    @JsOverlay
    public final ChartOption set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
        return this;
    }

}
