package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.db.DbFieldType;
import cn.mapway.ui.client.tools.JSON;
import elemental2.core.JsObject;
import jsinterop.base.Js;

/**
 * AttributeAdaptor
 *
 * @author zhang
 */

public abstract class AttributeAdaptor implements IAttribute {

    protected String name;
    protected String altName;
    protected int dataType;
    protected int uiType;
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
    protected IOptionProvider optionProvider = null;

    public AttributeAdaptor(String name) {
        this(name, null, 0, 0, null);
    }

    public AttributeAdaptor(String name, String alterName) {
        this(name, alterName, DbFieldType.FLD_TYPE_STRING.getCode(), InputTypeEnum.INPUT_TEXTBOX.code, null);
    }

    public AttributeAdaptor() {
        this("", "", DbFieldType.FLD_TYPE_STRING.getCode(), InputTypeEnum.INPUT_TEXTBOX.code, "");
    }

    public AttributeAdaptor(String name, String altName, int dataType, int uiType, String defaultValue) {
        this.name = name;
        this.altName = altName;
        this.dataType = dataType;
        this.uiType = uiType;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return name;
    }

    public AttributeAdaptor setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getAltName() {
        if (this.altName == null) {
            return this.name;
        } else {
            return this.altName;
        }
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
    public int getInputType() {
        return uiType;
    }

    public AttributeAdaptor setInputType(int inputType) {
        this.uiType = inputType;
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
