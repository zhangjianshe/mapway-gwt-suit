package cn.mapway.xterm.client.event;

import jsinterop.annotations.JsFunction;

/**
 * IOnKeyHandler
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsFunction
public interface IOnKeyHandler {
    void apply(XtermKeyEvent event);
}
