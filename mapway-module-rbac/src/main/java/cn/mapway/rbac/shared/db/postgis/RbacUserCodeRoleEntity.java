package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>null.rbac_user_code_role</b>
 *
 *
 * @author zhangjianshe@gmail.com
 * select user_code,role_code,careate_time,create_user from rbac_user_code_role ;
 */
@Table(value="rbac_user_code_role")
@PK(value={"userCode","roleCode"})
@Doc("rbac_user_code_role")
public class RbacUserCodeRoleEntity implements Serializable, IsSerializable {
  /**
   * user_code
   * 数据库字段序号:1
   */
  public static final String FLD_USER_CODE = "user_code";

  /**
   * role_code
   * 数据库字段序号:2
   */
  public static final String FLD_ROLE_CODE = "role_code";

  /**
   * careate_time
   * 数据库字段序号:3
   */
  public static final String FLD_CAREATE_TIME = "careate_time";

  /**
   * create_user
   * 数据库字段序号:4
   */
  public static final String FLD_CREATE_USER = "create_user";

  /**
   * 数据库表名称:rbac_user_code_role
   */
  public static final String TBL_RBAC_USER_CODE_ROLE = "rbac_user_code_role";

  /**
   * 用户身份 
   * 缺省值:null
   * 数据类型 varchar
   * 数据库字段长度:128(不允许为空)
   */
  @ApiField("用户身份")
  @Column("user_code")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("用户身份")
  private String userCode;

  /**
   * roleId 
   * 缺省值:null
   * 数据类型 varchar
   * 数据库字段长度:64(不允许为空)
   */
  @ApiField("roleId")
  @Column("role_code")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("roleId")
  private String roleCode;

  /**
   * 创建时间 
   * 缺省值:null
   * 数据类型 timestamp
   * 数据库字段长度:29(允许为空)
   */
  @ApiField("创建时间")
  @Column("create_time")
  @ColDefine
  @Comment("创建时间")
  private Date createTime;

  /**
   * 创建人 
   * 缺省值:null
   * 数据类型 int8
   * 数据库字段长度:19(允许为空)
   */
  @ApiField("创建人")
  @Column("create_user")
  @ColDefine(
      type = ColType.VARCHAR,
      width = 64
  )
  @Comment("创建人")
  private String createUser;

  public String getUserCode() {
    return this.userCode;
  }

  public void setUserCode(String userCode) {
    this.userCode=userCode;
  }

  public String getRoleCode() {
    return this.roleCode;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode=roleCode;
  }

  public Date getCreateTime() {
    return this.createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public String getCreateUser() {
    return this.createUser;
  }

  public void setCreateUser(String createUser) {
    this.createUser=createUser;
  }
}
