package cn.mapway.rbac.shared.rpc;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import cn.mapway.rbac.shared.RbacUserOrg;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
@Doc(value = "用户所属组织列表")
public class QueryUserOrgResponse implements Serializable, IsSerializable {
    @ApiField(value = "用户所属组织列表",example = "[]")
    List<RbacUserOrg> userOrgs; //用户所属组织列表
}
