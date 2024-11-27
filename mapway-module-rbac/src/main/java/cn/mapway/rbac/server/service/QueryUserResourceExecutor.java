package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.shared.rpc.QueryUserResourceRequest;
import cn.mapway.rbac.shared.rpc.QueryUserResourceResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

/**
 * QueryUserResourceExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryUserResourceExecutor extends AbstractBizExecutor<QueryUserResourceResponse, QueryUserResourceRequest> {
    @Override
    protected BizResult<QueryUserResourceResponse> process(BizContext context, BizRequest<QueryUserResourceRequest> bizParam) {
        QueryUserResourceRequest request = bizParam.getData();
        log.info("QueryUserResourceExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);

        return BizResult.success(null);
    }
}
