package cn.mapway.ui.client.mvc.attribute.editor.checkbox;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValues;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.widget.buttons.AiCheckBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

@AttributeEditor(value = CheckBoxAttributeEditor.EDITOR_CODE,
        name = "布尔型编辑器",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "布尔型的编辑器",
        author = "ZJS",
        icon = Fonts.CONTROLER
)
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
    public void editAttribute(ParameterValues runtimeOption, IAttribute attribute) {
        super.editAttribute(runtimeOption, attribute);
        updateUI();
    }


    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (attribute == null) {
            return;
        }
        if (getAttribute().isReadonly()) {
            checkBox.setEnabled(false);
        }
        if (getAttribute().getTip() != null) {
            checkBox.setTitle(getAttribute().getTip());
        }
        Object obj = attribute.getValue();
        checkBox.setValue(DataCastor.castToBoolean(obj));
    }

    @Override
    public void fromUI() {
        if(getAttribute()!=null){
            getAttribute().setValue(checkBox.getValue());
        }
    }

    interface CheckBoxAttributeEditorUiBinder extends UiBinder<HTMLPanel, CheckBoxAttributeEditor> {
    }
}