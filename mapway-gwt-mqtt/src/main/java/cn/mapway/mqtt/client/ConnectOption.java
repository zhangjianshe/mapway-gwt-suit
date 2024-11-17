package cn.mapway.mqtt.client;

import jsinterop.annotations.JsType;

/**
 * ConnecOption
 *
 * @author zhangjianshe@gmail.com
 */
@JsType()
public class ConnectOption {

    public int mqttVersion=4;
    public int timeout=6000;
    public String   userName;
    public String   password;
    public String willMessage;
    public int keepAliveInterval;
    public boolean cleanSession=true;
    public boolean useSSL=false;
    public String[] hosts;
    public boolean reconnect=true;

}
