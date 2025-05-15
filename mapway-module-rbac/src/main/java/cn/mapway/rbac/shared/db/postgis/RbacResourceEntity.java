package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * <b>null.rbac_resource</b>
 *
 *
 * @author zhangjianshe@gmail.com
 * select name,kind,resourceCode,data,summary,catalog from rbac_resource ;
 */
@Table(value="rbac_resource")
@Doc("rbac_resource")
@PK( {"resourceCode"} )
  public class RbacResourceEntity implements Serializable, IsSerializable {
  /**
   * name
   * 数据库字段序号:1
   */
  public static final String FLD_NAME = "name";

  /**
   * kind
   * 数据库字段序号:2
   */
  public static final String FLD_KIND = "kind";

  /**
   * resourceCode
   * 数据库字段序号:3
   */
  public static final String FLD_RESOURCE_CODE = "resource_code";

  /**
   * data
   * 数据库字段序号:4
   */
  public static final String FLD_DATA = "data";

  /**
   * summary
   * 数据库字段序号:5
   */
  public static final String FLD_SUMMARY = "summary";

  /**
   * catalog
   * 数据库字段序号:6
   */
  public static final String FLD_CATALOG = "catalog";

  /**
   * 数据库表名称:rbac_resource
   */
  public static final String TBL_RBAC_RESOURCE = "rbac_resource";

  /**
   * 资源名称 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:128(允许为空)
   */
  @ApiField("资源名称")
  @Column("name")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("资源名称")
  private String name;

  /**
   * 资源类型　0 None 1 region 2 function 
   * 缺省值:null
   * 数据类型int4
   * 数据库字段长度:10(允许为空)
   */
  @ApiField("资源类型　0 None 1 region 2 function")
  @Column("kind")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.INT,
      width = 10
  )
  @Comment("资源类型　0 None 1 region 2 function")
  private Integer kind;

  /**
   * 资源代码,用于查询 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(不允许为空)
   */
  @ApiField("资源代码,用于查询")
  @Name()
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("资源代码,用于查询")
  @Column("resource_code")
  private String resourceCode;

  /**
   * 资源关联数据　wkt,module_code 
   * 缺省值:null
   * 数据类型text
   * 数据库字段长度:2147483647(允许为空)
   */
  @ApiField("资源关联数据　wkt,module_code")
  @Column("data")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.TEXT
  )
  @Comment("资源关联数据　wkt,module_code")
  private String data;

  /**
   * 资源说明 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:512(允许为空)
   */
  @ApiField("资源说明")
  @Column("summary")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR,
          width =1024
  )
  @Comment("资源说明")
  private String summary;

  /**
   * 资源分类 
   * 缺省值:null
   * 数据类型varchar
   * 数据库字段长度:255(允许为空)
   */
  @ApiField("资源分类")
  @Column("catalog")
  @ColDefine(
      type = org.nutz.dao.entity.annotation.ColType.VARCHAR
  )
  @Comment("资源分类")
  private String catalog;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name=name;
  }

  public Integer getKind() {
    return this.kind;
  }

  public void setKind(Integer kind) {
    this.kind=kind;
  }

  public String getResourceCode() {
    return this.resourceCode;
  }

  public void setResourceCode(String resourceCode) {
    this.resourceCode = resourceCode;
  }

  public String getData() {
    return this.data;
  }

  public void setData(String data) {
    this.data=data;
  }

  public String getSummary() {
    return this.summary;
  }

  public void setSummary(String summary) {
    this.summary=summary;
  }

  public String getCatalog() {
    return this.catalog;
  }

  public void setCatalog(String catalog) {
    this.catalog=catalog;
  }

  public static String generateResourceCode(String name, Integer kind) {
    return name + "!_!" + kind;
  }

  public String parseNameByResourceCode() {
    if (this.resourceCode == null) {
      return null;
    }
    return this.resourceCode.split("!_!")[0];
  }

  public Integer parseKindByResourceCode() {
    if (this.resourceCode == null) {
      return null;
    }
    String[] split = this.resourceCode.split("!_!");
    if (split.length != 2) {
      return null;
    } else {
      return Integer.valueOf(split[1]);
    }
  }

  public String parseContentByResourceCode() {
    if (this.resourceCode == null) {
      return null;
    }
    String[] split = this.resourceCode.split("!_!");
    if (split.length != 2) {
      return null;
    } else {
      return split[0];
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    RbacResourceEntity that = (RbacResourceEntity) o;
    return Objects.equals(resourceCode, that.resourceCode)  && Objects.equals(kind, that.kind) && Objects.equals(name, that.name) && Objects.equals(data, that.data) && Objects.equals(summary, that.summary) && Objects.equals(catalog, that.catalog);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, kind, resourceCode, data, summary, catalog);
  }

}
