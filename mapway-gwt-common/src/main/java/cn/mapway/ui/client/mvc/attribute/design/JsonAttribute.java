package cn.mapway.ui.client.mvc.attribute.design;

import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.IOptionProvider;
import elemental2.core.Global;
import elemental2.core.JsObject;
import jdk.nashorn.internal.objects.annotations.Property;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * 描述一个编辑参数
 */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class JsonAttribute implements IAttribute {

    @Property
    public String name;
    @JsProperty
    public String altName;

    @JsProperty
    public Object value;
    @JsProperty
    public String id;
    @JsProperty
    protected String defaultValue;
    @JsProperty
    protected String description;
    @JsProperty
    protected boolean readonly;
    @JsProperty
    protected boolean required;
    @JsProperty
    protected int dataType;
    @JsProperty
    protected String validateRegex;
    @JsProperty
    protected String group;
    @JsProperty
    protected int rank;
    @JsProperty
    protected String tip;
    @JsProperty
    protected String errorTip;
    @JsProperty
    protected String icon;

    /**
     * 唯一ID
     *
     * @return
     */
    @Override
    @JsOverlay
    public final String getId() {
        return id;
    }

    @Override
    @JsOverlay
    public final String getName() {
        return name;
    }

    @Override
    @JsOverlay
    public final String getAltName() {
        return altName;
    }

    @Override
    @JsOverlay
    public final Object getValue() {
        return value;
    }

    @Override
    @JsOverlay
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    @JsOverlay()
    public final String getDefaultValue() {
        return defaultValue;
    }

    @JsOverlay
    @Override
    public final String getDescription() {
        return description;
    }

    @JsOverlay
    @Override
    public final boolean isReadonly() {
        return readonly;
    }

    @JsOverlay
    @Override
    public boolean isRequired() {
        return false;
    }

    @JsOverlay
    @Override
    public final int getDataType() {
        return dataType;
    }

    @JsOverlay
    @Override
    public String getValidateRegx() {
        return validateRegex;
    }

    @JsOverlay
    @Override
    public final String getGroup() {
        return group;
    }

    @JsOverlay
    @Override
    public final int getRank() {
        return rank;
    }

    @JsOverlay
    @Override
    public final String getTip() {
        return tip;
    }

    @JsOverlay
    @Override
    public final String getErrorTip() {
        return errorTip;
    }

    @JsOverlay
    @Override
    public final String getIcon() {
        return icon;
    }

    @JsOverlay
    @Override
    public String getOptions() {
        return "";
    }

    @JsOverlay
    @Override
    public IOptionProvider getOptionProvider() {
        return null;
    }

    /**
     * 获取设计时期的组件选项
     * 解析为 JsObject 数据
     *
     * @return AttributeEditorMetaData
     */
    @JsOverlay
    @Override
    public JsObject getDesignOption() {
        return null;
    }

    /**
     * 编辑器组件的 代码
     * 可以通过这个方法获取创建组件需要的必要数据  编辑器代码
     *
     * @return
     */
    @JsOverlay
    @Override
    public String getEditorCode() {
        return null;
    }

    /**
     * 编辑这个属性需要的属性编辑器信息 将会替代 上面的getEditorCode designOption getOptions等字段
     * 完成后 上面的字段将会被删除
     *
     * @return
     */
    @Override
    @JsOverlay
    public IEditorData getEditorData() {
        return null;
    }

    @Override
    @JsOverlay
    public String toJSON() {
        return Global.JSON.stringify(this);
    }
}
