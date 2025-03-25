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
@Doc(value = "查询用户所在组织列表")
public class QueryUserOrgRequest implements Serializable, IsSerializable {
    @ApiField(value = "用户ID", example = "23")
    String userId;
    @ApiField(value = "系统CODE", example = "CIS")
    String systemCode;
}
