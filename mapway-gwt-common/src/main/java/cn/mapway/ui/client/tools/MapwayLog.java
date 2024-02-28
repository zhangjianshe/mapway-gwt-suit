package cn.mapway.ui.client.tools;

/**
 * MapwayLog
 *
 * @author zhangjianshe@gmail.com
 */
public class MapwayLog implements IShowMessage {

    public static void info(String message) {
        log(message);
    }

    native static void log(String message) /*-{
        console.log(message);
    }-*/;

    @Override
    public void showMessage(int level, Integer code, String message) {
        log(message);
    }
}
