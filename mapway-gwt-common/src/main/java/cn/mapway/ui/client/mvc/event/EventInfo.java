package cn.mapway.ui.client.mvc.event;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;

/**
 * 用于描述一个事件
 */
@JsType
public class EventInfo {
    public String name;
    public String code;
    public String summary;
    public String signature;
    public String group;

    @JsConstructor
    public EventInfo() {
    }

    public static EventInfo create(String name, String code, String summary, String signature, String group) {
        EventInfo eventInfo = new EventInfo();
        eventInfo.name = name;
        eventInfo.code = code;
        eventInfo.summary = summary;
        eventInfo.signature = signature;
        eventInfo.group = group;
        return eventInfo;
    }
}
