package cn.mapway.echart.client;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * @Author baoshuaiZealot@163.com  2023/3/22
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class AxisPointer {

    public String type;

    @JsOverlay
    public final AxisPointer set(String key, Object value){
        Js.asPropertyMap(this).set(key, value);
        return this;
    }

}
