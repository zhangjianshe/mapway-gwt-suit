package cn.mapway.ui.client.mvc.attribute;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * SlideProperty
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(namespace = JsPackage.GLOBAL)
public class SlideProperty {
    public double min;
    public double max;
    public double step;
    public String unit;
    public double exponent;
    //是否连续汇报
    public Boolean continueReport;
}
