package cn.mapway.ui.client.mvc.attribute.editor;

import com.google.gwt.user.client.ui.Widget;
import elemental2.core.JsObject;

/**
 * 属性编辑器 参数设计接口
 */
public interface IEditorDesigner {
    /**
     * 参数的编辑UI
     *
     * @return
     */
    Widget getDesignRoot();

    /**
     * 解析参数
     *
     * @param options
     */
    void setDesignOptions(JsObject designOptions);

    /**
     * 将设计的参数数据转化为字符串 JSON
     *
     * @return
     */
    String getDesignOptions();
}
