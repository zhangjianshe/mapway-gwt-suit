package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.rpc.LoginRequest;
import cn.mapway.rbac.shared.rpc.LoginResponse;
import cn.mapway.rbac.shared.rpc.LogoutRequest;
import cn.mapway.rbac.shared.rpc.LogoutResponse;
import cn.mapway.spring.tools.ServletUtils;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

/**
 * LoginExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class LogoutExecutor extends AbstractBizExecutor<LogoutResponse, LogoutRequest> {
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<LogoutResponse> process(BizContext context, BizRequest<LogoutRequest> bizParam) {
        LogoutRequest request = bizParam.getData();
        log.info("LoginExecutor {}", Json.toJson(request, JsonFormat.compact()));
        BizResult<LogoutResponse> result = rbacUserService.logout(request);
        if (result.isSuccess()) {
            Cookie cookie = new Cookie(CommonConstant.API_TOKEN, "");
            ServletUtils.getResponse().addCookie(cookie);
        }
        return result;
    }
}
