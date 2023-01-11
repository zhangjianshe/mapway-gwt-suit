package cn.mapway.xterm.client.addon;

import cn.mapway.xterm.client.Terminal;
import cn.mapway.xterm.client.service.ITerminalAddon;
import elemental2.dom.WebSocket;
import jsinterop.annotations.JsType;

/**
 * AttachAddon
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true, namespace = "AttachAddon", name = "AttachAddon")
public class AttachAddon implements ITerminalAddon {
    public AttachAddon(WebSocket webSocket,AttachOptions options) {
    }

    @Override
    public native void dispose();

    @Override
    public native void active(Terminal terminal);
}

