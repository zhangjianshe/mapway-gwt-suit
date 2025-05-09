package cn.mapway.rbac.shared.rpc;

import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

@Data
public class RbacResourceOperation implements Serializable, IsSerializable {

    public final static String OPERATION_NONE = "无操作";

    public final static String OPERATION_ADD = "添加";

    public final static String OPERATION_REMOVE = "删除";

    public final static String OPERATION_UPDATE = "更新";

    private RbacResourceEntity entity;

    private String operation;

    public RbacResourceOperation(RbacResourceEntity entity, String operation) {
        this.entity = entity;
        this.operation = operation;
    }

    public RbacResourceOperation() {
    }
}
