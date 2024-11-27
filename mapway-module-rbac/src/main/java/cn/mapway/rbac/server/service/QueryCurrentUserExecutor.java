package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.rpc.QueryCurrentUserRequest;
import cn.mapway.rbac.shared.rpc.QueryCurrentUserResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import cn.mapway.ui.shared.Messages;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

/**
 * QueryCurrentUserExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryCurrentUserExecutor extends AbstractBizExecutor<QueryCurrentUserResponse, QueryCurrentUserRequest> {
    @Override
    protected BizResult<QueryCurrentUserResponse> process(BizContext context, BizRequest<QueryCurrentUserRequest> bizParam) {
        QueryCurrentUserRequest request = bizParam.getData();
        log.info("QueryCurrentUserExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        if (user != null) {
            QueryCurrentUserResponse response = new QueryCurrentUserResponse();
            response.setCurrentUser(user);
            return BizResult.success(response);
        }
        return BizResult.error(Messages.NSG_NEED_LOGIN.getCode(), Messages.NSG_NEED_LOGIN.getMessage());
    }
}
