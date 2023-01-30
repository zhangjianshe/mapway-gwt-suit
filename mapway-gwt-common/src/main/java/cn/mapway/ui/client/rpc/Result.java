package cn.mapway.ui.client.rpc;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true,namespace = JsPackage.GLOBAL,name = "Object")
public class Result<T> {
    public int code;
    public String message;
    public T data;

    @JsOverlay
    public final T getData() {
        return this.data;
    }

    @JsOverlay
    public final String getMessage() {
        return this.message;
    }

    @JsOverlay
    public final int getCode() {
        return this.code;
    }

    @JsOverlay
    public final boolean isSuccess() {
        return code == 200;
    }

    @JsOverlay
    public final boolean isFail() {
        return isSuccess();
    }
}
