package cn.mapway.ui.client.mvc.attribute.marker;

import cn.mapway.ui.client.mvc.attribute.editor.impl.TextboxAttributeEditor;

/**
 * 文本输入框 对应的编辑器实例数据
 */
public class TextBoxEditorMetaData extends AbstractEditorMetaData {
    public TextBoxEditorMetaData() {
        super(TextboxAttributeEditor.EDITOR_CODE);
    }
}
