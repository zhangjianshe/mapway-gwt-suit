package cn.mapway.rbac.shared.rpc;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * QueryUserRoleRequest
 * 查询用户拥有的角色
 * @author zhangjianshe@gmail.com
 */
@Data
@Doc(value = "查询用户拥有的角色", desc = "查询用户拥有的角色")
public class QueryUserRoleRequest implements Serializable, IsSerializable {

    @ApiField(value = "用户 userCode")
    private String userCode;
}
