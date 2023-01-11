package cn.mapway.ui.client.event;

import java.util.*;

/**
 * 简单事件总线.
 *
 * @author zhangjianshe
 */
public class EventBus {

    /**
     * The events.
     */
    public Map<String, List<IEventHandler>> events;

    /**
     * Instantiates a new event bus.
     */
    public EventBus() {
        events = new HashMap<String, List<IEventHandler>>();
    }


    /**
     * 向消息总线注册消息句柄.
     *
     * @param topic   the topic
     * @param handler the handler
     * @return true, if successful
     */
    public boolean register(String topic, IEventHandler handler) {
        if (topic == null || topic.length() == 0 || handler == null) {
            return false;
        }
        List<IEventHandler> list = events.get(topic);
        if (list == null) {
            list = new ArrayList<IEventHandler>();
            events.put(topic, list);
        }
        boolean exist = exist(list, handler);
        if (exist) {
            return false;
        } else {
            list.add(handler);
            return true;
        }
    }

    /**
     * Exist.
     *
     * @param list    the list
     * @param handler the handler
     * @return true, if successful
     */
    private boolean exist(List<IEventHandler> list, IEventHandler handler) {
        for (IEventHandler h : list) {
            if (h.equals(handler)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 向事件总线移除消息处理句柄.
     *
     * @param topic   the topic
     * @param handler the handler
     * @return true, if successful
     */
    public boolean unregister(String topic, IEventHandler handler) {
        if (topic == null || handler == null) {
            return false;
        }
        List<IEventHandler> list = events.get(topic);
        if (list == null) {
            return false;
        }
        list.remove(handler);
        if (list.size() == 0) {
            events.remove(topic);
        }
        return true;
    }

    /**
     * 激发一个事件.
     *
     * @param topic the topic
     * @param type  the type
     * @param event the event
     */
    public void fire(String topic, int type, Object event) {
        List<IEventHandler> list = events.get(topic);
        if (list == null) {
            return;
        }
        Iterator<IEventHandler> it = list.iterator();
        while (it.hasNext()) {
            it.next().onEvent(topic, type, event);
        }
    }
}