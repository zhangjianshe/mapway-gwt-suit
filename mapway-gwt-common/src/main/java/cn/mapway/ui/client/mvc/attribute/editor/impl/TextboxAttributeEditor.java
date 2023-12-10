package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 文本框编辑器
 */
@AttributeEditor(TextboxAttributeEditor.EDITOR_CODE)
public class TextboxAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "TEXTBOX_EDITOR";
    private static final TextboxAttributeEditorUiBinder ourUiBinder = GWT.create(TextboxAttributeEditorUiBinder.class);
    @UiField
    TextBox txtBox;

    public TextboxAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        txtBox.addChangeHandler(event -> {
            getAttribute().setValue(castToValue(txtBox.getValue()));
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
    public void loadPopupData() {

    }

    @Override
    public Widget getDisplayWidget() {
        return txtBox;
    }


    @Override
    public void setAttribute(EditorOption editorOption, IAttribute attribute) {
        super.setAttribute(editorOption, attribute);
        updateUI();
    }

    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (getAttribute().isReadonly()) {
            txtBox.setReadOnly(true);
        }
        if (getAttribute().getTip() != null) {
            txtBox.getElement().setAttribute("placeholder", getAttribute().getTip());
        }
        Object obj = attribute.getValue();
        txtBox.setValue(castToString(obj));
    }


    interface TextboxAttributeEditorUiBinder extends UiBinder<HTMLPanel, TextboxAttributeEditor> {
    }
}