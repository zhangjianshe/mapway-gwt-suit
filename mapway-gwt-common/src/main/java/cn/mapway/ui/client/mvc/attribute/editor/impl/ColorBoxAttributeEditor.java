package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import cn.mapway.ui.client.widget.color.AiColor;
import cn.mapway.ui.client.widget.color.ColorBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 颜色  属性编辑
 */
@AttributeEditor(value=ColorBoxAttributeEditor.EDITOR_CODE,
        name = "颜色编辑器",
        group = "运行时",
        summary = "选择一个颜色#RGBA",
        author = "ZJS")
public class ColorBoxAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "COLOR_EDITOR";
    private static final ColorBoxAttributeEditorUiBinder ourUiBinder = GWT.create(ColorBoxAttributeEditorUiBinder.class);
    @UiField
    AiColor txtColor;

    public ColorBoxAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        txtColor.addValueChangeHandler(event -> {
            getAttribute().setValue(txtColor.getValue());
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
        return txtColor;
    }


    @Override
    public void setAttribute(EditorOption editorOption, IAttribute attribute) {
        super.setAttribute(editorOption,attribute);
        updateUI();
    }

    public void updateUI() {
        IAttribute attribute = getAttribute();
        Object obj = attribute.getValue();
        txtColor.setValue(castToString(obj));
    }

    interface ColorBoxAttributeEditorUiBinder extends UiBinder<HTMLPanel, ColorBoxAttributeEditor> {
    }
}