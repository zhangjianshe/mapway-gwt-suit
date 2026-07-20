package cn.mapway.ui.server.exception;

public class UserNotLoggedInException extends RuntimeException {

    public final static String MSG = "用户未登录或登录超时，请先登录";

    public UserNotLoggedInException() {
        super(MSG);
    }

    public UserNotLoggedInException(String message) {
        super(message);
    }

    public UserNotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
}
