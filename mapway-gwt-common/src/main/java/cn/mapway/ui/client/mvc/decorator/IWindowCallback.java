package cn.mapway.ui.client.mvc.decorator;


/**
 * IWindowCallback
 *
 * @author zhang
 */
public interface IWindowCallback {
    void moveBy(IWindowDecorator windowDecorator, int offsetX, int offsetY);
    void onClose(boolean success);
    void onSelect(IWindowDecorator windowDecorator);
}
