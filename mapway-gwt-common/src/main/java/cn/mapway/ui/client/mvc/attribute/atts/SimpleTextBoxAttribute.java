package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextboxAttributeEditor;

/**
 * 文本框编辑属性
 */
public class SimpleTextBoxAttribute extends AbstractAttribute {
    String value;

    public SimpleTextBoxAttribute(String name, String altName, String defaultValue) {
        super(name, altName, TextboxAttributeEditor.EDITOR_CODE);
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
