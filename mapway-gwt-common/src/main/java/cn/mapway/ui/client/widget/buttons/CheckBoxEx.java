package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.tools.IData;

public class CheckBoxEx implements IData {
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
