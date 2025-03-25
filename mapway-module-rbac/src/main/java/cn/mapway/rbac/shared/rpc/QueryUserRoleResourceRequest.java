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
@Doc(value = "QueryRegionRequest")
public class QueryUserRoleResourceRequest implements Serializable, IsSerializable {
    @ApiField(value = "用户Code")
    String userCode;
}
