package cn.mapway.ui.client.mvc.attribute.editor.textbox;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValues;
import cn.mapway.ui.client.mvc.attribute.editor.*;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 文本框编辑器
 */
@AttributeEditor(value = TextboxAttributeEditor.EDITOR_CODE,
        name = "单行文本编辑器",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "文本输入",
        author = "ZJS",
        icon = Fonts.RENAME
)
public class TextboxAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = EditorCodes.EDITOR_TEXTBOX;
    private static final TextboxAttributeEditorUiBinder ourUiBinder = GWT.create(TextboxAttributeEditorUiBinder.class);
    @UiField
    TextBox txtBox;

    public TextboxAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        txtBox.addChangeHandler(event -> {
            if (getAttribute() != null) {
                getAttribute().setValue(castToValue(txtBox.getValue()));
            }
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
        return txtBox;
    }



    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (attribute == null) {
            return;
        }
        if (getAttribute().isReadonly()) {
            txtBox.setReadOnly(true);
        }
        if (getAttribute().getTip() != null) {
            txtBox.getElement().setAttribute("placeholder", getAttribute().getTip());
        }
        Object obj = attribute.getValue();
        if (obj == null) {
            txtBox.setValue(castToString(attribute.getDefaultValue()));

        } else {
            txtBox.setValue(castToString(obj));

        }
    }

    @Override
    public void fromUI() {
        IAttribute attribute = getAttribute();
        if (attribute != null && !attribute.isReadonly()) {
            getAttribute().setValue(txtBox.getValue());
        }
    }

    interface TextboxAttributeEditorUiBinder extends UiBinder<HTMLPanel, TextboxAttributeEditor> {
    }
}
