package cn.mapway.ui.client.mvc.attribute;


/**
 * IAttribute
 * 用于描述一个属性
 *
 * @author zhang
 */
public interface IAttribute {


    String getName();

    String getAltName();

    Object getValue();

    void setValue(Object value);

    String getDefaultValue();

    String getDescription();

    boolean isReadonly();

    boolean isRequired();

    int getDataType();

    int getInputType();

    String getValidateRegx();

    String getGroup();

    int getRank();

    String getTip();

    String getErrorTip();

    String getIcon();

    //某种参数自定义的选项 一般为json字符串 由使用者解释
    String getOptions();

    IOptionProvider getOptionProvider();//选项提供者

    /**
     * 如果输入类型为10 INPUT_CUSTOM
     * 会根据这返回值 创建一个模块，该模块具备 HasValue 和 HasValueChangeHandler 接口
     * 系统会根据这个模块代码 创建模块 并展示
     *
     * @return
     */
    String getEditorModuleCode();


    /**
     * 属性对应编辑器的信息
     *
     * @return
     */
    String getEditorOptions();

    /**
     * 是否初始化显示
     *
     * @return
     */
    default boolean isInitVisible() {
        return true;
    }

}
