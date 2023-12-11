package cn.mapway.ui.client.mvc.attribute;


import cn.mapway.ui.client.db.DbFieldType;

/**
 * Attribute
 *
 * @author zhang
 */
public class Attribute implements IAttribute {

    String name;
    Object value;
    String summary;
    String altName;
    String options;
    private String defaultValue;
    private boolean readonly;
    private int uiType;
    private int dataType;
    private String validateRegx;
    private String group;
    private int rank;
    private String tip;
    private String errorTip;
    private boolean required;
    private String unicodeIcon;
    private IOptionProvider optionProvider;

    /**
     * 自定义模块代码
     */
    private String editorModuleCode;

    private String editorOptions;

    @Override
    public String getEditorOptions(){
        return editorOptions;
    }

    /**
     * 简单为本狂
     *
     * @param name
     * @param value
     * @param summary
     * @return
     */
    public static IAttribute createSimple(String name, Object value, String summary) {
        Attribute attribute = new Attribute();
        attribute.name = (name);
        attribute.setValue(value);
        attribute.summary = (summary);
        attribute.group = ("基本属性");
        attribute.readonly = (false);
        attribute.dataType = DbFieldType.FLD_TYPE_STRING.getCode();
        attribute.uiType = (InputTypeEnum.INPUT_TEXTBOX.code);
        return attribute;
    }

    public static IAttribute create(String name, String altName, Object value, String defaultValue, String summary, boolean readonly, int uiType, int dataType, String validateRegx, String group, int rank, String tip, String errorTip, boolean required, String unicodeIcon, IOptionProvider optionProvider) {
        Attribute attribute = new Attribute();
        attribute.name = name;
        attribute.altName = altName;
        attribute.value = value;
        attribute.defaultValue = defaultValue;
        attribute.summary = summary;
        attribute.readonly = readonly;
        attribute.uiType = uiType;
        attribute.dataType = dataType;
        attribute.validateRegx = validateRegx;
        attribute.group = group;
        attribute.rank = rank;
        attribute.tip = tip;
        attribute.errorTip = errorTip;
        attribute.required = required;
        attribute.unicodeIcon = unicodeIcon;
        attribute.optionProvider = optionProvider;
        return attribute;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAltName() {
        return altName;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDescription() {
        return summary;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public int getDataType() {
        return dataType;
    }

    @Override
    public int getInputType() {
        return uiType;
    }

    @Override
    public String getValidateRegx() {
        return validateRegx;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public String getTip() {
        return tip;
    }

    @Override
    public String getErrorTip() {
        return errorTip;
    }

    @Override
    public String getIcon() {
        return unicodeIcon;
    }

    @Override
    public IOptionProvider getOptionProvider() {
        return optionProvider;
    }

    /**
     * 如果输入类型为10 INPUT_CUSTOM
     * 会根据这返回值 创建一个模块，该模块具备 HasValue 和 HasValueChangeHandler 接口
     * 系统会根据这个模块代码 创建模块 并展示
     *
     * @return
     */
    @Override
    public String getEditorModuleCode() {
        return editorModuleCode;
    }

    @Override
    public String getOptions() {
        return options;
    }

    public void setEditorModuleCode(String editorModuleCode) {
        this.editorModuleCode = editorModuleCode;
    }
}
