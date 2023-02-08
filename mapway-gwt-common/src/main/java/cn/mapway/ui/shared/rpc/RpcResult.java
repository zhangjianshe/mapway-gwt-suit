package cn.mapway.ui.shared.rpc;

import jsinterop.annotations.JsOverlay;

import java.io.Serializable;

/**
 * RpcResult
 *
 * @author zhangjianshe@gmail.com
 */
//@JsType(isNative = true, namespace = JsPackage.GLOBAL,name="Object")
public class RpcResult<T> implements Serializable {
    Integer code;
    String message;
    T data;

    @JsOverlay
    public static final RpcResult success(Object data) {
        RpcResult r = new RpcResult();
        r.code = 200;
        r.data = data;
        return r;
    }

    @JsOverlay
    public static final RpcResult create(Integer code, String message, Object data) {
        RpcResult r = new RpcResult();
        r.code = code;
        r.message = message;
        r.data = data;
        return r;
    }

    @JsOverlay
    public static final RpcResult fail(Integer code, String message) {
        RpcResult r = new RpcResult();
        r.code = code;
        r.message = message;
        return r;
    }

    @JsOverlay
    public final boolean isSuccess() {
        return code == 200;
    }

    @JsOverlay
    public final T getData() {
        return this.data;
    }

    @JsOverlay
    public final String getMessage() {
        return this.message;
    }

    @JsOverlay
    public final Integer getCode()
    {
        return this.code;
    }
}
