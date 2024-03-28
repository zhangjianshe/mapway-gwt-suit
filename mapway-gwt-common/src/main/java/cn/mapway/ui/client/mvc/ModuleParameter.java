package cn.mapway.ui.client.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 模块参数
 *
 * @author zhangjianshe
 */
public class ModuleParameter {
    /**
     * 通用参数，用于从数据库中的参数传递到模块中.
     */
    public final static String PARA_KEY = "para_key";
    private String subModule = "";
    private final Map<String, Object> paras;

    /**
     * Instantiates a new Module parameter.
     */
    public ModuleParameter() {
        paras = new HashMap<String, Object>();
    }

    /**
     * 设置默认参数.
     *
     * @param value the value
     */
    public void put(Object value) {
        put(PARA_KEY, value);
    }

    /**
     * 获取子模块
     *
     * @return sub module
     */
    public String getSubModule() {
        return subModule;
    }

    /**
     * Sets sub module.
     *
     * @param moduleCode the module code
     */
    public void setSubModule(String moduleCode) {
        subModule = moduleCode;
    }

    /**
     * 获取默认参数.
     *
     * @return object
     */
    public Object get() {
        return get(PARA_KEY);
    }

    /**
     * Put.
     *
     * @param key   the key
     * @param value the value
     */
    public void put(String key, Object value) {
        paras.put(key, value);
    }

    /**
     * Get object.
     *
     * @param key the key
     * @return the object
     */
    public Object get(String key) {
        return paras.get(key);
    }

    /**
     * 序列化数据
     */
    @Override
    public String toString() {
        String r = "";
        for (Entry<String, Object> item : paras.entrySet()) {
            if (r.length() > 0) {
                r += ",";
            }
            r += item.getKey() + "|" + item.getValue().toString();

        }
        return r;
    }

    /**
     * 反序列化数据
     *
     * @param data the data
     */
    public void parse(String data) {
        if (data == null || data.length() == 0) {
            return;
        }
        String[] items = data.split(",");
        for (String item : items) {
            if (item.length() == 0) {
                continue;
            }
            String[] kv = item.split("\\|");
            if (kv.length != 2) {
                continue;
            }
            put(kv[0], kv[1]);
        }
    }

    /**
     * 参数的数量
     *
     * @return int
     */
    public int size() {
        return paras.size();
    }

    /**
     * 参数的额KEYS列表
     *
     * @return set
     */
    public Set<String> keys() {
        return paras.keySet();
    }

    public void merge(ModuleParameter source) {
        if (source != null) {
            for (Entry<String, Object> entry : source.paras.entrySet()) {
                paras.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
