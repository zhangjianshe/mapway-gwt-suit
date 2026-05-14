package cn.mapway.ui.client.debounce;

import cn.mapway.ui.client.util.StringUtil;
import jsinterop.base.JsPropertyMap;

public class Debounce {

    static Debounce instance;

    private final JsPropertyMap<Object> cache = new JsPropertyMap<Object>() {};

    private Debounce() {
    }

    public void debounce(String key, DebounceHandler handler, DebounceCallback callback, int delay) {
        if (handler == null) {
            throw new RuntimeException("handler is null");
        }
        if (StringUtil.isBlank(key)) {
            throw new RuntimeException("key is null");
        }
        if (delay <= 0) {
            delay = 500;
        }
        debounceInvoke(cache, key, handler, callback, delay);
    }

    public void debounce(String key, DebounceHandler handler) {
        debounce(key, handler, null, 500);
    }

    public void debounce(String key, DebounceHandler handler, int delay) {
        debounce(key, handler, null, delay);
    }

    public void debounce(String key, DebounceHandler handler, DebounceCallback callback ) {
        debounce(key, handler, callback, 500);
    }

    public static Debounce getInstance(boolean flag) {
        if(flag){
            if(instance == null){
                instance = new Debounce();
            }
            return instance;
        } else {
            return new Debounce();
        }
    }

    public static Debounce getInstance() {
        return getInstance(false);
    }


    private native void debounceInvoke(JsPropertyMap<Object> cache, String key, DebounceHandler handler,
                                             DebounceCallback callback, int delay) /*-{
        var id = cache[key];
        if(id != null){
            $wnd.clearTimeout(id);
        }
        var id = $wnd.setTimeout(function(){
            var result = handler.onInvoke();
            callback && callback.callback(result);
        }, delay)
        cache[key] = id;
    }-*/;

}
