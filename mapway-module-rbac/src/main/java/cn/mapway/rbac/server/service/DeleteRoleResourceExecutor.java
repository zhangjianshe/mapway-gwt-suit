package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacRoleResourceDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.db.postgis.RbacRoleResourceEntity;
import cn.mapway.rbac.shared.rpc.DeleteRoleResourceRequest;
import cn.mapway.rbac.shared.rpc.DeleteRoleResourceResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * DeleteRoleResourceExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class DeleteRoleResourceExecutor extends AbstractBizExecutor<DeleteRoleResourceResponse, DeleteRoleResourceRequest> {
    @Resource
    RbacRoleResourceDao rbacRoleResourceDao;
    @Resource
    RbacUserService rbacUserService;
    @Override
    protected BizResult<DeleteRoleResourceResponse> process(BizContext context, BizRequest<DeleteRoleResourceRequest> bizParam) {
        DeleteRoleResourceRequest request = bizParam.getData();
        log.info("DeleteRoleResourceExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限

        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_MAINTAINER);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }

        assertTrue(Strings.isNotBlank(request.getResourceCode()), "Resource code is blank");
        assertTrue(Strings.isNotBlank(request.getRoleCode()), "Role code is blank");
        Cnd where = Cnd.where(RbacRoleResourceEntity.FLD_ROLE_CODE, "=", request.getRoleCode())
                .and(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", request.getResourceCode());
        rbacRoleResourceDao.clear(where);
        return BizResult.success(new DeleteRoleResourceResponse());
    }
}
