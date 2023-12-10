package cn.mapway.ui.client.widget.color;

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
import com.google.gwt.event.shared.HandlerRegistration;
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
        draw(hsv[0]);
    }

    private void installEvent() {
        addDomHandler(event -> {
            Element element = getElement();
            int x = event.getRelativeX(element);
            int y = event.getRelativeY(element);
            double saturation = x * 1.0 / getOffsetWidth();
            double value = 1 - y * 1.0 / getOffsetHeight();
            int[] rgb = Colors.hsv2rgb(hue, saturation, value);
            colorData.fromIntegers(rgb);
            fireEvent(CommonEvent.colorEvent(colorData));
        }, ClickEvent.getType());
    }

    /**
     * 根据 HUE值 画出一个 面板 [saturation,value]
     *
     * @param hue
     */
    public void draw(double hue) {
        this.hue = hue;
        int width = getOffsetWidth();
        int height = getOffsetHeight();
        if (width == 0 || height == 0) {
            return;
        }
        Context2d context2d = getContext2d();
        ImageData imageData = context2d.getImageData(0, 0, width, height);

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
    }

    @Override
    protected void onDraw(double timestamp) {
        draw(this.hue);
    }

    @Override
    public void onResize() {
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
