package cn.mapway.ui.client.mvc.attribute.editor.button;

import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.mvc.attribute.editor.checkbox.CheckBoxAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.marker.AbstractEditorMetaData;
import cn.mapway.ui.shared.ButtonType;

public class ButtonEditorMetaData extends AbstractEditorMetaData {
    public ButtonEditorMetaData() {
        super(CheckBoxAttributeEditor.EDITOR_CODE);
    }

    @Override
    protected void initMetaData() {

    }

    public ButtonEditorMetaData asPrimary() {
        return setButtonType(ButtonType.PRIMARY);
    }

    public ButtonEditorMetaData asSecondary() {
        return setButtonType(ButtonType.SECOND);
    }

    public ButtonEditorMetaData asSuccess() {
        return setButtonType(ButtonType.SUCCESS);
    }

    public ButtonEditorMetaData asWarning() {
        return setButtonType(ButtonType.WARNING);
    }

    public ButtonEditorMetaData asError() {
        return setButtonType(ButtonType.ERROR);
    }

    public ButtonEditorMetaData setButtonType(ButtonType buttonType) {
        replaceParameter(ParameterKeys.KEY_BUTTON_TYPE, buttonType.getValue(), false);
        return this;
    }

}
