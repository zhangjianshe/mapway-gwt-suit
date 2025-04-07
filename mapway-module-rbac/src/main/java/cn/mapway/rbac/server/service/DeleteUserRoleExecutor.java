package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.rpc.DeleteUserRoleRequest;
import cn.mapway.rbac.shared.rpc.DeleteUserRoleResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * DeleteUserRoleExecutor
 * 删除用户角色
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class DeleteUserRoleExecutor extends AbstractBizExecutor<DeleteUserRoleResponse, DeleteUserRoleRequest> {

    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<DeleteUserRoleResponse> process(BizContext context, BizRequest<DeleteUserRoleRequest> bizParam) {
        DeleteUserRoleRequest request = bizParam.getData();
        log.info("DeleteUserRoleExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_AUTHORITY);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }
        //添加完成后修改用户的缓存信息
        rbacUserService.resetUserPermissionsCache(user);

        return rbacUserService.deleteUserRole(request.getUserCode(), request.getRoleCode());
    }
}
