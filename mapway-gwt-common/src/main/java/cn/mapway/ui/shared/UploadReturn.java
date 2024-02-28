package cn.mapway.ui.shared;

/**
 * UploadReturn
 *
 * @author zhangjianshe@gmail.com
 */

import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * The type Upload file return.
 */
@JsType(isNative = true,namespace = GLOBAL,name = "Object")
public class UploadReturn {

    /**
     * Instantiates a new upload file return.
     */
    public UploadReturn() {
    }

    /**
     * 客户端上传的 extra数据.
     */
    public String extra;


    /**
     * 返回代码 0成功 其他错误.
     */
    public int retCode;


    /**
     * 错误消息.
     */
    public String msg;

    /**
     * 相对路径.
     */
    public String relPath;

    /**
     * MD5.
     */
    public String md5;
    /**
     * 文件大小.
     */
    public long size;
    /**
     * 原始文件名
     */
    public String fileName;
}
