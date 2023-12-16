package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.impl.CheckBoxAttributeEditor;

/**
 * 文本框编辑属性
 */
public abstract class CheckBoxAttribute extends AbstractAttribute {
    public CheckBoxAttribute(String name, String altName) {
        super(name, altName, CheckBoxAttributeEditor.EDITOR_CODE);
    }
}
