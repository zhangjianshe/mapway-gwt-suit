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
@Doc(value = "删除组织机构用户", desc = "删除组织机构用户")
public class DeleteOrgUserRequest implements Serializable, IsSerializable {
    @ApiField("用户编码")
    String userCode;
}
