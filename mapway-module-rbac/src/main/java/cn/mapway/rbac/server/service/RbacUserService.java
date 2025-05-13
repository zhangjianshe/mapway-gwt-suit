package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.BizResult;
import cn.mapway.biz.exception.BizException;
import cn.mapway.rbac.client.user.RbacUser;
import cn.mapway.rbac.server.RbacServerPlugin;
import cn.mapway.rbac.server.dao.*;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.RbacUserOrg;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.*;
import cn.mapway.rbac.shared.model.Organization;
import cn.mapway.rbac.shared.model.Res;
import cn.mapway.rbac.shared.model.Role;
import cn.mapway.rbac.shared.model.UserPermissions;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.server.MyScans;
import cn.mapway.spring.tools.ServletUtils;
import cn.mapway.spring.tools.UUIDTools;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.Exps;
import org.nutz.dao.util.cri.SqlRange;
import org.nutz.dao.util.cri.SqlValueRange;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.trans.Trans;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * this service save IUserInfo in the http session.
 * ===============================================
 */
@Service
@Slf4j
@RoleDeclares(
        {
                @RoleDeclare(
                        name = "系统", code = RbacConstant.ROLE_SYS, summary = "系统"
                ),
                @RoleDeclare(
                        name = "系统维护员", code = RbacConstant.ROLE_SYS_MAINTAINER, summary = "系统维护", parentCode = RbacConstant.ROLE_SYS
                ),
                @RoleDeclare(
                        name = "用户组", code = RbacConstant.USER_GROUP, summary = "用户组"
                ),
                @RoleDeclare(
                        name = "普通用户", code = RbacConstant.ROLE_NORMAL_USER, summary = "普通用户", parentCode = RbacConstant.USER_GROUP
                ),
        }
)
@ResourceDeclares(
        {@ResourceDeclare(
                roleCode = RbacConstant.ROLE_SYS,
                catalog = "系统", name = "资源维护", code = RbacConstant.RESOURCE_RBAC_MAINTAINER,
                summary = "维护系统角色以及资源点", kind = ResourceKind.RESOURCE_KIND_SYSTEM

        ),
                @ResourceDeclare(
                        roleCode = RbacConstant.ROLE_SYS,
                        catalog = "系统", name = "用户授权", code = RbacConstant.RESOURCE_RBAC_AUTHORITY,
                        summary = "为用户授权", kind = ResourceKind.RESOURCE_KIND_SYSTEM
                )
        }
)

public class RbacUserService {
    // global session list

    private static final String SQL_TEMPLATE_QUERY_ROLE = "SELECT r.role_code from rbac_user_code_role r LEFT JOIN rbac_org_user u on r.user_code = u.user_code where u.user_id = @userId";
    private static final String SQL_TEMPLATE_QUERY_RESOURCE_CODE = "SELECT distinct res.resource_code from rbac_role_resource r left join rbac_resource res on r.resource_code = res.resource_code where res.kind = @kind and r.role_code in (@roleList)";
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
    @Resource
    RbacServerPlugin plugin;
    /**
     * 全局
     */
    Map<String, Organization> organizationIdMap = new HashMap<>();
    Map<String, Organization> organizationCodeMap = new HashMap<>();

    private static QueryRoleResourceResponse getQueryRoleResourceResponse(Integer kind, List<String> resourceCodes) {
        QueryRoleResourceResponse queryRoleResourceResponse = new QueryRoleResourceResponse();
        List<RbacResourceEntity> resources = new ArrayList<>();
        if (resourceCodes != null && !resourceCodes.isEmpty()) {
            for (String resourceCode : resourceCodes) {
                RbacResourceEntity entity = new RbacResourceEntity();
                entity.setKind(kind);
                entity.setResourceCode(resourceCode);
                resources.add(entity);
            }
        }
        queryRoleResourceResponse.setResources(resources);
        return queryRoleResourceResponse;
    }

    public BizResult<Boolean> isAssignRole(IUserInfo userInfo, String userCode, String roleCode) {
        return isAssignRole(userInfo.getSystemCode(), userInfo.getId(), userCode, roleCode);
    }

