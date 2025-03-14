package cn.mapway.ui.client.mvc.attribute.marker;

import cn.mapway.ui.client.mvc.attribute.design.EditorMetaDataFormat;
import cn.mapway.ui.client.mvc.attribute.design.IEditorMetaData;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextboxAttributeEditor;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述一个编辑器的实例化数据
 * 一般用于 @Attr 中 向属性提供实例化数据
 */
public abstract class AbstractEditorMetaData extends AttrEditorMetaData implements IEditorMetaData {

    public AbstractEditorMetaData() {
        this(TextboxAttributeEditor.EDITOR_CODE);
    }

    public AbstractEditorMetaData(String editorCode) {
        super();
        this.editorCode = editorCode;
        initMetaData();
    }

    /**
     * 初始化元数据对象
     */
    protected abstract void initMetaData();



    /**
     * 编辑器名称
     *
     * @return
     */
    @Override
    public String getEditorName() {
        return "";
    }

    public AbstractEditorMetaData setOptions(String options) {
        parameters.add(ParameterValue.create(ParameterKeys.KEY_OPTIONS, options));
        return this;
    }

    public AbstractEditorMetaData addParameter(String key, Object value, boolean init) {
        if (key == null || key.length() == 0 || value == null) {
            return this;
        }
        parameters.add(ParameterValue.create(key, value, init));
        return this;
    }



    @Override
    public List<ParameterValue> getParameterValues() {
        return parameters;
    }

    /**
     * 获取错误消息
     *
     * @return
     */
    @Override
    public String getErrorMessage() {
        return "";
    }

    /**
     * Version1 only support JSON format
     *
     * @param data
     * @param format
     * @return
     */
    @Override
    public boolean load(Object data, EditorMetaDataFormat format) {
        return false;
    }

    /**
     * 系列化数据到字符串中
     *
     * @param format
     * @return
     */
    @Override
    public Object save(EditorMetaDataFormat format) {
        return null;
    }


}
