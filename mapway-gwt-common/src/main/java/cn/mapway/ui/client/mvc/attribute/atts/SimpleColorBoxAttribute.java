package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.editor.impl.ColorBoxAttributeEditor;

/**
 * 文本框编辑属性
 */
public class SimpleColorBoxAttribute extends AbstractAttribute {
    String value;

    public SimpleColorBoxAttribute(String name, String altName, String defaultValue) {
        super(name, altName, ColorBoxAttributeEditor.EDITOR_CODE);
        value = defaultValue;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = DataCastor.castToString(value);
    }
}
