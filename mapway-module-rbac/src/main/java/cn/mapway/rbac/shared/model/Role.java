package cn.mapway.rbac.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;

import java.io.Serializable;

/**
 * 系统角色信息
 * 角色之间有树状关系　仅仅是用于管理，不代表角色之间的继承
 * 也就是说　父角色不会拥有子角色的权限
 */
@JsType
public class Role implements Serializable, IsSerializable {
    public String name;
    public String code;
    public String summary;
    public String icon;
    public String parentCode;
    public Res[] resources;
}
