package cn.mapway.geo.shared.stretch;

public enum StretchTypeEnum {
    linearStretch(0, "线性拉伸"),

    logStretch(1, "对数拉伸"),

    equalizeStretch(2, "直方图均衡"),

    sigmoidStretch(3, "Sigmoid拉伸"),

    gammaStretch(4, "Gamma拉伸");

    public final int value;

    public final String desc;

    private StretchTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static StretchTypeEnum getStretchType(int value) {
        for (StretchTypeEnum stretchTypeEnum : StretchTypeEnum.values()) {
            if (stretchTypeEnum.value == value) {
                return stretchTypeEnum;
            }
        }
        return null;
    }
}
