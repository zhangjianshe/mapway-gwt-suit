package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacRoleDao;
import cn.mapway.rbac.shared.RbacRole;
import cn.mapway.rbac.shared.db.postgis.RbacRoleEntity;
import cn.mapway.rbac.shared.rpc.QueryRoleRequest;
import cn.mapway.rbac.shared.rpc.QueryRoleResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * QueryRoleExecutor
 * 查询系统中角色树
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class QueryRoleExecutor extends AbstractBizExecutor<QueryRoleResponse, QueryRoleRequest> {
    @Resource
    RbacRoleDao rbacRoleDao;

    @Override
    protected BizResult<QueryRoleResponse> process(BizContext context, BizRequest<QueryRoleRequest> bizParam) {
        QueryRoleRequest request = bizParam.getData();
        log.info("QueryRoleExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);
        // todo only system manager can operator this function
        List<RbacRoleEntity> roles = rbacRoleDao.query(null);
        //根据父子关系创建一个TREE
        List<RbacRole> rootRoles = new ArrayList<>();

        Map<String, RbacRole> roleMap = new HashMap<>();
        List<RbacRole> allRoles = new ArrayList<>();

        for (RbacRoleEntity roleEntity : roles) {
            RbacRole role = new RbacRole();
            role.code = roleEntity.getCode();
            role.name = roleEntity.getName();
            role.icon = roleEntity.getIcon();
            role.summary = roleEntity.getSummary();
            role.parentCode = roleEntity.getParentCode();
            roleMap.put(roleEntity.getCode(), role);
            allRoles.add(role);
        }

        for (RbacRole role : allRoles) {
            if (Strings.isBlank(role.parentCode)) {
                rootRoles.add(role);
            } else {
                RbacRole parent = roleMap.get(role.parentCode);
                if (parent != null) {
                    parent.addChild(role);
                } else {
                    log.warn("role parent not found {}", role.parentCode);
                }
            }
        }
        QueryRoleResponse response = new QueryRoleResponse();
        response.setRoles(rootRoles);
        return BizResult.success(response);
    }
}
