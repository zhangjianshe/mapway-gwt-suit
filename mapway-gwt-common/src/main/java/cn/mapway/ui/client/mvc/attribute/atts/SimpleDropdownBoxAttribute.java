package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.impl.DropdownAttributeEditor;

/**
 * 文本框编辑属性
 */
public class SimpleDropdownBoxAttribute extends AbstractAttribute {
    Object value;

    public SimpleDropdownBoxAttribute(String name, String altName) {
        super(name, altName, DropdownAttributeEditor.EDITOR_CODE);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }
}
