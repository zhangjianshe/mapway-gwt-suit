package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.label.LabelAttributeEditor;

public abstract class LabelAttribute extends AbstractAttribute {

    public LabelAttribute(String name, String altName) {
        super(name, altName, LabelAttributeEditor.EDITOR_CODE);
    }

    @Override
    public void setValue(Object value) {

    }
}
