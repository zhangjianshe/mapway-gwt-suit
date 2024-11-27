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
@Doc("查询角色资源")
public class QueryRoleResourceRequest implements Serializable, IsSerializable {

    @ApiField(value = "角色编码", example = "abc")
    String roleCode;

    @ApiField(value = "是否包含子节点", example = "true")
    Boolean widthChildren;
}
