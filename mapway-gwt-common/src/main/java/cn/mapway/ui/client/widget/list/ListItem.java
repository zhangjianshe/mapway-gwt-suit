package cn.mapway.ui.client.widget.list;

import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ListItem extends CommonEventComposite implements IData<Object> {
    private static final ListItemUiBinder ourUiBinder = GWT.create(ListItemUiBinder.class);
    @UiField
    FontIcon icon;
    @UiField
    Label lbName;
    @UiField
    HorizontalPanel tools;
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
        lbName.setTitle(text);
    }

    public void setIcon(String iconUnicode) {
        if (StringUtil.isBlank(iconUnicode)) {
            icon.setVisible(false);
        } else {
            icon.setVisible(true);
            icon.setIconUnicode(iconUnicode);
        }
    }

    public void appendRight(Widget widget, Integer width) {
        if (widget == null) {
            return;
        }
        tools.add(widget);
        if (width != null) {
            tools.setCellWidth(widget, width + "px");
        }
    }

    public void clearRight() {
        tools.clear();
    }

    public Widget getRight(int index) {
        if (index >= 0 && index < tools.getWidgetCount()) {
            return tools.getWidget(index);
        }
        return null;
    }

    interface ListItemUiBinder extends UiBinder<HorizontalPanel, ListItem> {
    }
}