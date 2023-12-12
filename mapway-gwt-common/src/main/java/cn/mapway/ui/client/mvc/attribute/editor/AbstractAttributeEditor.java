package cn.mapway.ui.client.mvc.attribute.editor;

import cn.mapway.ui.client.db.DbFieldType;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.widget.CommonEventComposite;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import elemental2.core.JsObject;

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
public abstract class AbstractAttributeEditor<T> extends CommonEventComposite implements IAttributeEditor<T> {
    IAttribute attribute;
    EditorOption editorOption;
    private Object data;

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
     *
     * @return
     */
    public EditorOption getEditorOption() {
        return editorOption;
    }

    /**
     * 更新编辑器的某个选项
     *
     * @param key
     * @param value
     */
    public void updateEditorOption(String key, Object value) {
        if (key != null) {
            getEditorOption().set(key, value);
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
     *
     * @param editorOption 编辑器选项 是一个 KV Ma,继承的组件自己定义所需的参数
     * @param attribute    属性编辑器对应的属性内容
     */
    @Override
    public void setAttribute(EditorOption editorOption, IAttribute attribute) {
        assert attribute != null;
        this.attribute = attribute;
        this.editorOption = new EditorOption();
        //先从定义中获取 编辑器选项
        this.editorOption.parseOptionValue(attribute.getEditorOptions());
        //将传入的选项和 定义的选项merge
        this.editorOption.merge(editorOption);
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    /**
     * 重载 这个方法进行弹出窗口设置
     * @param editorOption
     */
    @Override
    public void popupInit(EditorOption editorOption) {

    }

    /**
     * 获取弹出的窗口内容
     *
     * @return
     */
    @Override
    public Widget getPopupWidget() {
        return null;
    }



    /**
     * Gets this object's value.
     *
     * @return the object's value
     */
    @Override
    public T getValue() {
        return null;
    }

    /**
     * Sets this object's value without firing any events. This should be
     * identical to calling setValue(value, false).
     * <p>
     * It is acceptable to fail assertions or throw (documented) unchecked
     * exceptions in response to bad values.
     * <p>
     * Widgets must accept null as a valid value. By convention, setting a widget to
     * null clears value, calling getValue() on a cleared widget returns null. Widgets
     * that can not be cleared (e.g. {@link CheckBox}) must find another valid meaning
     * for null input.
     *
     * @param value the object's new value
     */
    @Override
    public void setValue(T value) {

    }

    /**
     * Sets this object's value. Fires
     * {@link ValueChangeEvent} when
     * fireEvents is true and the new value does not equal the existing value.
     * <p>
     * It is acceptable to fail assertions or throw (documented) unchecked
     * exceptions in response to bad values.
     *
     * @param value      the object's new value
     * @param fireEvents fire events if true and value is new
     */
    @Override
    public void setValue(T value, boolean fireEvents) {

    }

    /**
     * Adds a {@link ValueChangeEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
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
}
