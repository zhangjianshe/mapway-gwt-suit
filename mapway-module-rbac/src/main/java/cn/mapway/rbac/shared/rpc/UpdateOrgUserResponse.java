package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
public class UpdateOrgUserResponse implements Serializable, IsSerializable {
    RbacOrgUserEntity orgUser;
}
