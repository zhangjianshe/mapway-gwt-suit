package cn.mapway.ui.client.mvc.attribute.design;

import elemental2.core.Global;
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

    @JsOverlay
    public final String toJSON() {
        return Global.JSON.stringify(this);
    }
}
