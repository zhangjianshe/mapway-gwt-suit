package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.*;
import cn.mapway.ui.client.mvc.attribute.design.IEditorData;
import cn.mapway.ui.client.mvc.attribute.editor.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.design.DropdownListDesign;
import cn.mapway.ui.client.mvc.attribute.editor.design.DropdownListDesignData;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.widget.Dropdown;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.core.Global;
import elemental2.core.JsArray;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;

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
    @UiField
    DropdownListDesign designWidget;

    public DropdownAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ddlDropdown.addValueChangeHandler(event -> {
            if (getAttribute() != null) {
                Object obj = event.getValue();
                getAttribute().setValue(DataCastor.castToString(obj));
            }
        });
    }

    /**
     * 设计期间的参数设计器
     *
     * @return
     */
    @Override
    public DropdownListDesign getDesigner() {
        return designWidget;
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
    public void setAttribute(EditorOption runtimeOption, IAttribute attribute) {
        super.setAttribute(runtimeOption, attribute);
        updateUI();
    }

    /**
     * 当数据发生变化后 调用这个方法更新界面的数据
     */
    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (attribute == null) {
            return;
        }
        if (attribute.isReadonly()) {
            ddlDropdown.setEnabled(false);
        }

        String dropdownParameter = option(EditorOption.KEY_DROPDOWN_OPTIONS,null);

        IOptionProvider optionProvider = attribute.getOptionProvider();
        if (optionProvider != null) {
            setOptionProvider(optionProvider);
        } else if (dropdownParameter != null) {
            try {
                JsArrayLike<DropdownListDesignData> arrayLike;
                try {
                    String dataString = DataCastor.castToString(dropdownParameter);
                    arrayLike = Js.uncheckedCast(Global.JSON.parse(dataString));
                } catch (Exception e) {
                    arrayLike = new JsArray<>();
                }
                OptionProvider optionProvider1 = new OptionProvider();

                for (int i = 0; i < arrayLike.getLength(); i++) {
                    Object o = arrayLike.getAt(i);
                    DropdownListDesignData data = Js.uncheckedCast(o);
                    Option option = new Option(data.key, data.value);
                    option.setInitSelected(data.init);
                    optionProvider1.getOptions().add(option);
                }
                setOptionProvider(optionProvider1);

            } catch (Exception e) {
                Logs.info("extract dropdwon list data error " + e.getMessage());
            }
        }
        Object obj = getAttribute().getValue();
        ddlDropdown.setValue(obj, false);
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