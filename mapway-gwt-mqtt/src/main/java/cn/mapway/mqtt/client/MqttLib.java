package cn.mapway.mqtt.client;


import elemental2.dom.DomGlobal;
import elemental2.dom.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 客户端MQTT工具
 * 用户登录后 会开启MQTT客户端直接连接 消息中枢，接受消息
 * 消息主要分为2类
 * catalog 1 : 系统消息 用于全局发送的消息 很少使用这种消息 topic为 /system/
 * catalog 2:  用户消息  用于接受与当前登录用户有关的消息  topic为 /user/{userid}/info
 * 在应用程序运行期间一直连接
 * 消息体为  {
 * type: integer
 * id:  integer userID
 * msg:  String
 * }
 * catalog 3:  任务消息  用于接受与当前用户打开的任务相关的消息 topic /ai/{taskid}/#
 * 这类消息 可以随时订阅和退订
 *
 * @author zhangjianshe@gmail.com
 */
public class MqttLib {

    private static MqttLib INSTANCE;
    Map<String, Integer> topics;
    private MqttClient mqttClient;
    private final IOnConnected clientConnected = new IOnConnected() {
        @Override
        public void onConnection(boolean reconnected, String url) {

            for (String topic : topics.keySet()) {
                mqttClient.subscribe(topic);
                info("    subscribe topic "+ topic);
            }
        }
    };

    public void clearTopic()
    {
        topics.clear();
    }
    /**
     * 链接之前 请先添加topic
     * addTopic(BizConstant.MQTT_TOPIC_PROJECT_MESSAGE + "#");
     */
    public MqttLib() {
        topics = new HashMap<>();
        //为了简化topic的层级 进行了简单的组合
    }

    public static synchronized MqttLib get() {
        if (INSTANCE == null) {
            INSTANCE = new MqttLib();
        }
        return INSTANCE;
    }

    private void info(String args) {
        DomGlobal.console.log(args);
    }

    public void addTopic(String topic) {
        topics.put(topic, 1);
    }

    /**
     * MQTT 连接的Url 由服务器返回  服务器从环境变量 AI_MQTT_WEB 变量获取
     * 一般 AI_MQTT_WEB 变量为 http(s)://imagebot.cn:8080/(events)/mqtt
     * 上面的设置 和部署方式有关
     * 分几种情况
     *
     * @param server
     * @param port
     * @return
     */
    public MqttLib connect(String server, Integer port, IOnMessageArrived onMessageArrived) {
        Random r = new Random(System.currentTimeMillis());

        String url = server;
        if (DomGlobal.window.location.protocol.startsWith("https")) {
            if (url.startsWith("http://")) {
                url = url.replace("http://", "https://");
            }
        }
        url = url.replaceFirst("http", "ws");
        info("wire to " + url);
        mqttClient = new MqttClient(url, "web" + r.nextLong());

        mqttClient.onConnected = clientConnected;
        mqttClient.onMessageArrived = onMessageArrived;
        ConnectOption option = new ConnectOption();
        option.mqttVersion = 4;
        //45秒
        option.keepAliveInterval = 45;
        try {
            mqttClient.connect(option);
        } catch (Exception e) {
            info("connect to " + url + " failed" + e.getMessage());
        }
        return this;
    }

    public void unsubAll() {
        if(mqttClient==null)
        {
            return;
        }
        if (mqttClient.isConnected()) {
            for (String topic : topics.keySet()) {
                mqttClient.unsubscribe(topic);
            }
            topics.clear();
        } else {
            topics.clear();
            mqttClient.reConnect();
        }
    }
}
