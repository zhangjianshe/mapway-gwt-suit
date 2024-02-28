package cn.mapway.ui.client.mvc.attribute.editor;
import com.google.gwt.core.client.GWT;

/**
 * 属性编辑器工厂类 全局唯一代理
 */
public class AttributeEditorFactory {
    private static IAttributeEditorFactory FACTORY;

    public static IAttributeEditorFactory get() {
        if (FACTORY == null) {
            FACTORY = GWT.create(IAttributeEditorFactory.class);
        }
        return FACTORY;
    }
}
