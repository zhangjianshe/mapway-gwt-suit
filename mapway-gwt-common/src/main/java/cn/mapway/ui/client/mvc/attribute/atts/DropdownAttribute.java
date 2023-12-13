package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AttributeAdaptor;
import cn.mapway.ui.client.mvc.attribute.editor.impl.DropdownAttributeEditor;

/**
 * 文本框编辑属性
 */
public abstract class DropdownAttribute extends AttributeAdaptor {
    public DropdownAttribute(String name, String altName) {
        super(name, altName, DropdownAttributeEditor.EDITOR_CODE);
    }
}
