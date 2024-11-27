package cn.mapway.rbac.shared.rpc;

import cn.mapway.ui.client.IUserInfo;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
public class LoginResponse implements Serializable, IsSerializable {
    IUserInfo currentUser;
    String token;
}
