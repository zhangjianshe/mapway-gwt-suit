package cn.mapway.echart.client;

import com.google.gwt.dom.client.Element;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Echarts
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "echarts")
public class Echarts {
    /**
     * 初始化
     *
     * @param element
     * @param theme   主题
     * @return
     */
    public native static EChartControl init(Element element, String theme);

    public native static EChartControl init(Element element);

}
