package cn.mapway.ui.client.mvc.attribute.editor.textbox;

import cn.mapway.ui.client.mvc.attribute.marker.AbstractEditorMetaData;

/**
 * 文本输入框 对应的编辑器实例数据
 */
public class UrlEditorMetaData extends AbstractEditorMetaData {
    public UrlEditorMetaData() {
        super(TextboxAttributeEditor.EDITOR_CODE);
    }

    /**
     * 初始化元数据对象
     */
    @Override
    protected void initMetaData() {
        asUrl();
    }
}
