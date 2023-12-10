package cn.mapway.ui.client.mvc.attribute.editor;

/**
 * 属性编辑窗口的工厂接口
 * 编译器会扫描源代码 生成这个接口的实现类
 */
public interface IAttributeEditorFactory {
    /**
     * 创建编辑器
     * 实现逻辑为
     * 根据列表 依次创建
     * @param code  编辑器的唯一代码
     * @param reuse 是否重用
     * @return
     */
    IAttributeEditor createEditor(String code, boolean reuse);
}