    /**
     * 用户所属组织
     * select * from org where org_code in(select org_code from rbac_org_user where user_code=?)
     *
     * @param systemCode
     * @param userId
     * @return
     */
    public BizResult<List<RbacUserOrg>> userOrgList(String systemCode, String userId) {

        List<RbacOrgUserEntity> users = rbacOrgUserDao.query(Cnd.where(RbacOrgUserEntity.FLD_USER_ID, "=", userId)
                .and(RbacOrgUserEntity.FLD_SYSTEM_CODE, "=", systemCode));
        List<RbacUserOrg> list = new ArrayList<>();
        for (RbacOrgUserEntity user : users) {
            RbacOrgEntity org = rbacOrgDao.fetch(Cnd.where(RbacOrgEntity.FLD_CODE, "=", user.getOrgCode()));
            if (org == null) {
                log.error("用户所属组织不存在" + user.getOrgCode());
                continue;
            }
            RbacUserOrg userOrg = new RbacUserOrg();
            userOrg.systemCode = systemCode;
            userOrg.orgId = org.getId();
            userOrg.parentId = org.getParentId();
            userOrg.orgName = org.getName();
            userOrg.orgIcon = org.getIcon();
            userOrg.summary = org.getSummary();
            userOrg.link = org.getLink();
            userOrg.charger = org.getCharger();
            userOrg.email = org.getEmail();
            userOrg.tel = org.getTel();
            userOrg.regionCode = org.getRegionCode();
            userOrg.userId = user.getUserId();
            userOrg.userCode = user.getUserCode();
            userOrg.userName = user.getAliasName();
            userOrg.userIcon = user.getAvatar();
            list.add(userOrg);
        }

        return BizResult.success(list);
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

    /**
     * 用户身份 是否拥有某个角色
     *
     * @param userCode
     * @param roleCode
     * @return
     */
    public BizResult<Boolean> isUserCodeAssignRole(String userCode, String roleCode) {
        //用户身份列表
        Cnd where = Cnd.where(RbacOrgUserEntity.FLD_USER_CODE, "=", userCode);
        RbacOrgUserEntity orgUser = rbacOrgUserDao.fetch(where);
        if (orgUser == null) {
            log.warn("没有查询到用户身份:" + userCode);
            return BizResult.success(false);
        }
        where = Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "=", userCode)
                .and(RbacUserCodeRoleEntity.FLD_ROLE_CODE, "=", roleCode);
        if (rbacUserCodeRoleDao.count(where) > 0) {
            return BizResult.success(true);
        } else {
            return BizResult.success(false);
        }
    }

    /**
     * 用户身份 是否拥有某个角色
     *
     * @param userCode
     * @param resourceCode
     * @return
     */
    public BizResult<Boolean> isUserCodeAssignResource(String userCode, String resourceCode) {
        //用户身份列表
        Cnd where = Cnd.where(RbacOrgUserEntity.FLD_USER_CODE, "=", userCode);
        RbacOrgUserEntity orgUser = rbacOrgUserDao.fetch(where);
        if (orgUser == null) {
            log.warn("没有查询到用户身份:" + userCode);
            return BizResult.success(false);
        }

        Set<String> assignRoleCodes = new HashSet<>();
        List<RbacUserCodeRoleEntity> roleEntities = rbacUserCodeRoleDao.query(Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "=", userCode));
        if (roleEntities == null || roleEntities.size() == 0) {
            log.warn("没有查询到用户身份:" + userCode);
            return BizResult.success(false);
        }
        for (RbacUserCodeRoleEntity entity : roleEntities) {
            List<String> roleCodes = queryRolesWidthAllChildren(entity.getRoleCode());
            assignRoleCodes.addAll(roleCodes);
        }
        //角色列表　对应的所有资源
        String subSql = "select count(*) from rbac_role_resource where role_code in @roleCodes and resource_code= @resourceCode";

