package cn.mapway.echart.client;

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
public class ChartLegend {

//    public JsArray<String> data;

    /**
     * plain 或  scroll
     **/
//    public String type;

    public boolean show;
    public String left;
    public String top;
    public String right;
    public String bottom;
    public String orient;
    public ChartItemStyle itemStyle;
    public ChartLabelStyle textStyle;
    @JsOverlay
    public final ChartLegend setLeft(String left) {
        this.left = left;
        return this;
    }
    @JsOverlay
    public final ChartLegend setRight(String right) {
        this.right = right;
        return this;
    }
    @JsOverlay
    public final ChartLegend setTop(String top) {
        this.top = top;
        return this;
    }
    @JsOverlay
    public final ChartLegend setBottom(String bottom) {
        this.bottom = bottom;
        return this;
    }

    @JsOverlay
    public final ChartLegend setOrient(boolean verticle) {
        if (verticle) {
            orient = "vertical";
        } else {
            orient = "horizontal";
        }
        return this;
    }
    @JsOverlay
    public final ChartItemStyle getItemStyle()
    {
        if (itemStyle==null)
        {
            itemStyle=new ChartItemStyle();
        }
        return itemStyle;
    }
    @JsOverlay
    public final ChartLabelStyle getTextStyle()
    {
        if (textStyle==null)
        {
            textStyle=new ChartLabelStyle();
        }
        return textStyle;
    }

    @JsOverlay
    public final void set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
    }
}
