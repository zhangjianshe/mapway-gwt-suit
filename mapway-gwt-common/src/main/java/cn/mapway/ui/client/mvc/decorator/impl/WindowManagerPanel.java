package cn.mapway.ui.client.mvc.decorator.impl;

import cn.mapway.ui.client.mvc.decorator.IWindowCallback;
import cn.mapway.ui.client.mvc.decorator.IWindowDecorator;
import cn.mapway.ui.client.mvc.decorator.WindowDecoratorFactory;
import cn.mapway.ui.client.widget.DecoratorComposite;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * WindowManagerPanel
 *
 * @author zhang
 */
public class WindowManagerPanel extends AbsolutePanel implements IWindowCallback, RequiresResize, ProvidesResize {


    public WindowManagerPanel() {
        setStyleName("ai-window-manager");
    }

    @Override
    public void moveBy(IWindowDecorator windowDecorator, int offsetX, int offsetY) {
        Widget widget = windowDecorator.getWidget();
        int absoluteLeft = this.getWidgetLeft(widget);
        int absoluteTop = this.getWidgetTop(widget);
        absoluteLeft += offsetX;
        absoluteTop += offsetY;
        this.setWidgetPosition(widget, absoluteLeft, absoluteTop);
    }

    @Override
    public void onClose(boolean success) {

    }

    @Override
    public void onSelect(IWindowDecorator windowDecorator) {

    }

    @Override
    public void onResize() {
        for (int i = 0; i < this.getWidgetCount(); i++) {
            Widget widget = this.getWidget(i);
            if (widget instanceof RequiresResize) {
                RequiresResize requiresResize = (RequiresResize) widget;
                requiresResize.onResize();
            }
        }
    }

    /**
     * 通过装饰器 装饰一个Panel 或者 widget
     * 怎么装饰呢？
     * 1. 通过装饰工厂创建一个装饰器容器
     * 2. 将panel或者widget添加到容器中
     * 3. 将装饰器 Panle 或者 Widget 添加到窗口管理器中
     *
     * @param widget
     * @param decorator
     * @param left
     * @param top
     * @param width
     * @param height
     * @param addStyleNames
     */
    @UiChild(tagname = "child")
    public void addChild(Widget widget, String decorator, int left, int top, int width, int height, String addStyleNames) {
        IWindowDecorator windowDecorator = null;
        if (widget instanceof DecoratorComposite) {
            //如果窗口实现了装饰效果 直接使用
            DecoratorComposite decoratorComposite = (DecoratorComposite) widget;
            windowDecorator = decoratorComposite.getDecoratorWindow();
        }
        if (windowDecorator == null) {
            if (decorator != null && decorator.length() > 0) {
                //用户没有设定 装饰器 直接添加
                windowDecorator = WindowDecoratorFactory.get().create(decorator);
                windowDecorator.addChild(widget);
            }
        }
        Widget wrapper = null;
        if (windowDecorator != null) {
            windowDecorator.setWindowCallback(this);

            wrapper = windowDecorator.getWidget();
            wrapper.setPixelSize(width, height);
            if (addStyleNames != null) {
                wrapper.addStyleName(addStyleNames);
            }
        } else {
            wrapper = widget;
        }
        wrapper.setPixelSize(width, height);
        if (addStyleNames != null) {
            wrapper.addStyleName(addStyleNames);
        }
        this.add(wrapper, left, top);
    }

    @UiChild(tagname = "backLayer", limit = 1)
    public void addBackLayer(Widget widget, String addStyleNames) {
        this.add(widget, 0, 0);
        Style style = widget.getElement().getStyle();
        style.setRight(-0.5, Style.Unit.PX);
        style.setBottom(-0.5, Style.Unit.PX);
        if (addStyleNames != null) {
            widget.addStyleName(addStyleNames);
        }
    }
}
