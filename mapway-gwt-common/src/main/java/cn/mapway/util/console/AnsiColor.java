package cn.mapway.util.console;

public enum AnsiColor {
    STYLE_RESET("\u001B[0m"),
    STYLE_HIGH_INTENSITY("\u001B[1m"),
    STYLE_LOW_INTENSITY("\u001B[2m"),

    STYLE_ITALIC("\u001B[3m"),
    STYLE_UNDERLINE("\u001B[4m"),
    STYLE_BLINK("\u001B[5m"),
    STYLE_RAPID_BLINK("\u001B[6m"),
    STYLE_REVERSE_VIDEO("\u001B[7m"),
    STYLE_INVISIBLE_TEXT("\u001B[8m"),

    FG_BLACK("\u001B[30m"),
    FG_RED("\u001B[31m"),
    FG_GREEN("\u001B[32m"),
    FG_YELLOW("\u001B[33m"),
    FG_BLUE("\u001B[34m"),
    FG_MAGENTA("\u001B[35m"),
    FG_CYAN("\u001B[36m"),
    FG_WHITE("\u001B[37m"),

    BG_BLACK("\u001B[40m"),
    BG_RED("\u001B[41m"),
    BG_GREEN("\u001B[42m"),
    BG_YELLOW("\u001B[43m"),
    BG_BLUE("\u001B[44m"),
    BG_MAGENTA("\u001B[45m"),
    BG_CYAN("\u001B[46m"),
    BG_WHITE("\u001B[47m");

    String code;

    public String getCode() {
        return this.code;
    }

    AnsiColor(String code) {
        this.code = code;
    }

}
