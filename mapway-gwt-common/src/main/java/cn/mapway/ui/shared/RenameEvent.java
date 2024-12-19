package cn.mapway.ui.shared;

import com.google.gwt.user.client.ui.Widget;

public class RenameEvent {
    public String oldName;
    public String newName;
    public Widget widget;

    public RenameEvent(String oldName, String newName, Widget widget) {
        this.oldName = oldName;
        this.newName = newName;
        this.widget = widget;
    }
}
