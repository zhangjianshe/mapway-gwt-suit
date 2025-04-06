package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>null.rbac_org_user</b>
 *
 *
 * @author zhangjianshe@gmail.com
 * select user_code,org_code,user_id,create_time,alias_name,avator from rbac_org_user ;
 */
@Table(value="rbac_org_user")
@Doc("rbac_org_user")
public class RbacOrgUserEntity implements Serializable, IsSerializable {
  /**
   * user_code
   * 数据库字段序号:1
   */
  public static final String FLD_USER_CODE = "user_code";

  /**
   * org_code
   * 数据库字段序号:2
   */
  public static final String FLD_ORG_CODE = "org_code";

  /**
   * user_id
   * 数据库字段序号:3
   */
  public static final String FLD_USER_ID = "user_id";

  /**
   * create_time
   * 数据库字段序号:4
   */
  public static final String FLD_CREATE_TIME = "create_time";

  /**
   * alias_name
   * 数据库字段序号:5
   */
  public static final String FLD_ALIAS_NAME = "alias_name";

  /**
   * avator
   * 数据库字段序号:6
   */
  public static final String FLD_AVATAR = "avatar";
  /**
   * avator
   * 数据库字段序号:7
   */
  public static final String FLD_SYSTEM_CODE = "system_code";
  /**
   * 数据库表名称:rbac_org_user
   */
  public static final String TBL_RBAC_ORG_USER = "rbac_org_user";

  /**
   * 用户代码 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:64(不允许为空)
   */
  @ApiField("用户代码")
  @Name
  @Column("user_code")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("用户代码")
  private String userCode;

  /**
   * 组织代码 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:64(不允许为空)
   */
  @ApiField("组织代码")
  @Column("org_code")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("组织代码")
  private String orgCode;

  /**
   * 用户ＩＤ 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:64(不允许为空)
   */
  @ApiField("用户ＩＤ")
  @Column("user_id")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("用户ＩＤ")
  private String userId;

  /**
   *  
   * 缺省值:null
   * 数据类型timestamp
   * 数据库字段长度:29(不允许为空)
   */
  @ApiField("create_time")
  @Column("create_time")
  @ColDefine
  @Comment("")
  private Date createTime;

  /**
   * 别名 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("别名")
  @Column("alias_name")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("别名")
  private String aliasName;

  /**
   * avator 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("avatar")
  @Column("avatar")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("avatar")
  private String avatar;

  /**
   * 是否是主要组织机构
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("major")
  @Column("major")
  @ColDefine(
          type = ColType.BOOLEAN
  )
  @Default("false")
  @Comment("major")
  private Boolean major;

  /**
   * system
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("system_code")
  @Column("system_code")
  @ColDefine(
          type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("system code")
  private String systemCode;

  public String getSystemCode() {
    return systemCode;
  }

  public void setSystemCode(String systemCode) {
    this.systemCode = systemCode;
  }
  public String getUserCode() {
    return this.userCode;
  }

  public void setUserCode(String userCode) {
    this.userCode=userCode;
  }

  public String getOrgCode() {
    return this.orgCode;
  }

  public void setOrgCode(String orgCode) {
    this.orgCode=orgCode;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setUserId(String userId) {
    this.userId=userId;
  }

  public Date getCreateTime() {
    return this.createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime=createTime;
  }

  public String getAliasName() {
    return this.aliasName;
  }

  public void setAliasName(String aliasName) {
    this.aliasName=aliasName;
  }

  public String getAvatar() {
    return this.avatar;
  }
  public void setAvatar(String avatar) {
    this.avatar=avatar;
  }

  public Boolean getMajor() {
    return this.major;
  }
  public void setMajor(Boolean major) {
    this.major=major;
  }

}
