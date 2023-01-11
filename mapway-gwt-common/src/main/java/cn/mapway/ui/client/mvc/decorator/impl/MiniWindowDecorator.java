package cn.mapway.ui.client.mvc.decorator.impl;

import cn.mapway.ui.client.mvc.decorator.IWindowCallback;
import cn.mapway.ui.client.mvc.decorator.IWindowDecorator;
import cn.mapway.ui.client.mvc.decorator.WindowDecorator;
import cn.mapway.ui.client.mvc.decorator.link.AnchorInfo;
import cn.mapway.ui.client.mvc.decorator.link.Links;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * MiniWindowDecorator
 *
 * @author zhang
 */
@WindowDecorator("mini")
public class MiniWindowDecorator extends AbstractDecoratorPanel implements IWindowDecorator, RequiresResize, ProvidesResize {
    private static final MiniWindowDecoratorUiBinder ourUiBinder = GWT.create(MiniWindowDecoratorUiBinder.class);
    WindowDraggerAdaptor windowDraggerAdaptor;
    @UiField
    LayoutPanel rootLayoutPanel;
    @UiField
    LayoutPanel centerPanel;
    Widget child;

    public MiniWindowDecorator() {
        setWidget(ourUiBinder.createAndBindUi(this));
        windowDraggerAdaptor = new WindowDraggerAdaptor(this, rootLayoutPanel, null);
    }

    @Override
    public void onResize() {
        rootLayoutPanel.onResize();
    }

    @Override
    public void setPixelSize(int width, int height) {
        rootLayoutPanel.setPixelSize(width, height);
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    @UiChild(tagname = "child")
    public void addChild(Widget widget) {
        child = widget;
        centerPanel.add(widget);
    }

    @Override
    public Widget getChild() {
        return child;
    }


    @Override
    public void setWindowCallback(IWindowCallback callback) {
        windowDraggerAdaptor.setWindowCallback(callback);
    }

    @Override
    public boolean contain(double x, double y) {
        return false;
    }

    @Override
    public Links getOutLinks() {
        return new Links();
    }

    @Override
    public Links getInLinks() {
        return new Links();
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

    interface MiniWindowDecoratorUiBinder extends UiBinder<LayoutPanel, MiniWindowDecorator> {
    }
}
