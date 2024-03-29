package cn.mapway.ui.client.widget.editor;

import com.google.gwt.user.client.Element;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
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
    public native void resize(String width, String height,boolean isContentHeight);

    @JsMethod
    public native void editable(Element element);

    @JsMethod
    public native boolean isDestroyed();

    @JsMethod
    public native void destroy();

    @JsProperty
    public HTMLElement container;
}
