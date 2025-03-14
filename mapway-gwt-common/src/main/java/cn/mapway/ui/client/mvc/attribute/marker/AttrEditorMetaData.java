package cn.mapway.ui.client.mvc.attribute.marker;

import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextInputKind;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextboxAttributeEditor;

import java.util.ArrayList;
import java.util.List;

public class AttrEditorMetaData {
    List<ParameterValue> parameters;
    String editorCode;

    public AttrEditorMetaData() {
        parameters = new ArrayList<>();
        editorCode = "";
    }

    public void setEditorCode(String code) {
        editorCode = code;
    }
    public String getEditorCode(){
        return editorCode;
    }

    public List<ParameterValue> getParameters() {
        return parameters;
    }
    /**
     * 查找 [key] 的参数信息
     *
     * @param key
     * @return
     */
    public ParameterValue findParameterValue(String key) {
        if (parameters == null || key == null || key.length() == 0) {
            return null;
        }
        for (ParameterValue parameter : parameters) {
            if (key.equals(parameter.name)) {
                return parameter;
            }
        }
        return null;
    }

    /**
     * 替换参数
     *
     * @param key
     * @param value
     * @param init
     * @return
     */
    public AttrEditorMetaData replaceParameter(String key, Object value, boolean init) {
        if (key == null || key.length() == 0 || value == null) {
            return this;
        }
        ParameterValue parameterValue = findParameterValue(key);
        if (parameterValue == null) {
            parameterValue = ParameterValue.create(key, value, init);
            parameters.add(parameterValue);
        } else {
            parameterValue.name = key;
            parameterValue.value = value;
            parameterValue.init = init;
        }
        return this;
    }

    public void asNumber() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.NUMBER,false);
    }

    public void asEmail() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.EMAIL,false);
    }

    public void asDate() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.DATE,false);
    }

    public void asTime() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.TIME,false);
    }

    public void asUrl() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.URL,false);
    }

    public void asDateTime() {
        replaceParameter(TextboxAttributeEditor.KEY_INPUT_KIND, TextInputKind.DATE_TIME,false);
    }
}
