package cn.mapway.rbac.shared.rpc;

import cn.mapway.document.annotation.ApiField;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
public class DeleteRoleResourceRequest implements Serializable, IsSerializable {
    @ApiField(value = "角色CODE",example = "1")
    String roleCode;
    @ApiField(value = "资源CODE",example = "1")
    String resourceCode;
}
