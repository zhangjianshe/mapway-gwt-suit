package cn.mapway.ui.client.mvc.attribute.design;

import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import elemental2.core.Global;
import elemental2.core.JsArray;
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

    List<IAttribute> parameters;

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

    /**
     * 编辑器名称
     *
     * @return
     */
    @Override
    public String getEditorName() {
        return editorName;
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
                JsonAttribute jsonAttribute = dataJson.parameters.getAt(i);
                parameters.add(jsonAttribute);
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
                ("editorCode", editorCode,
                        "editorName", editorName
                );
        JsArray params = new JsArray();
        for (IAttribute attribute : parameters) {
            params.push(attribute.toJSON());
        }
        obj.set("parameters", Global.JSON.stringify(params));
        return Global.JSON.stringify(obj);
    }

    /**
     * 获取创建编辑器所需要的参数列表
     *
     * @return
     */
    @Override
    public List<IAttribute> getParameters() {
        return parameters;
    }
}
