package cn.mapway.rbac.shared.model;

import jsinterop.annotations.JsType;

/**
 * 系统角色信息
 * 角色之间有树状关系　仅仅是用于管理，不代表角色之间的继承
 * 也就是说　父角色不会拥有子角色的权限
 */
@JsType
public class Role {
    public String name;
    public String code;
    public String summary;
    public String icon;
    public String parentCode;
    public Res[] resources;
}
