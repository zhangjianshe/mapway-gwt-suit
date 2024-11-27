package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacOrgUserDao;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.rpc.QueryOrgUserRequest;
import cn.mapway.rbac.shared.rpc.QueryOrgUserResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * QueryOrgUserExecutor
 * 查询组织用户
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryOrgUserExecutor extends AbstractBizExecutor<QueryOrgUserResponse, QueryOrgUserRequest> {
    @Resource
    RbacOrgUserDao rbacOrgUserDao;

    @Override
    protected BizResult<QueryOrgUserResponse> process(BizContext context, BizRequest<QueryOrgUserRequest> bizParam) {
        QueryOrgUserRequest request = bizParam.getData();
        log.info("QueryOrgUserExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        assertNotNull(request, "请求数据不能为空");
        assertTrue(Strings.isNotBlank(request.getOrgCode()), "组织机构ID不能为空");
        List<RbacOrgUserEntity> users = rbacOrgUserDao.query(Cnd.where(RbacOrgUserEntity.FLD_ORG_CODE, "=", request.getOrgCode()));

        QueryOrgUserResponse response = new QueryOrgUserResponse();
        response.setUsers(users);

        return BizResult.success(response);
    }
}
