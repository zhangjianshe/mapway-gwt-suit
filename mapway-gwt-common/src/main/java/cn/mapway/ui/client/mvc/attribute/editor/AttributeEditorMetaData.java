package cn.mapway.ui.client.mvc.attribute.editor;

import cn.mapway.ui.client.mvc.attribute.editor.impl.TextboxAttributeEditor;
import cn.mapway.ui.client.util.Logs;
import elemental2.core.Global;
import elemental2.core.JsObject;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import java.util.function.BiConsumer;

/**
 * 属性编辑器信息 根据此可以构建一个编辑器实例
 */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class AttributeEditorMetaData {
    /**
     * 编辑器代码
     */
    public String code;
    public String name;
    /**
     * 编辑器设计属性
     */
    public JsObject options;

    /**
     * 解析一个JSON字符串 为JSON对象
     *
     * @param json
     * @return
     */
    @JsOverlay
    public static AttributeEditorMetaData parse(String json) {
        AttributeEditorMetaData editorValue = null;
        if (json != null && json.length() >= 2) {
            try {
                editorValue = Js.uncheckedCast(Global.JSON.parse(json));
            } catch (Exception e) {
                Logs.info("Error parsing editor options " + json);
            }
        }
        if (editorValue == null) {
            editorValue = new AttributeEditorMetaData();
        }
        if (editorValue.code == null || editorValue.code.length() == 0) {
            editorValue.code = TextboxAttributeEditor.EDITOR_CODE;
        }
        return editorValue;
    }

    @JsOverlay
    public final String toJSON() {
        return Global.JSON.stringify(this);
    }

    @JsOverlay
    public final JsObject opt(String key) {
        if (options == null) {
            return null;
        }
        return Js.uncheckedCast(Js.asPropertyMap(options).get(key));
    }

    /**
     * 设置属性
     *
     * @param key
     * @param value
     * @return
     */
    @JsOverlay
    public final AttributeEditorMetaData set(String key, String value) {
        if (options == null) {
            options = new JsObject();
        }
        Js.asPropertyMap(options).set(key, value);
        return this;
    }

    /**
     * 设置设计时的参数数据
     *
     * @param optionsString
     * @return
     */
    @JsOverlay
    public final AttributeEditorMetaData setDesignOption(String optionsString) {
        if (optionsString == null) {
            optionsString = "";
        }
        return set(EditorOption.KEY_DESIGN_OPTIONS, optionsString);
    }


    /**
     * 将选项信息转移到 editorOptionzhong 中
     *
     * @return
     */
    @JsOverlay
    public final EditorOption toEditorOption() {
        EditorOption editorOption = new EditorOption();
        if (options == null) {
            return editorOption;
        }
        JsPropertyMap<Object> propertyMap = Js.asPropertyMap(options);
        propertyMap.forEach(key -> {
            editorOption.set(key, propertyMap.get(key));
        });
        return editorOption;
    }

    @JsOverlay
    public final AttributeEditorMetaData mergeEditorOption(EditorOption editorOption) {
        if (editorOption != null) {
            if (options == null) {
                options = new JsObject();
            }
            JsPropertyMap<Object> propertyMap = Js.asPropertyMap(options);
            editorOption.getOptions().forEach(new BiConsumer<String, Object>() {
                @Override
                public void accept(String key, Object value) {
                    propertyMap.set(key, value);
                }
            });

        }
        return this;
    }

    /**
     * merge edition option
     *
     * @param editorOptionJson is a  json object
     *                         {
     *                         key:value
     *                         }
     * @return
     */
    @JsOverlay
    public final AttributeEditorMetaData mergeEditorOptionString(String editorOptionJson) {
        EditorOption editorOption = EditorOption.parse(editorOptionJson);
        return mergeEditorOption(editorOption);
    }
}
