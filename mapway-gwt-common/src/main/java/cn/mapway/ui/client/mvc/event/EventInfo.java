package cn.mapway.ui.client.mvc.event;

import lombok.Data;

/**
 * 用于描述一个事件
 */
@Data
public class EventInfo {
    String name;
    String code;
    String summary;
    String signature;
    String group;

    public EventInfo() {

    }

    public EventInfo(String name, String code, String summary, String signature, String group) {
        this.name = name;
        this.code = code;
        this.summary = summary;
        this.signature = signature;

        this.group = group;
    }
}
