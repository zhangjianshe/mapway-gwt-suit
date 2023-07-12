package cn.mapway.ui.client.mvc.attribute;


/**
 * IAttribute
 * 用于描述一个属性
 *
 * @author zhang
 */
public interface IAttribute {
    // Shape File  字段类型 0 String 1 Integer 2 Float 3 DateTime 4 Boolean
    int DT_STRING = 0;
    int DT_INTEGER = 1;
    int DT_FLOAT = 2;
    int DT_DATETIME = 3;
    int DT_BOOLEAN = 4;


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

}
