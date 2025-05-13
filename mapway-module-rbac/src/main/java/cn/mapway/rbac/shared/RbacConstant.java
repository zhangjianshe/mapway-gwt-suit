package cn.mapway.rbac.shared;

public class RbacConstant {
    public static final String X_PROTOCOL = "X-Protocol";
    public static final String MODULE_GROUP_RBAC = "角色权限";
    public static final String MODULE_GROUP_MAIN = "应用页面";
    public static final String SALT = "8L+*&^N$#SDS__213213";
    public static final String DEFAULT_SERVER_PATH="rbac";

    public static final String KEY_AUTH_CODE = "KEY_AUTH_CODE";
    /**
     *
     * may be other application will pass ServletInitParameter to overide this key
     * <initParameter>TOKEN-KEY</initParameter>
     * <initParameterValue>Authorization</initParameterValue>
     */
    public static final String TOKEN_HEADER_KEY = "TOKEN-KEY";

    /////////////////// RBAC 子系统的资源点常量定义/////////////////////
    public static final String RESOURCE_RBAC_MAINTAINER = "RESOURCE_RBAC_MAINTAINER";
    public static final String RESOURCE_RBAC_AUTHORITY = "RESOURCE_RBAC_AUTHORITY";

    //　　系统管理组织机构
    public static final String SYSTEM_MANAGER_ORG_CODE = "RBAC_ORG_SYSTEM";

    //  系统角色
    public static final String ROLE_SYS = "ROLE_SYS";
    public static final String ROLE_SYS_MAINTAINER = "ROLE_SYS_MAINTAINER";

    public static final String USER_GROUP = "USER_GROUP";

    public static final String ROLE_NORMAL_USER = "ROLE_NORMAL_USER";

    //超级用户ID
    public static final Long SUPER_USER_ID = 1L;


    //用户权限的缓存KEY
    /**
     * 每个用户的权限缓存 为该KEY加上用户ID 比如 USER_PERMISSION_1
     */
    public static final String USER_PERMISSION_CACHE_KEY_PREFIX = "USER_PERMISSION_";
    public static final String SESSION_CACHE_GROUP = "rbac";
    
}
