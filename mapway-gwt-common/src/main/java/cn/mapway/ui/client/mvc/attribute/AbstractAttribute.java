package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.mvc.attribute.design.*;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextboxAttributeEditor;
import cn.mapway.ui.client.util.StringUtil;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

/**
 * AbstractAttribute
 * 提供大部分的属性定义接口
 *
 * @author zhang
 */

public abstract class AbstractAttribute implements IAttribute {

    public static final String BASIC_GROUP = "基本属性";
    private final String id;
    private final ParameterValues runtimeParameters;
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
    protected IOptionProvider optionProvider = null;
    // 属性名称
    protected String name;
    protected boolean visible = true;
    IEditorMetaData editorData;
    IAttributePropertyChangeCallback callback;

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
        EditorMetaData editorMetaData1 = new EditorMetaData();
        editorMetaData1.setEditorCode(customEditCode);
        this.editorData = editorMetaData1;
        this.name = name;
        this.altName = alterName;

        this.runtimeParameters = new ParameterValues();
    }

    public AbstractAttribute() {
        this("未命名", "未命名");
    }

    public String getId() {
        return id;
    }

    @Override
    public ParameterValues getRuntimeParameters() {
        return runtimeParameters;
    }

    /**
     * 解析设计器的组件参数
     *
     * @param parameters is a list [{IAttribute},{IAttribute}]
     * @return
     */
    public AbstractAttribute parseParameters(String parameters) {
        try {
            JsArray<ParameterValue> attributes = Js.uncheckedCast(Global.JSON.parse(parameters));
            for (int i = 0; i < attributes.length; i++) {
                getEditorMetaData().getParameterValues().add(attributes.getAt(i));
            }
        } catch (Exception e) {
            DomGlobal.console.log("Error parsing (AbstractAttribute) parameters " + parameters);
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
        getEditorMetaData().load(editor, EditorMetaDataFormat.EDF_JSON);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public AbstractAttribute setName(String name) {
        this.name = name;
        return this;
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
        return (group == null || group.length() == 0) ? BASIC_GROUP : group;
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
    public IOptionProvider getOptionProvider() {
        return optionProvider;
    }

    public AbstractAttribute setOptionProvider(IOptionProvider optionProvider) {
        this.optionProvider = optionProvider;
        return this;
    }

    @Override
    public IEditorMetaData getEditorMetaData() {
        return editorData;
    }

    public AbstractAttribute setEditorData(IEditorMetaData editorData) {
        assert editorData != null;
        this.editorData = editorData;
        return this;
    }

    @Override
    public boolean getAttrVisible() {
        return visible;
    }

    /**
     * 设置编辑器显示或者隐藏
     *
     * @param visible
     * @return
     */
    @Override
    public IAttribute setAttrVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            notifyPropertyChange();
        }
        return this;
    }

    private void notifyPropertyChange() {
        if (callback != null) {
            callback.onAttributePropertyChange(this);
        }
    }

    @Override
    public IAttribute setChangeCallback(IAttributePropertyChangeCallback callback) {
        this.callback = callback;
        return this;
    }

    public AbstractAttribute param(String key,String value)
    {
        editorData.getParameterValues().add(ParameterValue.create(key,value));
        return this;
    }
}
