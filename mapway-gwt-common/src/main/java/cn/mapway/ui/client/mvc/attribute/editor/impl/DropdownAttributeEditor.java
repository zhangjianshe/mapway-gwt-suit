package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.*;
import cn.mapway.ui.client.mvc.attribute.editor.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.widget.Dropdown;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 下拉框的属性编辑器
 */
@AttributeEditor(value = DropdownAttributeEditor.EDITOR_CODE,
        name = "下拉框编辑器",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "一组数据的选择器",
        author = "ZJS",
        icon = Fonts.DROPDOWN
)
public class DropdownAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "DROPDOWN_EDITOR";
    private static final DropdownAttributeEditorUiBinder ourUiBinder = GWT.create(DropdownAttributeEditorUiBinder.class);
    @UiField
    Dropdown ddlDropdown;

    public DropdownAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ddlDropdown.addValueChangeHandler(event -> {
            Object obj = event.getValue();
            getAttribute().setValue(DataCastor.castToString(obj));
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
        return ddlDropdown;
    }

    @Override
    public void setAttribute(EditorOption editorOption, IAttribute attribute) {
        super.setAttribute(editorOption, attribute);
        updateUI();
    }

    /**
     * 当数据发生变化后 调用这个方法更新界面的数据
     */
    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (getAttribute().isReadonly()) {
            ddlDropdown.setEnabled(false);
        }

        IOptionProvider optionProvider = attribute.getOptionProvider();
        if (optionProvider != null) {
            setOptionProvider(optionProvider);
        } else if (attribute.getOptions() != null && attribute.getOptions().length() > 0) {
            OptionProvider optionProvider1 = new OptionProvider();
            optionProvider1.parse(attribute.getOptions());
            setOptionProvider(optionProvider1);
        }

        Object obj = attribute.getValue();
        ddlDropdown.setValue(obj, true);
    }

    private void setOptionProvider(IOptionProvider provider) {
        ddlDropdown.clear();
        if (provider != null) {
            for (Option option : provider.getOptions()) {
                ddlDropdown.addItem(option.getIcon(), option.getText(), option.getValue());
            }
        }
    }

    interface DropdownAttributeEditorUiBinder extends UiBinder<HTMLPanel, DropdownAttributeEditor> {
    }
}