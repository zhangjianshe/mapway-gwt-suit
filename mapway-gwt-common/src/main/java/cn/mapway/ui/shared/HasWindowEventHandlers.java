package cn.mapway.ui.shared;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasWindowEventHandlers extends HasHandlers {
    HandlerRegistration addCommonHandler(WindowEventHandler handler);
}
