package cn.mapway.ui.client.mvc.attribute.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 属性组件状态改变事件处理句柄
 */
public interface AttributeStateChangeEventHandler extends EventHandler {
    void onAttributeStateChange(AttributeStateChangeEvent event);
}
