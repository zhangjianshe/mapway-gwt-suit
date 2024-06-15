package cn.mapway.ui.client.mvc.attribute;

/**
 * 属性的特性发生变化的时候通知这个事件
 * 比如 属性不可见 或者 可见
 */
public interface IAttributePropertyChangeCallback {
    /**
     * 属性更新后 调用这个方法通知
     */
    void onAttributePropertyChange(IAttribute senderAttribute);
}
