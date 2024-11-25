package cn.mapway.ui.shared;

/**
 * Messages
 *
 * @author zhangjianshe@gmail.com
 */
public enum Messages {
    NSG_RELOAD_CAPTCHA(300000, "重新刷新验证码"),
    NSG_NEED_LOGIN(300001, "需要重新登录");

    Integer code;
    String message;

    Messages(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
