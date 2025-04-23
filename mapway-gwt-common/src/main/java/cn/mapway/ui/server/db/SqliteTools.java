package cn.mapway.ui.server.db;

import cn.mapway.ui.shared.db.ColumnMetadata;
import cn.mapway.ui.shared.db.TableMetadata;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.LoopException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sqlite数据库工具
 */
public class SqliteTools implements IDbSource , Closeable {
    final Connection connection;

    public SqliteTools(Connection connection) {
        this.connection = connection;
    }

    public static SqliteTools create(String dbPath) throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        Connection connection = DriverManager.getConnection(url);
        return new SqliteTools(connection);
    }

    public List<String> listTable(String schema) {
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement("select table_name from information_schema.tables where table_schema=? and table_type='BASE TABLE' and table_name not like 'pg_%' and table_name not like 'spatial_ref_sys' order by table_name");
            stmt.setString(1, schema);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> listView(Connection conn, String schema) {
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("select table_name from information_schema.tables where table_schema=? and table_type='VIEW' and table_name not like 'pg_%' order by table_name");
            stmt.setString(1, schema);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



    /**
     * 遍历数据库中没一条记录
     *
     * @param tableMetadata
     * @param consumer
     */
    public void eachRow(TableMetadata tableMetadata, Each<ResultSet> consumer) throws SQLException {
        // Generate SELECT query for PostgreSQL
        String selectQuery = generateSelectQuery(tableMetadata);
        int rowCount = Math.toIntExact(tableMetadata.getTotalCount());

        PreparedStatement pgStmt = connection.prepareStatement(selectQuery);

        // Fetch data from PostgreSQL
        int count = 0;
        try (ResultSet rs = pgStmt.executeQuery()) {
            while (rs.next()) {
                consumer.invoke(count, rs, rowCount);
                count++;
            }
        }
        pgStmt.close();
    }


    public void restore(IDbSource dbSource, TableMetadata tableMetadata, IProgressHandler handler) throws SQLException {
        String pgTableName = tableMetadata.getTableName();
        List<ColumnMetadata> columns = tableMetadata.getColumns();

        //创建新表
        createTable(tableMetadata, true);

        StringBuilder insertSql = new StringBuilder("INSERT INTO ").append(pgTableName).append(" (");
        for (int i = 0; i < columns.size(); i++) {
            insertSql.append("\"").append(columns.get(i).getColumnName()).append("\"");
            if (i < columns.size() - 1) {
                insertSql.append(", ");
            }
        }
        insertSql.append(") VALUES (");
        for (int i = 0; i < columns.size(); i++) {
            ColumnMetadata column = columns.get(i);

            insertSql.append("?");

            if (i < columns.size() - 1) {
                insertSql.append(", ");
            }
        }
        insertSql.append(")");
        connection.setAutoCommit(false);
        try (
                PreparedStatement insertStatement = connection.prepareStatement(insertSql.toString());
        ) {

            dbSource.eachRow(tableMetadata, new Each<ResultSet>() {
                @Override
                public void invoke(int index, ResultSet rs, int length) throws ExitLoop, ContinueLoop, LoopException {
                    try {
                        for (int i = 0; i < columns.size(); i++) {
                            ColumnMetadata column = columns.get(i);
                            String pgType = column.getTypeName().toLowerCase();
                            int paramIndex = i + 1;

                            if (pgType.equals("geometry")) {
                                // WKT to GEOMETRY
                                String wkt = rs.getString(i + 1);
                                if (wkt == null || wkt.endsWith("EMPTY")) {
                                    wkt = column.getGeometryType()+" EMPTY";
                                }
                                insertStatement.setString(paramIndex, wkt);
                            } else if (pgType.equals("bytea")) {
                                byte[] bytes = rs.getBytes(i + 1);
                                insertStatement.setBytes(paramIndex, bytes);
                            } else {
                                Object object = rs.getObject(i + 1);
                                if (pgType.equals("bool") || pgType.equals("boolean")) {
                                    // INTEGER (0/1) to BOOLEAN
                                    insertStatement.setBoolean(paramIndex, rs.getBoolean(i + 1));
                                } else {
                                    Object value = object;
                                    insertStatement.setObject(paramIndex, value);
                                }
                            }
                        }
                        // Execute INSERT
                        insertStatement.executeUpdate();

                        if (handler != null) {
                            if (length > 0) {
                                int percent = (int) (Math.round(index * 100.) / length);
                                handler.progress(percent, "");
                            } else {
                                handler.progress(100, "");
                            }
                        } else {
                            System.out.println("Inserting row " + index + " of " + length);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        connection.commit();
    }

    // Generate SELECT query with ST_AsText for geometry columns
    public String generateSelectQuery(TableMetadata tableMetadata) {
        StringBuilder selectQuery = new StringBuilder("SELECT ");
        List<ColumnMetadata> columns = tableMetadata.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            String colName = columns.get(i).getColumnName();
            selectQuery.append("\"").append(colName).append("\"");
            if (i < columns.size() - 1) {
                selectQuery.append(", ");
            }
        }
        selectQuery.append(" FROM ").append(tableMetadata.getTableName()); // SQLite table name same as PG for simplicity
        return selectQuery.toString();
    }

    /**
     * 获取表的记录数量
     *
     * @param tableMetadata
     * @return
     */
    public long getRowCount(TableMetadata tableMetadata) {
        String tableName = tableMetadata.getTableName();
        String schemaName = tableMetadata.getSchema();

        // Construct the SQL query for counting rows
        String sql = "SELECT COUNT(*) FROM ";
        if (schemaName != null && !schemaName.isEmpty()) {
            sql += "\"" + schemaName + "\".";
        }
        sql += "\"" + tableName + "\"";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Long count = rs.getLong(1);
                tableMetadata.setTotalCount(count);
                return count;
            }
        } catch (SQLException e) {
            // Log the error and handle it appropriately
            throw new RuntimeException("Failed to get count for table " + tableName, e);
        }
        tableMetadata.setTotalCount(0L);
        return 0; // Fallback return in case of no results
    }

    public boolean isTableExist(String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, "", tableName, null);
            return tables.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check table existence", e);
        }
    }

    public void dropTable(String tableName) {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("DROP TABLE " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop table", e);
        }
    }

    public void truncateTable(String tableName) {

        try {
            Statement stmt = connection.createStatement();
            stmt.execute("TRUNCATE TABLE " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to truncate table", e);
        }
    }

    public void createTable(TableMetadata tableMetadata, boolean dropIfExists) {
        try {
            Statement stmt = connection.createStatement();
            String sql = createSqlFromMetadata(tableMetadata, tableMetadata.getSchema(), tableMetadata.getTableName());
            if (dropIfExists) {
                stmt.execute("DROP TABLE IF EXISTS " + tableMetadata.getTableName());
            }
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table", e);
        }
    }

    /**
     * 生成创建表的SQL
     *
     * @param tableMetadata
     * @return
     */
    public String createSqlFromMetadata(TableMetadata tableMetadata, String schemaName, String newTableName) {
        // Default and sanitize new table name
        if (newTableName == null || newTableName.trim().isEmpty()) {
            newTableName = "generated_table";
        }
        //newTableName = newTableName.replaceAll("[^a-zA-Z0-9_]", "_");

        StringBuilder sql = new StringBuilder("-- SQLite CREATE TABLE for backup\n");
        sql.append("CREATE TABLE ").append(newTableName).append(" (\n");

        // Build column definitions
        List<ColumnMetadata> columns = tableMetadata.getColumns();
        List<String> primaryKeys = tableMetadata.getPrimaryKeyColumns();
        for (int i = 0; i < columns.size(); i++) {
            ColumnMetadata column = columns.get(i);
            String columnName = column.getColumnName();
            String sqlType = mapPgTypeToSqliteBackupType(
                    column.getTypeName(),
                    column.getPrecision(),
                    column.getScale(),
                    column.isNotNull()
            );

            sql.append("    \"").append(columnName).append("\" ").append(sqlType);

            // Inline PRIMARY KEY for single-column PK
            if (primaryKeys.contains(columnName) && primaryKeys.size() == 1) {
                sql.append(" PRIMARY KEY");
            }

            if (i < columns.size() - 1 || (primaryKeys.size() > 1 && !primaryKeys.isEmpty())) {
                sql.append(",");
            }
            sql.append("\n");
        }

        // Add table-level PRIMARY KEY for multi-column PK
        if (primaryKeys.size() > 1) {
            sql.append("    PRIMARY KEY (").append(String.join(", ", primaryKeys)).append(")\n");
        }

        sql.append(");");

        // Add table comment
        String tableComment = tableMetadata.getComment() != null && !tableMetadata.getComment().trim().isEmpty()
                ? tableMetadata.getComment()
                : "Backup table generated on " + new java.util.Date();
        sql.append("\n\n-- Table comment: ").append(tableComment.replace("\n", " ").replace("\r", "")).append("\n");

        // Add column comments
        for (ColumnMetadata column : columns) {
            String columnName = column.getColumnName();
            String comment = column.getComment() != null && !column.getComment().trim().isEmpty()
                    ? column.getComment()
                    : generateColumnComment(
                    column.getColumnName(),
                    column.getTypeName(),
                    column.isNotNull(),
                    column.getPrecision(),
                    column.getScale(),
                    column.getGeometryType(),
                    column.getSrid()
            );
            sql.append("-- Column ").append(columnName).append(" comment: ")
                    .append(comment.replace("\n", " ").replace("\r", "")).append("\n");
        }

        return sql.toString();

    }

    // Generate fallback column comment, including PostGIS details
    private String generateColumnComment(String columnName, String pgType, boolean notNull, int precision, int scale, String geometryType, Integer srid) {
        StringBuilder comment = new StringBuilder();
        comment.append("Column ").append(columnName).append(" (PostgreSQL type: ").append(pgType.toUpperCase());

        if (precision > 0 && pgType.equalsIgnoreCase("varchar")) {
            comment.append(", length ").append(precision);
        } else if (precision > 0 && (pgType.equalsIgnoreCase("numeric") || pgType.equalsIgnoreCase("decimal"))) {
            comment.append(", precision ").append(precision).append(", scale ").append(scale);
        }

        comment.append("), ").append(notNull ? "not nullable" : "nullable");

        if (pgType.equalsIgnoreCase("geometry")) {
            comment.append(", stores WKT representation of PostGIS spatial data");
            if (geometryType != null && srid != null) {
                comment.append(" (").append(geometryType.toUpperCase()).append(", SRID ").append(srid).append(")");
            }
        } else if (pgType.equalsIgnoreCase("jsonb")) {
            comment.append(", stores JSON data");
        }

        return comment.toString();
    }

    private String mapPgTypeToSqliteBackupType(String pgType, int precision, int scale, boolean notNull) {
        StringBuilder sqlType = new StringBuilder();
        pgType = pgType.toLowerCase();

        switch (pgType) {
            case "geometry":
            case "varchar":
            case "char":
            case "text":
            case "json":
            case "jsonb":
            case "date":
            case "timestamp":
            case "timestamptz":
                sqlType.append("TEXT"); // GEOMETRY stored as WKT
                break;
            case "int4":
            case "integer":
            case "int8":
            case "bigint":
            case "int2":
            case "smallint":
            case "bool":
            case "boolean":
                sqlType.append("INTEGER");
                break;
            case "float8":
            case "double precision":
            case "numeric":
            case "decimal":
                sqlType.append("REAL");
                break;
            case "bytea":
                sqlType.append("BLOB");
                break;
            default:
                sqlType.append("TEXT");
        }

        if (notNull) {
            sqlType.append(" NOT NULL");
        }

        return sqlType.toString();
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