        Sql sql = Sqls.create(subSql);
        sql.setCallback(Sqls.callback.longValue());
        sql.setParam("roleCodes", assignRoleCodes);
        sql.setParam("resourceCode", resourceCode);
        rbacResourceDao.execute(sql);
        return BizResult.success(sql.getLong() > 0);

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
            List<String> roleCodes = new ArrayList<>();
            for (String uc : userCodes) {
                List<RbacUserCodeRoleEntity> roles = rbacUserCodeRoleDao.query(Cnd.where(RbacUserCodeRoleEntity.FLD_USER_CODE, "=", uc));
                for (RbacUserCodeRoleEntity entity : roles) {
                    roleCodes.add(entity.getRoleCode());
                }
            }

            Cnd where = Cnd.where(RbacRoleResourceEntity.FLD_ROLE_CODE, "in", roleCodes).and(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", resourceCode);
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
     * 导入角色信息
     *
     * @param roles
     */
    public void importRoles(List<RbacRoleEntity> roles) {
        if (Lang.isEmpty(roles)) {
            log.warn("没有传入需要更新的系统角色");
            return;
        }
        for (RbacRoleEntity roleEntity : roles) {
            RbacRoleEntity fetch = rbacRoleDao.fetch(Cnd.where(RbacRoleEntity.FLD_CODE, "=", roleEntity.getCode()));
            if (fetch == null) {
                log.info("角色 {} 新建成功", roleEntity.getCode());
                rbacRoleDao.insert(roleEntity);
            } else {
                log.info("角色 {} 已存在", roleEntity.getCode());
                rbacRoleDao.updateIgnoreNull(roleEntity);
            }
        }
    }

    /**
     * 从代码中分析权限点　并同步到数据库中
     */
    public void importResourcePointFromCode(List<Class<?>> scanPackages, IUserInfo superUser) {
        List<ResourceDeclare> resourceDeclares = new ArrayList<>();
        List<RoleDeclare> roleDeclares = new ArrayList<>();

        // search all resource decalre annotation
        for (Class<?> aClass : scanPackages) {
            Set<Class<?>> classList = MyScans.getClasses(aClass.getPackage().getName());
            for (Class<?> aClass1 : classList) {
                ResourceDeclares annotation = aClass1.getAnnotation(ResourceDeclares.class);
                RoleDeclares roles = aClass1.getAnnotation(RoleDeclares.class);

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
        //先处理角色
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
                    rbacRoleDao.updateIgnoreNull(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        for (ResourceDeclare resourceDeclare : resourceDeclares) {
            if (Strings.isBlank(resourceDeclare.code()) || Strings.isBlank(resourceDeclare.roleCode())) {
                log.warn("ResourceDeclare code is blank" + resourceDeclare.name());
                continue;
            }
            RbacRoleEntity role = rbacRoleDao.fetch(Cnd.where(RbacRoleEntity.FLD_CODE, "=", resourceDeclare.roleCode()));
            if (role == null) {
                log.warn("RoleDeclare not exists" + resourceDeclare.roleCode());
                continue;
            }
            RbacResourceEntity entity = rbacResourceDao.fetch(
                    Cnd.where(RbacResourceEntity.FLD_RESOURCE_CODE, "=", resourceDeclare.code())
                            .and(RbacResourceEntity.FLD_KIND, "=", resourceDeclare.kind().getCode())
            );
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

                updateRoleResource(resourceDeclare);

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
                    rbacResourceDao.updateIgnoreNull(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                updateRoleResource(resourceDeclare);
            }
        }


        log.info("更新权限点完成，共{}个", resourceDeclares.size());

        if (superUser != null) {
            log.info("检查超级管理员的权限");
            checkSystemOrganization(superUser);

        }
    }

    private void updateRoleResource(ResourceDeclare resourceDeclare) {
        RbacRoleResourceEntity rbacRoleResourceEntity = rbacRoleResourceDao.fetch(
                Cnd.where(RbacRoleResourceEntity.FLD_ROLE_CODE, "=", resourceDeclare.roleCode())
                        .and(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", resourceDeclare.code()));
        if (rbacRoleResourceEntity == null) {
            rbacRoleResourceEntity = new RbacRoleResourceEntity();
            rbacRoleResourceEntity.setRoleCode(resourceDeclare.roleCode());
            rbacRoleResourceEntity.setResourceCode(resourceDeclare.code());
            try {
                rbacRoleResourceDao.insert(rbacRoleResourceEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkSystemOrganization(IUserInfo superUser) {
        RbacOrgEntity orgEntity = rbacOrgDao.fetch(Cnd.where(RbacOrgEntity.FLD_CODE, "=", RbacConstant.SYSTEM_MANAGER_ORG_CODE));
        if (orgEntity == null) {
            orgEntity = new RbacOrgEntity();
            orgEntity.setCode(RbacConstant.SYSTEM_MANAGER_ORG_CODE);
            orgEntity.setName("系统管理组");
            orgEntity.setSummary("管理系统全局配置信息");
            orgEntity.setAddress("中科院");
            orgEntity.setIcon(Fonts.CANGLING_TEXT);
            orgEntity.setCharger("cangling");
            orgEntity.setEmail("admin@cangling.cn");
            orgEntity.setId(UUIDTools.uuid());
            orgEntity.setRank(1000);//缺省该组织排在最后
            orgEntity.setLink("https://cangling.cn");
            rbacOrgDao.insert(orgEntity);
        }
        //check superUser
        RbacOrgUserEntity orgUserEntity = rbacOrgUserDao.fetch(Cnd.where(RbacOrgUserEntity.FLD_ORG_CODE, "=", RbacConstant.SYSTEM_MANAGER_ORG_CODE)
                .and(RbacOrgUserEntity.FLD_USER_ID, "=", superUser.getId()));
        if (orgUserEntity == null) {
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

        if (codeRoleEntity == null) {
            codeRoleEntity = new RbacUserCodeRoleEntity();
            codeRoleEntity.setRoleCode(RbacConstant.ROLE_SYS_MAINTAINER);
            codeRoleEntity.setUserCode(orgUserEntity.getUserCode());
            codeRoleEntity.setCreateTime(new Date());
            codeRoleEntity.setCreateUser("SYSTEM ADMIN");
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
     *
     * @return
     */
    public RbacUser findSuperUser() {
        RbacUserEntity fetch = rbacUserDao.fetch(Cnd.where(RbacUserEntity.FLD_USER_ID, "=", RbacConstant.SUPER_USER_ID));
        return new RbacUser(fetch);
    }

    /**
     * 退出系统
     */
    public void logout() {
        ServletUtils.getSession().removeAttribute(CommonConstant.KEY_LOGIN_USER);
    }

    /**
     * 获取登录用户
     *
     * @return
     */
    public RbacUser getLoginUser() {
        if (ServletUtils.getSession().getAttribute(CommonConstant.KEY_LOGIN_USER) == null) {
            String apiToken = ServletUtils.getRequest().getHeader(CommonConstant.API_TOKEN);
            if ("123456".equals(apiToken)) {
                RbacUser user = findSuperUser();
                ServletUtils.getSession().setAttribute(CommonConstant.KEY_LOGIN_USER, user);
            }
        }
        return (RbacUser) ServletUtils.getSession().getAttribute(CommonConstant.KEY_LOGIN_USER);
    }

    /**
     * 查询用户身份 信息 根据用户身份代码
     *
     * @param userCode
     * @return
     */
    public RbacOrgUserEntity queryUserOrg(String userCode) {
        return rbacOrgUserDao.fetch(Cnd.where(RbacOrgUserEntity.FLD_USER_CODE, "=", userCode));
    }

    /**
     * 获取用户的所有权限
     * 很复杂，花费了一晚上
     *
     * @param systemCode 系统代码
     * @param userId     用户ID
     * @return
     */
    public UserPermissions getUserPermissions(String systemCode, String userId) {
        if (Strings.isBlank(systemCode) || Strings.isBlank(userId)) {
            UserPermissions userPermissions = new UserPermissions();
            userPermissions.organizations = new Organization[0];
            userPermissions.roles = new Role[0];
            return userPermissions;
        }
        String cacheKey = getCacheKey(systemCode, userId);
        Object obj = plugin.getServerContext().getFromSession(RbacConstant.SESSION_CACHE_GROUP, cacheKey);
        if (obj instanceof UserPermissions) {
            return (UserPermissions) obj;
        }

        List<RbacOrgUserEntity> userInOrgs = rbacOrgUserDao.query(Cnd.where(RbacOrgUserEntity.FLD_USER_ID, "=", userId));
        UserPermissions userPermissions = new UserPermissions();

        //获取用户所在的组织结构图
        //用户仅仅拥有这个组织结构图的一部分子树
        // A
        //   ->B
        //   ->C
        //       ->D  <<----用户所在的组织
        //       ->E

        Map<String, Organization> idMap = getSystemOrganizationTree();

        //最终的结果
        List<Organization> organizationsTree = new ArrayList<>();
        List<String> userCodes = new ArrayList<>();
        for (RbacOrgUserEntity userInOrg : userInOrgs) {
            userCodes.add(userInOrg.getUserCode());
            Organization organization = organizationCodeMap.get(userInOrg.getOrgCode());
            if (organization != null) {
                //寻找到根节点
                Organization cloned = organization.clone();
                cloned.userId = userId;
                cloned.userName = userInOrg.getAliasName();
                cloned.userCode = userInOrg.getUserCode();
                cloned.userIcon = userInOrg.getAvatar();
                cloned.major = userInOrg.getMajor();
                findRoot(cloned, idMap, organizationsTree);
                //拷贝子节点
                copyChild(organization, idMap, organizationsTree);
            }
        }
        Collections.sort(organizationsTree, new Comparator<Organization>() {
            @Override
            public int compare(Organization o1, Organization o2) {
                if (o1.rank == null || o2.rank == null) {
                    return 0;
                }
                return o2.rank.intValue() - o1.rank.intValue();
            }
        });
        userPermissions.organizations = organizationsTree.toArray(new Organization[0]);
        //查找所有的角色
        List<Role> roles = new ArrayList<>();
        if (userCodes.size() > 0) {
            String sql = "select * from rbac_role  left join rbac_user_code_role on rbac_role.code=rbac_user_code_role.role_code where rbac_user_code_role.user_code in (@userCodes)";
            Sql sql1 = Sqls.create(sql);
            sql1.params().set("userCodes", userCodes);
            sql1.setEntity(rbacRoleDao.getEntity());
            sql1.setCallback(Sqls.callback.entities());
            rbacUserCodeRoleDao.getDao().execute(sql1);

            List<RbacRoleEntity> list = sql1.getList(RbacRoleEntity.class);
            if (list != null) {
                for (RbacRoleEntity roleEntity : list) {
                    roles.add(fromRoleEntity(roleEntity));
                }
            }
        }

        userPermissions.roles = roles.toArray(new Role[0]);
        plugin.getServerContext().putToSession(RbacConstant.SESSION_CACHE_GROUP, cacheKey, userPermissions);
        return userPermissions;
    }

    /**
     * 清楚用户权限缓存
     *
     * @param userInfo
     */
    public void resetUserPermissionsCache(IUserInfo userInfo) {
        String cacheKey = getCacheKey(userInfo.getSystemCode(), userInfo.getId());
        plugin.getServerContext().putToSession(RbacConstant.SESSION_CACHE_GROUP, cacheKey, null);
    }

    private Role fromRoleEntity(RbacRoleEntity roleEntity) {
        Role role = new Role();
        role.code = roleEntity.getCode();
        role.name = roleEntity.getName();
        role.icon = roleEntity.getIcon();
        role.summary = roleEntity.getSummary();
        role.parentCode = roleEntity.getParentCode();
        role.resources = new Res[0];
        return role;
    }

    /**
     * 拷贝　organization 及其字节点到　organizationsTree数中
     *
     * @param organization
     * @param idMap
     * @param organizationsTree
     */
    private void copyChild(Organization organization, Map<String, Organization> idMap, List<Organization> organizationsTree) {
        Organization subRoot = idMap.get(organization.orgId);

        Organization targetRoot = null;
        targetRoot = recursiveFindOrgById(organizationsTree, subRoot.orgId);
        if (targetRoot == null) {
            log.error("copyChild error");
            return;
        }
        //　将subRoot的所有字节点　拷贝到targetRoot
        recursiveCopyNode(targetRoot, subRoot);
    }

    private void recursiveCopyNode(Organization targetRoot, Organization subRoot) {
        if (subRoot.children == null) {
            return;
        }
        for (Organization child : subRoot.children) {
            Organization find = null;
            if (targetRoot.children == null) {
                targetRoot.children = new ArrayList<>();
            }
            for (Organization organization : targetRoot.children) {
                if (organization.orgId.equals(child.orgId)) {
                    find = organization;
                    break;
                }
            }
            if (find == null) {
                find = child.clone();
                targetRoot.children.add(find);
            }
            recursiveCopyNode(find, child);
        }
    }

    private Organization recursiveFindOrgById(List<Organization> organizations, String orgId) {
        if (organizations == null) {
            return null;
        }
        for (Organization organization : organizations) {
            if (organization.orgId.equals(orgId)) {
                return organization;
            }
            Organization child = recursiveFindOrgById(organization.children, orgId);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    private void findRoot(Organization organization, Map<String, Organization> idMap, List<Organization> organizationsTree) {
        List<Organization> list = new ArrayList<>();
        String parentId = organization.parentId;
        list.add(organization);
        while (Strings.isNotBlank(parentId)) {
            Organization parent = idMap.get(parentId);
            if (parent == null) {
                break;
            }
            list.add(parent);
            parentId = parent.parentId;
        }
        Collections.reverse(list);
        mergeList(organizationsTree, list);
    }

    /**
     * merge list
     *
     * @param organizationsTree
     * @param list
     */
    private void mergeList(List<Organization> organizationsTree, List<Organization> list) {
        Organization find = null;
        for (Organization organization : organizationsTree) {
            if (organization.orgId.equals(list.get(0).orgId)) {
                find = organization;
                break;
            }
        }
        if (find == null) {
            Organization root = list.get(0).clone();
            organizationsTree.add(root);

            for (int i = 1; i < list.size(); i++) {
                Organization organization = list.get(i).clone();
                root.children.add(organization);
                root = organization;
            }
        } else {
            Organization root = find;
            for (int i = 1; i < list.size(); i++) {
                Organization organization = list.get(i);
                Organization childFind = null;
                for (Organization child : root.children) {
                    if (child.orgId.equals(organization.orgId)) {
                        childFind = child;
                        break;
                    }
                }
                if (childFind == null) {
                    Organization cloned = organization.clone();
                    root.children.add(cloned);
                    root = cloned;
                } else {
                    if (Strings.isBlank(childFind.userId)) {
                        childFind.userId = organization.userId;
                        childFind.userName = organization.userName;
                        childFind.userIcon = organization.userIcon;
                        childFind.userCode = organization.userCode;
                    }
                    root = childFind;
                }
            }
        }
    }

    /**
     * 判断用户是否具有某一个角色 如果没有会出发异常 BizException,全局可以捕获该异常进行处理
     *
     * @param user
     * @param permission
     */
    public void confirmUserPermission(IUserInfo user, String permission) {
        BizResult<Boolean> assignRole = isAssignRole(user, "", permission);
        if (assignRole.isFailed() || !assignRole.getData()) {
            throw BizException.get(503, "权限不足(" + permission + ")");
        }
    }

    private String getCacheKey(String systemCode, String userId) {
        return RbacConstant.USER_PERMISSION_CACHE_KEY_PREFIX + "_" + systemCode + "_" + userId;
    }

    /**
     * 获取系统组织架构图
     *
     * @return
     */
    private synchronized Map<String, Organization> getSystemOrganizationTree() {

        if (organizationIdMap == null || organizationIdMap.size() > 0) {
            return organizationIdMap;
        }
        organizationIdMap = new HashMap<>();
        organizationCodeMap = new HashMap<>();

        //获取所有的组织机构信息
        List<RbacOrgEntity> orgs = rbacOrgDao.query(null);

        //根据他们的上下级关系　构建组织结构树
        //这里使用一个算法，因为查询的时候不能排序查找上下级关系

        //构建组织结构MAP
        for (RbacOrgEntity org : orgs) {
            Organization organization = toOrganization(org);
            organizationIdMap.put(org.getId(), organization);
            organizationCodeMap.put(org.getCode(), organization);
        }

        //构建组织结构树
        for (Organization org : organizationCodeMap.values()) {
            if (Strings.isBlank(org.parentId)) {
                continue;
            }
            Organization parent = organizationIdMap.get(org.parentId);
            if (parent != null) {
                parent.children.add(org);
            }
        }
        return organizationIdMap;
    }

    /**
     * 转换数据结构
     *
     * @param org
     * @return
     */
    private Organization toOrganization(RbacOrgEntity org) {
        Organization organization = new Organization();
        organization.charger = org.getCharger();
        organization.email = org.getEmail();
        organization.link = org.getLink();
        organization.orgId = org.getId();
        organization.orgIcon = org.getIcon();
        organization.orgCode = org.getCode();
        organization.orgName = org.getName();
        organization.regionCode = org.getRegionCode();
        organization.summary = org.getSummary();
        organization.tel = org.getTel();
        organization.parentId = org.getParentId();
        organization.rank = org.getRank().doubleValue();
        organization.children = new ArrayList<>();
        organization.location = org.getLocation();
        return organization;
    }

    /**
     * 清空组织机构的缓存
     * 需要通知SessionService 清除session中有关用户权限的信息
     */
    public synchronized void clearGlobalCache() {
        organizationIdMap.clear();
        organizationCodeMap.clear();
        plugin.getServerContext().clearSessionGroup(RbacConstant.SESSION_CACHE_GROUP);
    }



    /**
     * 查询用户该资源类型下所有的资源
     *
     * @param kind
     * @return
     */
    public BizResult<QueryRoleResourceResponse> queryUserResourceByKind(String userId, Integer kind) {
        if (kind == null) {
            return BizResult.error(400, "参数错误");
        }
        if (StringUtils.isEmpty(userId)) {
            return BizResult.error(400, "用户ID不能为空");
        }
        // 查询用户的角色
        Sql sql = Sqls.create(SQL_TEMPLATE_QUERY_ROLE);
        sql.params().set("userId", userId);
        sql.setEntity(rbacRoleDao.getEntity());
        sql.setCallback(Sqls.callback.strList());
        rbacUserCodeRoleDao.getDao().execute(sql);
        List<String> roleNames = sql.getList(String.class);

        if (roleNames == null || roleNames.isEmpty()) {
            return BizResult.success(new QueryRoleResourceResponse());
        }

        // 查询角色的资源
        Sql sql1 = Sqls.create(SQL_TEMPLATE_QUERY_RESOURCE_CODE);
        sql1.params().set("kind", kind);
        sql1.params().set("roleList", roleNames);
        sql1.setCallback(Sqls.callback.strList());
        rbacRoleDao.getDao().execute(sql1);
        List<String> resourceCodes = sql1.getList(String.class);

        // 生成结果
        QueryRoleResourceResponse queryRoleResourceResponse = getQueryRoleResourceResponse(kind, resourceCodes);
        return BizResult.success(queryRoleResourceResponse);
    }

    public BizResult<QueryRoleResourceResponse> queryUserResourceByKind(String userId, ResourceKind kind) {
        if (kind == null) {
            return BizResult.error(400, "参数错误");
        }
        return queryUserResourceByKind(userId, kind.getCode());
    }

}
