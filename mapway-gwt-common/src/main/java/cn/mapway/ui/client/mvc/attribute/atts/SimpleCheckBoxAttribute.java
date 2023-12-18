package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.editor.impl.CheckBoxAttributeEditor;

/**
 * 文本框编辑属性
 */
public class SimpleCheckBoxAttribute extends AbstractAttribute {
    Boolean value;

    public SimpleCheckBoxAttribute(String name, String altName, boolean defaultValue) {
        super(name, altName, CheckBoxAttributeEditor.EDITOR_CODE);
        value = defaultValue;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = DataCastor.castToBoolean(value);
    }
}
