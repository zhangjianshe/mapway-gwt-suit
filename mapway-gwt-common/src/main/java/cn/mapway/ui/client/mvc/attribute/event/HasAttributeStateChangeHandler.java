package cn.mapway.ui.client.mvc.attribute.event;

import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasAttributeStateChangeHandler extends HasHandlers {
    HandlerRegistration addAttributeStateChangeHandler(AttributeStateChangeEventHandler handler);
}
