package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacResourceDao;
import cn.mapway.rbac.server.dao.RbacRoleResourceDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleResourceEntity;
import cn.mapway.rbac.shared.rpc.DeleteResourceRequest;
import cn.mapway.rbac.shared.rpc.DeleteResourceResponse;
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
 * UpdateResourceExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class DeleteResourceExecutor extends AbstractBizExecutor<DeleteResourceResponse, DeleteResourceRequest> {
    @Resource
    RbacResourceDao rbacResourceDao;
    @Resource
    RbacRoleResourceDao rbacRoleResourceDao;
    @Resource
    private RbacUserService rbacUserService;

    @Override
    protected BizResult<DeleteResourceResponse> process(BizContext context, BizRequest<DeleteResourceRequest> bizParam) {
        DeleteResourceRequest request = bizParam.getData();
        log.info("UpdateResourceExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_MAINTAINER);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }

        assertTrue(Strings.isNotBlank(request.getResourceCode()), "Resource code is blank");
        Cnd and = Cnd.where(RbacResourceEntity.FLD_RESOURCE_CODE, "=", request.getResourceCode())
                .and(RbacResourceEntity.FLD_KIND, "=", request.getKind());
        RbacResourceEntity resource = rbacResourceDao.fetch(and);
        assertNotNull(resource, "没有该资源");
        ResourceKind kind = ResourceKind.fromCode(resource.getKind());
        assertTrue(kind == ResourceKind.RESOURCE_KIND_CUSTOM, "只能删除自定义资源点");
        int count = rbacRoleResourceDao.count(Cnd.where(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", request.getResourceCode()));
        if (count > 0) {
            return BizResult.error(500, "该资源已被角色使用，无法删除");
        }
        rbacResourceDao.clear(Cnd.where(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", request.getResourceCode()));
        return BizResult.success(new DeleteResourceResponse());
    }
}
