package cn.mapway.rbac.shared.rpc;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
@Doc(value = "更新角色对应的资源", retClazz = {UpdateRoleResourceResponse.class})
public class UpdateRoleResourceRequest implements Serializable, IsSerializable {
    @ApiField(value = "角色CODE",example = "1")
    String roleCode;
    @ApiField(value = "资源CODE",example = "1")
    String resourceCode;
}
