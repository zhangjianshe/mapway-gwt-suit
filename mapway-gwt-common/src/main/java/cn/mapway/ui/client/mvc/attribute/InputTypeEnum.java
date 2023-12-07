package cn.mapway.ui.client.mvc.attribute;

/**
 * int INPUT_TEXTBOX = 0;
 * int INPUT_DROPDOWNLIST = 1;
 * int INPUT_CHECKBOX = 2;
 * int INPUT_COLOR = 3;
 * int INPUT_PATH = 4;
 * int INPUT_FILE = 5;
 * int INPUT_SLIDER = 6;
 * int INPUT_OTHERS = 99;
 * int INPUT_PATHALL = 7;
 * int INPUT_TEXTAREA = 8;
 * int INPUT_MULTIFILE = 9;
 */
public enum InputTypeEnum {
    INPUT_OTHERS(99, "其他输入类型"),
    INPUT_TEXTBOX(0, "文本框"),
    INPUT_DROPDOWN(1, "下拉框"),
    INPUT_CHECKBOX(2, "布尔输入框"),
    INPUT_COLOR(3, "颜色输入框"),
    INPUT_PATH(4, "目录选择"),
    INPUT_FILE(5, "文件选择"),
    INPUT_SLIDER(6, "滑动条"),
    INPUT_PATH_ALL(7, "目录或者文件"),
    INPUT_TEXTAREA(8, "多行文本"),
    INPUT_MULTI_FILE(9, "多文件目录输入"),
    INPUT_CUSTOM(10, "自定义输入类型");

    int code;
    String name;

    InputTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static InputTypeEnum valueOfCode(Integer code) {
        if (code == null) return INPUT_OTHERS;
        for (InputTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return INPUT_OTHERS;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
