package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextboxAttributeEditor;

/**
 * 文本框编辑属性
 */
public abstract class TextBoxAttribute extends AbstractAttribute {
    public TextBoxAttribute(String name, String altName) {
        super(name, altName, TextboxAttributeEditor.EDITOR_CODE);
    }
}
