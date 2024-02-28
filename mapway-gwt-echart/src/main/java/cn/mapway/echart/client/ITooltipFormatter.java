package cn.mapway.echart.client;

import elemental2.core.JsObject;
import jsinterop.annotations.JsFunction;

/**
 * ITooltipFormatter
 *
 * @author zhang
 */

@JsFunction
@FunctionalInterface
public interface ITooltipFormatter {
    /**
     * 图形组件 需要显示tooltip的时候调用这个方法
     *
     * @param obj      TooltipParameter
     * @param ticket
     * @param callback
     * @return
     */
    String format(JsObject tooltipParameter, String ticket, ITooltipFormatterCallback callback);
}
