package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacOrgUserDao;
import cn.mapway.rbac.server.dao.RbacUserCodeRoleDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.db.postgis.RbacUserCodeRoleEntity;
import cn.mapway.rbac.shared.rpc.DeleteOrgUserRequest;
import cn.mapway.rbac.shared.rpc.DeleteOrgUserResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * DeleteOrgUserExecutor
 * 删除一个组织机构的用户
 * 同时删除该用户关联的角色
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class DeleteOrgUserExecutor extends AbstractBizExecutor<DeleteOrgUserResponse, DeleteOrgUserRequest> {
    @Resource
    RbacOrgUserDao rbacOrgUserDao;
    @Resource
    RbacUserCodeRoleDao rbacUserCodeRoleDao;
    @Resource
    RbacUserService rbacUserService;
    @Override
    protected BizResult<DeleteOrgUserResponse> process(BizContext context, BizRequest<DeleteOrgUserRequest> bizParam) {
        DeleteOrgUserRequest request = bizParam.getData();
        log.info("DeleteOrgUserExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_AUTHORITY);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }

        assertNotNull(request, "请求数据不能为空");
        assertNotNull(request.getUserCode(), "userCode不能为空");

        Trans.exec(new Atom() {
            @Override
            public void run() {
                rbacUserCodeRoleDao.clear(Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "=", request.getUserCode()));
                rbacOrgUserDao.clear(Cnd.where(RbacOrgUserEntity.FLD_USER_CODE, "=", request.getUserCode()));
            }
        });
        //添加完成后修改用户的缓存信息
        rbacUserService.resetUserPermissionsCache(user);

        return BizResult.success(new DeleteOrgUserResponse());
    }
}
