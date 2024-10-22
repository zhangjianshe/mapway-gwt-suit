package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextInputKind;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextboxAttributeEditor;

/**
 * 文本框编辑属性
 */
public abstract class TextBoxAttribute extends AbstractAttribute {
    public TextBoxAttribute(String name, String altName) {
        super(name, altName, TextboxAttributeEditor.EDITOR_CODE);
    }

    public TextBoxAttribute asNumber() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.NUMBER, false);
        return this;
    }

    public TextBoxAttribute asEmail() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.EMAIL, false);
        return this;
    }

    public TextBoxAttribute asDate() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.DATE, false);
        return this;
    }

    public TextBoxAttribute asTime() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.TIME, false);
        return this;
    }

    public TextBoxAttribute asUrl() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.URL, false);
        return this;
    }

    public TextBoxAttribute asDateTime() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.DATE_TIME, false);
        return this;
    }
}
