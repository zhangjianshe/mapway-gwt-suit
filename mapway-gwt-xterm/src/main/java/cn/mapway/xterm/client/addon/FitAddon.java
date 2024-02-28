package cn.mapway.xterm.client.addon;

import cn.mapway.xterm.client.Terminal;
import cn.mapway.xterm.client.service.ITerminalAddon;
import jsinterop.annotations.JsType;

/**
 * FitAddon
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true, namespace = "FitAddon", name = "FitAddon")
public class FitAddon implements ITerminalAddon {
    public FitAddon() {
    }

    @Override
    public native void dispose();

    @Override
    public native void active(Terminal terminal);

    public native void fit();
}
