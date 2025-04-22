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
import java.util.stream.Collectors;

/**
 * Pg数据库工具
 */
public class PgTools implements IDbSource , Closeable {
    final Connection connection;
    final String db;

    public PgTools(Connection connection, String db) {
        this.connection = connection;
        this.db = db;
    }

    public static PgTools create(String host, String port, String db, String userName, String password) throws SQLException {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + db + "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection connection = DriverManager.getConnection(url, userName, password);
        return new PgTools(connection, db);
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
     * 获取表元数据
     *
     * @param sourceTableName 　"public.sys_config"
     * @return 表的元数据
     * @throws SQLException
     */
    public TableMetadata fetchTableMetadata(String sourceTableName) throws SQLException {
        // Parse schema and table name
        String schema = "public";
        String[] tableParts = sourceTableName.split("\\.");
        if (tableParts.length == 2) {
            schema = tableParts[0];
            sourceTableName = tableParts[1];
        }

        // Fetch table comment
        String tableComment = "Table metadata fetched on " + new java.util.Date();
        String tableCommentQuery = "SELECT d.description " +
                "FROM pg_catalog.pg_description d " +
                "JOIN pg_catalog.pg_class c ON d.objoid = c.oid " +
                "JOIN pg_catalog.pg_namespace n ON c.relnamespace = n.oid " +
                "WHERE c.relname = ? AND n.nspname = ? AND d.objsubid = 0";
        try (PreparedStatement stmt = connection.prepareStatement(tableCommentQuery)) {
            stmt.setString(1, sourceTableName);
            stmt.setString(2, schema);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbComment = rs.getString("description");
                    if (dbComment != null && !dbComment.trim().isEmpty()) {
                        tableComment = dbComment;
                    }
                }
            }
        }

        // Create TableMetadata
        TableMetadata tableMetadata = new TableMetadata(db, schema, sourceTableName, tableComment);

