package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacRoleDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.db.postgis.RbacRoleEntity;
import cn.mapway.rbac.shared.rpc.UpdateRoleRequest;
import cn.mapway.rbac.shared.rpc.UpdateRoleResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * UpdateRoleExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class UpdateRoleExecutor extends AbstractBizExecutor<UpdateRoleResponse, UpdateRoleRequest> {
    @Resource
    RbacRoleDao rbacRoleDao;
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<UpdateRoleResponse> process(BizContext context, BizRequest<UpdateRoleRequest> bizParam) {
        UpdateRoleRequest request = bizParam.getData();
        log.info("UpdateRoleExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_AUTHORITY);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }
        
        assertNotNull(request.getRole(), "Role is null");
        assertTrue(Strings.isNotBlank(request.getRole().code), "Role code is blank");
        assertTrue(Strings.isNotBlank(request.getRole().name), "Role name is blank");
        List<RbacRoleEntity> query = rbacRoleDao.query(Cnd.where(RbacRoleEntity.FLD_CODE, "=", request.getRole().code));
        if (query.size() > 0) {
            RbacRoleEntity roleEntity = query.get(0);
            roleEntity.setName(request.getRole().name);
            roleEntity.setIcon(request.getRole().icon);
            roleEntity.setSummary(request.getRole().summary);
            roleEntity.setParentCode(request.getRole().parentCode);
            rbacRoleDao.updateIgnoreNull(roleEntity);
        } else {
            RbacRoleEntity roleEntity = new RbacRoleEntity();
            roleEntity.setName(request.getRole().name);
            roleEntity.setIcon(request.getRole().icon);
            roleEntity.setSummary(request.getRole().summary);
            roleEntity.setParentCode(request.getRole().parentCode);
            roleEntity.setCode(request.getRole().code);
            rbacRoleDao.insert(roleEntity);
        }

        return BizResult.success(new UpdateRoleResponse());
    }
}
