package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.RbacRole;
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
public class QueryRoleResponse implements Serializable, IsSerializable {
    List<RbacRole> roles;
}
