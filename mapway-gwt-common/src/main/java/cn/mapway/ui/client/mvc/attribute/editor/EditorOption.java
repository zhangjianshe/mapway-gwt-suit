package cn.mapway.ui.client.mvc.attribute.editor;

import cn.mapway.ui.client.tools.JSON;
import cn.mapway.ui.client.util.Logs;
import elemental2.core.JsObject;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import java.util.HashMap;
import java.util.Map;

public class EditorOption {
    public final static String KEY_INIT_DIR = "init_dir";
    public final static String KEY_IMAGE_UPLOAD_ACTION = "upload_action";
    public final static String KEY_IMAGE_UPLOAD_REL = "upload_action_rel";
    public final static String KEY_WIDTH = "width";
    public final static String KEY_HEIGHT = "height";
    public final static String KEY_SELECT_FILE_TYPE = "select_file_type";
    public final static String KEY_FILE_SUFFIX = "file_suffix";
    public final static String KEY_OPTIONS = "options";
    public static final String KEY_EDITOR_CODE = "editorCode";
    public static final String KEY_EDITOR_NAME = "editorName";
    public static final String KEY_DROPDOWN_OPTIONS = "dropdown_options";

    Map<String, Object> options;

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

    public final static EditorOption parse(String json) {
        EditorOption option = new EditorOption();
        option.parseOptionValue(json);
        return option;
    }



    /**
     * 获取选项
     *
     * @return
     */
    public Map<String, Object> getOptions() {
        return options;
    }

    public EditorOption set(String key, Object value) {
        options.put(key, value);
        return this;
    }

    public EditorOption width(String width) {
        options.put(KEY_WIDTH, width);
        return this;
    }

    public EditorOption height(String height) {
        options.put(KEY_HEIGHT, height);
        return this;
    }

    public String getWidth() {
        return (String) get(KEY_WIDTH);
    }

    public String getHeight() {
        return (String) get(KEY_HEIGHT);
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

    public String getImageUploadAction() {
        return (String) get(KEY_IMAGE_UPLOAD_ACTION);
    }

    public void setImageUploadAction(String uploadAction) {
        set(KEY_IMAGE_UPLOAD_ACTION, uploadAction);
    }

    public String getImageUploadRelPath() {
        return (String) get(KEY_IMAGE_UPLOAD_REL);
    }

    public void setImageUploadRelPath(String relPath) {
        set(KEY_IMAGE_UPLOAD_REL, relPath);
    }

    /**
     * value is a json Serialize String
     *
     * @param jsonString
     */
    public void parseOptionValue(String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            return;
        }
        try {
            JsObject object = Js.uncheckedCast(JSON.parse(jsonString));
            parseOptionJsObject(object);
        } catch (Exception e) {
            Logs.info("Error parsing JSON String " + jsonString);
        }
    }

    public void parseOptionJsObject(JsObject jsObject) {
        if (jsObject == null) {
            return;
        }
        JsPropertyMap<Object> propertyMap = Js.asPropertyMap(jsObject);
        propertyMap.forEach(key -> set(key, propertyMap.get(key)));
    }

    /**
     * 将对象序列化为一个JSON对象
     *
     * @return
     */
    public String toJson() {
        JsPropertyMap<Object> all = JsPropertyMap.of();
        for (String key : options.keySet()) {
            all.set(key, options.get(key));
        }
        return JSON.stringify(all);
    }

    public void merge(EditorOption editorOption) {
        if (editorOption == null) {
            return;
        }
        for (String key : editorOption.options.keySet()) {
            set(key, editorOption.options.get(key));
        }
    }
}
