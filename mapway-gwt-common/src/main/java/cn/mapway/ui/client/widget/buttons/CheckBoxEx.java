package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.tools.IData;
import com.google.gwt.user.client.ui.CheckBox;

public class CheckBoxEx extends CheckBox implements IData {
    Object data;

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        this.data = obj;
    }
}
