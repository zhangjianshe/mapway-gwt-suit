package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * CreateResourceRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data

public class CreateResourceRequest implements Serializable, IsSerializable {
    RbacResourceEntity resource;
}
