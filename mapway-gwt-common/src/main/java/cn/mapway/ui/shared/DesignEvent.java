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
    public Object getData() {
        return data;
    }

    public DesignEventType getType() {
        return type;
    }
}
