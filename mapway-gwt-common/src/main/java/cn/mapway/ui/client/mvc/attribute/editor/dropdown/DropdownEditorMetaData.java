package cn.mapway.ui.client.mvc.attribute.editor.dropdown;

import cn.mapway.ui.client.mvc.attribute.design.ParameterValues;
import cn.mapway.ui.client.mvc.attribute.marker.AbstractEditorMetaData;

/**
 * DropdownEditorMetaData 对应的编辑器实例数据
 */
public abstract class DropdownEditorMetaData extends AbstractEditorMetaData {
    public DropdownEditorMetaData() {
        super(DropdownAttributeEditor.EDITOR_CODE);
    }

    @Override
    protected void initMetaData() {
        ParameterValues parameterValues = initDropdownOptions();
        if (parameterValues != null) {
            setOptions(parameterValues.toJson());
        } else {
            setOptions("[]");
        }
    }

    /**
     * 这里提供一个下拉框选项
     *
     * @return
     */
    protected abstract ParameterValues initDropdownOptions();
}
