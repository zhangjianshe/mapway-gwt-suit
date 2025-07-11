package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.client.user.RbacUser;
import cn.mapway.rbac.server.dao.RbacOrgUserDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.db.postgis.RbacUserEntity;
import cn.mapway.rbac.shared.rpc.CreateUserRoleRequest;
import cn.mapway.rbac.shared.rpc.CreateUserRoleResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    RbacOrgUserDao rbacOrgUserDao;

    @Override
    protected BizResult<CreateUserRoleResponse> process(BizContext context, BizRequest<CreateUserRoleRequest> bizParam) {
        CreateUserRoleRequest request = bizParam.getData();
        log.info("CreateUserRoleExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        RbacOrgUserEntity rbacOrgUserEntity = rbacOrgUserDao.fetch(Cnd.where(RbacOrgUserEntity.FLD_USER_CODE, "=", request.getUserCode()));
        if(rbacOrgUserEntity == null){
            return BizResult.error(400, "用户不存在");
        }
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user.getSystemCode(),
                rbacOrgUserEntity.getUserId(), null, RbacConstant.RESOURCE_RBAC_MAINTAINER);
//         BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_MAINTAINER);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }

        BizResult<CreateUserRoleResponse> response = rbacUserService.createUserRole(request.getUserCode(), request.getRoleCode());
        rbacUserService.resetUserPermissionsCache(user.getSystemCode(), rbacOrgUserEntity.getUserId());
        return response;
    }
}
