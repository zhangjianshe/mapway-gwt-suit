package cn.mapway.ui.client.rpc;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true,namespace = JsPackage.GLOBAL,name = "Object")
public class HttpError {
    public int code;
    public String message;
    @JsOverlay
    public final String getMessage() { return this.message; }
    @JsOverlay
    public final int getCode() { return this.code; }
}
