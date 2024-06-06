package cn.mapway.ui.client.widget.color;

import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.Colors;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.widget.canvas.CanvasWidget;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
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


/**
 * HueChooser
 * 色相选择
 *
 * @author zhang
 */
public class HueChooser extends CanvasWidget implements HasValueChangeHandlers<Double>, IData<Double> {
    boolean mouseDown = false;
    double currentX = 0;
    ImageData bar;
    double radius;
    int barWidth;
    int barHeight;
    double hueTemp;

    public HueChooser() {
        setPixelSize(200, 15);
        installEvent();
        setContinueDraw(true);
    }

    @Override
    public void setPixelSize(int width, int height) {
        barWidth = width;
        barHeight = height;
        super.setPixelSize(width, height);
    }

    private void initWidgetState() {
        barHeight = getOffsetHeight();
        barWidth = getOffsetWidth();
        radius = barHeight / 2.;
    }

    private void installEvent() {
        //半径
        radius = getOffsetHeight() / 2.0;
        addDomHandler(event -> {
            Element element = getElement();
            currentX = event.getRelativeX(element);
            double hue = updateHue();
            ValueChangeEvent.fire(HueChooser.this, hue);
        }, ClickEvent.getType());

        addDomHandler(event -> {
            mouseDown = true;
            DOM.setCapture(getElement());
        }, MouseDownEvent.getType());
        addDomHandler(event -> {
            if (mouseDown) {
                currentX = event.getRelativeX(getElement());
                double hue = updateHue();
                draw();
                ValueChangeEvent.fire(HueChooser.this, hue);
                event.stopPropagation();
                event.preventDefault();
            }
        }, MouseMoveEvent.getType());
        addDomHandler(event -> {
            mouseDown = false;
            DOM.releaseCapture(getElement());
        }, MouseUpEvent.getType());
    }

    private double updateHue() {
        if (currentX <= radius) {
            currentX = radius;
        }
        if (currentX >= barWidth - radius) {
            currentX = barWidth - radius;
        }
        double hue = 360.0 * (currentX - radius) / (barWidth - 2 * radius);
        return hue;
    }

    public void draw() {
        if (barWidth == 0 || barHeight == 0) {
            return;
        }
        Context2d context2d = getContext2d();
        context2d.putImageData(bar, 0, 0);

        context2d.beginPath();
        context2d.setStrokeStyle("rgba(0,0,0,.7");
        context2d.setLineWidth(1);
        context2d.arc(currentX, radius, radius, 0, Math.PI * 2);
        context2d.stroke();

        context2d.beginPath();
        context2d.setStrokeStyle("#f0f0f0");
        context2d.setLineWidth(2);
        context2d.arc(currentX, radius, radius - 1, 0, Math.PI * 2);
        context2d.stroke();
    }

    @Override
    protected void onDraw(double timestamp) {
        draw();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        initWidgetState();
        setContinueDraw(true);
        prepareBar();
    }

    private void prepareBar() {
        Context2d context2d = getContext2d();
        //画出整条BAR 0->radius 使用0的hue
        int radiusInt = (int) Math.floor(radius);
        int[] startColor = Colors.hsv2rgb(0, 1., 1.);
        int[] endColor = Colors.hsv2rgb(1.0, 1., 1.);
        context2d.setFillStyle(Colors.toRGB(startColor));
        context2d.fillRect(0, 0, radius, barHeight + 1);
        context2d.setFillStyle(Colors.toRGB(endColor));
        context2d.fillRect(barWidth - radius, 0, radius, barHeight + 1);
        bar = context2d.getImageData(0, 0, barWidth + 1, barHeight + 1);
        double totalWidth = barWidth - radiusInt * 2;
        for (int col = radiusInt; col < barWidth - radiusInt + 1; col++) {
            int[] rgb = Colors.hsv2rgb(((col - radiusInt) / totalWidth), 1., 1.);
            for (int row = 0; row < barHeight + 1; row++) {
                bar.setRedAt(rgb[0], col, row);
                bar.setGreenAt(rgb[1], col, row);
                bar.setBlueAt(rgb[2], col, row);
                bar.setAlphaAt(0xff, col, row);
            }
        }
    }


    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public Double getData() {
        return hueTemp;
    }

    @Override
    public void setData(Double obj) {
        Logs.info("hue value " + obj);
        if (obj == null) {
            hueTemp = 0;
        } else {
            hueTemp = obj;
        }
        if (hueTemp >= 360) {
            hueTemp = 360;
        }
        if (hueTemp <= 0) {
            hueTemp = 0;
        }
        updateLocation();
    }

    private void updateLocation() {
        double scaleWidth = (barWidth - 2 * radius);
        currentX = radius + scaleWidth * (hueTemp / 360.0);
    }
}
