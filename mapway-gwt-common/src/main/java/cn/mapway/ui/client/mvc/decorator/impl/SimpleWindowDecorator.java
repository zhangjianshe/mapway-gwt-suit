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
 * SimpleWindowDecorator
 * 具有装饰边框的表单组件。与 HTML的 Form没有关系
 * 这个组件可以作为其他组件的一个容器 拥有自定义的边框
 * uibinder 中使用 通过 下面的@UIChild 方法可以添加子组件
 * <p>
 * 该组件可以拖动
 *
 * @author zhang
 */
@WindowDecorator("simple")
public class SimpleWindowDecorator extends AbstractDecoratorPanel implements IWindowDecorator, RequiresResize, ProvidesResize {
    private static final SimpleWindowDecoratorUiBinder ourUiBinder = GWT.create(SimpleWindowDecoratorUiBinder.class);
    @UiField
    LayoutPanel centerPanel;
    @UiField
    LayoutPanel rootLayoutPanel;
    WindowDraggerAdaptor windowDraggerAdaptor;
    Widget child;

    public SimpleWindowDecorator() {
        setWidget(ourUiBinder.createAndBindUi(this));
        windowDraggerAdaptor = new WindowDraggerAdaptor(this, rootLayoutPanel, null);
    }

    @Override
    public void onResize() {
        rootLayoutPanel.onResize();
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    public void setPixelSize(int width, int height) {
        rootLayoutPanel.setPixelSize(width, height);
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

    interface SimpleWindowDecoratorUiBinder extends UiBinder<LayoutPanel, SimpleWindowDecorator> {
    }
}
