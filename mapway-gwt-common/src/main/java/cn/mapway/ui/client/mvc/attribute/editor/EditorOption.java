package cn.mapway.ui.client.mvc.attribute.editor;

import java.util.HashMap;
import java.util.Map;

public class EditorOption {
    Map<String, Object> options;
    public final static String KEY_INIT_DIR="init_dir";
    public EditorOption() {
        options = new HashMap<String, Object>();
    }

    public EditorOption(Map<String, Object> options) {
        this.options.putAll(options);
    }

    public EditorOption(String key, Object value) {
        this();
        options.put(key, value);
    }

    public EditorOption set(String key, Object value) {
        options.put(key, value);
        return this;
    }

    public EditorOption width(String width) {
        options.put("width", width);
        return this;
    }

    public EditorOption height(String height) {
        options.put("height", height);
        return this;
    }

    public String getWidth() {
        return (String) get("width");
    }

    public String getHeight() {
        return (String) get("height");
    }

    public Object get(String key) {
        return options.get(key);
    }

    public void clear() {
        options.clear();
    }

    public void remove(String key) {
        options.remove(key);
    }
}
