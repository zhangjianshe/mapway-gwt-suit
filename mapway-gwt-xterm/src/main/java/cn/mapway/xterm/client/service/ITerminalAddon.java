package cn.mapway.xterm.client.service;

import cn.mapway.xterm.client.Terminal;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * ITerminalAddon
 * 具体的实现可以引用这个类
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface ITerminalAddon extends IDisposable {
    void active(Terminal terminal);
}
