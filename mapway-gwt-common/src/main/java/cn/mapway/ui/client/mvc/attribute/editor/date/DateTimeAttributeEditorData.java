package cn.mapway.ui.client.mvc.attribute.editor.date;

import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.mvc.attribute.marker.AbstractEditorMetaData;

/**
 * 日期时间格式
 */
public class DateTimeAttributeEditorData extends AbstractEditorMetaData {

    public DateTimeAttributeEditorData(String format)
    {
        super();
        getParameterValues().add(ParameterValue.create(ParameterKeys.KEY_DATETIME_FORMAT, format));
    }
    /**
     *
     */
    @Override
    protected void initMetaData() {
        setEditorCode(DateTimeAttributeEditor.EDITOR_CODE);
    }
}
