package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.db.postgis.RbacUserEntity;
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
public class QueryUserResponse implements Serializable, IsSerializable {
    List<RbacUserEntity> users;
}
