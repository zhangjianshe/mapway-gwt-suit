package cn.mapway.ui.shared.db;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库中一张表的元数据
 */
@Table(value = "table_metadata")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PK({"db", "schema", "tableName"})
public class TableMetadata implements Serializable, IsSerializable {

    @Column("db")
    private String db;
    @Column("schema")
    private String schema;
    @Column("table_name")
    private String tableName;

    @Column("comment")
    private String comment;
    @Column("columns")
    @ColDefine(type = ColType.PSQL_JSON)
    private List<ColumnMetadata> columns;
    @ColDefine(type = ColType.PSQL_JSON)
    @Column("primary_key_columns")
    private List<String> primaryKeyColumns;
    @Column("total_count")
    private Long totalCount;
    @Column("file_size")
    private Long fileSize;

    public TableMetadata(String db, String schema, String tableName, String comment) {
        this.db = db;
        this.schema = schema;
        this.tableName = tableName;
        this.comment = comment;
        this.columns = new ArrayList<>();
        this.primaryKeyColumns = new ArrayList<>();
    }
    // Helper methods
    public void addColumn(ColumnMetadata column) {
        this.columns.add(column);
    }

    public void addPrimaryKeyColumn(String columnName) {
        this.primaryKeyColumns.add(columnName);
    }
}