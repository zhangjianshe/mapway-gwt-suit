package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.rpc.QueryRoleResourceRequest;
import cn.mapway.rbac.shared.rpc.QueryRoleResourceResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * QueryRoleResourceExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryRoleResourceExecutor extends AbstractBizExecutor<QueryRoleResourceResponse, QueryRoleResourceRequest> {


    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<QueryRoleResourceResponse> process(BizContext context, BizRequest<QueryRoleResourceRequest> bizParam) {
        QueryRoleResourceRequest request = bizParam.getData();
        log.info("QueryRoleResourceExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        assertTrue(Strings.isNotBlank(request.getRoleCode()), "Role code is blank");
        return rbacUserService.queryResourceByRoleCode(request.getRoleCode(), request.getWidthChildren());
    }
}
