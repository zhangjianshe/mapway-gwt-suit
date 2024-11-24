package cn.mapway.ui.client.mvc.attribute.editor.sys;

import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.mvc.attribute.marker.AbstractEditorMetaData;

/**
 * 多行文本编辑器 初始化需要的参数
 */
public class TextAreaAttributeEditorData extends AbstractEditorMetaData {

    public TextAreaAttributeEditorData() {
        this("150px");
    }

    public TextAreaAttributeEditorData(String height) {
        super(TextAreaAttributeEditor.EDITOR_CODE);
        getParameterValues().add(ParameterValue.create(ParameterKeys.KEY_HEIGHT, height));
    }

    /**
     *
     */
    @Override
    protected void initMetaData() {

    }
}
