package cn.mapway.ui.client.widget;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * BigFileUploadReturn
 *
 * @author zhangjianshe@gmail.com
 */
@JsType(isNative = true,namespace = JsPackage.GLOBAL,name = "Object")
public class BigFileUploadReturn {
    public int code;
    public String message;
    public FileReturnData data;
}
