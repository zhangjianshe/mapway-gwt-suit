package cn.mapway.ui.shared.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 数据相关的事件数据
 */
public class DataEvents extends GwtEvent<DataEventsHandler> {
    public static Type<DataEventsHandler> TYPE = new Type<DataEventsHandler>();
    public DataEventKind kind;
    Object data;

    public DataEvents(DataEventKind kind, Object data) {
        this.kind = kind;
        this.data = data;
    }

    public static DataEvents create(DataEventKind kind, Object data) {
        return new DataEvents(kind, data);
    }

    public Type<DataEventsHandler> getAssociatedType() {
        return TYPE;
    }

    public DataEventKind getKind() {
        return kind;
    }

    public <T> T getData() {
        return (T) data;
    }

    protected void dispatch(DataEventsHandler handler) {
        handler.onDataEvents(this);
    }
}
