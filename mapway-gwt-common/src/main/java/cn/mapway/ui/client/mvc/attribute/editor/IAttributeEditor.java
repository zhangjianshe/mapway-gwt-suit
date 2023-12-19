package cn.mapway.ui.client.mvc.attribute.editor;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValues;
import cn.mapway.ui.client.mvc.decorator.IProvideSize;
import cn.mapway.ui.client.tools.IData;
import com.google.gwt.user.client.ui.Widget;

/**
 * 属性编辑器的约束接口
 * 2.提供一个缺省的大小
 * D is design option data type
 */
public interface IAttributeEditor extends IProvideSize, IData {
    String CATALOG_RUNTIME = "运行时";
    String CATALOG_DESIGN = "设计时";
    String CATALOG_BUSINESS = "业务编辑器";
    String CATALOG_UNKNOWN = "无分类";
    String CATALOG_SYSTEM = "系统编辑器";

    /**
     * 编辑器的唯一识别代码
     *
     * @return
     */
    String getCode();

    /**
     * 显示显示面板
     *
     * @return
     */
    Widget getDisplayWidget();


    void setReadonly(boolean readonly);

    /**
     * 设置编辑器显示或者隐藏
     *
     * @param visible
     */
    void setVisible(boolean visible);

    /**
     * 设置属性代理对象
     *
     * @param runtimeParameters 编辑器的选项 由编辑器自己决定里面的值
     * @param attribute
     */
    void editAttribute(ParameterValues runtimeParameters, IAttribute attribute);

    /**
     * 获取编辑器对应的属性定义
     *
     * @return
     */
    IAttribute getAttribute();

    /**
     * 编辑器的缺省大小
     *
     * @return
     */
    Size getSize();

    void updateEditorOption(String key, Object value);

    void updateAllEditorOption();

    /**
     * 属性变化通知事件
     *
     * @param handler
     */
    void setValueChangedHandler(IAttributeEditorValueChangedHandler handler);

    /**
     * 获取编辑器参数设计组件
     * 缺省返回null 不需要设计
     *
     * @return
     */
    default IEditorDesigner getDesigner() {
        return null;
    }


    /**
     * 根据用户的参数信息 提示给用户
     *
     * @return
     */
    default String getEditorTip() {
        return "";
    }

    /**
     * 从 attribtue 中获取Value更新界面
     */
    void updateUI();
}
