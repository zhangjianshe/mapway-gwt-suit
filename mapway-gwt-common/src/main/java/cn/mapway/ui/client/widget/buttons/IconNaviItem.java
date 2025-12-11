package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;

public class IconNaviItem extends CommonEventComposite implements IData<Object> {
    private static final IconNaviItemUiBinder ourUiBinder = GWT.create(IconNaviItemUiBinder.class);
    @UiField
    FontIcon icon;
    @UiField
    Anchor btn;
    Object obj;

    public IconNaviItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        addDomHandler(event -> {
            fireEvent(CommonEvent.selectEvent(obj));
        }, ClickEvent.getType());
    }

    public void setUnicode(String unicode) {
        icon.setIconUnicode(unicode);
    }

    public void setText(String text) {
        btn.setText(text);
    }

    /**
     * @return
     */
    @Override
    public Object getData() {
        return obj;
    }

    /**
     * @param o
     */
    @Override
    public void setData(Object o) {
        this.obj = o;
    }

    interface IconNaviItemUiBinder extends UiBinder<HTMLPanel, IconNaviItem> {
    }
}