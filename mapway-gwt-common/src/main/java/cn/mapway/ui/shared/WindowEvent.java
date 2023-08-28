package cn.mapway.ui.shared;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 窗口事件
 */
public class WindowEvent extends GwtEvent<WindowEventHandler> {
    private static final int WE_TITLE = 0;
    public static Type<WindowEventHandler> TYPE = new Type<WindowEventHandler>();
    private final int type;
    private final Object data;

    public WindowEvent(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public static WindowEvent titleEvent(WindowTitleData data) {
        return new WindowEvent(WE_TITLE, data);
    }

    public int getType() {
        return type;
    }

    public boolean isTitleEvent() {
        return type == WE_TITLE;
    }

    public Type<WindowEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(WindowEventHandler handler) {
        handler.onWindowEvent(this);
    }
}
