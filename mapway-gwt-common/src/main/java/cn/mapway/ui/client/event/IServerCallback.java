package cn.mapway.ui.client.event;

/**
 * 服务器回调的简单用法 用于 Lambda方式书写
 *
 * @param <T>
 */
public interface IServerCallback<T> {
    /**
     * 将结果封装为一个参数
     *
     * @param data
     */
    void onResult(T data);
}
