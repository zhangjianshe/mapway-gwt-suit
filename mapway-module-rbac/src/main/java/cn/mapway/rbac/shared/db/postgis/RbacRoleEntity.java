package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * <b>null.rbac_role</b>
 *
 *
 * @author zhangjianshe@gmail.com
 * select name,icon,summary,parent_code,code from rbac_role ;
 */
@Table(value="rbac_role")
@Doc("rbac_role")
public class RbacRoleEntity implements Serializable, IsSerializable {
  /**
   * name
   * 数据库字段序号:1
   */
  public static final String FLD_NAME = "name";

  /**
   * icon
   * 数据库字段序号:2
   */
  public static final String FLD_ICON = "icon";

  /**
   * summary
   * 数据库字段序号:3
   */
  public static final String FLD_SUMMARY = "summary";

  /**
   * parent_code
   * 数据库字段序号:4
   */
  public static final String FLD_PARENT_CODE = "parent_code";

  /**
   * code
   * 数据库字段序号:5
   */
  public static final String FLD_CODE = "code";

  /**
   * 数据库表名称:rbac_role
   */
  public static final String TBL_RBAC_ROLE = "rbac_role";

  /**
   * 角色名称 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("角色名称")
  @Column("name")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("角色名称")
  private String name;

  /**
   * 角色图标 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("角色图标")
  @Column("icon")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("角色图标")
  private String icon;

  /**
   * 角色描述 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("角色描述")
  @Column("summary")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("角色描述")
  private String summary;

  /**
   * 角色的上级角色 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("角色的上级角色")
  @Column("parent_code")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("角色的上级角色")
  private String parentCode;

  /**
   * 角色代码 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(不允许为空)
   */
  @ApiField("角色代码")
  @Name
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("角色代码")
  private String code;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name=name;
  }

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(String icon) {
    this.icon=icon;
  }

  public String getSummary() {
    return this.summary;
  }

  public void setSummary(String summary) {
    this.summary=summary;
  }

  public String getParentCode() {
    return this.parentCode;
  }

  public void setParentCode(String parentCode) {
    this.parentCode=parentCode;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code=code;
  }
}
