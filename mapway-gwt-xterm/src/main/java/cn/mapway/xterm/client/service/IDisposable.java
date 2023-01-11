package cn.mapway.xterm.client.service;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * IDisposable
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true,namespace = JsPackage.GLOBAL)
public interface IDisposable {
    void dispose();
}
