package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorInfo;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;


public class EditorHorizontalPanel extends CommonEventComposite implements IData<List<AttributeEditorInfo>> {
    private static final EditorHoritalPanelUiBinder ourUiBinder = GWT.create(EditorHoritalPanelUiBinder.class);
    @UiField
    HorizontalPanel list;
    List<AttributeEditorInfo> data;
    EditItem selectedItem = null;
    private final ClickHandler clickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            selectItem((EditItem) event.getSource());
        }
    };

    public EditorHorizontalPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        data = new ArrayList<>();
    }

    private void selectItem(EditItem newItem) {
        if (selectedItem != null) {
            selectedItem.setSelect(false);
            selectedItem = null;
        }
        selectedItem = newItem;
        selectedItem.setSelect(true);
        fireEvent(CommonEvent.selectEvent(selectedItem.getData()));
    }

    @Override
    public List<AttributeEditorInfo> getData() {
        return data;
    }

    @Override
    public void setData(List<AttributeEditorInfo> obj) {
        this.data = obj;
        toUI();
    }

    private void toUI() {
        list.clear();
        for (AttributeEditorInfo info : data) {
            EditItem editItem = new EditItem();
            editItem.addDomHandler(clickHandler, ClickEvent.getType());
            editItem.setData(info);
            list.add(editItem);
        }
    }

    public void selectEditorCode(String initEditorCode) {
        if (initEditorCode == null || initEditorCode.length() == 0) {
        }

        for (int i = 0; i < list.getWidgetCount(); i++) {
            Widget widget = list.getWidget(i);
            if (widget instanceof EditItem) {
                EditItem item = (EditItem) widget;
                if (initEditorCode.equals(item.getData().code)) {
                    selectItem(item);
                    return;
                }
            }
        }

    }

    interface EditorHoritalPanelUiBinder extends UiBinder<ScrollPanel, EditorHorizontalPanel> {
    }
}