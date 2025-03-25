package cn.mapway.rbac.server.service;

import cn.mapway.rbac.shared.RbacRoleResource;
import cn.mapway.rbac.shared.RbacUserOrg;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleResourceEntity;
import cn.mapway.rbac.shared.rpc.QueryRoleResourceResponse;
import cn.mapway.rbac.shared.rpc.QueryUserRoleResourceRequest;
import cn.mapway.rbac.shared.rpc.QueryUserRoleResourceResponse;
import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;

import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询用户身份的所有角色和权限
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryUserRoleResourceExecutor extends AbstractBizExecutor<QueryUserRoleResourceResponse, QueryUserRoleResourceRequest> {
    @Resource
    RbacUserService rbacUserService;
    @Override
    protected BizResult<QueryUserRoleResourceResponse> process(BizContext context, BizRequest<QueryUserRoleResourceRequest> bizParam) {
        QueryUserRoleResourceRequest request = bizParam.getData();
        log.info("QueryUserRoleResourceExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user= (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        assertNotNull(request.getUserCode(), "请求数据不能为空");
        RbacOrgUserEntity orgUser= rbacUserService.queryUserOrg(request.getUserCode());
        assertNotNull(orgUser, "用户身份不存在");
        if(!user.getId().equals(orgUser.getUserId())) {
            return BizResult.error(500,"没有权限查询用户的组织列表");
        }
        QueryUserRoleResourceResponse response = new QueryUserRoleResourceResponse();
        response.setOrgUser(orgUser);
        response.setRoles(rbacUserService.queryUserRole(request.getUserCode()).getData().getRoles());

        List<RbacRoleResource> resources=new ArrayList<>();
        for(RbacRoleEntity role:response.getRoles()){
            BizResult<QueryRoleResourceResponse> result = rbacUserService.queryResourceByRoleCode(role.getCode(), false);
            if(result.isSuccess()){
                result.getData().getResources().forEach((r)->{
                    RbacRoleResource resource=new RbacRoleResource();
                    resource.roleCode=role.getCode();
                    resource.roleName=role.getName();
                    resource.resourceCode=r.getResourceCode();
                    resource.name=r.getName();
                    resource.kind=r.getKind();
                    resource.data=r.getData();
                    resource.summary=r.getSummary();
                    resource.catalog=r.getCatalog();
                    resources.add(resource);
                });
            }
        }
        response.setResource(resources);
        return BizResult.success(response);
    }
}
