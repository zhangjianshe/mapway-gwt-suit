package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * <b>null.rbac_role_resource</b>
 *
 *
 * @author zhangjianshe@gmail.com
 * select role_code,resource_code from rbac_role_resource ;
 */
@Table(value="rbac_role_resource")
@PK(value={"roleCode","resourceCode"})
@Doc("rbac_role_resource")
public class RbacRoleResourceEntity implements Serializable, IsSerializable {
  /**
   * role_code
   * 数据库字段序号:1
   */
  public static final String FLD_ROLE_CODE = "role_code";

  /**
   * resource_code
   * 数据库字段序号:2
   */
  public static final String FLD_RESOURCE_CODE = "resource_code";

  /**
   * 数据库表名称:rbac_role_resource
   */
  public static final String TBL_RBAC_ROLE_RESOURCE = "rbac_role_resource";

  /**
   * role id 
   * 缺省值:null
   * 数据类型 varchar
   * 数据库字段长度:64(不允许为空)
   */
  @ApiField("role id")
  @Column("role_code")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("role id")
  private String roleCode;

  /**
   * 角色代码 
   * 缺省值:null
   * 数据类型 varchar
   * 数据库字段长度:256(不允许为空)
   */
  @ApiField("角色代码")
  @Column("resource_code")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("角色代码")
  private String resourceCode;

  public String getRoleCode() {
    return this.roleCode;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode=roleCode;
  }

  public String getResourceCode() {
    return this.resourceCode;
  }

  public void setResourceCode(String resourceCode) {
    this.resourceCode=resourceCode;
  }
}
