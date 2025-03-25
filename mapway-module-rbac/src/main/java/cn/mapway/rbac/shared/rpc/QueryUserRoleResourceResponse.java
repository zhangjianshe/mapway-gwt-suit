package cn.mapway.rbac.shared.rpc;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import cn.mapway.rbac.shared.RbacRoleResource;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleEntity;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
@Doc(value = "QueryUserRoleResourceResponse")
public class QueryUserRoleResourceResponse implements Serializable, IsSerializable {
    @ApiField(value = "用户角色列表", example = "[]")
    public List<RbacRoleEntity> roles;
    @ApiField(value = "用户角色资源列表", example = "[]")
    public List<RbacRoleResource> resource;
    @ApiField(value = "组织信息")
    public RbacOrgUserEntity orgUser;
}
