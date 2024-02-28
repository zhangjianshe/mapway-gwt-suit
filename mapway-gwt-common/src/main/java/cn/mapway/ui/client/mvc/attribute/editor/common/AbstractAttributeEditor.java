package cn.mapway.ui.client.mvc.attribute.editor.common;

import cn.mapway.ui.client.db.DbFieldType;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.IEditorMetaData;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValues;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditorValueChangedHandler;
import cn.mapway.ui.client.widget.CommonEventComposite;
import elemental2.core.JsObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽象的属性编辑器基类
 * 继承类实现 setValue getValue
 * 提供 popupWidget 会提供一个 button进行选择
 * 提供 DisplayWidget 会显示widget 没有 会使用缺省的 TextBox
 * <p>
 * 如何自定义个一个属性编辑器 参考 TextBoxAttributeEditor
 * 1.继承此类
 * 2.提供一个属性编辑器的CODE
 * 3.重载 setAttribute getDisplayWidget getPopupWidget
 */
public abstract class AbstractAttributeEditor<T> extends CommonEventComposite implements IAttributeEditor {
    IAttribute attribute;
    /**
     * 运行时的参数
     */
    List<ParameterValue> runtimeParameters;
    private Object data;

    private IAttributeEditorValueChangedHandler attributeValueChangedHandler;

    /**
     * 返回编辑器弹出窗的缺省大小
     *
     * @return
     */
    @Override
    public Size getSize() {
        return new Size(700, 600);
    }

    public IAttribute getAttribute() {
        return attribute;
    }

    /**
     * 获取编辑器选项
     * 这个选项一定不为空 并且融合了 设计期和运行期 的所有编辑器参数选项
     *
     * @return
     */
    public List<ParameterValue> getRuntimeParameters() {
        return runtimeParameters;
    }

    /**
     * 首先从运行时中寻找参数
     * 然后从 设计时中寻找参数
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public <T> T option(String name, T defaultValue) {
        if (name == null || name.length() == 0) {
            return defaultValue;
        }
        if (runtimeParameters != null) {
            for (ParameterValue value : runtimeParameters) {
                if (value.equals(name)) {
                    return (T) value.value;
                }
            }
        }
        if (getAttribute() == null) {
            return defaultValue;
        }
        IEditorMetaData editorData = getAttribute().getEditorMetaData();
        if (editorData != null) {
            ParameterValue parameter = editorData.findParameterValue(name);
            return (T) readAttributeValue(parameter, defaultValue);
        }
        return defaultValue;

    }

    /**
     * 更新编辑器的某个选项
     *
     * @param key
     * @param value
     */
    public void updateEditorOption(String key, Object value) {
        if (key != null) {

            onEditorOptionChanged(key);
        }
    }

    /**
     * 更新所有的编辑器选项
     */
    public void updateAllEditorOption() {
        onEditorOptionChanged(null);
    }

    /**
     * 某个编辑属性改变了值
     *
     * @param key 如果key is null or empty 更新所有的属性编辑器组件的选项
     */
    public void onEditorOptionChanged(String key) {

    }

    /**
     * 设置组件编辑器的内容
     * 通过组件工厂 AttributeEditorFactory 创建一个编辑器组件实例后
     * 子组件必须首先调用此方法 处理一些基础数据
     * 参见下拉框编辑器的设计 DropdownAttributeEditor
     *
     * @param runtimeParameters 编辑器选项 是一个 KV Ma,继承的组件自己定义所需的参数
     * @param attribute         属性编辑器对应的属性内容
     */
    @Override
    public void editAttribute(ParameterValues runtimeParameters, IAttribute attribute) {
        assert attribute != null;
        this.attribute = attribute;

        this.runtimeParameters = runtimeParameters;
        //check if it is null
        if (runtimeParameters == null) {
            this.runtimeParameters = new ArrayList<>();
        }
        //从模型将数据更新到界面
        updateUI();
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }


    public String castToString(Object obj) {
        if (obj == null) {
            return "";
        }

        if (obj instanceof JsObject) {
            JsObject jsObject = (JsObject) obj;
            return jsObject.toString();
        }
        return DataCastor.castToString(obj);
    }

    /**
     * 根据 DataType 将字符串转化为对象
     *
     * @param value
     * @return
     */
    public Object castToValue(String value) {
        DbFieldType fieldType = DbFieldType.valueOfCode(attribute.getDataType());
        switch (fieldType) {
            case FLD_TYPE_BOOLEAN:
                return DataCastor.castToBoolean(value);
            case FLD_TYPE_FLOAT:
                return DataCastor.castToFloat(value);
            case FLD_TYPE_INTEGER:
                return DataCastor.castToInteger(value);
            case FLD_TYPE_BIGINTEGER:
            case FLD_TYPE_SERIAL:
                return DataCastor.castToLong(value);
            case FLD_TYPE_CLOB:
            case FLD_TYPE_STRING:
            default:
                return DataCastor.castToString(value);
        }
    }

    @Override
    public void setValueChangedHandler(IAttributeEditorValueChangedHandler handler) {
        this.attributeValueChangedHandler = handler;
    }

    /**
     * 通知监听器 属性发生了变化
     */
    public void notifyValueChanged() {
        if (attributeValueChangedHandler != null) {
            attributeValueChangedHandler.onAttributeChanged(this, getAttribute());
        }
    }

    /**
     * 读取attribute的值 如果为空 返回缺省值
     *
     * @param attribute
     * @param defaultValue
     * @return
     */
    public Object readAttributeValue(ParameterValue attribute, Object defaultValue) {
        if (attribute == null || attribute.value == null) {
            return defaultValue;
        }
        return attribute.value;
    }

    /**
     * 从UI获取数据到 对象实体中
     */
    public abstract void fromUI();

}
