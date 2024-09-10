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

    public final static DesignEvent createDraw(Object data) {
        return new DesignEvent(DesignEventType.Draw, data);
    }

    public final static DesignEvent createResize(Object data) {
        return new DesignEvent(DesignEventType.Resize, data);
    }

    public final static DesignEvent createRefresh(Object data) {
        return new DesignEvent(DesignEventType.Refresh, data);
    }
    public final static DesignEvent createCreate(Object data) {
        return new DesignEvent(DesignEventType.Create, data);
    }

    public final static DesignEvent createLocked(Object data) {
        return new DesignEvent(DesignEventType.Locked, data);
    }

    public final static DesignEvent createCopy(Object data) {
        return new DesignEvent(DesignEventType.Copy, data);
    }

    public final static DesignEvent createPaste(Object data) {
        return new DesignEvent(DesignEventType.Paste, data);
    }

    public final static DesignEvent createUndo(Object data) {
        return new DesignEvent(DesignEventType.Undo, data);
    }

    public final static DesignEvent createRedo(Object data) {
        return new DesignEvent(DesignEventType.Redo, data);
    }
    public final static DesignEvent createLeft(Object data){
        return new DesignEvent(DesignEventType.Left,data);
    }

    public final static DesignEvent createRight(Object data){
        return new DesignEvent(DesignEventType.Right,data);
    }

    public final static DesignEvent createAlignLeft(Object data) {
        return new DesignEvent(DesignEventType.AlignLeft, data);
    }

    public final static DesignEvent createAlignRight(Object data) {
        return new DesignEvent(DesignEventType.AlignRight, data);
    }

    public final static DesignEvent createAlignTop(Object data) {
        return new DesignEvent(DesignEventType.AlignTop, data);
    }

    public final static DesignEvent createAlignBottom(Object data) {
        return new DesignEvent(DesignEventType.AlignBottom, data);
    }

    public final static DesignEvent createAlignCenter(Object data) {
        return new DesignEvent(DesignEventType.AlignCenter, data);
    }

    public final static DesignEvent createAlignMiddle(Object data) {
        return new DesignEvent(DesignEventType.AlignMiddle, data);
    }



    public final static DesignEvent createSave(Object data) {
        return new DesignEvent(DesignEventType.Save, data);
    }

    public final static DesignEvent createOpen(Object data) {
        return new DesignEvent(DesignEventType.Open, data);
    }

    public Object getData() {
        return data;
    }

    public DesignEventType getType() {
        return type;
    }
}
