package cn.mapway.rbac.server;

import cn.mapway.rbac.server.service.RbacUserService;
import cn.mapway.rbac.shared.db.postgis.*;
import cn.mapway.server.IServerPlugin;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.server.IServerContext;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * RBAC 控制PLUGIN模块
 */
@Component
@Slf4j
public class RbacServerPlugin implements IServerPlugin {

    @Resource
    IServerContext serverContext;

    @Override
    public String getName() {
        return "用户权限";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public String author() {
        return "imagebot.cn";
    }

    @Override
    public void init(IServerContext iServerContext) {
        serverContext = iServerContext;
        log.info("RbacServerPlugin init");
        Dao dao = iServerContext.getBean(Dao.class);

        // build tables

        for (Class table : getTableClasses()) {
            dao.create(table, false);
            Daos.migration(dao, table, true, false, false);
        }
        RbacUserService rbacUserService = iServerContext.getBean(RbacUserService.class);

        // make sure super user exist
        rbacUserService.sureSuperUser();

        // register system resource point
        List<Class<?>> scanPackages = new ArrayList<>();
        scanPackages.add(RbacServerPlugin.class);
        Collection<Class<?>> scanPackages1 = iServerContext.getScanPackages();
        if (scanPackages1 != null) {
            scanPackages.addAll(scanPackages1);
        }
        rbacUserService.importResourcePointFromCode(scanPackages, iServerContext.getSuperUser());

    }

    @Override
    public void destroy(IServerContext iServerContext) {

    }

    @Override
    public IUserInfo requestUser() {
        return serverContext.requestUser();
    }

    @Override
    public List<Class> getTableClasses() {
        List<Class> tables = new ArrayList<>();
        tables.add(RbacOrgEntity.class);
        tables.add(RbacOrgUserEntity.class);
        tables.add(RbacRoleEntity.class);
        tables.add(RbacRoleResourceEntity.class);
        tables.add(RbacUserCodeRoleEntity.class);
        tables.add(RbacUserEntity.class);
        tables.add(RbacResourceEntity.class);
        return tables;
    }

}
