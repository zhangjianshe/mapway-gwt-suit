package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * UpdateOrgRequest
 * 创建或者更新组织机构
 * @author zhangjianshe@gmail.com
 */
@Data
public class UpdateOrgRequest implements Serializable, IsSerializable {
    RbacOrgEntity org;
}
