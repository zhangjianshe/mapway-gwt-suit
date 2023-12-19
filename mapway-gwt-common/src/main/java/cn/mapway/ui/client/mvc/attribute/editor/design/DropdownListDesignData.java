package cn.mapway.ui.client.mvc.attribute.editor.design;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class DropdownListDesignData {
    /**
     * key
     */
    public String name;
    /**
     * Value值
     */
    public String value;
    /**
     * 是否是初始化选择
     */
    public boolean init;
}
