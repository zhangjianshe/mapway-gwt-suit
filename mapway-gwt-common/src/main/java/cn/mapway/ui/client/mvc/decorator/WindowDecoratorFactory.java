package cn.mapway.ui.client.mvc.decorator;

import com.google.gwt.core.client.GWT;

/**
 * WindowDecoratorFactory
 * 窗口装饰器的工厂类
 *
 * @author zhang
 */
public class WindowDecoratorFactory {
    private static final IWindowDecoratorFactory INSTANCE = GWT.create(IWindowDecoratorFactory.class);

    public static IWindowDecoratorFactory get() {
        return INSTANCE;
    }
}
