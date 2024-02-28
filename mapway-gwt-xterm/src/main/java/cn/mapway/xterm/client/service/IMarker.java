package cn.mapway.xterm.client.service;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * IMarker
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true,namespace = JsPackage.GLOBAL)
public interface IMarker extends IDisposable{
    @JsProperty
    Integer getId();

    @JsProperty
    Boolean isDisposed();

    @JsProperty
    Integer getLine();
}
