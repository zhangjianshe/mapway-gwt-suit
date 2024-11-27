package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.rpc.LoginRequest;
import cn.mapway.rbac.shared.rpc.LoginResponse;
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
public class LoginExecutor extends AbstractBizExecutor<LoginResponse, LoginRequest> {
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<LoginResponse> process(BizContext context, BizRequest<LoginRequest> bizParam) {
        LoginRequest request = bizParam.getData();
        log.info("LoginExecutor {}", Json.toJson(request, JsonFormat.compact()));
        BizResult<LoginResponse> result = rbacUserService.login(request);
        if (result.isSuccess()) {
            Cookie cookie = new Cookie(CommonConstant.API_TOKEN, result.getData().getToken());
            ServletUtils.getResponse().addCookie(cookie);
        }
        return result;
    }
}
