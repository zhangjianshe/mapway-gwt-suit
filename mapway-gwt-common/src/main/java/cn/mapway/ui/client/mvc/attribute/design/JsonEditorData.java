package cn.mapway.ui.client.mvc.attribute.design;

import cn.mapway.ui.client.mvc.attribute.editor.impl.TextboxAttributeEditor;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsObject;
import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class JsonEditorData {
    public String editorCode;
    public String editorName;

    public JsArrayLike<JsonAttribute> parameters;


    protected JsonEditorData() {
    }

    @JsOverlay
    public final static JsonEditorData create(String editorCode) {
        JsonEditorData data = new JsonEditorData();
        data.editorCode = editorCode;
        return data;
    }

    @JsOverlay
    public final static JsonEditorData load(String jsonString) {
        try {
            JsObject json = Js.uncheckedCast(Global.JSON.parse(jsonString));
            // TODO 我们确定这个字符串里保存的就是这样的数据 不再解析了 组件的使用开发人员请确保这个问题
            return Js.uncheckedCast(json);
        } catch (Exception e) {
            DomGlobal.console.log(e.getMessage());
            JsonEditorData jsonEditorData = new JsonEditorData();
            jsonEditorData.editorCode = TextboxAttributeEditor.EDITOR_CODE;
            jsonEditorData.parameters = new JsArray<>();
            return jsonEditorData;
        }
    }
}
