package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Getter;
import lombok.Setter;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <b>null.rbac_org</b>
 *
 *
 * @author zhangjianshe@gmail.com
 * select id,name,icon,summary,link,parent_id,address,charger,email,tel,code from rbac_org ;
 */
@Table(value="rbac_token")
@Doc("rbac_token")
@Getter
@Setter
public class RbacTokenEntity implements Serializable, IsSerializable {

  public static final String FLD_ID = "id";
  public static final String FLD_CREATE_TIME = "create_time";
  public static final String FLD_USER_ID = "user_id";
  public static final String FLD_EXPIRE_TIME = "expire_time";
  public static final String FLD_SUMMARY = "summary";
  public static final String TBL_RBAC_TOKEN = "rbac_token";

  /**
   * TOKEN ID
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:64(不允许为空)
   */
  @ApiField("TOKEN ID")
  @ColDefine(
      type = ColType.VARCHAR,
          width = 64
  )
  @Comment("TOKEN ID")
  @Name
  private String id;


  @ApiField("用户ID")
  @Column(hump = true)
  @ColDefine(notNull = true)
  private Long userId;


  @ApiField("创建时间")
  @Column(hump = true)
  private Timestamp createTime;

  @ApiField("到期时间")
  @Column(hump = true)
  private Timestamp expireTime;

  /**
   * 简要介绍
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:512(允许为空)
   */
  @ApiField("简要介绍")
  @Column("summary")
  @ColDefine(
      type = ColType.VARCHAR,
          width = 512
  )
  @Comment("简要介绍")
  private String summary;

}
