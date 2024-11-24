package cn.mapway.ui.client.mvc.attribute.editor.sys;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;

public abstract class TextAreaAttribute extends AbstractAttribute {
    public TextAreaAttribute(String name, String altName) {
        super(name, altName, TextAreaAttributeEditor.EDITOR_CODE);
    }
}
