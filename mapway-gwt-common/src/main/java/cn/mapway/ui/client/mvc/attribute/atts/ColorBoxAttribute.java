package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AttributeAdaptor;
import cn.mapway.ui.client.mvc.attribute.editor.impl.ColorBoxAttributeEditor;

/**
 * 文本框编辑属性
 */
public abstract class ColorBoxAttribute extends AttributeAdaptor {
    public ColorBoxAttribute(String name, String altName) {
        super(name, altName, ColorBoxAttributeEditor.EDITOR_CODE);
    }
}
