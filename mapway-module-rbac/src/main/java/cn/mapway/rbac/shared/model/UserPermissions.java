package cn.mapway.rbac.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;

import java.io.Serializable;

/**
 *　用户权限信息,该信息包含了用户的所有组织.角色.资源.数据
 *  ------------------------------------------------------
 *  有两种表达方式　
 *  1.(FLAT) 用户-组织-角色-资源-数据
 *  2.(TREE) 用户-组织-角色-资源-数据
 *  当用户登录的时候　系统加载用户的权限信息，到缓存数据库中。
 */
@JsType
public class UserPermissions implements Serializable, IsSerializable {
    /**
     * 用户所属组织机构
     * 组织机构是一个树状结构
     * 然会的时候，不会压扁这个结构　方便展示用途，当一个用户被分派到一个组织中的时候，他能看到这个组织机构的
     * 所有子结构，能够切换组织结构，他的上级机构只能查看不能切换。所以　Organization中有一字段表示用户是否为
     * 这个组织机构的成员。
     */
    public Organization[] organizations;

    /**
     * 用户拥有的角色列表　虽然角色之间有树状关系，但是这里将会压扁这一结构使之成为一个列表
     * 每个角色下自然会有一些资源，写到这里感觉纯属多余,留着吧。
     */
    public Role[] roles;
}
