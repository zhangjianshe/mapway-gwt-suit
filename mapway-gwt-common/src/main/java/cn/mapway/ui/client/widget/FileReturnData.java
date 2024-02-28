package cn.mapway.ui.client.widget;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * FileReturnData
 *
 * @author zhangjianshe@gmail.com
 */
@JsType(isNative = true,namespace = JsPackage.GLOBAL,name = "Object")
public class FileReturnData {
    public String relPath;
    public String md5;
}
