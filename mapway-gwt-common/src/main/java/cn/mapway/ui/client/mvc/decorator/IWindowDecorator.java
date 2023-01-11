package cn.mapway.ui.client.mvc.decorator;

import cn.mapway.ui.client.mvc.decorator.link.AnchorInfo;
import cn.mapway.ui.client.mvc.decorator.link.Links;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * IWindowDecorator
 * 窗口装饰器
 * 提供窗口操作之外 还提供一个通用事件处理器
 *
 * @author zhang
 */
public interface IWindowDecorator extends HasCommonHandlers {

    String getId();

    void setId(String id);

    /**
     * 获取装饰器的UI Widget
     * 装饰器 包含了一个widget
     *
     * @return
     */
    Widget getWidget();

    /**
     * 装饰器包裹的窗口
     *
     * @param widget
     */
    void addChild(Widget widget);

    /**
     * 获取装饰器的窗口
     *
     * @return
     */
    Widget getChild();


    /**
     * 设置窗口的回调
     *
     * @param callback
     */
    void setWindowCallback(IWindowCallback callback);

    /**
     * 坐标是否在组件内
     *
     * @param x 相对于组件的坐标
     * @param y 相对于组件的坐标
     * @return
     */
    boolean contain(double x, double y);

    Links getOutLinks();

    Links getInLinks();

    /**
     * 获取一个连接终端点的坐标
     * 次坐标为窗口内的局部坐标
     *
     * @param anchorId
     * @return
     */
    AnchorInfo findAnchor(int anchorId);

    /**
     * 根据鼠标信息获取anchor信息
     *
     * @param mouseEvent
     * @return
     */
    AnchorInfo findAnchorByMouseEvent(MouseEvent mouseEvent);

    /**
     * 删除窗口的link
     *
     * @return
     */
    boolean removeLink(String linkId);


}
