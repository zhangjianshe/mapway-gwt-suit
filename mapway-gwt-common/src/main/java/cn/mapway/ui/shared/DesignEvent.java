package cn.mapway.ui.shared;

public class DesignEvent {
    private final Object data;
    private final DesignEventType type;

    public DesignEvent(DesignEventType type, Object data) {
        this.data = data;
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public DesignEventType getType() {
        return type;
    }
}
