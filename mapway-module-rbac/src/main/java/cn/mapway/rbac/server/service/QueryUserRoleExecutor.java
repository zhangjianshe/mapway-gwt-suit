package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.rpc.QueryUserRoleRequest;
import cn.mapway.rbac.shared.rpc.QueryUserRoleResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * QueryUserRoleExecutor
 * 查询用户拥有的角色
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryUserRoleExecutor extends AbstractBizExecutor<QueryUserRoleResponse, QueryUserRoleRequest> {
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<QueryUserRoleResponse> process(BizContext context, BizRequest<QueryUserRoleRequest> bizParam) {
        QueryUserRoleRequest request = bizParam.getData();
        log.info("QueryUserRoleExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);

        return rbacUserService.queryUserRole(request.getUserCode());
    }
}
