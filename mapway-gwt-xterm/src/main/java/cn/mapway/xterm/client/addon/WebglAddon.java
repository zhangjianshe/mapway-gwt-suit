package cn.mapway.xterm.client.addon;

import cn.mapway.xterm.client.Terminal;
import cn.mapway.xterm.client.service.ITerminalAddon;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "WebglAddon", name = "WebglAddon")
public class WebglAddon implements ITerminalAddon {

    public IContextLose onContextLoss;

    @Override
    public native void dispose();

    @Override
    public native void active(Terminal terminal);
}
