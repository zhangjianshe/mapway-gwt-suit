package cn.mapway.xterm.client;

import cn.mapway.xterm.client.event.IOnKeyHandler;
import cn.mapway.xterm.client.service.IDisposable;
import cn.mapway.xterm.client.service.ISelectionPosition;
import cn.mapway.xterm.client.service.ITerminalAddon;
import cn.mapway.xterm.client.theme.Theme;
import com.google.gwt.dom.client.Element;
import elemental2.core.JsObject;
import jsinterop.annotations.*;

/**
 * Terminal
 * Xterm.js Main UI
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Terminal")
public class Terminal implements IDisposable {
    /**
     * 构造一个终端对象
     *
     * @param options
     */
    public Terminal(TerminalOptions options) {
    }

    @JsMethod
    public native IDisposable onKey(IOnKeyHandler handler);

    /**
     * 打开一个终端
     *
     * @param e
     */
    public native void open(Element e);

    @JsProperty
    public native TerminalOptions getOptions();

    @JsProperty
    public native int getCols();

    @JsProperty
    public native Element getElement();

    @JsProperty
    public native int getRows();

    public native void write(String data);

    public native void writeln(String data);

    public native void resize(int columns, int rows);

    public native void scrollLines(int amount);

    public native void reset();

    public native void paste(String data);

    public native void refresh(int start, int end);

    public native void loadAddon(ITerminalAddon addon);

    public native boolean hasSelection();

    public native String getSelection();

    public native ISelectionPosition getSelectionPosition();

    public native JsObject getOption(String key);

    public native void focus();

    public native void deregisterLinkMatcher(Integer matcherId);

    public native void deregisterCharacterJoiner(Integer joinerId);

    public native void clearSelection();

    public native void clear();

    public native void blur();

    public native void scrollToBottom();

    public native void scrollToTop();

    public native void scrollToLine(int line);

    public native void select(int column, int row, int length);

    public native void selectAll();

    public native void selectLines(int start, int end);

    /**
     * fontFamily”	“termName”	“bellSound”	“wordSeparator”,
     *
     * @param key   @seeAlso OptionKeys
     * @param value
     */
    public native void setOption(String key, JsObject value);


    public native void dispose();

    @JsOverlay
    public final void setTheme(Theme theme) {
        getOptions().theme=theme;
    }
}
