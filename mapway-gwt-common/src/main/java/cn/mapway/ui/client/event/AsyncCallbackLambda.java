package cn.mapway.ui.client.event;

import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 将服务器的回调接口转化为 单一的函数式 用于简单的Lambda方式书写回调
 *
 * @param <T>
 */
public abstract class AsyncCallbackLambda<T> implements AsyncCallback<T>, IServerCallback<T> {
    @Override
    public void onFailure(Throwable caught) {
        RpcResult rpcResult = RpcResult.fail(506, caught.getMessage());
        onResult((T) rpcResult);
    }

    @Override
    public void onSuccess(T result) {
        onResult(result);
    }

    /**
     * 这个方法子类实现
     *
     * @param data
     */
    @Override
    abstract public void onResult(T data);
}
