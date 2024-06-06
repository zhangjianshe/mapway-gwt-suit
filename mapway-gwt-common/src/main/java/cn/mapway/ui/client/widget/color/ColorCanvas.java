package cn.mapway.ui.client.widget.color;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.util.Colors;
import cn.mapway.ui.client.widget.canvas.CanvasWidget;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * ColorCanvas
 *
 * @author zhang
 */
public class ColorCanvas extends CanvasWidget implements RequiresResize, HasCommonHandlers {
    double hue;
    ColorData colorData = new ColorData();
    Size colorPosition = new Size();
    ImageData imageData;
    boolean mouseDown = false;

    public ColorCanvas() {
        // setPixelSize(260, 260);
        getElement().getStyle().setCursor(Style.Cursor.CROSSHAIR);
        installEvent();
    }

    public ColorData getColor() {
        return colorData;
    }

    public void setColor(ColorData colorData) {
        this.colorData = colorData;
        double[] hsv = Colors.rgb2hsv(colorData.r(), colorData.g(), colorData.b());
        //计算颜色的位置
        colorPosition.set(hsv[1] * getOffsetWidth(), hsv[2] * getOffsetHeight());
        draw(hsv[0], false);
    }

    private void installEvent() {
        addDomHandler(event -> {
            Element element = getElement();
            int x = event.getRelativeX(element);
            int y = event.getRelativeY(element);
            calPositionAndFireEvent(x, y);
        }, ClickEvent.getType());
        addDomHandler(event -> {
            mouseDown = true;
            Element element = getElement();
            DOM.setCapture(element);
            int x = event.getRelativeX(element);
            int y = event.getRelativeY(element);
            calPositionAndFireEvent(x, y);
            event.stopPropagation();
            event.preventDefault();
        }, MouseDownEvent.getType());

        addDomHandler(event -> {
            if (mouseDown) {
                Element element = getElement();
                int x = event.getRelativeX(element);
                int y = event.getRelativeY(element);
                calPositionAndFireEvent(x, y);
                event.stopPropagation();
                event.preventDefault();
            }
        }, MouseMoveEvent.getType());
        addDomHandler(event -> {
            mouseDown = false;
            DOM.releaseCapture(getElement());
            event.stopPropagation();
            event.preventDefault();
        }, MouseUpEvent.getType());
    }

    private void calPositionAndFireEvent(int x, int y) {
        colorPosition.set(x, y);
        this.draw(hue, true);
    }

    private void doFireColor() {
        double saturation = colorPosition.getX() / getOffsetWidth();
        double value = 1 - (double) colorPosition.getYAsInt() / getOffsetHeight();
        int[] rgb = Colors.hsv2rgb(hue, saturation, value);
        colorData.fromIntegers(rgb);
        fireEvent(CommonEvent.colorEvent(colorData));
    }

    /**
     * 绘制十字线
     */
    private void drawCross() {
        int width = getOffsetWidth();
        int height = getOffsetHeight();
        double x = colorPosition.getXAsInt() + 0.5;
        double y = colorPosition.getYAsInt() + 0.5;
        Context2d context2d = getContext2d();
        context2d.setStrokeStyle("#fff");
        context2d.setLineWidth(0.5);
        context2d.beginPath();
        context2d.moveTo(x, 0);
        context2d.lineTo(x, height);
        context2d.moveTo(0, y);
        context2d.lineTo(width, y);
        context2d.stroke();
    }

    /**
     * 根据 HUE值 画出一个 面板 [saturation,value]
     *
     * @param hue
     */
    public void draw(double hue, boolean fireEvent) {
        this.hue = hue;
        int width = getOffsetWidth();
        int height = getOffsetHeight();
        if (width == 0 || height == 0) {
            return;
        }
        Context2d context2d = getContext2d();
        if (imageData == null) {
            imageData = context2d.getImageData(0, 0, width, height);
        }
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int[] rgb = Colors.hsv2rgb(hue, col * 1. / width, 1 - row * 1. / height);
                imageData.setRedAt(rgb[0], col, row);
                imageData.setGreenAt(rgb[1], col, row);
                imageData.setBlueAt(rgb[2], col, row);
                imageData.setAlphaAt(255, col, row);
            }
        }
        context2d.putImageData(imageData, 0, 0);
        drawCross();

        if (fireEvent) {
            doFireColor();
        }
    }

    @Override
    protected void onDraw(double timestamp) {
        draw(this.hue, false);
    }

    @Override
    public void onResize() {
        imageData = null;
        Scheduler.get().scheduleFinally(() -> {
            com.google.gwt.dom.client.Element parentElement = getElement().getParentElement();
            int clientWidth = parentElement.getClientWidth();
            int clientHeight = parentElement.getClientHeight();
            setPixelSize(clientWidth, clientHeight);
            redraw();
        });
    }


    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }
}
