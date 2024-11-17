package cn.mapway.mqtt.client;

import jsinterop.annotations.JsFunction;

/**
 * IOnMessageArrived
 *
 * @author zhangjianshe@gmail.com
 */
@JsFunction
public interface IOnMessageArrived {
    void onMessageArrived(MqttMessage message);
}
