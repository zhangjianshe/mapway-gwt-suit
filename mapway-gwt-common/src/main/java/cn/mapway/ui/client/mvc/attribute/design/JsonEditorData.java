package cn.mapway.ui.client.mvc.attribute.design;

import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorFactory;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorInfo;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextboxAttributeEditor;
import cn.mapway.ui.client.util.Jss;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.annotations.*;
import jsinterop.base.JsArrayLike;

@JsType
public class JsonEditorData {
    public JsArrayLike<ParameterValue> parameters;
    @JsProperty
    private String editorCode;
    @JsProperty
    private String editorName;

    @JsConstructor
    public JsonEditorData() {
    }

    public final static JsonEditorData create(String editorCode) {
        JsonEditorData data = new JsonEditorData();
        data.editorCode = editorCode;
        return data;
    }
    public final static JsonEditorData load(String jsonString) {
        if (jsonString == null || jsonString.length() < 2) {
            DomGlobal.console.log("field editor is null ");
            return createDefaultEditor();
        }
        try {
            Object json = Global.JSON.parse(jsonString);
            JsonEditorData data = Jss.castTo(json, JsonEditorData.class);
            data.check();
            return data;
        } catch (Exception e) {
            DomGlobal.console.log(e.getMessage());
            return createDefaultEditor();
        }
    }
    public final static JsonEditorData createDefaultEditor() {
        JsonEditorData jsonEditorData = new JsonEditorData();
        jsonEditorData.editorCode = TextboxAttributeEditor.EDITOR_CODE;
        jsonEditorData.parameters = new JsArray<>();
        return jsonEditorData;
    }
    public final void check() {
        if (editorCode == null || editorCode.length() == 0) {
            DomGlobal.console.log("解析出的编辑器对象数据格式 不正确,转为缺省的输入框");
            editorCode = TextboxAttributeEditor.EDITOR_CODE;
            editorName = AttributeEditorFactory.get().findByCode(editorCode).name;
            parameters = new JsArray<>();
        } else {
            if (editorName == null || editorName.length() == 0) {
                AttributeEditorInfo byCode = AttributeEditorFactory.get().findByCode(editorCode);
                if (byCode == null) {
                    editorName = editorCode + "编辑器不能识别";
                } else {
                    editorName = byCode.name;
                }
            }
        }
    }
    public final String getEditorCode() {
        return editorCode;
    }
    public final String getEditorName() {
        return editorName;
    }
}
