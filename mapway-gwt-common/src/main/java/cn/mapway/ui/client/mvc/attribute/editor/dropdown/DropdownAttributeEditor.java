package cn.mapway.ui.client.mvc.attribute.editor.dropdown;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.*;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.EditorCodes;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
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
 * <p>
 * 1、在设计的时候可以通过组件选择器UI进行下拉选项的填写
 * 2.通过组件PortalWidget 的@Attr 注解的属性 可以通过 设置器 options字段构造 下拉选项
 * options 字段的内容为一个 JSON字符串 这个JSON字符串为一个数组
 * 参考格式如下
 * [
 * {"key":"上","value"：“up”,"init":true},
 * {"key":"下","value"：“down”,"init":true},
 * ]
 */
@AttributeEditor(value = DropdownAttributeEditor.EDITOR_CODE,
        name = "下拉框编辑器",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "一组数据的选择器",
        author = "ZJS",
        icon = Fonts.DROPDOWN
)
public class DropdownAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = EditorCodes.EDITOR_DROPDOWN;
    private static final DropdownAttributeEditorUiBinder ourUiBinder = GWT.create(DropdownAttributeEditorUiBinder.class);
    @UiField
    Dropdown ddlDropdown;
    @UiField
    DropdownListDesign designWidget;
    private boolean hasInit = false;

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
    public void onEditorOptionChanged(String key) {
        hasInit = false;
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

        if (!hasInit) {
            String dropdownParameter = option(ParameterKeys.KEY_OPTIONS, null);

            IOptionProvider optionProvider = attribute.getOptionProvider();
            if (optionProvider != null) {
                setOptionProvider(optionProvider);
            } else if (dropdownParameter != null && dropdownParameter.length() > 2) {
                try {
                    JsArrayLike<ParameterValue> arrayLike;
                    try {
                        String dataString = DataCastor.castToString(dropdownParameter);
                        arrayLike = Js.uncheckedCast(Global.JSON.parse(dataString));
                    } catch (Exception e) {
                        arrayLike = new JsArray<>();
                    }
                    OptionProvider optionProvider1 = new OptionProvider();

                    for (int i = 0; i < arrayLike.getLength(); i++) {
                        Object o = arrayLike.getAt(i);
                        ParameterValue data = Js.uncheckedCast(o);
                        //TODO 未来能不能 去掉Option 用ParameterValue代替
                        Option option = new Option(data.name, DataCastor.castToString(data.value));
                        option.setIcon(data.unicode);
                        option.setInitSelected(data.init);
                        optionProvider1.getOptions().add(option);
                    }
                    setOptionProvider(optionProvider1);

                } catch (Exception e) {
                    Logs.info("extract dropdwon list data error " + e.getMessage());
                }
            } else {
                OptionProvider optionProvider1 = new OptionProvider();
                for (ParameterValue data : attribute.getRuntimeParameters()) {
                    Option option = new Option(data.name, DataCastor.castToString(data.value));
                    option.setIcon(data.unicode);
                    option.setInitSelected(data.init);
                    optionProvider1.getOptions().add(option);
                }
                setOptionProvider(optionProvider1);
            }
            hasInit = true;
        }

        Object obj = getAttribute().getValue();
        ddlDropdown.setValue(DataCastor.castToString(obj), false);
    }


    @Override
    public void fromUI() {
        if (getAttribute() != null) {
            getAttribute().setValue(ddlDropdown.getValue());
        }
    }

    private void setOptionProvider(IOptionProvider provider) {
        ddlDropdown.clear();

        Object selectedValue = null;
        if (provider != null) {
            for (Option option : provider.getOptions()) {
                ddlDropdown.addItem(option.getIcon(), option.getText(), option.getValue());
                if (option.isInitSelected()) {
                    selectedValue = option.getValue();
                }
            }
        }
        if (selectedValue != null) {
            ddlDropdown.setValue(selectedValue, false);
        }
    }

    interface DropdownAttributeEditorUiBinder extends UiBinder<HTMLPanel, DropdownAttributeEditor> {
    }
}
