package cn.mapway.rbac.shared.rpc;

import cn.mapway.document.annotation.ApiField;
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
public class QueryUserRoleResponse implements Serializable, IsSerializable {
    @ApiField("角色列表")
    public List<RbacRoleEntity> roles;
}
