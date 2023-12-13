package cn.mapway.ui.client.mvc.attribute.editor;

import cn.mapway.ui.client.mvc.attribute.IAttribute;

/**
 * 编辑器属性发生变化的通知事件
 */
public interface IAttributeEditorValueChangedHandler {
    /**
     * 属性变化
     *
     * @param editor
     * @param attribute
     */
    void onAttributeChanged(IAttributeEditor editor, IAttribute attribute);
}
