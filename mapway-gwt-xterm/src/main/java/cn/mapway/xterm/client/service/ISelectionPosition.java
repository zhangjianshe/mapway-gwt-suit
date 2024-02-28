package cn.mapway.xterm.client.service;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * ISelectionPosition
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true,namespace = JsPackage.GLOBAL)
public interface ISelectionPosition {
    @JsProperty
    int getEndColumn();
    @JsProperty
    int getEndRow();
    @JsProperty
    int getStartColumn();
    @JsProperty
    int getStartRow();
}
