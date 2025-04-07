package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacOrgUserDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.rpc.UpdateOrgUserRequest;
import cn.mapway.rbac.shared.rpc.UpdateOrgUserResponse;
import cn.mapway.spring.tools.UUIDTools;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * UpdateOrgUserExecutor
 * 组织添加用户
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class UpdateOrgUserExecutor extends AbstractBizExecutor<UpdateOrgUserResponse, UpdateOrgUserRequest> {
    @Resource
    RbacOrgUserDao rbacOrgUserDao;
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<UpdateOrgUserResponse> process(BizContext context, BizRequest<UpdateOrgUserRequest> bizParam) {
        UpdateOrgUserRequest request = bizParam.getData();
        log.info("UpdateOrgUserExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_AUTHORITY);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }
        
        RbacOrgUserEntity orgUser = request.getOrgUser();
        assertNotNull(orgUser, "组织机构ID不能为空");
        if (Strings.isBlank(orgUser.getUserCode())) {
            assertNotNull(orgUser.getUserId(), "用户ID不能为空");
            assertNotNull(orgUser.getOrgCode(), "组织机构ID不能为空");
            orgUser.setCreateTime(new Date());
            orgUser.setUserCode(UUIDTools.uuid());
            rbacOrgUserDao.insert(orgUser);
        } else {
            rbacOrgUserDao.updateIgnoreNull(orgUser);
        }
        //添加完成后修改用户的缓存信息
        rbacUserService.resetUserPermissionsCache(user);

        UpdateOrgUserResponse updateOrgUserResponse = new UpdateOrgUserResponse();
        updateOrgUserResponse.setOrgUser(rbacOrgUserDao.fetch(orgUser.getUserCode()));
        return BizResult.success(updateOrgUserResponse);
    }
}
