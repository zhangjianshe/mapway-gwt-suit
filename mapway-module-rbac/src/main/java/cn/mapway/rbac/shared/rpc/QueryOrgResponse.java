package cn.mapway.rbac.shared.rpc;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
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
@Doc("查询组织机构")
public class QueryOrgResponse implements Serializable, IsSerializable {
    @ApiField("组织机构列表")
    List<RbacOrgEntity> orgs;
}
