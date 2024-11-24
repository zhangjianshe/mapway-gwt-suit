package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.mvc.attribute.SlideProperty;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

public class SliderEx extends CommonEventComposite implements IData, HasValue<Double>, HasFocusHandlers, HasValueChangeHandlers<Double> {
    private static final SliderExUiBinder ourUiBinder = GWT.create(SliderExUiBinder.class);
    public String unit = "";
    int x0;
    int y0;
    float mx = 0;
    int my = 0;
    boolean moving = false;
    double value;
    Object data;
    double min = 0;
    double max = 100;
    @UiField
    HTMLPanel mover;
    private final MouseDownHandler mouseDownHandler = new MouseDownHandler() {
        @Override
        public void onMouseDown(MouseDownEvent mouseDownEvent) {
            moving = true;
            x0 = mouseDownEvent.getScreenX();
            y0 = mouseDownEvent.getScreenY();
            Style style = mover.getElement().getStyle();
            style.setColor("#007bc3");
            String left = style.getLeft();
            if (left.length() == 0) {
                left = "0";
            } else {
                left = left.substring(0, left.length() - 2);
            }
            mx = Float.parseFloat(left);
            mouseDownEvent.preventDefault();
            Event.setCapture(SliderEx.this.getElement());
            mover.getElement().getStyle().setCursor(Style.Cursor.MOVE);
        }
    };
    private final MouseUpHandler mouseUpHandler = new MouseUpHandler() {
        @Override
        public void onMouseUp(MouseUpEvent mouseUpEvent) {
            moving = false;
            Style style = mover.getElement().getStyle();
            style.setCursor(Style.Cursor.DEFAULT);
            Event.releaseCapture(SliderEx.this.getElement());
            ValueChangeEvent.fire(SliderEx.this, value);
            style.setColor("#32a9e5");
        }

    };
    @UiField
    HTMLPanel box;
    @UiField
    Label text;
    boolean continueReport = false;
    private double step = 1;
    private final MouseMoveHandler mouseMoverHandler = new MouseMoveHandler() {
        @Override
        public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
            if (moving) {
                int length = SliderEx.this.getOffsetWidth() - mover.getOffsetWidth();
                int x = mouseMoveEvent.getScreenX();
                int y = mouseMoveEvent.getScreenY();

                int dx = x - x0;
                int dy = y - y0;

                float left = mx + dx;
                if (left < 0) {
                    left = 0;
                } else if (left > length) {
                    left = length;
                }

                Style style = mover.getElement().getStyle();
                style.setLeft(left, Style.Unit.PX);

                double v = min + (max - min) * (left * 1.0 / length);
                value = v;
                if (step >= 1) {
                    value = StringUtil.attachTo((int) value, (int) step);
                }
                text.setText(StringUtil.formatDouble(value, 1) + unit);
                if (continueReport) {
                    ValueChangeEvent.fire(SliderEx.this, value);
                }
            }
        }
    };

    public SliderEx() {
        initWidget(ourUiBinder.createAndBindUi(this));
        init();
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    void init() {
        this.addDomHandler(mouseDownHandler, MouseDownEvent.getType());
        this.addDomHandler(mouseUpHandler, MouseUpEvent.getType());
        this.addDomHandler(mouseMoverHandler, MouseMoveEvent.getType());
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
        moveToValue();
    }

    public double getRange() {
        return max - min;
    }

    void setRange(double min, double max) {
        this.min = min;
        this.max = max;
        moveToValue();
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        setValue(value, false);
    }

    public void update() {
        setValue(getValue());
    }

    private void moveToValue() {
        if (step >= 1) {
            value = StringUtil.attachTo((int) value, (int) step);
        }
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                double left = (SliderEx.this.getOffsetWidth() - mover.getOffsetWidth()) * (value - min) / (max - min);
                mover.getElement().getStyle().setLeft(left, Style.Unit.PX);
                text.setText(value + unit);
            }
        });
    }

    @Override
    public void setValue(Double valuePara, boolean b) {
        if (valuePara <= min) {
            value = min;
        } else if (valuePara > max) {
            value = max;
        } else {
            value = valuePara;
        }
        moveToValue();

        if (b) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addHandler(handler, FocusEvent.getType());
    }

    public void setProperty(SlideProperty slideProperty) {
        if (slideProperty != null) {
            min = slideProperty.min;
            max = slideProperty.max;
            step = slideProperty.step;
            unit = slideProperty.unit;
            if (slideProperty.continueReport == null) {
                continueReport = false;
            } else {
                continueReport = slideProperty.continueReport;
            }
        }
    }

    public void setStep(int stepParam) {
        step = stepParam;
    }

    public void setTipLocation(String locationTop) {

    }

    interface SliderExUiBinder extends UiBinder<HTMLPanel, SliderEx> {
    }
}
