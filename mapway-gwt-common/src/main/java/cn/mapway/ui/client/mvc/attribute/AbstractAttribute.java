package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.mvc.attribute.design.EditorDataFormat;
import cn.mapway.ui.client.mvc.attribute.design.IEditorData;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import cn.mapway.ui.client.mvc.attribute.editor.impl.TextboxAttributeEditor;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.util.StringUtil;
import elemental2.core.Global;
import elemental2.core.JsObject;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * AttributeAdaptor
 *
 * @author zhang
 */

public abstract class AbstractAttribute implements IAttribute {

    private final String id;
    protected String altName;
    protected int dataType;
    protected String defaultValue;
    protected String description;
    protected Boolean readOnly = false;
    protected Boolean required = false;
    protected String validator = "";
    protected String group = "";
    protected Integer rank = 0;
    protected String tip = "";
    protected String errorTip = "";
    protected String icon = "";
    protected String options = "";
    /**
     * 设计期间的选项
     */
    protected JsObject designOption;
    protected IOptionProvider optionProvider = null;
    protected boolean initVisible = true;
    // 属性名称
    protected String name;
    IEditorData editorData;
    private String editorCode;
    private EditorOption runtimeOption;

    public AbstractAttribute(String name) {
        this(name, name);
    }

    public AbstractAttribute(String name, String alterName) {
        this(name, alterName, TextboxAttributeEditor.EDITOR_CODE);
    }

    /**
     * 构建一个自定义模块的属性
     *
     * @param name           输出名称
     * @param customEditCode 编辑器代码
     */
    public AbstractAttribute(String name, String alterName, String customEditCode) {
        //每个属性定义都会有一个唯一的实力ID
        this.id = StringUtil.randomString(8);

        //编辑器代码
        this.editorCode = customEditCode;
        this.name = name;
        this.altName = alterName;

        this.runtimeOption = new EditorOption();
    }


    public AbstractAttribute() {
        this("未命名", "未命名");
    }

    public String getId() {
        return id;
    }

    @Override
    public EditorOption getRuntimeOption() {
        return runtimeOption;
    }

    public void setRuntimeOption(EditorOption runtimeOption) {
        if (runtimeOption == null) {
            this.runtimeOption = new EditorOption();
        } else {
            this.runtimeOption = runtimeOption;
        }
    }

    @Override
    public JsObject getDesignOption() {
        return designOption;
    }

    /**
     * 解析设计器的组件参数
     *
     * @param designOptionJson
     * @return
     */
    public AbstractAttribute parseDesignOption(String designOptionJson) {
        if (designOptionJson == null || designOptionJson.length() == 0) {
            designOption = new JsObject();
            return this;
        }
        try {
            this.designOption = Js.uncheckedCast(Global.JSON.parse(designOptionJson));
        } catch (Exception e) {
            Logs.info("parseEditor error " + designOptionJson);
            designOption = new JsObject();
        }
        return this;
    }

    /**
     * editor JSON字符串中包含了所有的额编辑器信息
     * 处理编辑器代码 editorCode 以及设计器的参数 designOpotions
     *
     * @return
     */
    public AbstractAttribute parseEditor(String editor) {
        EditorOption option = EditorOption.parse(editor);
        Object o = option.get(EditorOption.KEY_EDITOR_CODE);
        if (o instanceof String) {
            editorCode = (String) o;
        }
        return parseDesignOption(option.getDesignOptions());
    }

    @Override
    public String getName() {
        return name;
    }

    public AbstractAttribute setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * editorCode is a readonly property
     * 只有通过构造函数设定
     *
     * @return
     */
    public String getEditorCode() {
        return editorCode;
    }

    @Override
    public String getAltName() {
        return altName;
    }

    public AbstractAttribute setAltName(String altName) {
        this.altName = altName;
        return this;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    public AbstractAttribute setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public AbstractAttribute setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean isReadonly() {
        return readOnly;
    }

    public AbstractAttribute setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public AbstractAttribute setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public int getDataType() {
        return dataType;
    }

    public AbstractAttribute setDataType(int dataType) {
        this.dataType = dataType;
        return this;
    }

    @Override
    public String getValidateRegx() {
        return validator;
    }

    public AbstractAttribute setValidateRegx(String validateRegx) {
        this.validator = validateRegx;
        return this;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public AbstractAttribute setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public int getRank() {
        return rank;
    }

    public AbstractAttribute setRank(int rank) {
        this.rank = rank;
        return this;
    }

    @Override
    public String getTip() {
        return tip;
    }

    public AbstractAttribute setTip(String tip) {
        this.tip = tip;
        return this;
    }

    @Override
    public String getErrorTip() {
        return errorTip;
    }

    public AbstractAttribute setErrorTip(String errorTip) {
        this.errorTip = errorTip;
        return this;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public AbstractAttribute setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public boolean isInitVisible() {
        return initVisible;
    }

    /**
     * 设置初始显示
     *
     * @param visible
     * @return
     */
    public AbstractAttribute setInitVisible(Boolean visible) {
        this.initVisible = visible;
        return this;
    }

    @Override
    public IOptionProvider getOptionProvider() {
        return optionProvider;
    }

    public AbstractAttribute setOptionProvider(IOptionProvider optionProvider) {
        this.optionProvider = optionProvider;
        return this;
    }

    @Override
    public String getOptions() {
        return options;
    }

    public AbstractAttribute setOptions(String options) {
        this.options = options;
        //TODO 处理此处逻辑 很有可能不再使用了
        return this;
    }

    @Override
    public IEditorData getEditorData() {
        return editorData;
    }

    public void setEditorData(IEditorData editorData) {
        this.editorData = editorData;
    }

    /**
     * 这个方法 把一个属性定义信息转化为JSON对象的字符串
     *
     * @return
     */
    @Override
    public String toJSON() {

        JsPropertyMap object = JsPropertyMap.of("id", id);
        object.set("name", name);
        object.set("altName", altName);
        object.set("dataType", dataType);
        object.set("defaultValue", defaultValue);
        object.set("description", description);
        object.set("readOnly", readOnly);
        object.set("required", required);
        object.set("initVisible", initVisible);
        object.set("validator", validator);
        object.set("rank", rank);
        object.set("tip", tip);
        object.set("errorTip", errorTip);
        object.set("icon", icon);
        if (getEditorData() != null) {
            object.set("editorData", getEditorData().save(EditorDataFormat.EDF_JSON));
        } else {
            object.set("editorData", "");
        }
        return Global.JSON.stringify(object);
    }

}
