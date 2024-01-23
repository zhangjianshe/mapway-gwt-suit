package cn.mapway.ui.client.mvc.attribute.marker;

import cn.mapway.ui.client.fonts.Fonts;

/**
 * 参数类型 输入 输出运行
 */
public enum ParameterTypeEnum {
    PT_INPUT(0, "输入参数", Fonts.P_INPUT),
    PT_OUTPUT(2, "输出参数", Fonts.P_OUT),
    PT_RUNNING(3, "运行参数", Fonts.P_RUNNING);

    private final Integer code;
    private final String name;
    private final String unicode;

    ParameterTypeEnum(Integer code, String name, String unicode) {
        this.code = code;
        this.name = name;
        this.unicode = unicode;
    }

    public static ParameterTypeEnum valueOfCode(Integer code) {
        if (code == null) {
            return PT_RUNNING;
        }
        for (ParameterTypeEnum type : ParameterTypeEnum.values()) {
            if (code.equals(type.code)) {
                return type;
            }
        }
        return PT_RUNNING;
    }

    public String getName() {
        return name;
    }

    public String getUnicode() {
        return unicode;
    }

    public Integer getCode() {
        return code;
    }
}
