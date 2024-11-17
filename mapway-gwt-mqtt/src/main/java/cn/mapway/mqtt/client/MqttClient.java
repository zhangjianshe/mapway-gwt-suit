package cn.mapway.mqtt.client;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * MqttClient
 *
 * @author zhangjianshe@gmail.com
 */
@JsType(isNative = true, namespace = "Paho.MQTT", name = "Client")
public class MqttClient {
    public String host;
    public String port;
    public String path;
    public String clientId;
    public IOnConnected onConnected;
    public IOnMessageArrived onMessageArrived;


    @JsConstructor
    public MqttClient(String url, Integer port,String path,String clientId) {
    }
    @JsConstructor
    public MqttClient(String url, String clientId) {
    }

    public native void connect(ConnectOption option);

    public native void subscribe(String topic);

    public native boolean isConnected();

    public native void unsubscribe(String topic);

    public native void send(MqttMessage message);

    @JsOverlay
    public final void send(String topic, String message) {
        MqttMessage msg = new MqttMessage(message);
        msg.destinationName = topic;
        send(msg);
    }

    @JsOverlay
    public final void reConnect() {
        this.connect(new ConnectOption());
    }
}
