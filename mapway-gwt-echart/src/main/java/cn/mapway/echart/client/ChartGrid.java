package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * @Author baoshuaiZealot@163.com  2023/3/22
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ChartGrid {

    public String left;

    public String right;

    public String bottom;

    public String top;

    public boolean containLabel;

    @JsOverlay
    public static final ChartGrid create(String left, String right, String bottom, String top, boolean containLabel) {
        ChartGrid grid = new ChartGrid();
        grid.left = left;
        grid.right = right;
        grid.bottom = bottom;
        grid.top = top;
        grid.containLabel = containLabel;
        return grid;
    }

    @JsOverlay
    public static final ChartGrid create(String left, String right, String bottom, String top) {
        ChartGrid grid = new ChartGrid();
        grid.left = left;
        grid.right = right;
        grid.bottom = bottom;
        grid.top = top;
        return grid;
    }
    @JsOverlay
    public final ChartGrid set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
        return this;
    }

}
