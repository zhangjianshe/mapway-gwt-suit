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
@Doc("查询组织机构")
public class QueryOrgRequest implements Serializable, IsSerializable {
    @ApiField("组织机构ID")
    String orgId;
    @ApiField("是否级联查询子节点")
    boolean cascade;
}
