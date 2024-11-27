package cn.mapway.ui.client.tools;

import cn.mapway.ui.client.event.EventBus;
import cn.mapway.ui.client.event.IEventHandler;
import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.frame.ModuleDispatcher;
import cn.mapway.ui.client.mvc.SwitchModuleData;
import cn.mapway.ui.shared.CommonConstant;

/**
 * Databus 全局的数据总线
 * 组件核模块可以订阅或者
 * 浏览器页面是一个应用程序代码 这个应用和MQTT服务器之间 只有两个连接
 * 1.个人消息  /ai/userid/#
 * 2.系统消息  /ai/system/#
 * 各种应用消息都通过这一个连接进行
 * MQTT会将接受的消息转发到这个数据总线上，由数据总线进行转发
 * @author zhangjianshe@gmail.com
 */
public class DataBus {
    public static final String TOPIC_UI_THEME_CHANGED = "ui_theme_changed";
    private static DataBus DATA_BUS;
    private EventBus eventBus;

    protected DataBus() {
        eventBus = new EventBus();
    }

    /**
     * 全局数据总线
     *
     * @return
     */
    public static DataBus get() {
        if (DATA_BUS == null) {
            DATA_BUS = new DataBus();
        }
        return DATA_BUS;
    }

    /**
     * 注册事件
     *
     * @param topic
     * @param handler
     */
    public void register(String topic, IEventHandler handler) {
        eventBus.register(topic, handler);
    }

    /**
     * 撤销对事件的监听
     *
     * @param topic
     * @param handler
     */
    public void unregister(String topic, IEventHandler handler) {
        eventBus.unregister(topic, handler);
    }

    public void fire(String topic, int type, Object event) {
        eventBus.fire(topic, type, event);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void switchModule(SwitchModuleData data) {
        getEventBus().fire(ModuleDispatcher.MODULE_DISPATCH_EVENT,0,data);
    }

    public void message(String message) {
        getEventBus().fire(CommonConstant.BUS_EVENT_MESSAGE,0, MessageObject.info(0,message));
    }

    /**
     * 像总线发送 返回上个页面请求
     */
    public void back() {
        getEventBus().fire(ModuleDispatcher.MODULE_RETURN_EVENT,0,0);
    }
}
