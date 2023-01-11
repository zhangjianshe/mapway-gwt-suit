package cn.mapway.xterm.client.addon;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * IAttachOptions
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType( namespace = JsPackage.GLOBAL,name = "Object")
public class AttachOptions {
    @JsProperty
    boolean bidirectional;

    protected AttachOptions() {

    }

    public static class Builder {
        Boolean bidirectional;

        public Builder setBidirectional(boolean bidirectional) {
            this.bidirectional = bidirectional;
            return this;
        }

        public AttachOptions build() {
            AttachOptions options = new AttachOptions();
            if (bidirectional != null) {
                options.bidirectional = bidirectional;
            }
            return options;
        }
    }
}
