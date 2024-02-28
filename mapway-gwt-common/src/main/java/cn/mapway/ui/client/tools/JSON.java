package cn.mapway.ui.client.tools;

/**
 * JSON
 *
 * @author zhangjianshe@gmail.com
 */

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * The type Json.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JSON {


    /**
     * Stringify string.
     *
     * @param obj the obj
     * @return the string
     */
    public native static String stringify(Object obj);

    /**
     * Parse object.
     *
     * @param obj the obj
     * @return the object
     */
    public native static Object parse(String obj);

}