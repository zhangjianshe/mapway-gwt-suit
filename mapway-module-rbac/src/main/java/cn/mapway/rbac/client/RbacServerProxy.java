package cn.mapway.rbac.client;


import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.servlet.IRbacServer;
import cn.mapway.rbac.shared.servlet.IRbacServerAsync;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.shared.UploadReturn;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import elemental2.core.ArrayBuffer;
import elemental2.core.Global;
import elemental2.dom.*;
import elemental2.promise.Promise;
import jsinterop.base.JsPropertyMap;

/**
 * 服务代理工厂类.
 *
 * @author zhangjianshe
 */
public class RbacServerProxy {

    /**
     * The proxy.
     */
    private static IRbacServerAsync PROXY;
    private static RbacRequestWithToken requestBuilder;

    public static Promise<ArrayBuffer> getBinary(String url) {

        RequestInit init = RequestInit.create();
        init.setMethod("GET");
        init.setHeaders(RbacRequestWithToken.getHeaders());
        init.setKeepalive(false);
        return new Promise<>((resolve, reject) -> {
            DomGlobal.fetch(url, init).then(response -> {
                if (response.status == 200) {
                    response.arrayBuffer().then(buffer -> {
                        resolve.onInvoke(buffer);
                        return null;
                    }, error -> {
                        reject.onInvoke(error.toString());
                        return null;
                    });
                } else {
                    reject.onInvoke(String.valueOf(response.status));
                }
                return null;
            }, error -> {
                reject.onInvoke(error.toString());
                return null;
            });
        });

    }

    public static void httpFetch(String method, String url, JsPropertyMap headers, String data, Callback<String, String> callback) {
        XMLHttpRequest request = new XMLHttpRequest();
        request.open(method, url);
        if (headers != null) {
            headers.forEach(key -> request.setRequestHeader(key, DataCastor.castToString(headers.get(key))));
        }
        request.setRequestHeader("Content-Type", "*");
        request.onreadystatechange = new XMLHttpRequest.OnreadystatechangeFn() {
            @Override
            public Object onInvoke(Event p0) {
                if (request.readyState == XMLHttpRequest.DONE) {
                    if (callback != null) {
                        if (request.status == 200) {
                            callback.onSuccess(request.responseText);
                        } else {
                            callback.onFailure(request.responseText);
                        }
                    }
                }
                return true;
            }
        };
        request.onerror = p0 -> {
            if (callback != null) {
                callback.onFailure(p0.toString());
            }
            return null;
        };
        request.send(data);
    }

    public static void httpGet(String url, String data, Callback<String, String> callback) {
        XMLHttpRequest request = new XMLHttpRequest();
        request.open("get", url);
        RbacRequestWithToken.appendHeader(request);
        request.onreadystatechange = p0 -> {
            if (request.readyState == XMLHttpRequest.DONE) {
                if (callback != null) {
                    if (request.status == 200) {
                        callback.onSuccess(request.responseText);
                    } else {
                        callback.onFailure(request.responseText);
                    }
                }
            }
            return true;
        };
        request.send(data);
    }

    /**
     * Gets the.
     *
     * @return the i ui server async
     */
    public static IRbacServerAsync get() {
        if (PROXY == null) {
            PROXY = GWT.create(IRbacServer.class);
            requestBuilder = new RbacRequestWithToken();
            ServiceDefTarget t = (ServiceDefTarget) PROXY;
            String context = GWT.getHostPageBaseURL();
            if (context.endsWith("/")) {
                context = context.substring(0, context.length() - 1);
            }
            //context = "https://ib.cangling.io";
//            context="https://nmgwxyy.cn/cl";
            //context="http://water.mapway.cn:7200/";
            //context="http://nanyang.mapway.cn:7600/";
            // context = "http://192.168.100.163:7100/";
            String entryPoint = context + "/" + RbacConstant.DEFAULT_SERVER_PATH;
            t.setServiceEntryPoint(entryPoint);
            t.setRpcRequestBuilder(requestBuilder);
        }
        return PROXY;
    }

    /**
     * 获取当前登录用户信息
     * 有可能为 empty
     *
     * @return
     */
    public static String getAuthorizationToken() {
        String cookieToken = Cookies.getCookie("Admin-Iot-Token");
        if (cookieToken == null || cookieToken.length() == 0) {
            return "";
        } else {
            return "Bearer " + cookieToken;
        }
    }

    /**
     * 通过接口上传文件
     * @param name
     * @param relativePath
     * @param blob
     * @param callback
     */
    public static void uploadImage(String name, String relativePath, Blob blob, Callback<UploadReturn, String> callback) {
        String sb = "relPath=" + URL.encodeQueryString(relativePath);
        String actionUrl =GWT.getHostPageBaseURL() + "/fileUpload?" + sb;
        XMLHttpRequest request = new XMLHttpRequest();
        FormData data = new FormData();
        data.set("file", blob);
        request.open("POST", actionUrl);
        request.onloadend = (p0) -> {
            if(callback!=null){
                UploadReturn r = (UploadReturn) Global.JSON.parse(request.responseText);
                r.extra=name;
                if (r.retCode == 0) {
                    callback.onSuccess(r);
                } else {
                    callback.onFailure(r.msg);
                }
            }
        };
        request.send(data);
    }
}
