package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacRoleDao;
import cn.mapway.rbac.server.dao.RbacRoleResourceDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.db.postgis.RbacRoleEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleResourceEntity;
import cn.mapway.rbac.shared.rpc.DeleteRoleRequest;
import cn.mapway.rbac.shared.rpc.DeleteRoleResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * DeleteRoleExecutor
 * 删除角色
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class DeleteRoleExecutor extends AbstractBizExecutor<DeleteRoleResponse, DeleteRoleRequest> {
    @Resource
    RbacRoleDao rbacRoleDao;
    @Resource
    RbacRoleResourceDao rbacRoleResourceDao;
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<DeleteRoleResponse> process(BizContext context, BizRequest<DeleteRoleRequest> bizParam) {
        DeleteRoleRequest request = bizParam.getData();
        log.info("DeleteRoleExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_MAINTAINER);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }
        
        assertTrue(Strings.isNotBlank(request.getRoleCode()), "Role id is blank");
        if (rbacUserService.isRoleInUse(request.getRoleCode())) {
            return BizResult.error(500, "角色正在被使用");
        }
        Trans.exec(new Atom() {
            @Override
            public void run() {
                int deleteNum = rbacRoleResourceDao.clear(Cnd.where(RbacRoleResourceEntity.FLD_ROLE_CODE, "=", request.getRoleCode()));
                rbacRoleDao.clear(Cnd.where(RbacRoleEntity.FLD_CODE, "=", request.getRoleCode()));
                if (deleteNum > 0) {
                    rbacUserService.resetGroupCache();
                }
            }
        });
        return BizResult.success(new DeleteRoleResponse());
    }
}
