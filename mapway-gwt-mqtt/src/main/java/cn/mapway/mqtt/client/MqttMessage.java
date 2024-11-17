package cn.mapway.mqtt.client;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;

/**
 * MqttMessage
 * use
 * @author zhangjianshe@gmail.com
 */
@JsType(isNative = true,namespace = "Paho.MQTT",name = "Message")
public class MqttMessage {
    @JsConstructor
    public MqttMessage(String payload)
    {
    }
    public String payloadString;
    public byte[] payloadBytes;
    public int qos;
    public boolean retained;
    public boolean duplicate;
    public String destinationName;
}
