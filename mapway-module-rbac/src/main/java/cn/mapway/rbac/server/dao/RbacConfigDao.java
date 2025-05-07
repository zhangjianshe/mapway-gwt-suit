package cn.mapway.rbac.server.dao;

import cn.mapway.dao.BaseDao;
import cn.mapway.rbac.shared.db.postgis.RbacConfigEntity;
import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import org.springframework.stereotype.Component;

@Component
public class RbacConfigDao extends BaseDao<RbacConfigEntity> {
}
