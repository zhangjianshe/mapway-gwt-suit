package cn.mapway.ui.client.widget.editor;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/**
 * Editor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true)
public class ComponentFactory {

    @JsMethod
    public native String[] names();
}
