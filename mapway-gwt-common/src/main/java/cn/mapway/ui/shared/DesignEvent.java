package cn.mapway.ui.shared;

public class DesignEvent {
    private final Object data;
    private final DesignEventType type;

    public DesignEvent(DesignEventType type, Object data) {
        this.data = data;
        this.type = type;
    }

    public final static DesignEvent create(DesignEventType type, Object data) {
        return new DesignEvent(type, data);
    }

    public final static DesignEvent createLayout(Object data) {
        return new DesignEvent(DesignEventType.Layout, data);
    }

    public final static DesignEvent createSelect(Object data) {
        return new DesignEvent(DesignEventType.Select, data);
    }
    public final static DesignEvent createUpdate(Object data) {
        return new DesignEvent(DesignEventType.Update, data);
    }

    public final static DesignEvent createMove(Object data) {
        return new DesignEvent(DesignEventType.Move, data);
    }

    public final static DesignEvent createDelete(Object data) {
        return new DesignEvent(DesignEventType.Delete, data);
    }

    public final static DesignEvent createAdd(Object data) {
        return new DesignEvent(DesignEventType.Add, data);
    }

    public final static DesignEvent createUp(Object data) {
        return new DesignEvent(DesignEventType.Up, data);
    }

    public final static DesignEvent createDown(Object data) {
        return new DesignEvent(DesignEventType.Down, data);
    }

    public final static DesignEvent createOk(Object data) {
        return new DesignEvent(DesignEventType.Ok, data);
    }

    public final static DesignEvent createCancel(Object data) {
        return new DesignEvent(DesignEventType.Cancel, data);
    }

    public final static DesignEvent createMenu(Object data) {
        return new DesignEvent(DesignEventType.Menu, data);
    }

    public final static DesignEvent createMode(Object data) {
        return new DesignEvent(DesignEventType.Mode, data);
    }

    public Object getData() {
        return data;
    }

    public DesignEventType getType() {
        return type;
    }
}
