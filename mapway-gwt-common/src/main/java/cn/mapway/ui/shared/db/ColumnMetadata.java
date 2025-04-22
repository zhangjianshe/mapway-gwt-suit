package cn.mapway.ui.shared.db;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import java.io.Serializable;

/**
 * 数据库列信息
 */
@Table("column_metadata")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ColumnMetadata implements Serializable, IsSerializable {
    @Column("column_name")
    private String columnName;
    @Column("type_name")
    private String typeName; // PostgreSQL type (e.g., varchar, integer, geometry)
    @Column("precision")
    private int precision; // For varchar, numeric
    @Column("scale")
    private int scale; // For numeric
    @Column("not_null")
    private boolean notNull;
    @Column("comment")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    private String comment;
    @Column("geometry_type")
    private String geometryType; // PostGIS geometry type (e.g., POLYGON, POINT)
    @Column("srid")
    private Integer srid; // PostGIS SRID (e.g., 4326), nullable
    @Column("default_value")
    private String defaultValue;
    @Column("seq_name")
    private String seqName;
    @Column("seq_value")
    private Long seqValue;
}