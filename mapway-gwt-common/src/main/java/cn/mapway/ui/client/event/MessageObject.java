package cn.mapway.ui.client.event;

/**
 * MessageObject
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class MessageObject {
    public static final int LEVEL_INFO = 0;
    public static final int LEVEL_WARN = 1;
    public static final int LEVEL_ERROR = 2;
    public static final Integer CODE_OK = 200;
    public static final Integer CODE_FAIL = -1;
    public int level;
    public String message;
    public Integer code;

    public MessageObject(int level, Integer code, String msg) {
        this.level = level;
        this.message = msg;
        this.code = code;
    }

    public static MessageObject info(Integer code, String message) {
        return new MessageObject(LEVEL_INFO, code, message);
    }

    public static MessageObject warn(Integer code, String message) {
        return new MessageObject(LEVEL_WARN, code, message);
    }

    public static MessageObject error(Integer code, String message) {
        return new MessageObject(LEVEL_ERROR, code, message);
    }

    public boolean isInfo() {
        return LEVEL_INFO == level;
    }

    public boolean isWarn() {
        return LEVEL_WARN == level;
    }

    public boolean isError() {
        return LEVEL_ERROR == level;
    }

}
