package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * <b>null.rbac_org</b>
 *
 *
 * @author zhangjianshe@gmail.com
 * select id,name,icon,summary,link,parent_id,address,charger,email,tel,code from rbac_org ;
 */
@Table(value="rbac_org")
@Doc("rbac_org")
public class RbacOrgEntity implements Serializable, IsSerializable {
  /**
   * id
   * 数据库字段序号:1
   */
  public static final String FLD_ID = "id";

  /**
   * name
   * 数据库字段序号:2
   */
  public static final String FLD_NAME = "name";

  /**
   * icon
   * 数据库字段序号:3
   */
  public static final String FLD_ICON = "icon";

  /**
   * summary
   * 数据库字段序号:4
   */
  public static final String FLD_SUMMARY = "summary";

  /**
   * link
   * 数据库字段序号:5
   */
  public static final String FLD_LINK = "link";

  /**
   * parent_id
   * 数据库字段序号:6
   */
  public static final String FLD_PARENT_ID = "parent_id";

  /**
   * address
   * 数据库字段序号:7
   */
  public static final String FLD_ADDRESS = "address";

  /**
   * charger
   * 数据库字段序号:8
   */
  public static final String FLD_CHARGER = "charger";

  /**
   * email
   * 数据库字段序号:9
   */
  public static final String FLD_EMAIL = "email";

  /**
   * tel
   * 数据库字段序号:10
   */
  public static final String FLD_TEL = "tel";

  /**
   * code
   * 数据库字段序号:11
   */
  public static final String FLD_CODE = "code";

  /**
   * 数据库表名称:rbac_org
   */
  public static final String TBL_RBAC_ORG = "rbac_org";

  /**
   * 组织ID 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:64(不允许为空)
   */
  @ApiField("组织ID")
  @Name
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("组织ID")
  private String id;

  /**
   * 组织名称 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(不允许为空)
   */
  @ApiField("组织名称")
  @Column("name")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("组织名称")
  private String name;

  /**
   * 组织图标 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("组织图标")
  @Column("icon")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("组织图标")
  private String icon;

  /**
   * 组织简介 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:512(允许为空)
   */
  @ApiField("组织简介")
  @Column("summary")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("组织简介")
  private String summary;

  /**
   * 相关连接 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("相关连接")
  @Column("link")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("相关连接")
  private String link;

  /**
   * 父ID 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:64(允许为空)
   */
  @ApiField("父ID")
  @Column("parent_id")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("父ID")
  private String parentId;

  /**
   * 地址 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("地址")
  @Column("address")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("地址")
  private String address;

  /**
   * 负责人 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("负责人")
  @Column("charger")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("负责人")
  private String charger;

  /**
   * 电子邮件 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("电子邮件")
  @Column("email")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("电子邮件")
  private String email;

  /**
   * 电话 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("电话")
  @Column("tel")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("电话")
  private String tel;

  /**
   * 组织代码 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(不允许为空)
   */
  @ApiField("组织代码")
  @Column("code")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("组织代码")
  private String code;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id=id;
  }

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

  public String getLink() {
    return this.link;
  }

  public void setLink(String link) {
    this.link=link;
  }

  public String getParentId() {
    return this.parentId;
  }

  public void setParentId(String parentId) {
    this.parentId=parentId;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(String address) {
    this.address=address;
  }

  public String getCharger() {
    return this.charger;
  }

  public void setCharger(String charger) {
    this.charger=charger;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email=email;
  }

  public String getTel() {
    return this.tel;
  }

  public void setTel(String tel) {
    this.tel=tel;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code=code;
  }
}
