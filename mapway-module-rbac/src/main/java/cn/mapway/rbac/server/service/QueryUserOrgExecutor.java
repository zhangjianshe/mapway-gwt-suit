package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.RbacUserOrg;
import cn.mapway.rbac.shared.rpc.QueryUserOrgRequest;
import cn.mapway.rbac.shared.rpc.QueryUserOrgResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 查询用户 所在的组织列表
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryUserOrgExecutor extends AbstractBizExecutor<QueryUserOrgResponse, QueryUserOrgRequest> {
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<QueryUserOrgResponse> process(BizContext context, BizRequest<QueryUserOrgRequest> bizParam) {
        QueryUserOrgRequest request = bizParam.getData();
        log.info("QueryUserOrgExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        BizResult<Boolean> assignRole = rbacUserService.isAssignRole(user, "", RbacConstant.ROLE_SYS_MAINTAINER);
        boolean isAdmin=assignRole.isSuccess() && assignRole.getData();
        if(!(isAdmin || user.getId().equals(request.getUserId()))) {
            return BizResult.error(500,"没有权限查询用户的组织列表");
        }
        BizResult<List<RbacUserOrg>> listBizResult = rbacUserService.userOrgList(request.getSystemCode(), request.getUserId());
        if(listBizResult.isFailed()) {
            return listBizResult.asBizResult();
        }
        QueryUserOrgResponse response = new QueryUserOrgResponse();
        response.setUserOrgs(listBizResult.getData());
        return BizResult.success(response);
    }
}
