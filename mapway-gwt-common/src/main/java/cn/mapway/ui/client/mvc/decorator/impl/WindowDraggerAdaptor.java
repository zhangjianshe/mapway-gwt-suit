package cn.mapway.ui.client.mvc.decorator.impl;

import cn.mapway.ui.client.mvc.decorator.IWindowCallback;
import cn.mapway.ui.client.mvc.decorator.IWindowDecorator;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * WindowDraggerAdaptor
 *
 * @author zhang
 */
public class WindowDraggerAdaptor implements MouseDownHandler, MouseUpHandler, MouseMoveHandler {
    Widget widget;
    IWindowCallback callback;
    IWindowDecorator window;

    int mouseDownX;
    int mouseDownY;
    boolean down = false;
    Element captureElement;

    /**
     * @param window   窗口最曾需要移动的对象
     * @param dragger  鼠标按住移动的对象
     * @param callback 回调函数
     */
    public WindowDraggerAdaptor(IWindowDecorator window, Widget dragger, IWindowCallback callback) {
        this.widget = dragger;
        this.window = window;
        this.callback = callback;

        widget.addDomHandler(this, MouseDownEvent.getType());
        widget.addDomHandler(this, MouseUpEvent.getType());
        widget.addDomHandler(this, MouseMoveEvent.getType());
    }

    // 鼠标拖动操作
    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.stopPropagation();
        event.preventDefault();
        down = true;
        mouseDownX = event.getScreenX();
        mouseDownY = event.getScreenY();
        captureElement = widget.getElement();
        DOM.setCapture(captureElement);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        if (down) {
            int offX = event.getScreenX() - mouseDownX;
            int offY = event.getScreenY() - mouseDownY;
            mouseDownX = event.getScreenX();
            mouseDownY = event.getScreenY();
            if (callback != null) {
                callback.moveBy(window, offX, offY);
            }

        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        down = false;
        DOM.releaseCapture(captureElement);
    }

    public IWindowCallback getWindowCallback() {
        return this.callback;
    }

    public void setWindowCallback(IWindowCallback callback) {
        this.callback = callback;
    }
}
