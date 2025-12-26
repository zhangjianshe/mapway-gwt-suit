package cn.mapway.ui.client.mvc.attribute.editor.label;

import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.mvc.attribute.marker.AbstractEditorMetaData;

/**
 * 文本输入框 对应的编辑器实例数据
 */
public class LabelEditorMetaData extends AbstractEditorMetaData {
    public LabelEditorMetaData() {
        super(LabelAttributeEditor.EDITOR_CODE);
    }

    /**
     * 初始化元数据对象
     */
    @Override
    protected void initMetaData() {

    }

    public LabelEditorMetaData asHTMLLabel() {
        replaceParameter(ParameterKeys.KEY_AS_HTML, true, false);
        return this;
    }

    public LabelEditorMetaData setHeight(String height) {
        replaceParameter(ParameterKeys.KEY_HEIGHT, height, false);
        return this;
    }
}
