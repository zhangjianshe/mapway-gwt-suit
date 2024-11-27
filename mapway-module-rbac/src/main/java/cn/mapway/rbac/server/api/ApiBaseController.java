package cn.mapway.rbac.server.api;


import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.RbacServerPlugin;
import cn.mapway.rbac.server.service.RbacUserService;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.spring.tools.ServletUtils;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import cn.mapway.ui.shared.rpc.RpcResult;
import org.nutz.lang.Strings;

import javax.annotation.Resource;

/**
 * BaseController
 *
 * @author zhangjianshe@gmail.com
 */
public class ApiBaseController {

    @Resource
    RbacServerPlugin rbacServerPlugin;
    /**
     * 将BizResult 转换为RpcResult
     *
     * @param result
     * @param <T>
     * @return
     */
    public <T> RpcResult<T> toRpcResult(BizResult<T> result) {
        if (result.isFailed()) {
            return RpcResult.fail(result.getCode(), result.getMessage());
        } else {
            return RpcResult.create(result.getCode(), result.getMessage(), result.getData());
        }
    }

    public BizContext getBizContext() {
        //会从请求中构造用户信息
        BizContext context = new BizContext();
        String token= ServletUtils.readToken();
        if(Strings.isBlank(token))
        {
            throw new RuntimeException("请先登录");
        }
        IUserInfo userByToken = rbacServerPlugin.requestUser();
        if(userByToken==null){
            throw new RuntimeException("登录信息过期");
        }
        context.put(CommonConstant.KEY_LOGIN_USER,userByToken);
        return context;
    }
}
