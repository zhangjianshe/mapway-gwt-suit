package cn.mapway.echart.client;

import elemental2.core.JsArray;
import elemental2.core.JsObject;
import elemental2.core.JsString;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

import static cn.mapway.echart.client.constant.EchartsConstant.LINE_STYLE_SYMBOL_NONE;


/**
 * AxisLineStyle
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class AxisLineStyle {

    public boolean show;
    public boolean onZero;
    public int onZeroAxisIndex;
    public JsObject symbol;
    public JsArray<Float> symbolSize;
    public JsArray<Float> symbolOffset;
    private LineStyle lineStyle;

    @JsOverlay
    public final static AxisLineStyle create() {
        AxisLineStyle axisLineStyle = new AxisLineStyle();
        return axisLineStyle;
    }

    /**
     * AxisLineStyle
     *
     * @param xSymbol none || arrow
     * @param ySymbol
     * @return
     */
    @JsOverlay
    public final AxisLineStyle setSymbol(String xSymbol, String ySymbol) {
        if (xSymbol == null && ySymbol == null) {
            symbol = Js.uncheckedCast(new JsString(LINE_STYLE_SYMBOL_NONE));
            return this;
        }
        JsArray<String> values = new JsArray<String>();
        if (xSymbol == null || xSymbol.length() == 0) {
            values.push(LINE_STYLE_SYMBOL_NONE);
        } else {
            values.push(xSymbol);
        }
        if (ySymbol == null || ySymbol.length() == 0) {
            values.push(LINE_STYLE_SYMBOL_NONE);
        } else {
            values.push(ySymbol);
        }
        symbol = Js.uncheckedCast(values);
        return this;
    }

    @JsOverlay
    public final AxisLineStyle setSymbolSize(float xSize, float ySize) {
        symbolSize = Js.uncheckedCast(new Float[]{xSize, ySize});
        return this;
    }

    @JsOverlay
    public final AxisLineStyle setSymbolOffset(float offsetX, float offsetY) {
        symbolOffset = Js.uncheckedCast(new Float[]{offsetX, offsetY});
        return this;
    }

    @JsOverlay
    public final LineStyle getSymbolStyle() {
        if (lineStyle == null) {
            lineStyle = LineStyle.create();
        }
        return lineStyle;
    }

    @JsOverlay
    public final AxisLineStyle setShow(boolean b) {
        this.show = b;
        return this;
    }

    @JsOverlay
    public final void set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
    }
}
