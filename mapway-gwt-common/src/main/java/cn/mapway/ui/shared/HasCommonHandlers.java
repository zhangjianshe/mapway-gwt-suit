package cn.mapway.ui.shared;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * HasCommonHandlers
 * 通用句柄处理接口
 * @author zhangjianshe@gmail.com
 */
public interface HasCommonHandlers extends HasHandlers {
    HandlerRegistration addCommonHandler(CommonEventHandler handler);
}
