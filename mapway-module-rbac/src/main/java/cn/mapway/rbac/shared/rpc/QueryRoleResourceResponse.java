package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
public class QueryRoleResourceResponse implements Serializable, IsSerializable {
     List<RbacResourceEntity> resources;
     public QueryRoleResourceResponse() {
          resources=new ArrayList<>();
     }
}
