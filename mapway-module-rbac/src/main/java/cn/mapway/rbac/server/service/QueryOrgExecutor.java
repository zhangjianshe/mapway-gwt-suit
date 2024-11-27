package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacOrgDao;
import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import cn.mapway.rbac.shared.rpc.QueryOrgRequest;
import cn.mapway.rbac.shared.rpc.QueryOrgResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * QueryOrgExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryOrgExecutor extends AbstractBizExecutor<QueryOrgResponse, QueryOrgRequest> {
    @Resource
    RbacOrgDao rbacOrgDao;

    @Override
    protected BizResult<QueryOrgResponse> process(BizContext context, BizRequest<QueryOrgRequest> bizParam) {
        QueryOrgRequest request = bizParam.getData();
        log.info("QueryOrgExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        log.info("current user {},{}",user.getId(),user.getUserName());
        QueryOrgResponse queryOrgResponse = new QueryOrgResponse();

        if (Strings.isBlank(request.getOrgId())) {
            //查询所有组织结构数据
            List<RbacOrgEntity> list = rbacOrgDao.query(Cnd.orderBy().asc(RbacOrgEntity.FLD_ID));
            queryOrgResponse.setOrgs(list);
        } else {
            if (request.isCascade()) {
                //查询子组织结构数据
                List<RbacOrgEntity> orgs = recursiveQuery(request.getOrgId());
                queryOrgResponse.setOrgs(orgs);
            } else {
                //查询组织结构数据
                RbacOrgEntity org = rbacOrgDao.fetch(request.getOrgId());
                queryOrgResponse.setOrgs(List.of(org));
            }
        }
        return BizResult.success(queryOrgResponse);
    }

    /**
     * 递归ＳＱＬ查询
     *
     * @param orgId
     * @return
     */
    private List<RbacOrgEntity> recursiveQuery(String orgId) {
        // postgres to recursive query record based on parent id
        String sql = "WITH RECURSIVE org_hierarchy AS (\n" +
                "    SELECT *\n" +
                "    FROM \"rbac_org\"\n" +
                "    WHERE \"id\" = '" + orgId + "'" +
                "    UNION ALL\n" +
                "    SELECT child.*\n" +
                "    FROM \"rbac_org\" child\n" +
                "    INNER JOIN org_hierarchy parent\n" +
                "    ON child.\"parent_id\" = parent.\"id\"\n" +
                ")\n" +
                "SELECT *\n" +
                "FROM org_hierarchy;\n";
        Sql sql1 = Sqls.create(sql);
        sql1.setEntity(rbacOrgDao.getEntity());
        rbacOrgDao.getDao().execute(sql1);
        return sql1.getList(RbacOrgEntity.class);
    }
}
