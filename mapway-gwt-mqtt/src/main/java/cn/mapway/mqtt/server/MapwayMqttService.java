package cn.mapway.mqtt.server;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.nutz.json.Json;
import org.nutz.lang.random.R;

import java.util.HashSet;
import java.util.Set;

/**
 * 抽象类
 *
 * @author zhangjianshe@gmail.com
 */
@Slf4j
public abstract class MapwayMqttService implements MqttCallback {

    MqttProxyInfo info;
    MqttClient client;
    Set<String> topics = null; //订阅的主题列表 <String>

    public MapwayMqttService(MqttProxyInfo info) {
        log.info("连接MQTT服务器信息{}", Json.toJson(info));
        topics = new HashSet<>();
        this.info = info;
        try {
            client = new MqttClient(info.server + ":" + info.port, R.UU16(), new MemoryPersistence());
            client.setCallback(this);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String topic, String message) {
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void addTopic(String topic) {
        topics.add(topic);
    }

    public void clearTopic() {
        topics.clear();
    }

    public void connect() {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(info.userName);
        options.setPassword(info.password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);


        try {
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void send(String topic, String message) {
        sendMessage(topic, message);
    }


    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("mqtt disconnected");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

}
