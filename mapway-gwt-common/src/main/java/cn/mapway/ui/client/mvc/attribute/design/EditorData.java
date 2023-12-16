package cn.mapway.ui.client.mvc.attribute.design;

import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.base.JsPropertyMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑器实例的所有数据
 */
public class EditorData implements IEditorData {
    String editorCode;
    String editorName;
    /**
     * 运行时数据 不会被序列化
     */
    String errorMessage;

    List<ParameterValue> parameters;

    public EditorData() {
        editorCode = "";
        editorName = "";
        parameters = new ArrayList<>();
    }

    /**
     * 获取编辑器的代码
     *
     * @return
     */
    @Override
    public String getEditorCode() {
        return editorCode;
    }

    public EditorData setEditorCode(String editorCode) {
        this.editorCode = editorCode;
        return this;
    }

    /**
     * 编辑器名称
     *
     * @return
     */
    @Override
    public String getEditorName() {
        return editorName;
    }

    public EditorData setEditorName(String editorName) {
        this.editorName = editorName;
        return this;
    }

    public EditorData addParameter(String name, Object value) {
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.name = name;
        parameterValue.value = value;
        parameters.add(parameterValue);
        return this;
    }

    /**
     * 获取错误消息
     *
     * @return
     */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Version1 only support JSON format
     *
     * @param data
     * @param format
     * @return
     */
    @Override
    public boolean load(Object data, EditorDataFormat format) {
        if (data == null) {
            errorMessage = "load editor data is null";
            return false;
        }
        if (!(data instanceof String)) {
            errorMessage = "only support String format in this version";
            return false;
        }

        JsonEditorData dataJson = JsonEditorData.load(DataCastor.castToString(data));
        this.editorCode = dataJson.editorCode;
        this.editorName = dataJson.editorName;
        if (dataJson.parameters != null && dataJson.parameters.getLength() > 0) {
            for (int i = 0; i < dataJson.parameters.getLength(); i++) {
                try {
                    ParameterValue jsonAttribute = dataJson.parameters.getAt(i);
                    parameters.add(jsonAttribute);
                } catch (Exception e) {
                    DomGlobal.console.error(e.getMessage());
                }
            }
        }
        return true;
    }

    /**
     * 系列化数据到字符串中
     *
     * @param format
     * @return
     */
    @Override
    public Object save(EditorDataFormat format) {
        JsPropertyMap obj = JsPropertyMap.of
                (EditorOption.KEY_EDITOR_CODE, editorCode,
                        EditorOption.KEY_EDITOR_NAME, editorName
                );
        JsArray params = new JsArray();
        for (ParameterValue attribute : parameters) {
            params.push(attribute);
        }
        obj.set("parameters", params);
        return Global.JSON.stringify(obj);
    }

    /**
     * 获取创建编辑器所需要的参数列表
     *
     * @return
     */
    @Override
    public List<ParameterValue> getParameterValues() {
        return parameters;
    }

    /**
     * 查找 名字为key 的参数描述信息
     *
     * @param key
     * @return
     */
    @Override
    public ParameterValue findParameterValue(String key) {
        if (key == null || key.length() == 0 || parameters == null || parameters.size() == 0) {
            return null;
        }

        for (ParameterValue value : parameters) {
            if (value.name.equals(key)) {
                return value;
            }
        }
        return null;
    }
}
