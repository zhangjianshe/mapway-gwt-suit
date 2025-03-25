package cn.mapway.rbac.shared;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * <b>角色对应的资源</b>
 *
 * @author zhangjianshe@gmail.com
 * select role_code,resource_code from rbac_role_resource ;
 */
@Doc("角色对应的资源")
public class RbacRoleResource implements Serializable, IsSerializable {
    @ApiField(value = "角色CODE", example = "1")
    public String roleCode;
    @ApiField(value = "角色名称", example = "1")
    public String roleName;
    @ApiField(value = "资源CODE", example = "1")
    public String resourceCode;
    @ApiField(value = "资源名称", example = "1")
    public String name;
    @ApiField(value = "资源类型", example = "1")
    public Integer kind;
    @ApiField(value = "资源数据", example = "1")
    public String data;
    @ApiField(value = "资源描述", example = "1")
    public String summary;
    @ApiField(value = "资源分类", example = "1")
    public String catalog;
}
