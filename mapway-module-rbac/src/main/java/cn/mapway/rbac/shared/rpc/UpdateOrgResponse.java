package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
public class UpdateOrgResponse implements Serializable, IsSerializable {
    RbacOrgEntity org;
}
