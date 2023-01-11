package cn.mapway.ui.client.widget.editor;

import cn.mapway.ui.client.util.StringUtil;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import java.util.ArrayList;
import java.util.List;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * EditOptions
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true, namespace = GLOBAL, name = "Object")
public class EditOptions {
    @JsProperty
    public String[] extraPlugins;

    @JsProperty
    public String filebrowserImageUploadUrl;

    public static class Builder {
        List<Object> toolbars;
        List<String> _plugins;
        String fileUploadUrl;

        public Builder() {
        }

        public Builder uploadUrl(String url) {
            this.fileUploadUrl = url;
            return this;
        }

        public Builder addToolbarGroup(String... tools) {
            if (toolbars == null) {
                toolbars = new ArrayList();
            }
            if (tools != null) {
                List<String> group = new ArrayList();
                for (String tool : tools) {
                    group.add(tool);
                }
                toolbars.add(group);
            }
            return this;
        }

        public Builder addToolbar(String... tools) {
            if (toolbars == null) {
                toolbars = new ArrayList();
            }
            if (tools != null) {
                for (String tool : tools) {
                    toolbars.add(tool);
                }
            }
            return this;
        }

        public Builder addPlugins(String... plugins) {
            if (plugins != null && plugins.length > 0) {
                if (_plugins == null) {
                    _plugins = new ArrayList<>();
                }
                for (String plugin : plugins) {
                    _plugins.add(plugin);
                }
            }
            return this;
        }

        public Builder fullToolBar() {
            //
            addToolbar("heading", "|",
                    "fontfamily", "fontsize", "|",
                    "alignment", "|",
                    "fontColor", "fontBackgroundColor", "|",
                    "bold", "italic", "strikethrough", "underline", "subscript", "superscript", "|",
                    "link", "|",
                    "outdent", "indent", "|",
                    "bulletedList", "numberedList", "todoList", "|",
                    "code", "codeBlock", "|",
                    "insertTable", "|",
                    "uploadImage", "blockQuote", "|",
                    "undo", "redo");
            return this;
        }

        public EditOptions build() {
            EditOptions options = new EditOptions();
            if (!StringUtil.isBlank(fileUploadUrl)) {
                options.filebrowserImageUploadUrl = fileUploadUrl;
            }
            if (_plugins != null && _plugins.size() > 0) {
                options.extraPlugins = new String[_plugins.size()];
                for (int i = 0; i < _plugins.size(); i++) {
                    options.extraPlugins[i] = _plugins.get(i);
                }
            }
            return options;
        }
    }
}
