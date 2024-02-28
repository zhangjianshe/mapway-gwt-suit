package cn.mapway.ui.client.event;

/**
 * 成功处理数据
 *
 * @param <T>
 */
public interface ISuccess<T> {
    void onSuccess(T value);
}
