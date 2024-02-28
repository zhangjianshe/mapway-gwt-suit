package cn.mapway.xterm.client.theme;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * FitAddon
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Theme {
    @JsProperty
    public String background;
    @JsProperty
    public String foreground;
    @JsProperty
    public String selection;
    @JsProperty
    public String cursor;

    public Theme() {
    }
}
