package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import cn.mapway.ui.client.mvc.attribute.editor.impl.TextboxAttributeEditor;
import cn.mapway.ui.client.tools.JSON;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.util.StringUtil;
import elemental2.core.Global;
import elemental2.core.JsObject;
import jsinterop.base.Js;

/**
 * AttributeAdaptor
 *
 * @author zhang
 */

public abstract class AttributeAdaptor implements IAttribute {

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
    private String editorCode;
    private EditorOption runtimeOption;

    public AttributeAdaptor(String name) {
        this(name, name);
    }

    public AttributeAdaptor(String name, String alterName) {
        this(name, alterName, TextboxAttributeEditor.EDITOR_CODE);
    }

    /**
     * 构建一个自定义模块的属性
     *
     * @param name           输出名称
     * @param customEditCode 编辑器代码
     */
    public AttributeAdaptor(String name, String alterName, String customEditCode) {
        //每个属性定义都会有一个唯一的实力ID
        this.id = StringUtil.randomString(8);

        //编辑器代码
        this.editorCode = customEditCode;
        this.name = name;
        this.altName = alterName;
    }

    public AttributeAdaptor() {
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
        this.runtimeOption = runtimeOption;
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
    public AttributeAdaptor parseDesignOption(String designOptionJson) {
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
    public AttributeAdaptor parseEditor(String editor) {
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

    public AttributeAdaptor setName(String name) {
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

    public AttributeAdaptor setAltName(String altName) {
        this.altName = altName;
        return this;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    public AttributeAdaptor setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public AttributeAdaptor setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean isReadonly() {
        return readOnly;
    }

    public AttributeAdaptor setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public AttributeAdaptor setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public int getDataType() {
        return dataType;
    }

    public AttributeAdaptor setDataType(int dataType) {
        this.dataType = dataType;
        return this;
    }


    @Override
    public String getValidateRegx() {
        return validator;
    }

    public AttributeAdaptor setValidateRegx(String validateRegx) {
        this.validator = validateRegx;
        return this;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public AttributeAdaptor setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public int getRank() {
        return rank;
    }

    public AttributeAdaptor setRank(int rank) {
        this.rank = rank;
        return this;
    }

    @Override
    public String getTip() {
        return tip;
    }

    public AttributeAdaptor setTip(String tip) {
        this.tip = tip;
        return this;
    }

    @Override
    public String getErrorTip() {
        return errorTip;
    }

    public AttributeAdaptor setErrorTip(String errorTip) {
        this.errorTip = errorTip;
        return this;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public AttributeAdaptor setIcon(String icon) {
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
    public AttributeAdaptor setInitVisible(Boolean visible) {
        this.initVisible = visible;
        return this;
    }

    @Override
    public IOptionProvider getOptionProvider() {
        return optionProvider;
    }

    public AttributeAdaptor setOptionProvider(IOptionProvider optionProvider) {
        this.optionProvider = optionProvider;
        return this;
    }

    @Override
    public String getOptions() {
        return options;
    }

    public AttributeAdaptor setOptions(String options) {
        this.options = options;
        //TODO 处理此处逻辑 很有可能不再使用了
        return this;
    }

    public String sliderProperty(double min, double max, double step, String unit, double exponent, boolean continueReport) {
        JsObject object = JsObject.create(null);
        SlideProperty slider = Js.uncheckedCast(object);
        slider.min = min;
        slider.max = max;
        slider.step = step;
        slider.unit = unit;
        slider.exponent = exponent;
        slider.continueReport = continueReport;
        return JSON.stringify(slider);
    }

}
