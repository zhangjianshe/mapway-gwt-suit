package cn.mapway.rbac.shared.rpc;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * DeleteUserRoleRequest
 * 删除用户角色
 * @author zhangjianshe@gmail.com
 */
@Data
@Doc(value = "删除用户角色", desc = "删除用户角色")
public class DeleteUserRoleRequest implements Serializable, IsSerializable {
    @ApiField(value = "用户Code")
    private String userCode;

    @ApiField(value = "角色CODE")
    private String roleCode;
}
