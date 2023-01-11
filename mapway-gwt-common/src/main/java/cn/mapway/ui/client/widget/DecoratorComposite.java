package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.mvc.decorator.IWindowDecorator;
import cn.mapway.ui.client.mvc.decorator.WindowDecoratorFactory;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * 装饰容器
 *
 * @author zhang
 */
public class DecoratorComposite extends Composite {
    IWindowDecorator decoratorWindow;

    public DecoratorComposite(String decorator) {
        super();
        if (decorator != null && decorator.length() > 0) {
            decoratorWindow = WindowDecoratorFactory.get().create(decorator);
        }
    }

    @Override
    protected void initWidget(Widget widget) {
        if (decoratorWindow != null) {
            decoratorWindow.addChild(widget);
            super.initWidget(decoratorWindow.getWidget());
        } else {
            super.initWidget(widget);
        }
    }

    public IWindowDecorator getDecoratorWindow() {
        return decoratorWindow;
    }

}
