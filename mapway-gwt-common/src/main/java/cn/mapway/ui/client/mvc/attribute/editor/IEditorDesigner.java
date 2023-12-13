package cn.mapway.ui.client.mvc.attribute.editor;

import com.google.gwt.user.client.ui.Widget;

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
    void parseDesignOptions(String options);

    /**
     * 将设计的参数数据转化为字符串 JSON
     *
     * @return
     */
    String toDesignOptions();
}
