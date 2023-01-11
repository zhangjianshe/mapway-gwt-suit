package cn.mapway.ui.client.widget.editor;

import com.google.gwt.user.client.Element;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/**
 * Editor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true)
public class Editor {

    @JsMethod
    public native void focus();

    @JsMethod
    public native String getData();

    @JsMethod
    public native void setData(String data);

    @JsMethod
    public native void resize(int width, int height);

    @JsMethod
    public native void editable(Element element);

    @JsMethod
    public native boolean isDestroyed();

    @JsMethod
    public native void destroy();
}
