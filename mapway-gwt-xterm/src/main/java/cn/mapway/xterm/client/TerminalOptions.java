package cn.mapway.xterm.client;

import cn.mapway.xterm.client.theme.Theme;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * TerminalOptions
 * Options for terminal
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true, namespace = GLOBAL, name = "Object")
public class TerminalOptions {
    @JsProperty
    boolean allowProposedApi;
    @JsProperty
    boolean allowTransparency;
    @JsProperty
    int cols;
    @JsProperty
    int rows;
    @JsProperty
    boolean cursorBlink;
    @JsProperty
    boolean convertEol;
    @JsProperty
    String rendererType;
    @JsProperty
    public Theme theme;

    @JsProperty
    public String fontFamily;

    @JsProperty
    public float fontSize;
    @JsProperty
    public float letterSpacing;
    @JsProperty
    public float lineHeight;
    @JsProperty
    public String fontWeight;
    @JsProperty
    public String fontWeightBold;

    protected TerminalOptions() {

    }

    public static class Builder {
        Boolean allowProposedApi;
        Boolean allowTransparency;
        Integer cols;
        Integer rows;
        Boolean cursorBlink;
        Boolean convertEol;
        String rendererType;

        public Builder() {
            convertEol = true;
        }

        public Builder setCols(int cols) {
            this.cols = cols;
            return this;
        }

        public Builder setRows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder setCursorBlink(Boolean cursorBlink) {
            this.cursorBlink = cursorBlink;
            return this;
        }

        public Builder setAllowProposedApi(boolean allowProposedApi) {
            this.allowProposedApi = allowProposedApi;
            return this;
        }

        public Builder setAllowTransparency(boolean allowTransparency) {
            this.allowTransparency = allowTransparency;
            return this;
        }

        public Builder setConvertEol(boolean convertEol) {
            this.convertEol = convertEol;
            return this;
        }
        public Builder setRendererType(String rendererType) {
            this.rendererType = rendererType;
            return this;
        }


        public TerminalOptions build() {
            TerminalOptions options = new TerminalOptions();
            if (allowProposedApi != null) {
                options.allowProposedApi = allowProposedApi;
            }
            if (allowTransparency != null) {
                options.allowTransparency = allowTransparency;
            }
            if (cols != null) {
                options.cols = cols;
            }
            if (rows != null) {
                options.rows = rows;
            }
            if (cursorBlink != null) {
                options.cursorBlink = cursorBlink;
            }
            if (convertEol != null) {
                options.convertEol = convertEol;
            }
            if (rendererType!= null) {
                options.rendererType = rendererType;
            }
            return options;
        }
    }
}
