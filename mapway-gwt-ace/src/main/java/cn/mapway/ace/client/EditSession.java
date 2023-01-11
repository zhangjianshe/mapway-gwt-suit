package cn.mapway.ace.client;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class EditSession {

    @JsOverlay
    public final void setMode(String mode){
        _setMode("ace/mode/"+mode);
    }

    @JsMethod(name = "setMode")
    public final native void _setMode(String mode);
}
