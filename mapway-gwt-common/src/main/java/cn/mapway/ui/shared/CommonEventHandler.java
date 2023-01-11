package cn.mapway.ui.shared;

import com.google.gwt.event.shared.EventHandler;

/**
 * CommonEventHandler
 *
 * @author zhangjianshe@gmail.com
 */
public interface CommonEventHandler extends EventHandler {

    /**
     * On message.
     *
     * @param event
     */
    void onCommonEvent(CommonEvent event);

}
