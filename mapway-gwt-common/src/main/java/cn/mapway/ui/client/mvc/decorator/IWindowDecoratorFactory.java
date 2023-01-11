package cn.mapway.ui.client.mvc.decorator;

import java.util.List;

/**
 * IWindowDecoratorFactory
 * 窗口装饰器的工厂类
 * @author zhang
 */
public interface IWindowDecoratorFactory {
    IWindowDecorator create(String decorator);
    List<WindowDecoratorInfo> getDecorators();
}
