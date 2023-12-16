package cn.mapway.ui.client.mvc.attribute.editor;

import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

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
     * 获取参数设计器中的数据
     *
     * @return
     */
    List<IAttribute> getParameters();

    /**
     * 获取编辑组件的参数数据
     *
     * @return
     */
    List<ParameterValue> getParameterValues();

    /**
     * 初始化参数设计器
     *
     * @param title      标题
     * @param parameters
     */
    void setParameters(String title, List<IAttribute> parameters);


    /**
     * 上面两个方法 用于构造UI 这个方法用于更新数据 这个数据是保存在Editor中的
     */
    void updateValue(List<ParameterValue> parameterValues);

}
