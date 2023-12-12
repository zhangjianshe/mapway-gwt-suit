package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import cn.mapway.ui.client.widget.buttons.AiCheckBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

@AttributeEditor(CheckBoxAttributeEditor.EDITOR_CODE)
public class CheckBoxAttributeEditor extends AbstractAttributeEditor<Boolean> {
    public static final String EDITOR_CODE = "CHECKBOX_EDITOR";
    private static final CheckBoxAttributeEditorUiBinder ourUiBinder = GWT.create(CheckBoxAttributeEditorUiBinder.class);
    @UiField
    AiCheckBox checkBox;

    public CheckBoxAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        checkBox.addValueChangeHandler(event -> {
            getAttribute().setValue(event.getValue());
        });
    }

    /**
     * 编辑器的唯一识别代码
     *
     * @return
     */
    @Override
    public String getCode() {
        return EDITOR_CODE;
    }

    @Override
    public Widget getDisplayWidget() {
        return checkBox;
    }


    @Override
    public void setAttribute(EditorOption editorOption, IAttribute attribute) {
        super.setAttribute(editorOption, attribute);
        updateUI();
    }


    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (getAttribute().isReadonly()) {
            checkBox.setEnabled(false);
        }
        if (getAttribute().getTip() != null) {
            checkBox.setTitle(getAttribute().getTip());
        }
        Object obj = attribute.getValue();
        checkBox.setValue(DataCastor.castToBoolean(obj));
    }

    interface CheckBoxAttributeEditorUiBinder extends UiBinder<HTMLPanel, CheckBoxAttributeEditor> {
    }
}