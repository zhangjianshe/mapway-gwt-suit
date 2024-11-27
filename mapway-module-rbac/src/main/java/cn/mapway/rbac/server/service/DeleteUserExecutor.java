package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.rpc.DeleteUserRequest;
import cn.mapway.rbac.shared.rpc.DeleteUserResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

/**
 * DeleteUserExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class DeleteUserExecutor extends AbstractBizExecutor<DeleteUserResponse, DeleteUserRequest> {
    @Override
    protected BizResult<DeleteUserResponse> process(BizContext context, BizRequest<DeleteUserRequest> bizParam) {
        DeleteUserRequest request = bizParam.getData();
        log.info("DeleteUserExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);

        return BizResult.success(null);
    }
}
