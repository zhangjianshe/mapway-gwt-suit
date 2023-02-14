package cn.mapway.echart.client;

import jsinterop.annotations.JsFunction;

/**
 * IFormatter
 *
 * @author zhang
 */
@JsFunction
public interface IFormatter {
    String format(double value);
}
