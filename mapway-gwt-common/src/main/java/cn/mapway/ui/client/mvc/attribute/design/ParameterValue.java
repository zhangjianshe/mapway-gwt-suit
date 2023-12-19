package cn.mapway.ui.client.mvc.attribute.design;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * 描述一个编辑参数
 */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class ParameterValue {

    @JsProperty
    public String name;
    @JsProperty
    public Object value;
    @JsProperty
    public boolean init;

    @JsOverlay
    public final static ParameterValue create(String name, Object value) {
        return create(name, value, false);
    }

    @JsOverlay
    public final static ParameterValue create(String name, Object value, boolean init) {
        ParameterValue pv = new ParameterValue();
        pv.value = value;
        pv.name = name;
        pv.init = init;
        return pv;
    }

    @JsOverlay
    public final String toJson() {
        String sb = "{ \"name\":\"" + ParameterValues.escapeString(name) + "\"," +
                " \"value\":\"" + ParameterValues.escapeString(value) + "\"," +
                " \"init\":" + init + "}";
        return sb;
    }


}
