package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacResourceDao;
import cn.mapway.rbac.server.dao.RbacRoleDao;
import cn.mapway.rbac.server.dao.RbacRoleResourceDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleResourceEntity;
import cn.mapway.rbac.shared.rpc.UpdateRoleResourceRequest;
import cn.mapway.rbac.shared.rpc.UpdateRoleResourceResponse;
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
 * UpdateRoleResourceExecutor
 * 更新角色对应的资源
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class UpdateRoleResourceExecutor extends AbstractBizExecutor<UpdateRoleResourceResponse, UpdateRoleResourceRequest> {
    @Resource
    RbacRoleResourceDao rbacRoleResourceDao;
    @Resource
    RbacRoleDao rbacRoleDao;
    @Resource
    RbacResourceDao rbacResourceDao;
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<UpdateRoleResourceResponse> process(BizContext context, BizRequest<UpdateRoleResourceRequest> bizParam) {
        UpdateRoleResourceRequest request = bizParam.getData();
        log.info("UpdateRoleResourceExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);

        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_MAINTAINER);
        if (assignResource.isFailed()) {
            return assignResource.asBizResult();
        }

        for (String resourceCode : request.getResourceCodes()) {
            if (Strings.isBlank(resourceCode)) {
                continue;
            }
            Cnd where = Cnd.where(RbacRoleResourceEntity.FLD_ROLE_CODE, "=", request.getRoleCode())
                    .and(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", resourceCode);
            //添加关联关系
            int count = rbacRoleResourceDao.count(where);
            if (count > 0) {
                log.warn("role[{}] already has resource[{}]", request.getRoleCode(), resourceCode);
            } else {
                //first check role and resource exists
                int roleCount = rbacRoleDao.count(Cnd.where(RbacRoleEntity.FLD_CODE, "=", request.getRoleCode()));
                int resourceCount = rbacResourceDao.count(Cnd.where(RbacResourceEntity.FLD_RESOURCE_CODE, "=", resourceCode));
                if (roleCount == 0) {
                    throw new RuntimeException("role[" + request.getRoleCode() + "] not exists");
                }
                if (resourceCount == 0) {
                    throw new RuntimeException("resource[" + resourceCode + "] not exists");
                }
                RbacRoleResourceEntity entity = new RbacRoleResourceEntity();
                entity.setRoleCode(request.getRoleCode());
                entity.setResourceCode(resourceCode);
                rbacRoleResourceDao.insert(entity);
            }
        }
        rbacUserService.resetGroupCache();
        return BizResult.success(new UpdateRoleResourceResponse());
    }
}
