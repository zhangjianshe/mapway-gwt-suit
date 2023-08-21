package cn.mapway.geo.shared.color;

public enum ColorMapType {
    COLOR_MAP_TYPE_UNIQUE(0, "唯一值映射"),
    COLOR_MAP_TYPE_RANGE(1, "范围映射");


    public final Integer code;
    public final String name;

    ColorMapType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public static ColorMapType valueOfCode(Integer code) {
        if (code == null) {
            return COLOR_MAP_TYPE_UNIQUE;
        }

        for (ColorMapType colorMapType : values()) {
            if (colorMapType.code.equals(code)) {
                return colorMapType;
            }
        }

        return COLOR_MAP_TYPE_UNIQUE;
    }
}
