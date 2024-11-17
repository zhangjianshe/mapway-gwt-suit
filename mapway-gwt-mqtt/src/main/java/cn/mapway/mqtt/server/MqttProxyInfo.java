package cn.mapway.mqtt.server;

import lombok.Data;

/**
 * MqttProxyInfo
 *
 * @author zhangjianshe@gmail.com
 */

@Data
public class MqttProxyInfo {
    String server;
    String port;
    String userName;
    String password;
}
