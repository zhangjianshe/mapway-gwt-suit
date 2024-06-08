package cn.mapway.ui.client.mvc.attribute.editor.padding;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.checkbox.CheckBoxAttributeEditor;

public abstract class PaddingBoxAttribute extends AbstractAttribute {
    public PaddingBoxAttribute(String name, String altName) {
        super(name, altName, PaddingBoxAttributeEditor.EDITOR_CODE);
    }
}
