package cn.mapway.ui.client.mvc.attribute.design;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;

/**
 * 构造一个简单的用于 编辑器参数的属性对象
 * 他的 EditorData is TextBoxAttribueEditor.EDITOR_CODE
 */
public class ParameterAttribute extends AbstractAttribute {
    Object value;

    public ParameterAttribute(String parameterName, Object parameterValue) {
        super(parameterName);
        this.value = parameterValue;
    }

    public ParameterAttribute(String parameterName, String altName, Object parameterValue) {
        super(parameterName, altName);
        this.value = parameterValue;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }
}
