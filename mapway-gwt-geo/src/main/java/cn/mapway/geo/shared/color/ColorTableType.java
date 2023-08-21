package cn.mapway.geo.shared.color;

public enum ColorTableType {
    COLOR_TYPE_UNKNOWN(0, "未知颜色表"),
    COLOR_TYPE_SINGLE_BAND_PSEUDO_COLOR(1, "单波段伪彩色");


    public final Integer code;
    public final String name;

    ColorTableType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public  static ColorTableType valueOfCode(Integer code) {
        if (code == null) {
            return COLOR_TYPE_UNKNOWN;
        }

        for (ColorTableType colorTableType : values()) {
            if (colorTableType.code.equals(code)) {
                return colorTableType;
            }
        }

        return COLOR_TYPE_UNKNOWN;
    }
}
