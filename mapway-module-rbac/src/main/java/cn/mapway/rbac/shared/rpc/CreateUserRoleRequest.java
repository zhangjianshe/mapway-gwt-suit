package cn.mapway.rbac.shared.rpc;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * CreateUserRoleRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
@Doc("创建用户角色请求")
public class CreateUserRoleRequest implements Serializable, IsSerializable {

    @ApiField(value = "用户Code")
    private String userCode;

    @ApiField(value = "角色CODE")
    private String roleCode;
}
