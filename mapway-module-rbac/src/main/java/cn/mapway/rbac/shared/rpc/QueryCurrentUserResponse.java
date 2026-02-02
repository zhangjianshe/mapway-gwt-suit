package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.model.UserPermissions;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.client.widget.list.List;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
public class QueryCurrentUserResponse implements Serializable, IsSerializable {
    IUserInfo currentUser;
    UserPermissions userPermissions;
}
