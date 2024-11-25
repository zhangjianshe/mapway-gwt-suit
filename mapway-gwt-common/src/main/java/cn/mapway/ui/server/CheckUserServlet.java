package cn.mapway.ui.server;


import cn.mapway.ui.shared.CommonConstant;
import cn.mapway.ui.shared.Messages;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * CheckUserServlet
 * <p>
 * Session 不会存放用户信息
 * API 调用 利用TOKEN去查找 用户信息
 * GWTRPC 和API共享TOKEN
 * 登录后 返回TOKEN，客户端将TOKEN保存 每次请求都添加到HTTP头中
 * GWT RPC 从头中读取TOKEN信息,利用TOKEN信息从数据库中获取用户信息存放到Request中,也就是requestUser
 *
 * @author zhangjianshe@gmail.com
 */
@Slf4j
public abstract class CheckUserServlet<T> extends RemoteServiceServlet {
    /**
     * 调用接口需要TOKEN
     */

    private final ArrayList<String> excludesMethod = new ArrayList<>();

    public CheckUserServlet() {
        extendCheckToken(excludesMethod);
    }

    public String getTheme() {
        return "";
    }

    /**
     * 子类重构
     *
     * @param methodList the method list
     */
    public   void extendCheckToken(List<String> methodList) {

    }

    /**
     * 检查TOKEN
     */
    @Override
    public String processCall(RPCRequest rpcRequest) throws SerializationException {

        boolean canCall = checkToken(rpcRequest);
        if (!canCall) {
            RpcResult result = RpcResult.fail(Messages.NSG_NEED_LOGIN.getCode(), getTheme());
            return RPC.encodeResponseForSuccess(rpcRequest.getMethod(), result);
        }
        String r = "";
        try {
            r = super.processCall(rpcRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            RpcResult result = RpcResult.fail(500, "操作错误:" + e.getMessage());
            return RPC.encodeResponseForSuccess(rpcRequest.getMethod(), result);
        }
        return r;
    }

    /**
     * 打印Http Header信息
     *
     * @param threadLocalRequest
     */
    private void printHeaders(HttpServletRequest threadLocalRequest) {
        Enumeration<String> headerNames = threadLocalRequest.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = threadLocalRequest.getHeader(name);
            log.info("HTTP {}:{}", name, value);
        }
    }

    @Override
    protected void onAfterResponseSerialized(String serializedResponse) {
        super.onAfterResponseSerialized(serializedResponse);
    }

    /**
     * 当前请求中的TOKEN
     *
     * @return
     */
    public String requestToken() {
        HttpServletRequest localRequest = this.getThreadLocalRequest();
        return getToken(localRequest);
    }

    /**
     * 需要子类 根据调用者的Token 返回一个Object对象
     *
     * @param token
     * @return
     */
    public abstract T findUserByToken(String token);


    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    public String getToken(HttpServletRequest request) {

        String token = request.getHeader(getHeadTokenTag());
        if (Strings.isNotBlank(token) && token.startsWith(CommonConstant.TOKEN_PREFIX)) {
            token = token.replace(CommonConstant.TOKEN_PREFIX, "");
        }
        return token;
    }

    public abstract String getHeadTokenTag();

    /**
     * reques中的用户信息,如果没有会从HttpRequest中获取TOKEN 重建
     *
     * @return
     */
    public T requestUser() {
        String token = getToken(getThreadLocalRequest());
        if (Strings.isEmpty(token)) {
            return null;
        }
        return findUserByToken(token);
    }

    /**
     * Check token boolean.
     *
     * @param rpcRequest the rpc request
     * @return boolean
     */
    public boolean checkToken(RPCRequest rpcRequest) {

        boolean excluded = excludeFromCheckToken(rpcRequest);
        if (!excluded) {
            T requestUser = requestUser();
            return requestUser != null;
        }
        return true;
    }


    /**
     * 检查过滤的TOKEN
     *
     * @param rpcRequest
     * @return
     */
    private boolean excludeFromCheckToken(RPCRequest rpcRequest) {
        String name = rpcRequest.getMethod().getName();
        for (String excludeName : excludesMethod) {
            if (excludeName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
