package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.RbacRole;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * UpdateRoleRequest
 * 创建或者更新一个角色
 * @author zhangjianshe@gmail.com
 */
@Data
public class UpdateRoleRequest implements Serializable, IsSerializable {
    RbacRole role;
}
