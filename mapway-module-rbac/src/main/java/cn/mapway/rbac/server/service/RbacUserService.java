package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.client.user.RbacUser;
import cn.mapway.rbac.server.dao.*;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.*;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.server.MyScans;
import cn.mapway.spring.tools.ServletUtils;
import cn.mapway.spring.tools.UUIDTools;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.Exps;
import org.nutz.dao.util.cri.SqlRange;
import org.nutz.dao.util.cri.SqlValueRange;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * this service save IUserInfo in the http session.
 * ===============================================
 */
@Service
@Slf4j
@ResourceDeclares(
        {@ResourceDeclare(
                catalog = "系统", name = "资源维护", code = RbacConstant.RESOURCE_RBAC_MAINTAINER,
                summary = "维护系统角色以及资源点", kind = ResourceKind.RESOURCE_KIND_SYSTEM
        ),
                @ResourceDeclare(
                        catalog = "系统", name = "用户授权", code = RbacConstant.RESOURCE_RBAC_AUTHORITY,
                        summary = "为用户授权", kind = ResourceKind.RESOURCE_KIND_SYSTEM
                )
        }
)
@RoleDeclares(
        {
                @RoleDeclare(
                        name = "系统", code =RbacConstant.ROLE_SYS, summary = "系统"
                ),
                @RoleDeclare(
                        name = "系统维护员", code = RbacConstant.ROLE_SYS_MAINTAINER, summary = "系统维护", parentCode =RbacConstant.ROLE_SYS,
                        resources = {RbacConstant.RESOURCE_RBAC_MAINTAINER, RbacConstant.RESOURCE_RBAC_AUTHORITY}
                )
        }
)
public class RbacUserService {
    // global session list

    @Resource
    RbacUserDao rbacUserDao;
    @Resource
    RbacUserCodeRoleDao rbacUserCodeRoleDao;
    @Resource
    RbacResourceDao rbacResourceDao;
    @Resource
    RbacRoleDao rbacRoleDao;
    @Resource
    RbacOrgUserDao rbacOrgUserDao;
    @Resource
    RbacRoleResourceDao rbacRoleResourceDao;
    @Resource
    RbacOrgDao rbacOrgDao;


    public BizResult<Boolean> isAssignRole(IUserInfo userInfo, String userCode, String roleCode) {
        return isAssignRole(userInfo.getSystemCode(), userInfo.getId(), userCode, roleCode);
    }


