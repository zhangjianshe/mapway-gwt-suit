package cn.mapway.ui.client.debounce;

import cn.mapway.ui.client.util.StringUtil;
import elemental2.dom.DomGlobal;
import jsinterop.base.JsPropertyMap;

public class Debounce {

    static Debounce instance;

    private final JsPropertyMap<Double> cache = new JsPropertyMap<Double>() {
    };

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
            delay = 100;
        }
        debounceInvoke(cache, key, handler, callback, delay);
    }

    public void debounce(String key, DebounceHandler handler) {
        debounce(key, handler, null, 100);
    }

    public void debounce(String key, DebounceHandler handler, int delay) {
        debounce(key, handler, null, delay);
    }

    public void debounce(String key, DebounceHandler handler, DebounceCallback callback) {
        debounce(key, handler, callback, 100);
    }

    public static Debounce getInstance(boolean flag) {
        if (flag) {
            if (instance == null) {
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

    private void debounceInvoke(JsPropertyMap<Double> cache, String key, DebounceHandler handler,
                                DebounceCallback callback, int delay) {
        Double id = cache.get(key);
        if (id != null) {
            DomGlobal.clearTimeout(id);
        }
        id = DomGlobal.setTimeout(
                p0 -> {
                    if (handler != null) {
                        Object result = handler.onInvoke();
                        if (callback != null) {
                            callback.callback(result);
                        }
                    }
                }, delay);
        cache.set(key, id);
    }

}
