package cn.mapway.ui.client.mvc.decorator.impl;

import cn.mapway.ui.client.mvc.decorator.IWindowCallback;
import cn.mapway.ui.client.mvc.decorator.IWindowDecorator;
import cn.mapway.ui.client.mvc.decorator.link.AnchorInfo;
import cn.mapway.ui.client.mvc.decorator.link.Links;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * DefaultWindowDecorator
 *
 * @author zhang
 */
public class DefaultWindowDecorator extends AbstractDecoratorPanel implements IWindowDecorator {
    Widget child;

    @Override
    public void addChild(Widget widget) {
        child = widget;
        add(widget);
    }

    @Override
    public Widget getChild() {
        return child;
    }


    @Override
    public void setWindowCallback(IWindowCallback callback) {

    }

    @Override
    public boolean contain(double x, double y) {
        return false;
    }

    @Override
    public Links getOutLinks() {
        return null;
    }

    @Override
    public Links getInLinks() {
        return null;
    }

    @Override
    public AnchorInfo findAnchor(int anchorId) {
        return null;
    }

    @Override
    public AnchorInfo findAnchorByMouseEvent(MouseEvent mouseEvent) {
        return null;
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }

}