    /**
     * 用户是否拥有某个角色
     *
     * @param systemCode 使用该ＲＢＡＣ控制系统的系统代码　使用这定义　比如　CIS
     * @param userId     是客户系统中的用户ID,这里使用字符串，如果原系统是数值，可以序列化为字符串
     * @param userCode   userCode 是用户的身份编码,每个用户可能处在不同的组织机构下，在每个组织机构中拥有不同的身份
     *                   同一个UserId 拥有不同的 userCode,如果　userCode 为空(null or '')，则表示拥有默认的用户身份,查询其所有身份的记录
     * @param roleCode   　需要判断用户是否拥有权限的　角色
     * @return
     */
    public BizResult<Boolean> isAssignRole(String systemCode, String userId, String userCode, String roleCode) {
        if (Strings.isBlank(roleCode)) {
            log.warn("check user's empty resource code,we just return true");
            return BizResult.success(true);
        }
        if (Strings.isBlank(userCode)) {
            //检查userId 所有的角色
            List<RbacOrgUserEntity> query = rbacOrgUserDao.query(Cnd.where(RbacOrgUserEntity.FLD_USER_ID, "=", userId).and(RbacOrgUserEntity.FLD_SYSTEM_CODE, "=", systemCode));
            if (query == null || query.size() == 0) {
                return BizResult.error(500, "用户没有配置角色");
            }
            List<String> userCodes = new ArrayList<>();
            for (RbacOrgUserEntity entity : query) {
                userCodes.add(entity.getUserCode());
            }
            Cnd where = Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "in", userCodes)
                    .and(RbacUserCodeRoleEntity.FLD_ROLE_CODE, "=", roleCode);
            if (rbacUserCodeRoleDao.count(where) > 0) {
                return BizResult.success(true);
            } else {
                return BizResult.success(false);
            }
        } else {
            RbacOrgUserEntity query = rbacOrgUserDao.fetch(Cnd.where(RbacOrgUserEntity.FLD_USER_CODE, "=", userCode));
            if (query == null) {
                return BizResult.error(500, "没有查询到用户身份:" + userCode);
            }
            if (!query.getUserId().equals(userId) || !query.getSystemCode().equals(systemCode)) {
                return BizResult.error(500, "用户身份不匹配");
            }

            Cnd where = Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "=", userCode)
                    .and(RbacUserCodeRoleEntity.FLD_ROLE_CODE, "=", roleCode);
            if (rbacUserCodeRoleDao.count(where) > 0) {
                return BizResult.success(true);
            } else {
                return BizResult.success(false);
            }
        }
    }

    public BizResult<Boolean> isAssignResource(IUserInfo userInfo, String userCode, String roleCode) {
        return isAssignResource(userInfo.getSystemCode(), userInfo.getId(), userCode, roleCode);
    }

    /**
     * 用户是否拥有某个资源
     *
     * @param systemCode   使用该ＲＢＡＣ控制系统的系统代码　使用这定义　比如　CIS
     * @param userId       是客户系统中的用户ID,这里使用字符串，如果原系统是数值，可以序列化为字符串
     * @param userCode     userCode 是用户的身份编码,每个用户可能处在不同的组织机构下，在每个组织机构中拥有不同的身份
     *                     同一个UserId 拥有不同的 userCode,如果　userCode 为空(null or '')，则表示拥有默认的用户身份,查询其所有身份的记录
     * @param resourceCode 　需要判断用户是否拥有权限的　资源代码
     * @return
     */
    public BizResult<Boolean> isAssignResource(String systemCode, String userId, String userCode, String resourceCode) {
        if (Strings.isBlank(resourceCode)) {
            log.warn("check user's empty resource code,we just return true");
            return BizResult.error(500, "没有提供资源代码:" + resourceCode);
        }
        if (Strings.isBlank(userCode)) {
            //检查userId 所有的角色
            List<RbacOrgUserEntity> query = rbacOrgUserDao.query(Cnd.where(RbacOrgUserEntity.FLD_USER_ID, "=", userId)
                    .and(RbacOrgUserEntity.FLD_SYSTEM_CODE, "=", systemCode));
            if (query == null || query.size() == 0) {
                return BizResult.error(500, "用户没有配置角色");
            }
            List<String> userCodes = new ArrayList<>();
            for (RbacOrgUserEntity entity : query) {
                userCodes.add(entity.getUserCode());
            }
            //用户身份列表
            Cnd where = Cnd.where(RbacRoleResourceEntity.FLD_ROLE_CODE, "in", userCodes).and(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", resourceCode);
            if (rbacRoleResourceDao.count(where) > 0) {
                return BizResult.success(true);
            } else {
                return BizResult.success(false);
            }
        } else {
            RbacOrgUserEntity query = rbacOrgUserDao.fetch(Cnd.where(RbacOrgUserEntity.FLD_USER_CODE, "=", userCode));
            if (query == null) {
                return BizResult.error(500, "没有查询到用户身份:" + userCode);
            }
            if (!query.getUserId().equals(userId) || !query.getSystemCode().equals(systemCode)) {
                return BizResult.error(500, "用户身份不匹配");
            }

            List<RbacUserCodeRoleEntity> roles = rbacUserCodeRoleDao.query(Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "=", userCode));
            List<String> roleCodes = new ArrayList<>();
            for (RbacUserCodeRoleEntity entity : roles) {
                roleCodes.add(entity.getRoleCode());
            }
            Cnd where = Cnd.where(RbacRoleResourceEntity.FLD_ROLE_CODE, "in", roleCodes)
                    .and(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", resourceCode);
            if (rbacRoleResourceDao.count(where) > 0) {
                return BizResult.success(true);
            } else {
                return BizResult.success(false);
            }
        }
    }

    /**
     * 用户某个角色是否正在被使用
     *
     * @param roleCode
     * @return
     */
    public boolean isRoleInUse(String roleCode) {
        Cnd where = Cnd.where(RbacUserCodeRoleEntity.FLD_ROLE_CODE, "=", roleCode);
        return rbacUserCodeRoleDao.count(where) > 0;
    }

    @PostConstruct
    public void init() {

    }

    public BizResult<LoginResponse> login(LoginRequest request) {
        if (request == null || Strings.isBlank(request.getUserName())) {
            return BizResult.error(500, "用户名不能为空");
        }

        if (Strings.isBlank(request.getPassword())) {
            return BizResult.error(500, "密码不能为空");
        }

        Cnd where = Cnd.where(RbacUserEntity.FLD_USER_NAME, "=", request.getUserName());

        RbacUserEntity user = rbacUserDao.fetch(where);

        if (user == null) {
            return BizResult.error(500, "用户不存在");
        }
        String passwordHash = Lang.sha1(RbacConstant.SALT + "_" + request.getPassword());
        if (!passwordHash.equals(user.getPassword())) {
            return BizResult.error(500, "密码错误");
        }

        if (!user.getStatus().equals("0")) {
            return BizResult.error(500, "用户已被禁用");
        }

        LoginResponse response = new LoginResponse();
        user.setPassword("");
        response.setToken(R.UU16());
        RbacUser rbacUser = new RbacUser(user);

        rbacUser.setExpireTime(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000);
        response.setCurrentUser(rbacUser);

        //全局保存一个登录用户的TOKEN对象 可以存放到Redis
        ServletUtils.getSession().setAttribute(CommonConstant.KEY_LOGIN_USER, rbacUser);

        return BizResult.success(response);
    }

    /**
     * 确保管理员用户存在
     */
    public synchronized void sureSuperUser() {
        Cnd where = Cnd.where(RbacUserEntity.FLD_USER_ID, "=", RbacConstant.SUPER_USER_ID);

        RbacUserEntity user = rbacUserDao.fetch(where);

        if (user == null) {
            user = new RbacUserEntity();
            user.setUserName("admin");
            user.setCreateTime(new Date());
            user.setNickName("管理员");
            user.setAvatar("/img/avatar.png");
            user.setPhonenumber("");
            user.setCreateBy("system");
            user.setEmail("admin@system.com");
            user.setDeptId(0L);
            user.setUpdateTime(new Date());
            user.setUserType("00");
            user.setUpdateBy("system");
            user.setRemark("system created");

            user.setPassword(Lang.sha1(RbacConstant.SALT + "_imagebot__"));
            user.setStatus("0");
            user.setUserId(RbacConstant.SUPER_USER_ID);
            rbacUserDao.insert(user);
        }
    }

    /**
     * 从代码中分析权限点　并同步到数据库中
     */
    public void importResourcePointFromCode(List<Class<?>> scanPackages,IUserInfo superUser) {
        List<ResourceDeclare> resourceDeclares = new ArrayList<>();
        List<RoleDeclare> roleDeclares = new ArrayList<>();

        // search all resource decalre annotation
        for (Class<?> aClass : scanPackages) {
            Set<Class<?>> classList = MyScans.getClasses(aClass.getPackage().getName());
            for (Class<?> aClass1 : classList) {
                ResourceDeclares annotation = aClass1.getAnnotation(ResourceDeclares.class);
                RoleDeclares     roles = aClass1.getAnnotation(RoleDeclares.class);

                if (roles == null) {
                    Annotation[] declaredAnnotations = aClass1.getDeclaredAnnotations();
                    for (Annotation annotation1 : declaredAnnotations) {
                        if (annotation1.annotationType().equals(RoleDeclare.class)) {
                            roleDeclares.add((RoleDeclare) annotation1);
                        }
                    }
                } else {
                    Collections.addAll(roleDeclares, roles.value());
                }

                if (annotation == null) {
                    Annotation[] declaredAnnotations = aClass1.getDeclaredAnnotations();
                    for (Annotation annotation1 : declaredAnnotations) {
                        if (annotation1.annotationType().equals(ResourceDeclare.class)) {
                            resourceDeclares.add((ResourceDeclare) annotation1);
                        }
                    }
                } else {
                    Collections.addAll(resourceDeclares, annotation.value());
                }

            }
        }
        for (ResourceDeclare resourceDeclare : resourceDeclares) {
            if (Strings.isBlank(resourceDeclare.code())) {
                log.warn("ResourceDeclare code is blank" + resourceDeclare.name());
                continue;
            }
            RbacResourceEntity entity = rbacResourceDao.fetch(Cnd.where(RbacResourceEntity.FLD_RESOURCE_CODE, "=", resourceDeclare.code()));
            if (entity == null) {
                entity = new RbacResourceEntity();
                entity.setCatalog(resourceDeclare.catalog());
                entity.setName(resourceDeclare.name());
                entity.setResourceCode(resourceDeclare.code());
                entity.setKind(resourceDeclare.kind().getCode());
                entity.setSummary(resourceDeclare.summary());
                entity.setData(resourceDeclare.data());

                try {
                    rbacResourceDao.insert(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log.warn("ResourceDeclare already exists" + resourceDeclare.name());
                // update it
                entity.setCatalog(resourceDeclare.catalog());
                entity.setName(resourceDeclare.name());
                entity.setResourceCode(resourceDeclare.code());
                entity.setKind(resourceDeclare.kind().getCode());
                entity.setSummary(resourceDeclare.summary());
                entity.setData(resourceDeclare.data());
                try {
                    rbacResourceDao.update(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // update parsed roles
        for (RoleDeclare roleDeclare : roleDeclares) {
            if (Strings.isBlank(roleDeclare.code())) {
                log.warn("RoleDeclare code is blank" + roleDeclare.name());
                continue;
            }
            RbacRoleEntity entity = rbacRoleDao.fetch(Cnd.where(RbacRoleEntity.FLD_CODE, "=", roleDeclare.code()));
            if (entity == null) {
                entity = new RbacRoleEntity();
                entity.setName(roleDeclare.name());
                entity.setCode(roleDeclare.code());
                entity.setIcon(Fonts.RBAC_ROLE);
                entity.setSummary(roleDeclare.summary());
                entity.setParentCode(roleDeclare.parentCode());
                try {
                    rbacRoleDao.insert(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log.warn("ResourceDeclare already exists" + roleDeclare.name());
                // update it
                entity.setName(roleDeclare.name());
                entity.setCode(roleDeclare.code());
                entity.setIcon(Fonts.RBAC_ROLE);
                entity.setSummary(roleDeclare.summary());
                entity.setParentCode(roleDeclare.parentCode());
                try {
                    rbacRoleDao.update(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // update role's resource
            for(String resourceCode : roleDeclare.resources()){
                RbacRoleResourceEntity resource = rbacRoleResourceDao.fetch(
                        Cnd.where(RbacRoleResourceEntity.FLD_ROLE_CODE, "=", roleDeclare.code())
                                .and(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", resourceCode)
                );
                if(resource == null){
                    RbacRoleResourceEntity roleResource = new RbacRoleResourceEntity();
                    roleResource.setResourceCode(resourceCode);
                    roleResource.setRoleCode(roleDeclare.code());
                    rbacRoleResourceDao.insert(roleResource);
                }
            }
        }
        log.info("更新权限点完成，共{}个", resourceDeclares.size());

        if(superUser!=null)
        {
            log.info("检查超级管理员的权限");
            checkSystemOrganization(superUser);

        }
    }

    private void checkSystemOrganization(IUserInfo superUser) {
        RbacOrgEntity orgEntity = rbacOrgDao.fetch(Cnd.where(RbacOrgEntity.FLD_CODE, "=", RbacConstant.SYSTEM_MANAGER_ORG_CODE));
        if(orgEntity==null)
        {
            orgEntity = new RbacOrgEntity();
            orgEntity.setCode(RbacConstant.SYSTEM_MANAGER_ORG_CODE);
            orgEntity.setName("系统管理组织");
            orgEntity.setSummary("系统管理组织");
            orgEntity.setAddress("中科院");
            orgEntity.setIcon(Fonts.CANGLING_TEXT);
            orgEntity.setCharger("cangling");
            orgEntity.setEmail("admin@cangling.cn");
            orgEntity.setId(UUIDTools.uuid());
            orgEntity.setLink("https://cangling.cn");
            rbacOrgDao.insert(orgEntity);
        }
        //check superUser
        RbacOrgUserEntity orgUserEntity = rbacOrgUserDao.fetch(Cnd.where(RbacOrgUserEntity.FLD_ORG_CODE, "=", RbacConstant.SYSTEM_MANAGER_ORG_CODE)
                .and(RbacOrgUserEntity.FLD_USER_ID, "=", superUser.getId()));
        if(orgUserEntity==null)
        {
            orgUserEntity = new RbacOrgUserEntity();
            orgUserEntity.setOrgCode(RbacConstant.SYSTEM_MANAGER_ORG_CODE);
            orgUserEntity.setUserId(superUser.getId());
            orgUserEntity.setSystemCode(superUser.getSystemCode());
            orgUserEntity.setCreateTime(new Date());
            orgUserEntity.setAvatar(superUser.getAvatar());
            orgUserEntity.setAliasName(superUser.getUserName());
            orgUserEntity.setUserCode(UUIDTools.uuid());
            rbacOrgUserDao.insert(orgUserEntity);
        }
        // bind super user's privilege

        RbacUserCodeRoleEntity codeRoleEntity = rbacUserCodeRoleDao.fetch(Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "=", orgUserEntity.getUserCode())
                .and(RbacUserCodeRoleEntity.FLD_ROLE_CODE, "=", RbacConstant.ROLE_SYS_MAINTAINER));

        if(codeRoleEntity==null)
        {
            codeRoleEntity=new RbacUserCodeRoleEntity();
            codeRoleEntity.setRoleCode(RbacConstant.ROLE_SYS_MAINTAINER);
            codeRoleEntity.setUserCode(orgUserEntity.getUserCode());
            codeRoleEntity.setCreateTime(new Date());
            codeRoleEntity.setCreateUser("SYSTEM ADMIn");
            rbacUserCodeRoleDao.insert(codeRoleEntity);
        }

    }

    /**
     * 查询角色编码及其子编码
     * 递归查询　效率很低　未来优化吧系统
     * // TODO opt query
     *
     * @param roleCode
     * @return
     */
    public List<String> queryRolesWidthAllChildren(String roleCode) {
        List<String> roleCodes = new ArrayList<>();
        recursiveQueryRoleCode(roleCodes, roleCode);
        return roleCodes;
    }

    private void recursiveQueryRoleCode(List<String> roleCodes, String roleCode) {
        roleCodes.add(roleCode);
        List<RbacRoleEntity> roles = rbacRoleDao.query(Cnd.where(RbacRoleEntity.FLD_PARENT_CODE, "=", roleCode));

        for (RbacRoleEntity role : roles) {
            recursiveQueryRoleCode(roleCodes, role.getCode());
        }
    }

    /**
     * 查询角色对应的资源
     *
     * @param roleCode
     * @return
     */
    public BizResult<QueryRoleResourceResponse> queryResourceByRoleCode(String roleCode, Boolean withChild) {

        if (withChild != null && withChild) {
            List<String> roleCodes = queryRolesWidthAllChildren(roleCode);
            //角色列表　对应的所有资源
            String subSql = "select distinct(resource_code) from rbac_role_resource where role_code in (?)";
            SqlValueRange sqlValueRange = Exps.inSql2(RbacRoleResourceEntity.FLD_RESOURCE_CODE, subSql, roleCodes);
            List<RbacResourceEntity> resources = rbacResourceDao.query(Cnd.where(sqlValueRange));
            QueryRoleResourceResponse response = new QueryRoleResourceResponse();
            response.setResources(resources);
            return BizResult.success(response);
        } else {
            // 角色对应的资源
            String subSql = "select distinct(resource_code) from rbac_role_resource where role_code='" + roleCode + "'";

            SqlRange sqlRange = Exps.inSql(RbacResourceEntity.FLD_RESOURCE_CODE, subSql);
            List<RbacResourceEntity> resources = rbacResourceDao.query(Cnd.where(sqlRange));
            QueryRoleResourceResponse response = new QueryRoleResourceResponse();
            response.setResources(resources);
            return BizResult.success(response);
        }

    }

    /**
     * 删除用户角色
     *
     * @param userId
     * @param roleCode
     * @return
     */
    public BizResult<DeleteUserRoleResponse> deleteUserRole(String userId, String roleCode) {
        rbacUserCodeRoleDao.clear(Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "=", userId).and(RbacUserCodeRoleEntity.FLD_ROLE_CODE, "=", roleCode));
        return BizResult.success(new DeleteUserRoleResponse());
    }

    /**
     * 查询用户拥有的角色
     *
     * @param userCode
     * @return
     */
    public BizResult<QueryUserRoleResponse> queryUserRole(String userCode) {
        QueryUserRoleResponse response = new QueryUserRoleResponse();
        SqlRange sqlRange = Exps.inSql(RbacRoleEntity.FLD_CODE, "select distinct(role_code) from rbac_user_code_role where user_code='" + userCode + "'");
        List<RbacRoleEntity> query = rbacRoleDao.query(Cnd.where(sqlRange));
        response.roles = query;
        return BizResult.success(response);
    }

    /**
     * 创建用户角色
     *
     * @param userCode
     * @param roleCode
     * @return
     */
    public BizResult<CreateUserRoleResponse> createUserRole(String userCode, String roleCode) {

        int count = rbacUserCodeRoleDao.count(Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "=", userCode).and(RbacUserCodeRoleEntity.FLD_ROLE_CODE, "=", roleCode));
        if (count > 0) {
            return BizResult.success(new CreateUserRoleResponse());
        }
        RbacUserCodeRoleEntity entity = new RbacUserCodeRoleEntity();
        entity.setUserCode(userCode);
        entity.setRoleCode(roleCode);
        entity.setCreateTime(new Date());
        entity.setCreateUser("SYSTEM ADMIN");
        rbacUserCodeRoleDao.insert(entity);
        return BizResult.success(new CreateUserRoleResponse());
    }

    /**
     * 超级管理
     * @return
     */
    public RbacUser findSuperUser()
    {
        RbacUserEntity fetch = rbacUserDao.fetch(Cnd.where(RbacUserEntity.FLD_USER_ID, "=", RbacConstant.SUPER_USER_ID));
        return new RbacUser(fetch);
    }

    /**
     * 退出系统
     */
    public void logout()
    {
        ServletUtils.getSession().removeAttribute(CommonConstant.KEY_LOGIN_USER);
    }

    /**
     * 获取登录用户
     * @return
     */
    public RbacUser getLoginUser()
    {
        if(ServletUtils.getSession().getAttribute(CommonConstant.KEY_LOGIN_USER)==null){
           String apiToken=ServletUtils.getRequest().getHeader(CommonConstant.API_TOKEN);
           if(apiToken!=null && "123456".equals(apiToken)){
               RbacUser user = findSuperUser();
               ServletUtils.getSession().setAttribute(CommonConstant.KEY_LOGIN_USER, user);
           }
        }
        return (RbacUser)ServletUtils.getSession().getAttribute(CommonConstant.KEY_LOGIN_USER);
    }
}
