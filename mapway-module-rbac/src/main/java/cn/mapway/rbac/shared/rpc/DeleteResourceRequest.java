package cn.mapway.rbac.shared.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * QueryRegionRequest
 *
 * @author zhangjianshe@gmail.com
 */
@Data
public class DeleteResourceRequest implements Serializable, IsSerializable {
    String resourceCode;
    Integer kind;
}
