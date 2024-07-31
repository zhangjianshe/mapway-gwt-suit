package cn.mapway.ui.shared.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 数据处理事件接口
 */
public interface HasDataEventsHandlers extends HasHandlers {
    HandlerRegistration addDataEventsHandler(DataEventsHandler handler);
}
