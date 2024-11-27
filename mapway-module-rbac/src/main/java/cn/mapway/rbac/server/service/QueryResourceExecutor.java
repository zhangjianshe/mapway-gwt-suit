package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacResourceDao;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.rpc.QueryResourceRequest;
import cn.mapway.rbac.shared.rpc.QueryResourceResponse;
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
 * QueryResourceExecutor
 * 查询所有资源点
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryResourceExecutor extends AbstractBizExecutor<QueryResourceResponse, QueryResourceRequest> {
    @Resource
    RbacResourceDao rbacResourceDao;

    @Override
    protected BizResult<QueryResourceResponse> process(BizContext context, BizRequest<QueryResourceRequest> bizParam) {
        QueryResourceRequest request = bizParam.getData();
        log.info("QueryResourceExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        List<RbacResourceEntity> entities = rbacResourceDao.query(Cnd.orderBy().asc(RbacResourceEntity.FLD_CATALOG).asc(RbacResourceEntity.FLD_NAME));

        QueryResourceResponse response = new QueryResourceResponse();
        response.setResources(entities);
        return BizResult.success(response);
    }
}
