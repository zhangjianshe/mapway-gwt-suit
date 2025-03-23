package cn.mapway.rbac.shared;

import cn.mapway.rbac.shared.db.postgis.RbacRoleEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleResourceEntity;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.List;


/**
 * 用户角色资源列表
 */
public class UserRoleResource implements Serializable, IsSerializable {
   public List<RbacRoleEntity> roles;
   public List<RbacRoleResourceEntity> resource;
}
