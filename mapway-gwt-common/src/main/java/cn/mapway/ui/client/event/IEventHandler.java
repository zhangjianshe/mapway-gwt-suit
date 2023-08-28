package cn.mapway.ui.client.event;

/**
 * 事件处理接口.
 *
 * @author zhangjianshe
 */
public interface IEventHandler<T> {

    /**
     * 事件处理.
     *
     * @param topic the topic
     * @param type  the type
     * @param event the event
     */
    void onEvent(String topic, int type, T event);
}