package cn.mapway.ui.client.mvc.attribute;

/**
 * IAttributeUpdateCallback
 *
 * @author zhang
 */
public interface IAttributeReadyCallback {
    /**
     * 属性准备好后 调用这个方法通知
     */
    void onAttributeReady(IAttributesProvider attributeProvider);
}
