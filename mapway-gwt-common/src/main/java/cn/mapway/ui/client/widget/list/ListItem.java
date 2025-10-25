package cn.mapway.ui.client.widget.list;

import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class ListItem extends CommonEventComposite implements IData<Object> {
    private static final ListItemUiBinder ourUiBinder = GWT.create(ListItemUiBinder.class);
    @UiField
    FontIcon icon;
    @UiField
    Label lbName;
    private Object data;

    public ListItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public Object getData() {
        return data;
    }


    @Override
    public void setData(Object obj) {
        data = obj;
    }

    public void setText(String text) {
        lbName.setText(text);
    }

    public void setIcon(String iconUnicode) {
        if (StringUtil.isBlank(iconUnicode)) {
            icon.setVisible(false);
        } else {
            icon.setVisible(true);
            icon.setIconUnicode(iconUnicode);
        }
    }

    interface ListItemUiBinder extends UiBinder<HTMLPanel, ListItem> {
    }
}