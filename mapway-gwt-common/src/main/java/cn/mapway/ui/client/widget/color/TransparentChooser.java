package cn.mapway.ui.client.widget.color;

import cn.mapway.ui.client.util.Colors;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

/**
 * TransparentChooser
 * 事件中 double值范围 [0-1]
 *
 * @author zhang
 */
public class TransparentChooser extends HTMLPanel implements HasValue<Double>, HasValueChangeHandlers<Double> {
    /**
     * [0-1]
     */
    double alpha;
    Label indicator;
    boolean mouseDown = false;

    public TransparentChooser(String text) {
        super(text);
        indicator = new Label();
        add(indicator);
        setPixelSize(200, 15);
        installStyle();
        installEvent();
    }

    public TransparentChooser() {
        this("");
    }

    /**
     * 0-255
     */
    private void installEvent() {
        addDomHandler(event -> {
            int x = event.getRelativeX(getElement());
            setValue(x * 1.0 / getOffsetWidth(), true);
        }, ClickEvent.getType());

        addDomHandler(event -> {
            mouseDown = true;
            DOM.setCapture(getElement());
        }, MouseDownEvent.getType());

        addDomHandler(event -> {
            if (mouseDown) {
                int x = event.getRelativeX(getElement());
                setValue(x * 1.0 / getOffsetWidth(), true);
                event.stopPropagation();
                event.preventDefault();
            }
        }, MouseMoveEvent.getType());

        addDomHandler(event -> {
            mouseDown = false;
            int x = event.getRelativeX(getElement());
            setValue(x * 1.0 / getOffsetWidth(), true);
            DOM.releaseCapture(getElement());
            event.stopPropagation();
            event.preventDefault();
        }, MouseUpEvent.getType());
    }

    private void installStyle() {
        Style style = getElement().getStyle();
        style.setProperty("border", "solid 1px #f0f0f0");
        style.setPosition(Style.Position.RELATIVE);

        Element element = indicator.getElement();
        Style style2 = element.getStyle();
        style2.setPosition(Style.Position.ABSOLUTE);
        style2.setWidth(1, Style.Unit.PX);
        style2.setTop(0, Style.Unit.PX);
        style2.setBottom(0, Style.Unit.PX);
        style2.setBackgroundColor("#000");
        style2.setLeft(alpha * getOffsetWidth(), Style.Unit.PX);
    }

    public void setColor(ColorData color, boolean fireEvent) {
        resetBackground(color.getColor());
        setValue((color.getAlpha() & 0xFF) / 255.0, fireEvent);
    }


    private void resetBackground(int color) {
        Style style = getElement().getStyle();

        int startColor = color;
        startColor = Colors.setA(startColor, 0x00);
        int endColor = color;
        endColor = Colors.setA(endColor, 0xFF);
        String start = Colors.toRGBA(startColor);
        String end = Colors.toRGBA(endColor);
        String back = "linear-gradient(90deg," + start + " 0%," + end + " 100%),url('img/transparent.png') ";
        style.setProperty("background", back);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public Double getValue() {
        return alpha;
    }

    @Override
    public void setValue(Double value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Double value, boolean fireEvents) {
        alpha = value;
        if (value < 0.01) {
            alpha = 0.0;
        }
        if (value >= 1.0) {
            alpha = 1.0;
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, alpha);
        }
        toUI();
    }

    private void toUI() {
        Element element = indicator.getElement();
        Style style2 = element.getStyle();
        style2.setLeft(alpha * getOffsetWidth(), Style.Unit.PX);
    }
}
