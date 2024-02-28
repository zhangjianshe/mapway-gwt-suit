package cn.mapway.echart.client;

import elemental2.core.JsArray;
import elemental2.core.JsObject;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * ChartTooltip
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ChartTooltip {
    public String text;
    public boolean show;
    public String trigger;
    public boolean showContent;
    public boolean alwaysShowContent;
    public int showDelay;
    public int hideDelay;
    public boolean enterable;
    public String renderMode;
    public boolean confine;
    public boolean appendToBody;
    public float transitionDuration;
    public JsArray<String> position;
    public String backgroundColor;
    public String borderColor;
    public float borderWidth;
    public String order;
    public int padding;
    public JsObject formatter;
    public AxisPointer axisPointer;

    @JsOverlay
    public final static ChartTooltip create() {
        ChartTooltip tooltip = new ChartTooltip();
        return tooltip;
    }
    @JsOverlay
    public final void set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
    }

    /**
     * 模板变量有 {a}, {b}，{c}，{d}，{e}，分别表示系列名，数据名，数据值等。 在 trigger 为 'axis' 的时候，会有多个系列的数据，此时可以通过 {a0}, {a1}, {a2} 这种后面加索引的方式表示系列的索引。 不同图表类型下的 {a}，{b}，{c}，{d} 含义不一样。 其中变量{a}, {b}, {c}, {d}在不同图表类型下代表数据含义为：
     * <p>
     * 折线（区域）图、柱状（条形）图、K线图 : {a}（系列名称），{b}（类目值），{c}（数值）, {d}（无）
     * <p>
     * 散点图（气泡）图 : {a}（系列名称），{b}（数据名称），{c}（数值数组）, {d}（无）
     * <p>
     * 地图 : {a}（系列名称），{b}（区域名称），{c}（合并数值）, {d}（无）
     * <p>
     * 饼图、仪表盘、漏斗图: {a}（系列名称），{b}（数据项名称），{c}（数值）, {d}（百分比）
     *
     * @param template
     * @return
     */
    @JsOverlay
    public final ChartTooltip setFormatter(String template) {
        this.formatter = Js.uncheckedCast(template);
        return this;
    }

    @JsOverlay
    public final ChartTooltip setFormatter(ITooltipFormatter formatFunction) {
        this.formatter = Js.uncheckedCast(formatFunction);
        return this;
    }

    @JsOverlay
    public final ChartTooltip setTrigger(String trigger) {
        this.trigger = trigger;
        return this;
    }

    @JsOverlay
    public final ChartTooltip setShow(boolean show) {
        this.show = show;
        return this;
    }
}
