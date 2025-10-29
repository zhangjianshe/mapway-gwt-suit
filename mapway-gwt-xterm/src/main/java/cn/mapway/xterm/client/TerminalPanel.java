package cn.mapway.xterm.client;

import cn.mapway.xterm.client.addon.FitAddon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * TerminalPanel
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class TerminalPanel extends SimplePanel implements RequiresResize {

    Terminal terminal;
    FitAddon fitAddon;


    public TerminalPanel() {
        setWidth("100%");
        setHeight("100%");
    }

    /**
     * 创建一个终端面板
     *
     * @param options
     * @return
     */
    public Terminal create(TerminalOptions options) {

        if (terminal != null) {
            GWT.log("terminal has already initialized");
            return terminal;
        }
        fitAddon = new FitAddon();
        terminal = new Terminal(options);
        terminal.loadAddon(fitAddon);
        terminal.open(getElement());
        fitAddon.fit();
        return terminal;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void resize() {
        if (fitAddon != null) {
            fitAddon.fit();
        }
    }

    public int getTerminalWidth() {
        return terminal.getCols();
    }

    public int getTerminalHeight() {
        return terminal.getRows();
    }

    /**
     * 清空UI
     */
    public void clear() {
        terminal.clear();
    }

    @Override
    public void onResize() {
        resize();
    }
}
