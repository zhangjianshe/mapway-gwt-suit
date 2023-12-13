package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AttributeAdaptor;
import cn.mapway.ui.client.mvc.attribute.editor.impl.CheckBoxAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.impl.TextboxAttributeEditor;

/**
 * 文本框编辑属性
 */
public abstract class CheckBoxAttribute extends AttributeAdaptor {
    public CheckBoxAttribute(String name, String altName) {
        super(name, altName, CheckBoxAttributeEditor.EDITOR_CODE);
    }
}
