package cn.mapway.ui.shared.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * RpcResult
 *
 * @author zhangjianshe@gmail.com
 */
public class RpcResult<T> implements IsSerializable {
    Integer code;
    String message;
    T data;

    public static RpcResult success(Object data) {
        RpcResult r = new RpcResult();
        r.code = 200;
        r.data = data;
        return r;
    }

    public static RpcResult create(Integer code, String message, Object data) {
        RpcResult r = new RpcResult();
        r.code = code;
        r.message = message;
        r.data = data;
        return r;
    }

    public static RpcResult fail(Integer code, String message) {
        RpcResult r = new RpcResult();
        r.code = code;
        r.message = message;
        return r;
    }

    public Boolean isSuccess() {
        return code == 200;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return this.message;
    }

    public Integer getCode()
    {
        return this.code;
    }
}
