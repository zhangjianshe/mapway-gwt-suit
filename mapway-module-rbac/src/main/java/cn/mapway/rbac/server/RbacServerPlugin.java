package cn.mapway.rbac.server;

import cn.mapway.rbac.server.dao.DbTools;
import cn.mapway.rbac.server.service.RbacConfigService;
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
import java.util.Arrays;
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
    @Resource
    RbacConfigService rbacConfigService;

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


        String SCHEMA_DATE="2026-01-07";
        if(!dao.exists(RbacConfigEntity.class) || rbacConfigService.needUpdate("SCHEMA_DATE",SCHEMA_DATE)) {

            if(!dao.exists(RbacConfigEntity.class))
            {
               dao.create(RbacConfigEntity.class,false);
            }
            else {
                Daos.migration(dao,RbacConfigEntity.class,true,true,false);
            }

            if(!dao.exists(RbacOrgEntity.class)){
                dao.create(RbacOrgEntity.class,false);
            } else {
                Daos.migration(dao,RbacOrgEntity.class,true,true,false);
            };

            if(!dao.exists(RbacOrgUserEntity.class)){
                dao.create(RbacOrgUserEntity.class,false);
            }else {
                Daos.migration(dao,RbacOrgUserEntity.class,true,true,false);
            };
            if(!dao.exists(RbacRoleEntity.class)){
                dao.create(RbacRoleEntity.class,false);
            }else {
                Daos.migration(dao,RbacRoleEntity.class,true,true,false);
            };
            if(!dao.exists(RbacRoleResourceEntity.class)){
                dao.create(RbacRoleResourceEntity.class,false);
            }else {
                Daos.migration(dao,RbacRoleResourceEntity.class,true,true,false);
            };
            if(!dao.exists(RbacUserCodeRoleEntity.class)){
                dao.create(RbacUserCodeRoleEntity.class,false);
            }else {
                Daos.migration(dao,RbacUserCodeRoleEntity.class,true,true,false);
            };
            if(!dao.exists(RbacUserEntity.class)){
                dao.create(RbacUserEntity.class,false);
            }else {
                Daos.migration(dao,RbacUserEntity.class,true,true,false);
            };
            if(!dao.exists(RbacTokenEntity.class)){
                dao.create(RbacTokenEntity.class,false);
            }else {
                Daos.migration(dao,RbacTokenEntity.class,true,true,false);
            };

            if(!dao.exists(RbacResourceEntity.class)){
                dao.create(RbacResourceEntity.class,false);
            }
            else{
                Daos.migration(dao,RbacResourceEntity.class,true,true,false);
                String dropConstrain="alter table public.rbac_resource drop CONSTRAINT rbac_resource_pkey;";
                String createnew="alter table public.rbac_resource add CONSTRAINT rbac_resource_pkey PRIMARY KEY (\"resource_code\", \"kind\");";
                DbTools dbTools=new DbTools(dao);
                dbTools.execute(Arrays.asList(dropConstrain,createnew));
                log.info("修复RBAC_RESOURCE TABLE {}",SCHEMA_DATE);
            };

            rbacConfigService.saveConfig("SCHEMA_DATE",SCHEMA_DATE);
            log.info("RBAC模式变更版本{}",SCHEMA_DATE);
        }
        else {
            log.info("RBAC模式版本{}",SCHEMA_DATE);
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

    public IServerContext getServerContext() {
            return serverContext;
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
        tables.add(RbacConfigEntity.class);
        tables.add(RbacOrgEntity.class);
        tables.add(RbacOrgUserEntity.class);
        tables.add(RbacRoleEntity.class);
        tables.add(RbacRoleResourceEntity.class);
        tables.add(RbacUserCodeRoleEntity.class);
        tables.add(RbacUserEntity.class);
        tables.add(RbacResourceEntity.class);
        tables.add(RbacTokenEntity.class);
        return tables;
    }

}
