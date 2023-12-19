package cn.mapway.ui.client.mvc.attribute.editor.editorselector;

import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorInfo;
import cn.mapway.ui.client.mvc.decorator.ISelectable;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 输入编辑器项
 */
public class EditItem extends Composite implements IData<AttributeEditorInfo>, ISelectable {
    private static final EditItemUiBinder ourUiBinder = GWT.create(EditItemUiBinder.class);
    @UiField
    Label lbName;
    @UiField
    FontIcon fontIcon;
    private AttributeEditorInfo info;

    public EditItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public AttributeEditorInfo getData() {
        return info;
    }

    @Override
    public void setData(AttributeEditorInfo obj) {
        this.info = obj;
        toUI();
    }


    private void toUI() {
        lbName.setText(info.name);
        fontIcon.setIconUnicode(info.icon);
    }

    @Override
    public void setSelect(boolean select) {
        if (select) {
            getElement().setAttribute(ISelectable.SELECT_ATTRIBUTE, "true");
        } else {
            getElement().removeAttribute(ISelectable.SELECT_ATTRIBUTE);
        }
    }

    interface EditItemUiBinder extends UiBinder<HTMLPanel, EditItem> {
    }
}