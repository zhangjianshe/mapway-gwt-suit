package cn.mapway.mqtt.client;

import jsinterop.annotations.JsFunction;

/**
 * IOnConnected
 *
 * @author zhangjianshe@gmail.com
 */
@JsFunction
public interface IOnConnected {
    void onConnection(boolean reconnected, String url);
}
