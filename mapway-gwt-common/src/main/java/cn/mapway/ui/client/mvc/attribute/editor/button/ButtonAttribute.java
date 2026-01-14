package cn.mapway.ui.client.mvc.attribute.editor.button;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.shared.ButtonType;

/**
 * 表达按钮属性
 */
public abstract class ButtonAttribute extends AbstractAttribute {
    public ButtonAttribute(String code, String name) {
        super(code, name, ButtonAttributeEditor.EDITOR_CODE);
    }

    @Override
    public Object getValue() {
        return getAltName();
    }
    public ButtonAttribute asPrimary() {
        return setButtonType(ButtonType.PRIMARY);
    }

    public ButtonAttribute asSecondary() {
        return setButtonType(ButtonType.SECOND);
    }

    public ButtonAttribute asSuccess() {
        return setButtonType(ButtonType.SUCCESS);
    }

    public ButtonAttribute asWarning() {
        return setButtonType(ButtonType.WARNING);
    }

    public ButtonAttribute asError() {
        return setButtonType(ButtonType.ERROR);
    }

    public ButtonAttribute setButtonType(ButtonType buttonType) {
        replaceParameter(ParameterKeys.KEY_BUTTON_TYPE, buttonType.getValue(), false);
        return this;
    }
}
