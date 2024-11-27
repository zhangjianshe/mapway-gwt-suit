package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacUserDao;
import cn.mapway.rbac.shared.db.postgis.RbacUserEntity;
import cn.mapway.rbac.shared.rpc.QueryUserRequest;
import cn.mapway.rbac.shared.rpc.QueryUserResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * QueryUserExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryUserExecutor extends AbstractBizExecutor<QueryUserResponse, QueryUserRequest> {
    @Resource
    RbacUserDao rbacUserDao;

    @Override
    protected BizResult<QueryUserResponse> process(BizContext context, BizRequest<QueryUserRequest> bizParam) {
        QueryUserRequest request = bizParam.getData();
        log.info("QueryUserExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);

        if (request.getSearchText() == null) {
            request.setSearchText("");
        }

        Cnd where = Cnd.where(RbacUserEntity.FLD_USER_NAME, "like", "%" + request.getSearchText() + "%");

        List<RbacUserEntity> list = rbacUserDao.query(where);
        QueryUserResponse response = new QueryUserResponse();
        response.setUsers(list);

        return BizResult.success(response);
    }
}
