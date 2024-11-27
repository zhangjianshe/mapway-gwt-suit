package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacOrgDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.rpc.DeleteOrgRequest;
import cn.mapway.rbac.shared.rpc.DeleteOrgResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Sqls;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * DeleteOrgExecutor
 * 删除组织机构
 * 删除该组织的所有用户
 * 删除用户下的所有角色
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class DeleteOrgExecutor extends AbstractBizExecutor<DeleteOrgResponse, DeleteOrgRequest> {
    @Resource
    RbacOrgDao rbacOrgDao;
    @Resource
    RbacUserService rbacUserService;
    @Override
    protected BizResult<DeleteOrgResponse> process(BizContext context, BizRequest<DeleteOrgRequest> bizParam) {
        DeleteOrgRequest request = bizParam.getData();
        log.info("DeleteOrgExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_AUTHORITY);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }

        assertTrue(Strings.isNotBlank(request.getOrgId()), "组织机构ID不能为空");

        String deleteUserRole = "delete  from rbac_user_code_role where user_code in" +
                " (select user_code from rbac_org_user where org_code = '" + request.getOrgId() + "') ";
        String deleteOrgUser = "delete  from rbac_org_user where org_code = '" + request.getOrgId() + "' ";
        String deleteOrg = "delete  from rbac_org where id = '" + request.getOrgId() + "' ";
        Trans.exec(new Atom() {
            @Override
            public void run() {
                rbacOrgDao.getDao().execute(Sqls.create(deleteUserRole));
                rbacOrgDao.getDao().execute(Sqls.create(deleteOrgUser));
                rbacOrgDao.getDao().execute(Sqls.create(deleteOrg));
            }
        });

        return BizResult.success(new DeleteOrgResponse());
    }
}
