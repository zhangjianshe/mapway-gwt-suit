package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AttributeAdaptor;
import cn.mapway.ui.client.mvc.attribute.editor.impl.TextboxAttributeEditor;

/**
 * 文本框编辑属性
 */
public abstract class TextBoxAttribute extends AttributeAdaptor {
    public TextBoxAttribute(String name, String altName) {
        super(name, altName, TextboxAttributeEditor.EDITOR_CODE);
    }
}
