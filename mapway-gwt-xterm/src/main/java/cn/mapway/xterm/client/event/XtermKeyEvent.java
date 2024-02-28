package cn.mapway.xterm.client.event;

import elemental2.dom.KeyboardEvent;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * XtermKeyEvent
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class XtermKeyEvent {
    @JsProperty
    public String key;
    @JsProperty
    public KeyboardEvent domEvent;
}
