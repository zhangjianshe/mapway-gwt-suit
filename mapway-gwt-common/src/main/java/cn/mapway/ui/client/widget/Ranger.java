package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;


/**
 * AiProgressBar
 *
 * @author zhangjianshe@gmail.com
 */
public class Ranger extends Composite implements HasValueChangeHandlers<Double> {

    private static final RangerUiBinder ourUiBinder = GWT.create(RangerUiBinder.class);
    double value = 0;
    @UiField
    DivElement bar;
    @UiField
    DivElement text;
    @UiField
    HTMLPanel root;
    String label = "";
    Double min = 0.0d;
    Double max = 100.0d;
    int x0;
    int y0;
    int mx = 0;
    int my = 0;
    boolean moving = false;
    private final MouseDownHandler mouseDownHandler = new MouseDownHandler() {
        @Override
        public void onMouseDown(MouseDownEvent mouseDownEvent) {
            moving = true;
            x0 = mouseDownEvent.getScreenX();
            y0 = mouseDownEvent.getScreenY();
            Style style = bar.getStyle();
            mx = mouseDownEvent.getRelativeX(root.getElement());
            style.setWidth(mx, Style.Unit.PX);
            mouseDownEvent.preventDefault();
            Event.setCapture(root.getElement());
            root.getElement().getStyle().setCursor(Style.Cursor.COL_RESIZE);
        }
    };
    int step = 0;
    int number = 0;
    private final MouseUpHandler mouseUpHandler = new MouseUpHandler() {
        @Override
        public void onMouseUp(MouseUpEvent mouseUpEvent) {
            moving = false;
            root.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
            Event.releaseCapture(root.getElement());

            ValueChangeEvent.fire(Ranger.this, getValue());
        }
    };
    private final MouseMoveHandler mouseMoverHandler = new MouseMoveHandler() {
        @Override
        public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
            if (moving) {
                int width = text.getOffsetWidth();
                int x = mouseMoveEvent.getScreenX();
                int y = mouseMoveEvent.getScreenY();

                int dx = x - x0;
                int dy = y - y0;

                int left = mx + dx;
                if (left <= 0) {
                    left = 0;
                }
                if (left >= width) {
                    left = width;
                }
                Style style = bar.getStyle();
                style.setWidth(left, Style.Unit.PX);
                double v = min + (max - min) * (left * 1.0f / width);
                value = v;
                text.setInnerText(format((float) value));
            }
        }
    };

    public Ranger() {
        initWidget(ourUiBinder.createAndBindUi(this));

        root.addDomHandler(mouseDownHandler, MouseDownEvent.getType());
        root.addDomHandler(mouseUpHandler, MouseUpEvent.getType());
        root.addDomHandler(mouseMoverHandler, MouseMoveEvent.getType());
    }

    public void setPrecision(int number) {
        if (number < 0) {
            number = 0;
        }
        if (number > 5) {
            number = 5;
        }
        this.number = number;
    }

    private String format(float value) {
        return StringUtil.formatFloat(value, number);
    }

    public void setValue(double value, boolean b) {
        if (value < this.min) {
            value = this.min;
        }
        if (value > max) {
            value = max;
        }
        this.value = value;
        int width = text.getClientWidth();
        double percent = (value - min) / (max - min);
        double w = (width) * percent;
        bar.getStyle().setWidth(w, Style.Unit.PX);
        text.setInnerText(this.label + format((float) value));
        if (b) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        setValue(this.value);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        setValue(this.value);
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);
        setValue(this.value);
    }

    @Override
    public void setSize(String width, String height) {
        super.setSize(width, height);
        setValue(this.value);
    }

    public void setLabel(String label) {
        this.label = label;
        text.setInnerText(this.label + " " + value);
    }

    public void setRange(Double min, Double max) {
        this.min = min;
        this.max = max;
        setValue(this.value);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        setValue(this.value);
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public Double getValue() {
        String text = format((float) value);
        return Double.parseDouble(text);
    }

    public void setValue(double value) {
        setValue(value, false);
    }


    interface RangerUiBinder extends UiBinder<HTMLPanel, Ranger> {
    }
}