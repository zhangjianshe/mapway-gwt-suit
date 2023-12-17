package cn.mapway.ui.client.mvc.attribute.editor;

import java.util.List;

/**
 * 属性编辑窗口的工厂接口
 * 编译器会扫描源代码 生成这个接口的实现类
 */
public interface IAttributeEditorFactory {


    /**
     * 创建编辑器
     * 实现逻辑为
     * 根据列表 依次创建
     *
     * @param editorCode  编辑器的唯一代码
     * @param reuse 是否重用
     * @return
     */
    IAttributeEditor createEditor(String editorCode, boolean reuse);

    /**
     * 返回系统中所有的属性编辑器
     *
     * @return
     */
    List<AttributeEditorInfo> getEditors();

    /**
     * 根据代码查找属性编辑器元数据
     *
     * @param code
     * @return
     */
    AttributeEditorInfo findByCode(String code);
}
