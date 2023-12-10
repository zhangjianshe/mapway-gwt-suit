package cn.mapway.ui.client.mvc.attribute.editor;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.decorator.IProvideSize;
import cn.mapway.ui.client.tools.IData;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * 属性编辑器的约束接口
 * 1.可以设置 Value 读取 Value Value变更事件  Value 类型是一个 JsObject
 * 2.提供一个缺省的大小
 */
public interface IAttributeEditor<T> extends HasValue<T>, IProvideSize, IData {
    /**
     * 编辑器的唯一识别代码
     *
     * @return
     */
    String getCode();

    /**
     * 返回弹出面板
     *
     * @return
     */
    Widget getPopupWidget();

    /**
     * 显示显示面板
     *
     * @return
     */
    Widget getDisplayWidget();


    void setEnabled(boolean enabled);

    /**
     * 设置属性代理对象
     *
     * @param editOption 编辑器的选项 由编辑器自己决定里面的值
     * @param attribute
     */
    void setAttribute(EditorOption editOption, IAttribute attribute);

    Size getSize();

    // 加载popup window's data
    void loadPopupData();

    void updateEditorOption(String key, Object value);

    void updateAllEditorOption();

    /**
     * 根据数值 更新UI
     */
    void updateUI();
}
