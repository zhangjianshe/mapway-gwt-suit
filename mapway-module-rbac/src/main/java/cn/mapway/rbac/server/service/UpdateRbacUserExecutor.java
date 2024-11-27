package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.rpc.UpdateRbacUserRequest;
import cn.mapway.rbac.shared.rpc.UpdateRbacUserResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * UpdateUserExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class UpdateRbacUserExecutor extends AbstractBizExecutor<UpdateRbacUserResponse, UpdateRbacUserRequest> {
    @Resource
    RbacUserService rbacUserService;

    @Override
    protected BizResult<UpdateRbacUserResponse> process(BizContext context, BizRequest<UpdateRbacUserRequest> bizParam) {
        UpdateRbacUserRequest request = bizParam.getData();
        log.info("UpdateUserExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_AUTHORITY);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }
        
        return BizResult.success(null);
    }
}
