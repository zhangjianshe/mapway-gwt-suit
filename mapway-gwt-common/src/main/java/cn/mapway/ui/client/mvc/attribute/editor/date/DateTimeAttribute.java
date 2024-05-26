package cn.mapway.ui.client.mvc.attribute.editor.date;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.util.StringUtil;

/**
 * 时间日期属性
 */
public abstract class DateTimeAttribute extends AbstractAttribute {
    public DateTimeAttribute(String name, String altName) {
        super(name, altName, DateTimeAttributeEditor.EDITOR_CODE);
    }

    public void setFormat(String format) {
        if (!StringUtil.isBlank(format)) {
            getRuntimeParameters().addParameter(ParameterKeys.KEY_DATETIME_FORMAT, format);
        }
    }
}
