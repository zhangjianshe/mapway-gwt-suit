package cn.mapway.ui.client.mvc.decorator.link;

/**
 * IDrawCallback
 *
 * @author zhang
 */
public interface IDrawCallback {

    /**
     * 绘制连接线的操作过程回调
     *
     * @param linkEvent
     */
    void onLinkEvent(LinkEvent linkEvent);
}
