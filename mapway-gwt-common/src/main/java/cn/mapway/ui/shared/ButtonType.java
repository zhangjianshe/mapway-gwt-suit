package cn.mapway.ui.shared;

public enum ButtonType {
    DEFAULT(""),
    PRIMARY("primary"),
    SECOND("second"),
    SUCCESS("success"),
    WARNING("warning"),
    ERROR("error");
    private final String value;

    ButtonType(String value) {
        this.value = value;
    }

    public static ButtonType fromValue(String value) {
        if (value == null || "".equals(value)) {
            return DEFAULT;
        }
        for (ButtonType buttonType : ButtonType.values()) {
            if (buttonType.getValue().equals(value)) {
                return buttonType;
            }
        }
        return DEFAULT;
    }

    public String getValue() {
        return value;
    }

}
