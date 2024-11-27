package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.rpc.CreateUserRoleRequest;
import cn.mapway.rbac.shared.rpc.CreateUserRoleResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * CreateUserRoleExecutor
 * 用户添加角色
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j

public class CreateUserRoleExecutor extends AbstractBizExecutor<CreateUserRoleResponse, CreateUserRoleRequest> {
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<CreateUserRoleResponse> process(BizContext context, BizRequest<CreateUserRoleRequest> bizParam) {
        CreateUserRoleRequest request = bizParam.getData();
        log.info("CreateUserRoleExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);

        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_MAINTAINER);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }

        return rbacUserService.createUserRole(request.getUserCode(), request.getRoleCode());
    }
}
