package cn.mapway.rbac.client;

import cn.mapway.rbac.shared.RbacConstant;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import elemental2.dom.Headers;
import elemental2.dom.XMLHttpRequest;

public class RbacRequestWithToken extends RpcRequestBuilder {
    /**
     * Instantiates a new Request builder with token.
     */
    public RbacRequestWithToken() {

    }

    private static String getProtocolHeader() {
        return Window.Location.getProtocol();
    }

    public static void appendHeader(XMLHttpRequest request) {
        //从Cookie中读取token
        request.setRequestHeader("Authorization", "Bearer " + RbacClient.get().getAuthCode());
        request.setRequestHeader(RbacConstant.X_PROTOCOL, getProtocolHeader());
        request.setRequestHeader("Access-Control-Allow-Origin", "*");
    }

    public static Headers getHeaders() {
        Headers headers=new Headers();
        headers.set("Authorization", "Bearer " + RbacClient.get().getAuthCode());
        headers.set(RbacConstant.X_PROTOCOL, getProtocolHeader());
        headers.set("Access-Control-Allow-Origin", "*");
        return headers;
    }

    @Override
    protected void doFinish(RequestBuilder rb) {
        super.doFinish(rb);
        //从Cookie中读取token
        rb.setHeader("Authorization", "Bearer " +RbacClient.get().getAuthCode());
        rb.setHeader(RbacConstant.X_PROTOCOL, getProtocolHeader());
    }

}
