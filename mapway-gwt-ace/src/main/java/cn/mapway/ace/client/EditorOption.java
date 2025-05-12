package cn.mapway.ace.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true,namespace = JsPackage.GLOBAL,name = "Object")
public class EditorOption {
    @JsOverlay
    public final EditorOption enableBasicAutocompletion(boolean enable)
    {
        return set("enableBasicAutocompletion",enable);
    }
    @JsOverlay
    public final EditorOption enableLiveAutocompletion(boolean enable)
    {
        return set("enableLiveAutocompletion",enable);
    }
    @JsOverlay
    public final EditorOption enableSnippets(boolean enable)
    {
        return set("enableSnippets",enable);
    }

    @JsOverlay
    public final EditorOption set(String key,Object value)
    {
        JsPropertyMap map = Js.asPropertyMap(this);
        map.set(key,value);
        return this;
    }
}
