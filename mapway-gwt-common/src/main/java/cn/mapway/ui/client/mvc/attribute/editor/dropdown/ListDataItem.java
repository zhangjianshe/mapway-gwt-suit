package cn.mapway.ui.client.mvc.attribute.editor.dropdown;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.buttons.AiCheckBox;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 列表框的选项编辑器
 */
public class ListDataItem extends CommonEventComposite implements IData<ParameterValue> {
    private static final ListDataItemUiBinder ourUiBinder = GWT.create(ListDataItemUiBinder.class);
    @UiField
    TextBox txtKey;
    @UiField
    TextBox txtValue;
    @UiField
    AiCheckBox checkInit;
    @UiField
    FontIcon btnDelete;
    private ParameterValue data;

    public ListDataItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        btnDelete.setIconUnicode(Fonts.REMOVE);
        txtKey.addChangeHandler(event -> {
            data.name = txtKey.getValue();
        });
        txtValue.addChangeHandler(event -> {
            data.value = txtValue.getValue();
        });
    }

    @Override
    public ParameterValue getData() {
        fromUI();
        return data;
    }


    @Override
    public void setData(ParameterValue obj) {
        data = obj;
        toUI();
    }

    private void toUI() {
        txtKey.setValue(data.name);
        txtValue.setValue(DataCastor.castToString(data.value));
        checkInit.setValue(data.init);
    }

    private void fromUI() {
        data.name = txtKey.getValue();
        data.value = txtValue.getValue();
        data.init = checkInit.getValue();
    }

    @UiHandler("btnDelete")
    public void btnDeleteClick(ClickEvent event) {
        fireEvent(CommonEvent.deleteEvent(null));
    }

    interface ListDataItemUiBinder extends UiBinder<HTMLPanel, ListDataItem> {
    }
}