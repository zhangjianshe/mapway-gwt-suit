package cn.mapway.ui.client.util;

import cn.mapway.ui.client.tools.IData;

/**
 * AiLog
 *
 * @author zhangjianshe@gmail.com
 */
public class Logs {
    static IData<Boolean> debugHandler;

    public static void hookLogs(IData<Boolean> debug) {
        debugHandler = debug;
    }

    public static void info(String message) {
        if (debugHandler != null && !debugHandler.getData()) {
            return;
        }
        log(message);
    }

    public static void info(String message,String css) {
        if (debugHandler != null && !debugHandler.getData()) {
            return;
        }
        log(message,css);
    }


    public native static void log(String message)/*-{
        console.log(message);
    }-*/;

   public native static void log(String message,String css) /*-{
    if (css==null){
        console.log(message);
    }
    else{
        console.log(message,css);
    }
    }-*/;

    public native static void log(Object message) /*-{
        console.log(message);
    }-*/;
}
