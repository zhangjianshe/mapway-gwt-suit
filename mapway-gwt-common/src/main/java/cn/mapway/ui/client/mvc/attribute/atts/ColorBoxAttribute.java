package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.color.ColorBoxAttributeEditor;

/**
 * 颜色输入框编辑属性
 */
public abstract class ColorBoxAttribute extends AbstractAttribute {
    public ColorBoxAttribute(String name, String altName) {
        super(name, altName, ColorBoxAttributeEditor.EDITOR_CODE);
    }
}
