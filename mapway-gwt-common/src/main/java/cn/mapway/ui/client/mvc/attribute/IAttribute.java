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
    // input type list 0 TextBox 1 DropDownList 2 CheckBox 3 Color
    int INPUT_TEXTBOX = 0;
    int INPUT_DROPDOWNLIST = 1;
    int INPUT_CHECKBOX = 2;
    int INPUT_COLOR = 3;
    int INPUT_PATH = 4; //路径选择器
    int INPUT_FILE = 5; //文件选择器
    int INPUT_SLIDER = 6; //Slider选择输入
    int INPUT_OTHERS=99;

    String getName();

    String getAltName();

    int INPUT_PATHALL = 7;//目录或者文件选择
    int INPUT_TEXTAREA = 8;//文本域
    int INPUT_MULTIFILE = 9;//多文件选择
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
