package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacOrgDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import cn.mapway.rbac.shared.rpc.UpdateOrgRequest;
import cn.mapway.rbac.shared.rpc.UpdateOrgResponse;
import cn.mapway.spring.tools.UUIDTools;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * UpdateOrgExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class UpdateOrgExecutor extends AbstractBizExecutor<UpdateOrgResponse, UpdateOrgRequest> {
    @Resource
    RbacOrgDao rbacOrgDao;
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<UpdateOrgResponse> process(BizContext context, BizRequest<UpdateOrgRequest> bizParam) {
        UpdateOrgRequest request = bizParam.getData();
        log.info("UpdateOrgExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_AUTHORITY);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }

        RbacOrgEntity org = request.getOrg();
        assertNotNull(org, "没有组织机构");
        if (Strings.isBlank(org.getId())) {
            assertTrue(Strings.isNotBlank(org.getCode()), "组织机构编码不能为空");
            assertTrue(Strings.isNotBlank(org.getName()), "组织机构名称不能为空");
            org.setId(UUIDTools.uuid());
            rbacOrgDao.insert(org);
        } else {
            rbacOrgDao.updateIgnoreNull(org);
        }
        UpdateOrgResponse updateOrgResponse = new UpdateOrgResponse();
        updateOrgResponse.setOrg(rbacOrgDao.fetch(org.getId()));
        //清空组织机构的缓存
        rbacUserService.clearGlobalCache();
        return BizResult.success(updateOrgResponse);
    }
}
