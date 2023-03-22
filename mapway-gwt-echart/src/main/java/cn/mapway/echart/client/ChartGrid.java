package cn.mapway.echart.client;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

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

    public static ChartGrid create(String left, String right, String bottom, String top, boolean containLabel) {
        ChartGrid grid = new ChartGrid();
        grid.left = left;
        grid.right = right;
        grid.bottom = bottom;
        grid.top = top;
        grid.containLabel = containLabel;
        return grid;
    }

    public static ChartGrid create(String left, String right, String bottom, String top) {
        ChartGrid grid = new ChartGrid();
        grid.left = left;
        grid.right = right;
        grid.bottom = bottom;
        grid.top = top;
        return grid;
    }


}
