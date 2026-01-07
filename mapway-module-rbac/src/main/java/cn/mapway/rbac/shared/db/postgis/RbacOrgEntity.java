package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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
   * code
   * 数据库字段序号:12
   */
  public static final String FLD_REGION_CODE = "region_code";


  /**
   * location
   * 数据库字段序号:13
   */
  public static final String FLD_LOCATION = "location";

  /**
   * location
   * 数据库字段序号:13
   */
  public static final String FLD_DEFAULT_ORG = "default_org";

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
   * 是否是缺省组织
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("是否是缺省组织")
  @Column("default_org")
  @ColDefine(
          type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("是否是缺省组织")
  @Default("false")
  private Boolean defaultOrg;

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


  /**
   * 行政区划代码
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(不允许为空)
   */
  @ApiField("行政区划代码")
  @Column("region_code")
  @ColDefine(
          type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("行政区划代码")
  private String regionCode;

  /**
   * 排序
   * 缺省值:100
   * 数据类型varchar
   * 数据库字段长度:255(不允许为空)
   */
  @ApiField("排序")
  @Column("rank")
  @ColDefine(
          type = ColType.INT
  )
  @Default("100")
  @Comment("排序")
  private Integer rank;

  /**
   * 初始化位置 lng,lat,zoom
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(不允许为空)
   */
  @ApiField("初始化位置")
  @Column("location")
  @ColDefine(
          type = org.nutz.dao.entity.annotation.ColType.VARCHAR,
          width = 255
  )
  @Comment("初始化位置")
  private String location;

}
