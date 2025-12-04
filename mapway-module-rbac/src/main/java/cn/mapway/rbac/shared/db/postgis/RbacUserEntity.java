package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Getter;
import lombok.Setter;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <b>null.rbac_user</b>
 *
 *
 * @author zhangjianshe@gmail.com
 * select user_id,dept_id,user_name,nick_name,user_type,email,phonenumber,sex,avatar,password,status,del_flag,login_ip,login_date,create_by,create_time,update_by,update_time,remark,config,rel_id from rbac_user ;
 */
@Table(value="rbac_user")
@Doc("rbac_user")
@Getter
@Setter
public class RbacUserEntity implements Serializable, IsSerializable {
  /**
   * user_id
   * 数据库字段序号:1
   */
  public static final String FLD_USER_ID = "user_id";

  /**
   * dept_id
   * 数据库字段序号:2
   */
  public static final String FLD_DEPT_ID = "dept_id";

  /**
   * user_name
   * 数据库字段序号:3
   */
  public static final String FLD_USER_NAME = "user_name";

  /**
   * nick_name
   * 数据库字段序号:4
   */
  public static final String FLD_NICK_NAME = "nick_name";

  /**
   * user_type
   * 数据库字段序号:5
   */
  public static final String FLD_USER_TYPE = "user_type";

  /**
   * email
   * 数据库字段序号:6
   */
  public static final String FLD_EMAIL = "email";

  /**
   * phonenumber
   * 数据库字段序号:7
   */
  public static final String FLD_PHONENUMBER = "phonenumber";

  /**
   * sex
   * 数据库字段序号:8
   */
  public static final String FLD_SEX = "sex";

  /**
   * avatar
   * 数据库字段序号:9
   */
  public static final String FLD_AVATAR = "avatar";

  /**
   * password
   * 数据库字段序号:10
   */
  public static final String FLD_PASSWORD = "password";

  /**
   * status
   * 数据库字段序号:11
   */
  public static final String FLD_STATUS = "status";

  /**
   * del_flag
   * 数据库字段序号:12
   */
  public static final String FLD_DEL_FLAG = "del_flag";

  /**
   * login_ip
   * 数据库字段序号:13
   */
  public static final String FLD_LOGIN_IP = "login_ip";

  /**
   * login_date
   * 数据库字段序号:14
   */
  public static final String FLD_LOGIN_DATE = "login_date";

  /**
   * create_by
   * 数据库字段序号:15
   */
  public static final String FLD_CREATE_BY = "create_by";

  /**
   * create_time
   * 数据库字段序号:16
   */
  public static final String FLD_CREATE_TIME = "create_time";

  /**
   * update_by
   * 数据库字段序号:17
   */
  public static final String FLD_UPDATE_BY = "update_by";

  /**
   * update_time
   * 数据库字段序号:18
   */
  public static final String FLD_UPDATE_TIME = "update_time";

  /**
   * remark
   * 数据库字段序号:19
   */
  public static final String FLD_REMARK = "remark";

  /**
   * config
   * 数据库字段序号:20
   */
  public static final String FLD_CONFIG = "config";

  /**
   * rel_id
   * 数据库字段序号:21
   */
  public static final String FLD_REL_ID = "rel_id";

  public static final String FLD_TOKEN = "token";
  /**
   * 数据库表名称:rbac_user
   */
  public static final String TBL_RBAC_USER = "rbac_user";

  /**
   * 用户ID 
   * 缺省值:nextval('rbac_user_user_id_seq'::regclass)
   * 数据类型bigserial
   * 数据库字段长度:19(不允许为空)
   */
  @ApiField("用户ID")
  @Id
  @Column("user_id")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.INT,
      width = 19
  )
  @Default("nextval('rbac_user_user_id_seq'::regclass)")
  @Comment("用户ID")
  private Long userId;

  /**
   * 部门ID 
   * 缺省值:null
   * 数据类型int8
   * 数据库字段长度:19(允许为空)
   */
  @ApiField("部门ID")
  @Column("dept_id")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.INT,
      width = 19
  )
  @Comment("部门ID")
  private Long deptId;

  /**
   * 用户账号 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("用户账号")
  @Column("user_name")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("用户账号")
  private String userName;

  /**
   * 用户昵称 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("用户昵称")
  @Column("nick_name")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("用户昵称")
  private String nickName;

  /**
   * 用户类型（00系统用户）(01 LDAP) 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("用户类型（00系统用户）(01 LDAP)")
  @Column("user_type")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("用户类型（00系统用户）(01 LDAP)")
  private String userType;

  /**
   * 用户邮箱 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("用户邮箱")
  @Column("email")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("用户邮箱")
  private String email;

  /**
   * 手机号码 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("手机号码")
  @Column("phonenumber")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("手机号码")
  private String phonenumber;

  /**
   * 用户性别（0男 1女 2未知） 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("用户性别（0男 1女 2未知）")
  @Column("sex")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("用户性别（0男 1女 2未知）")
  private String sex;

  /**
   * 头像地址 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("头像地址")
  @Column("avatar")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("头像地址")
  private String avatar;

  /**
   * 密码 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("密码")
  @Column("password")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("密码")
  private String password;

  /**
   * 帐号状态（0正常 1停用） 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("帐号状态（0正常 1停用）")
  @Column("status")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("帐号状态（0正常 1停用）")
  private String status;

  /**
   * 删除标志（0代表存在 2代表删除） 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("删除标志（0代表存在 2代表删除）")
  @Column("del_flag")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("删除标志（0代表存在 2代表删除）")
  private String delFlag;

  /**
   * 最后登陆IP 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("最后登陆IP")
  @Column("login_ip")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("最后登陆IP")
  private String loginIp;

  /**
   * 最后登陆时间 
   * 缺省值:null
   * 数据类型timestamp
   * 数据库字段长度:29(允许为空)
   */
  @ApiField("最后登陆时间")
  @Column("login_date")
  @ColDefine
  @Comment("最后登陆时间")
  private Date loginDate;

  /**
   * 创建者 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("创建者")
  @Column("create_by")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("创建者")
  private String createBy;

  /**
   * 创建时间 
   * 缺省值:null
   * 数据类型timestamp
   * 数据库字段长度:29(允许为空)
   */
  @ApiField("创建时间")
  @Column("create_time")
  @ColDefine
  @Comment("创建时间")
  private Date createTime;

  /**
   * 更新者 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("更新者")
  @Column("update_by")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("更新者")
  private String updateBy;

  /**
   * 更新时间 
   * 缺省值:null
   * 数据类型timestamp
   * 数据库字段长度:29(允许为空)
   */
  @ApiField("更新时间")
  @Column("update_time")
  @ColDefine
  @Comment("更新时间")
  private Date updateTime;

  /**
   * 备注 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("备注")
  @Column("remark")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("备注")
  private String remark;

  /**
   * 用户的配置信息 JSON格式 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("用户的配置信息 JSON格式")
  @Column("config")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("用户的配置信息 JSON格式")
  private String config;

  /**
   * 相关方的ID 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("相关方的ID")
  @Column("rel_id")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("相关方的ID")
  private String relId;

  /**
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("用户固定的TOKEN")
  @Column("token")
  @ColDefine(
          type = org.nutz.dao.entity.annotation.ColType.VARCHAR,
          width = 64
  )
  @Comment("用户固定的TOKEN")
  private String token;

}
