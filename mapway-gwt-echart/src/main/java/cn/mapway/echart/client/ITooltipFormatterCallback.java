package cn.mapway.echart.client;


import jsinterop.annotations.JsFunction;

/**
 * ITooltipFormatter
 *
 * @author zhang
 */

@JsFunction
@FunctionalInterface
public interface ITooltipFormatterCallback {
    void apply(String ticket, String html);
}