        // Fetch PostGIS geometry metadata
        Map<String, Map<String, Object>> geometryInfo = new HashMap<>();
        String geometryQuery = "SELECT f_geometry_column, type, srid " +
                "FROM geometry_columns " +
                "WHERE f_table_schema = ? AND f_table_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(geometryQuery)) {
            stmt.setString(1, schema);
            stmt.setString(2, sourceTableName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("type", rs.getString("type"));
                    info.put("srid", rs.getInt("srid"));
                    geometryInfo.put(rs.getString("f_geometry_column"), info);
                }
            }
        } catch (SQLException e) {
            // geometry_columns may not exist or be accessible; proceed without PostGIS metadata
            System.err.println("Warning: Could not query geometry_columns: " + e.getMessage());
        }

        // Fetch column metadata
        // Modified column query to include default value and sequence
        String columnQuery = "SELECT a.attname, t.typname, a.attnotnull, " +
                "CASE WHEN t.typname = 'varchar' THEN a.atttypmod - 4 " +
                "     WHEN t.typname IN ('numeric', 'decimal') THEN ((a.atttypmod - 4) >> 16) " +
                "     ELSE 0 END AS precision, " +
                "CASE WHEN t.typname IN ('numeric', 'decimal') THEN (a.atttypmod - 4) & 65535 " +
                "     ELSE 0 END AS scale, " +
                "d.description, ad.adsrc AS default_value, " +
                "s.seqname, s.last_value AS seq_value " +
                "FROM pg_catalog.pg_attribute a " +
                "JOIN pg_catalog.pg_class c ON a.attrelid = c.oid " +
                "JOIN pg_catalog.pg_namespace n ON c.relnamespace = n.oid " +
                "JOIN pg_catalog.pg_type t ON a.atttypid = t.oid " +
                "LEFT JOIN pg_catalog.pg_description d ON d.objoid = c.oid AND d.objsubid = a.attnum " +
                "LEFT JOIN pg_catalog.pg_attrdef ad ON ad.adrelid = c.oid AND ad.adnum = a.attnum " +
                "LEFT JOIN pg_catalog.pg_depend dep ON dep.objid = c.oid AND dep.refobjsubid = a.attnum " +
                "LEFT JOIN pg_sequences s ON s.oid = dep.refobjid " +
                "WHERE c.relname = ? AND n.nspname = ? AND a.attnum > 0 AND NOT a.attisdropped " +
                "ORDER BY a.attnum";

        try (PreparedStatement stmt = connection.prepareStatement(columnQuery)) {
            stmt.setString(1, sourceTableName);
            stmt.setString(2, schema);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("attname");
                    String typeName = rs.getString("typname");
                    boolean notNull = rs.getBoolean("attnotnull");
                    int precision = rs.getInt("precision");
                    int scale = rs.getInt("scale");
                    String comment = rs.getString("description");
                    String defaultValue = rs.getString("default_value");
                    String seqName = rs.getString("seqname");
                    Long seqValue = rs.getLong("seq_value");
                    if (rs.wasNull()) seqValue = null;

                    // Get PostGIS geometry type and SRID
                    String geometryType = null;
                    Integer srid = null;
                    if (typeName.equalsIgnoreCase("geometry") && geometryInfo.containsKey(columnName)) {
                        Map<String, Object> info = geometryInfo.get(columnName);
                        geometryType = (String) info.get("type");
                        srid = (Integer) info.get("srid");
                    }

                    ColumnMetadata column = new ColumnMetadata(
                            columnName,
                            typeName,
                            precision,
                            scale,
                            notNull,
                            comment,
                            geometryType,
                            srid,
                            defaultValue,
                            seqName,
                            seqValue
                    );
                    tableMetadata.addColumn(column);
                }
            }
        }

        // Fetch primary key constraints
        String pkQuery = "SELECT a.attname " +
                "FROM pg_catalog.pg_constraint con " +
                "JOIN pg_catalog.pg_class c ON con.conrelid = c.oid " +
                "JOIN pg_catalog.pg_namespace n ON c.relnamespace = n.oid " +
                "JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid AND a.attnum = ANY(con.conkey) " +
                "WHERE con.contype = 'p' AND c.relname = ? AND n.nspname = ?";

        try (PreparedStatement stmt = connection.prepareStatement(pkQuery)) {
            stmt.setString(1, sourceTableName);
            stmt.setString(2, schema);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String pkColumn = rs.getString("attname");
                    tableMetadata.addPrimaryKeyColumn(pkColumn);
                }
            }
        }
        long rowCount = getRowCount(tableMetadata);
        return tableMetadata;
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
        int rowCount = (int) getRowCount(tableMetadata);

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


    /**
     * 从数据源中恢复数据
     * @param dbSource
     * @param tableMetadata
     * @param handler
     * @throws SQLException
     */
    public void restore(IDbSource dbSource, TableMetadata tableMetadata, IProgressHandler handler) throws SQLException {
        String pgTableName = tableMetadata.getSchema() + "." + tableMetadata.getTableName();
        List<ColumnMetadata> columns = tableMetadata.getColumns();

        //创建新表
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
            if (column.getTypeName().equalsIgnoreCase("geometry")) {
                Integer srid = column.getSrid();
                insertSql.append("ST_GeomFromText(?, ").append(srid != null ? srid : 0).append(")");
            } else {
                insertSql.append("?");
            }
            if (i < columns.size() - 1) {
                insertSql.append(", ");
            }
        }
        insertSql.append(")");
        connection.setAutoCommit(false);
        try(
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
                                if (wkt == null || wkt.equalsIgnoreCase("GEOMETRY EMPTY")) {
                                    insertStatement.setNull(paramIndex, java.sql.Types.VARCHAR);
                                } else {
                                    insertStatement.setString(paramIndex, wkt);
                                }
                            } else if (pgType.equals("bytea")) {
                                byte[] bytes = rs.getBytes(i + 1);
                                insertStatement.setBytes(paramIndex, bytes);
                            } else if (pgType.equals("bool") || pgType.equals("boolean")) {
                                // INTEGER (0/1) to BOOLEAN
                                int value = rs.getInt(i + 1);
                                insertStatement.setBoolean(paramIndex, value != 0);
                            } else {
                                Object value = rs.getObject(i + 1);
                                insertStatement.setObject(paramIndex, value);
                            }
                        }
                        // Execute INSERT
                        insertStatement.executeUpdate();

                        if (handler != null) {
                            if(length>0) {
                                int percent = (int) (Math.round(index * 100.) / length);
                                handler.progress(percent, "");
                            }
                            else {
                                handler.progress(100, "");
                            }
                        }
                        else {
                            System.out.println("Inserting row " + index + " of " + length);
                        }
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        connection.commit();
    }

    // Generate SELECT query with ST_AsText for geometry columns
    public String generateSelectQuery(TableMetadata tableMetadata) {
        StringBuilder query = new StringBuilder("SELECT ");
        List<ColumnMetadata> columns = tableMetadata.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            String colName = columns.get(i).getColumnName();
            if (columns.get(i).getTypeName().equalsIgnoreCase("geometry")) {
                query.append("ST_AsText(").append(colName).append(") AS ").append(colName);
            } else {
                query.append("\"").append(colName).append("\"");
            }
            if (i < columns.size() - 1) {
                query.append(", ");
            }
        }
        query.append(" FROM ").append(tableMetadata.getSchema()).append(".").append(tableMetadata.getTableName());
        return query.toString();
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

    public boolean isTableExist(String schema, String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, schema, tableName, null);
            return tables.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check table existence", e);
        }
    }

    public void dropTable(String schema, String tableName) {
        if (schema == null || schema.isEmpty()) {
            schema = "public";
        }
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("DROP TABLE " + schema + "." + tableName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop table", e);
        }
    }

    public void truncateTable(String schema, String tableName) {
        if (schema == null || schema.isEmpty()) {
            schema = "public";
        }
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("TRUNCATE TABLE " + schema + "." + tableName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to truncate table", e);
        }
    }

    public void createTable(TableMetadata tableMetadata, boolean dropIfExists) {
        try {
            Statement stmt = connection.createStatement();
            String sql = createSqlFromMetadata(tableMetadata, tableMetadata.getSchema(), tableMetadata.getTableName());
            if (dropIfExists) {
                if(tableMetadata.getSchema() == null || tableMetadata.getSchema().isEmpty()) {
                    tableMetadata.setSchema("public");
                }
                String tableName = tableMetadata.getSchema() + "." + tableMetadata.getTableName();
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
        // Validate inputs
        if (tableMetadata == null || tableMetadata.getColumns().isEmpty()) {
            throw new IllegalArgumentException("TableMetadata is null or has no columns");
        }

        // Default and sanitize schema and table name
        if (schemaName == null || schemaName.trim().isEmpty()) {
            schemaName = "public";
        }
        if (newTableName == null || newTableName.trim().isEmpty()) {
            newTableName = "generated_table";
        }
        // Quote identifiers to handle special characters
        String quotedSchemaName = "\"" + schemaName.replace("\"", "\"\"") + "\"";
        String quotedTableName = "\"" + newTableName.replace("\"", "\"\"") + "\"";
        String tableFullName = quotedSchemaName + "." + quotedTableName;

        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableFullName + " (\n");

        // Build column definitions
        List<ColumnMetadata> columns = tableMetadata.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            ColumnMetadata column = columns.get(i);
            String columnName = "\"" + column.getColumnName().replace("\"", "\"\"") + "\"";
            String sqlType = mapPgTypeToSqlType(
                    column.getTypeName(),
                    column.getPrecision(),
                    column.getScale(),
                    column.isNotNull(),
                    column.getGeometryType(),
                    column.getSrid()
            );

            sql.append("    ").append(columnName).append(" ").append(sqlType);

            // Add default value if present
            if (column.getDefaultValue() != null) {
                sql.append(" DEFAULT ").append(column.getDefaultValue());
            }

            if (i < columns.size() - 1 || !tableMetadata.getPrimaryKeyColumns().isEmpty()) {
                sql.append(",");
            }
            sql.append("\n");
        }

        // Add primary key constraint
        List<String> primaryKeys = tableMetadata.getPrimaryKeyColumns();
        if (!primaryKeys.isEmpty()) {
            sql.append("    CONSTRAINT ").append(quotedTableName.replace("\"", "")).append("_pkey PRIMARY KEY (");
            sql.append(primaryKeys.stream()
                    .map(pk -> "\"" + pk.replace("\"", "\"\"") + "\"")
                    .collect(Collectors.joining(", "))).append(")\n");
        }

        sql.append(");");

        // Add table comment
        String tableComment = tableMetadata.getComment() != null && !tableMetadata.getComment().trim().isEmpty()
                ? tableMetadata.getComment()
                : "Table generated on " + new java.util.Date();
        sql.append("\n\nCOMMENT ON TABLE ").append(tableFullName)
                .append(" IS '").append(tableComment.replace("'", "''")).append("';");

        // Add column comments
        for (ColumnMetadata column : columns) {
            String columnName = "\"" + column.getColumnName().replace("\"", "\"\"") + "\"";
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
            sql.append("\nCOMMENT ON COLUMN ").append(tableFullName).append(".").append(columnName)
                    .append(" IS '").append(comment.replace("'", "''")).append("';");
        }

        // Add sequence definitions (if applicable)
        for (ColumnMetadata column : columns) {
            if (column.getSeqName() != null) {
                sql.append("\nCREATE SEQUENCE ").append(quotedSchemaName).append(".\"").append(column.getSeqName()).append("\"")
                        .append(" START WITH ").append(column.getSeqValue() != null ? column.getSeqValue() : 1).append(";");
            }
        }

        return sql.toString();

    }

    // Generate fallback column comment, including PostGIS details
    private String generateColumnComment(String columnName, String pgType, boolean notNull, int precision, int scale, String geometryType, Integer srid) {
        StringBuilder comment = new StringBuilder();
        comment.append("Column ").append(columnName).append(" of type ").append(pgType.toUpperCase());

        if (precision > 0 && pgType.equalsIgnoreCase("varchar")) {
            comment.append(" with length ").append(precision);
        } else if (precision > 0 && (pgType.equalsIgnoreCase("numeric") || pgType.equalsIgnoreCase("decimal"))) {
            comment.append(" with precision ").append(precision).append(" and scale ").append(scale);
        }

        comment.append(", ").append(notNull ? "not nullable" : "nullable");

        if (pgType.equalsIgnoreCase("geometry")) {
            comment.append(", stores PostGIS spatial data");
            if (geometryType != null && srid != null) {
                comment.append(" (").append(geometryType.toUpperCase()).append(", SRID ").append(srid).append(")");
            }
        } else if (pgType.equalsIgnoreCase("jsonb")) {
            comment.append(", stores JSON data");
        }

        return comment.toString();
    }

    // Map PostgreSQL type to SQL type, including PostGIS geometry
    private String mapPgTypeToSqlType(String pgType, int precision, int scale, boolean notNull, String geometryType, Integer srid) {
        StringBuilder sqlType = new StringBuilder();
        pgType = pgType.toLowerCase();

        if (pgType.equals("geometry") && geometryType != null && srid != null) {
            sqlType.append("GEOMETRY(").append(geometryType.toUpperCase()).append(",").append(srid).append(")");
        } else {
            switch (pgType) {
                case "varchar":
                    sqlType.append("VARCHAR").append(precision > 0 ? "(" + precision + ")" : "(255)");
                    break;
                case "char":
                    sqlType.append("CHAR").append(precision > 0 ? "(" + precision + ")" : "(1)");
                    break;
                case "text":
                    sqlType.append("TEXT");
                    break;
                case "int4":
                case "integer":
                    sqlType.append("INTEGER");
                    break;
                case "int8":
                case "bigint":
                    sqlType.append("BIGINT");
                    break;
                case "int2":
                case "smallint":
                    sqlType.append("SMALLINT");
                    break;
                case "float8":
                case "double precision":
                    sqlType.append("DOUBLE PRECISION");
                    break;
                case "numeric":
                case "decimal":
                    sqlType.append("NUMERIC")
                            .append(precision > 0 ? "(" + precision + "," + scale + ")" : "(10,2)");
                    break;
                case "date":
                    sqlType.append("DATE");
                    break;
                case "timestamp":
                case "timestamptz":
                    sqlType.append("TIMESTAMP");
                    break;
                case "bool":
                case "boolean":
                    sqlType.append("BOOLEAN");
                    break;
                case "bytea":
                    sqlType.append("BYTEA");
                    break;
                case "geometry":
                    sqlType.append("GEOMETRY"); // Fallback if no geometryType/srid
                    break;
                case "json":
                case "jsonb":
                    sqlType.append("JSONB");
                    break;
                default:
                    sqlType.append("TEXT");
            }
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
