package cn.mapway.ui.server.db;

import cn.mapway.ui.shared.db.ColumnMetadata;
import cn.mapway.ui.shared.db.TableIndex;
import cn.mapway.ui.shared.db.TableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.*;
import org.nutz.lang.util.Regex;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Pg数据库工具
 * --------------------------
 * 该工具可以将一数据库表的元数据和数据导出到一个SQLITE中
 * 也可以从Sqlite中恢复表
 * SQLite文件包含了数据表的定义和数据
 */
@Slf4j
public class PgTools implements IDbSource, Closeable {
    final Connection connection;
    final String db;

    public PgTools(Connection connection, String db) {
        this.connection = connection;
        this.db = db;
    }


    /**
     * 备份一张表到sqlite中
     * @param tableName
     * @param sqliteFile
     * @throws Exception
     */
    public void backupToSqlite(String schema,String tableName,String sqliteFile,IProgressHandler handler) throws Exception {

        File file = new File(sqliteFile);
        if(file.exists())
        {
            file.delete();
        }
        if(!isTableExist(schema,tableName)) {
            throw new RuntimeException("表不存在");
        }
        SqliteTools sqliteTools = SqliteTools.create(sqliteFile);
        TableMetadata tableMetadata = fetchTableMetadata(schema, tableName);
        sqliteTools.createMetaTable(tableMetadata);

        sqliteTools.createTable(tableMetadata, true);
        sqliteTools.restore(this,tableMetadata,handler);
        sqliteTools.close();
    }

    /**
     * 从sqlite中恢复一张表
     * @param schema
     * @param tableName
     * @param sqliteFile
     * @param handler
     * @throws Exception
     */
    public void restoreFromSqlite(String schema,String tableName,String sqliteFile, IProgressHandler handler) throws Exception {
        File file = new File(sqliteFile);
        if(!file.exists())
        {
            throw new RuntimeException("文件不存在"+sqliteFile);
        }
        SqliteTools sqliteTools = SqliteTools.create(sqliteFile);
        TableMetadata tableMetadata = sqliteTools.readMetaData();
        if(tableMetadata == null)
        {
            throw new RuntimeException("文件"+sqliteFile+"不是一个合法的数据表备份文件");
        }

        if(!sqliteTools.isTableExist(tableMetadata.getTableName()))
        {
            throw new RuntimeException("文件"+sqliteFile+"不是一个合法的数据表备份文件,数据表不存在");
        }
        restore(sqliteTools,tableMetadata,tableMetadata,handler);
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
    public TableMetadata fetchTableMetadata(String schema,String sourceTableName) throws SQLException {
        // Parse schema and table name
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
        // Fetch column metadata
        String columnQuery = "SELECT a.attname,\n" +
                "       CASE WHEN t.typname = 'varchar' AND a.atttypmod = -1 THEN 'text'\n" +
                "            ELSE t.typname END AS typname,\n" +
                "       a.attnotnull,\n" +
                "       CASE WHEN t.typname = 'varchar' AND a.atttypmod = -1 THEN 0\n" +
                "            WHEN t.typname = 'varchar' THEN a.atttypmod - 4\n" +
                "            WHEN t.typname IN ('numeric', 'decimal') THEN ((a.atttypmod - 4) >> 16)\n" +
                "            ELSE 0 END AS precision,\n" +
                "       CASE WHEN t.typname IN ('numeric', 'decimal') THEN (a.atttypmod - 4) & 65535\n" +
                "            ELSE 0 END AS scale,\n" +
                "       d.description,\n" +
                "       pg_get_expr(ad.adbin, ad.adrelid) AS default_value,\n" +
                "       n2.nspname AS seq_schema,\n" +
                "       s.relname AS seqname,\n" +
                "       NULL AS seq_value,\n" +
                "       CASE WHEN pg_get_expr(ad.adbin, ad.adrelid) LIKE 'nextval(%' THEN\n" +
                "            regexp_replace(pg_get_expr(ad.adbin, ad.adrelid), '.*''(.+)''.*', '\\1')\n" +
                "            ELSE NULL END AS derived_seqname\n" +
                "FROM pg_catalog.pg_attribute a\n" +
                "JOIN pg_catalog.pg_class c ON a.attrelid = c.oid\n" +
                "JOIN pg_catalog.pg_namespace n ON c.relnamespace = n.oid\n" +
                "JOIN pg_catalog.pg_type t ON a.atttypid = t.oid\n" +
                "LEFT JOIN pg_catalog.pg_description d ON d.objoid = c.oid AND d.objsubid = a.attnum\n" +
                "LEFT JOIN pg_catalog.pg_attrdef ad ON ad.adrelid = c.oid AND ad.adnum = a.attnum\n" +
                "LEFT JOIN pg_catalog.pg_depend dep ON dep.objid = c.oid AND dep.refobjsubid = a.attnum\n" +
                "LEFT JOIN pg_catalog.pg_class s ON s.oid = dep.refobjid AND s.relkind = 'S'\n" +
                "LEFT JOIN pg_catalog.pg_namespace n2 ON s.relnamespace = n2.oid\n" +
                "WHERE c.relname = ? AND n.nspname = ?\n" +
                "AND a.attnum > 0 AND NOT a.attisdropped\n" +
                "ORDER BY a.attnum;";

        try (PreparedStatement stmt = connection.prepareStatement(columnQuery)) {
            // Validate inputs
            if (sourceTableName == null || sourceTableName.trim().isEmpty()) {
                throw new IllegalArgumentException("Table name cannot be null or empty");
            }
            if (schema == null || schema.trim().isEmpty()) {
                schema = "public";
            }

            // Set parameters
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
                    // default value  nextval('sss'::regclass) nextval('layer_1_2_fid_seq'::regclass)
                    String seqSchema = rs.getString("seq_schema");
                    String seqName = rs.getString("seqname");
                    String derivedSeqName = rs.getString("derived_seqname");
                    Long seqValue = rs.getLong("seq_value");
                    if (rs.wasNull()) seqValue = 0L;

                    // Clean default value (remove type casts)
                    /*if (defaultValue != null && defaultValue.contains("::")) {
                    nextval('table_test_is_seq'::regclass)
                        defaultValue = defaultValue.substring(0, defaultValue.indexOf("::"));
                    }*/

                    // Combine seqSchema and seqName or use derivedSeqName
                    // default value  nextval('sss'::regclass) nextval('layer_1_2_fid_seq'::regclass)
                    // based defualt value to parse sqeuence
                    if (Strings.isNotBlank(defaultValue)) {
                        defaultValue = defaultValue.trim();

                        if (defaultValue.startsWith("nextval(")) {
                            String regex = "'(.*)'";
                            Pattern pattern = Regex.getPattern(regex);
                            Matcher matcher = pattern.matcher(defaultValue);
                            if (matcher.find()) {
                                seqName = matcher.group(1);
                                seqValue = getSeqValue(tableMetadata.getSchema(), seqName);
                            }
                        } else {
                            seqName = null;
                            seqValue = 0L;
                        }
                    } else {
                        seqName = null;
                        seqValue = 0L;
                    }


                    // Get PostGIS geometry type and SRID (from earlier query)
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
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage() + " for table: " + sourceTableName + ", schema: " + schema);
            throw e;
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
        //获取表Index
        String sql = "SELECT\n" +
                "                    i.relname AS index_name,\n" +
                "                    idx.indisprimary AS is_primary,\n" +
                "                    idx.indisunique AS is_unique,\n" +
                "                    am.amname AS index_type,\n" +
                "                    array_to_string(array_agg(a.attname), ', ') AS columns,\n" +
                "                    pg_get_indexdef(idx.indexrelid) AS index_definition\n" +
                "                FROM\n" +
                "                    pg_class t\n" +
                "                    JOIN pg_index idx ON t.oid = idx.indrelid\n" +
                "                    JOIN pg_class i ON i.oid = idx.indexrelid\n" +
                "                    JOIN pg_am am ON i.relam = am.oid\n" +
                "                    JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY(idx.indkey)\n" +
                "                    JOIN pg_namespace n ON t.relnamespace = n.oid\n" +
                "                WHERE\n" +
                "                    t.relname = ?\n" +
                "                    AND n.nspname = ?\n" +
                "                    AND t.relkind = 'r'\n" +
                "                GROUP BY\n" +
                "                    i.relname, idx.indisprimary, idx.indisunique, am.amname, idx.indexrelid\n" +
                "                ORDER BY\n" +
                "                    i.relname";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sourceTableName);
            pstmt.setString(2, schema);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TableIndex tableIndex = new TableIndex();
                tableIndex.name = rs.getString("index_name");
                tableIndex.isUnique = rs.getBoolean("is_unique");
                tableIndex.isPrimary = rs.getBoolean("is_primary");
                tableIndex.indexType = rs.getString("index_type");
                tableIndex.columns = rs.getString("columns");
                tableIndex.definition = rs.getString("index_definition");
                tableMetadata.getIndexes().add(tableIndex);
            }
        }

        long rowCount = getRowCount(tableMetadata);
        return tableMetadata;
    }

    private Long getSeqValue(String schema, String seqName) {
        String sql = "SELECT last_value FROM " + schema + "." + seqName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Long seqValue = rs.getLong(1);
                return seqValue;
            }
        } catch (SQLException e) {
            // Log the error and handle it appropriately
            throw new RuntimeException("Failed to get count for table " + seqName, e);
        }
        return 0L;
    }

    /**
     * 更新表索引
     *
     * @param tableMetadata
     */
    public void updateTableIndex(TableMetadata tableMetadata) {

        if (Lang.isNotEmpty(tableMetadata.getIndexes())) {
            for (TableIndex tableIndex : tableMetadata.getIndexes()) {
                if (!tableIndex.isPrimary) {
                    StringBuilder sql = new StringBuilder();
                    sql.append("DROP INDEX IF EXISTS " + tableMetadata.getSchema() + "." + tableIndex.name + ";");
                    sql.append(tableIndex.definition);
                    System.out.println("process table index sql:" + sql);
                    try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
                        pstmt.executeUpdate();
                        connection.commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
     *
     * @param dbSource
     * @param sourceMetadata 原表元数据
     * @param targetMetadata 目标表元数据
     * @param handler
     * @throws SQLException
     */
    public void restore(IDbSource dbSource, TableMetadata sourceMetadata,TableMetadata targetMetadata, IProgressHandler handler) throws SQLException {
        String pgTableName = targetMetadata.getSchema() + "." + targetMetadata.getTableName();
        List<ColumnMetadata> columns = targetMetadata.getColumns();

        //需不需要创建表
        createTable(targetMetadata, false);

        //插入记录对应的SQL statement
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
            } else if (column.getTypeName().equalsIgnoreCase("json")
                    || column.getTypeName().equalsIgnoreCase("jsonb")) {
                insertSql.append("?::json"); // Explicitly cast to json or jsonb
            } else {
                insertSql.append("?");
            }
            if (i < columns.size() - 1) {
                insertSql.append(", ");
            }
        }
        insertSql.append(")");
        connection.setAutoCommit(false);
        try (
                PreparedStatement insertStatement = connection.prepareStatement(insertSql.toString())
        ) {
            dbSource.eachRow(sourceMetadata, new Each<ResultSet>() {
                @Override
                public void invoke(int index, ResultSet rs, int length) throws ExitLoop, ContinueLoop, LoopException {
                    try {
                        for (int i = 0; i < columns.size(); i++) {
                            ColumnMetadata column = columns.get(i);
                            int paramIndex = i + 1;
                            fillColumn(insertStatement, rs, column, paramIndex);
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
                        try {
                            System.out.println("ERROR" + sourceMetadata.getTableName() + " " + e.getMessage() + Strings.safeToString(rs.getObject(1), ""));
                        } catch (SQLException ex) {
                        }
                        insertStatement.toString();
                        throw new RuntimeException(e);
                    }
                }
            });
            if (handler != null) {
                handler.progress(100, "");
            } else {
                System.out.println("process finished");
            }
        }
        connection.commit();

        // update table index
        updateTableIndex(targetMetadata);

        // update sequence value
        updateSequenceValue(targetMetadata);
    }

    /**
     *
     * @param tableMetadata
     */
    private void updateSequenceValue(TableMetadata tableMetadata) {
        List<ColumnMetadata> columns = tableMetadata.getColumns();
        for (ColumnMetadata column : columns) {
            if(Strings.isNotBlank(column.getSeqName()))
            {
                try {
                    //如果序列不存在 则创建，否则更新
                    String createSeqSql = "CREATE SEQUENCE IF NOT EXISTS \"" + tableMetadata.getSchema() + "\".\"" + column.getSeqName() + "\" INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;";
                    log.info("CREATE SEQUENCE {}", createSeqSql);
                    PreparedStatement createSeqStatement = connection.prepareStatement(createSeqSql);
                    // Use execute() instead of executeUpdate()
                    boolean hasResultSet = createSeqStatement.execute();
                    // Optionally, you can consume the result set if it exists
                    if (hasResultSet) {
                        ResultSet resultSet = createSeqStatement.getResultSet();
                        // You might not need to do anything with the resultSet here
                        resultSet.close();
                    }
                    createSeqStatement.close();
                    connection.commit();

                    // SELECT setval('"public"."biz_task_id"', 123, false);

                    String updateSeqSql = "SELECT setval('\"" + tableMetadata.getSchema() + "\".\"" + column.getSeqName() + "\"', " + (column.getSeqValue()+1) + ", false); ";
                    log.info("UPDATE SEQUENCE {}", updateSeqSql);
                    PreparedStatement updateSeqStatement = connection.prepareStatement(updateSeqSql);
                    // Use execute() instead of executeUpdate()
                     hasResultSet = updateSeqStatement.execute();
                    // Optionally, you can consume the result set if it exists
                    if (hasResultSet) {
                        ResultSet resultSet = updateSeqStatement.getResultSet();
                        // You might not need to do anything with the resultSet here
                        resultSet.close();
                    }
                    updateSeqStatement.close();
                    connection.commit();

                    // alter table's field's defaultValue
                    String alterSeqSql = "ALTER TABLE \"" + tableMetadata.getSchema() + "\".\"" + tableMetadata.getTableName()
                            + "\" ALTER COLUMN \"" + column.getColumnName()
                            + "\" SET DEFAULT nextval('\"" + tableMetadata.getSchema() + "\".\"" + column.getSeqName() + "\"');";

                    log.info("ALTER SEQUENCE {}", alterSeqSql);
                    PreparedStatement alterSeqStatement = connection.prepareStatement(alterSeqSql);
                    // Use execute() instead of executeUpdate()
                    hasResultSet = alterSeqStatement.execute();
                    // Optionally, you can consume the result set if it exists
                    if (hasResultSet) {
                        ResultSet resultSet = alterSeqStatement.getResultSet();
                        // You might not need to do anything with the resultSet here
                        resultSet.close();
                    }
                    alterSeqStatement.close();
                    connection.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 填充数据据
     *
     * @param insertStatement
     * @param rs
     * @param column
     * @param paramIndex
     */
    private void fillColumn(PreparedStatement insertStatement, ResultSet rs, ColumnMetadata column, int paramIndex) throws SQLException {
        String columnType=column.getTypeName().toLowerCase();
        if("geometry".equals(columnType))
        {
            String wkt = rs.getString(paramIndex);
            String temp = "";
            if (wkt != null) {
                temp = wkt.toLowerCase();
            }
            if (wkt == null || temp.endsWith("empty") || temp.contains("inf")) {
                if (Strings.isBlank(column.getGeometryType()) || column.getGeometryType().equalsIgnoreCase("geometry")) {
                    wkt = "GEOMETRYCOLLECTION EMPTY";
                } else {
                    wkt = column.getGeometryType() + " EMPTY";
                }
            }
            insertStatement.setString(paramIndex, wkt);
            return;
        }
        Object v=rs.getObject(paramIndex);
        if(v==null)
        {
            insertStatement.setObject(paramIndex,null);
            return;
        }
        switch (column.getTypeName().toLowerCase()) {
            case "geometry":

            case "char":
            case "varchar":
            case "text":
            case "json":
            case "jsonb":
                insertStatement.setString(paramIndex, rs.getString(paramIndex));
                break;
            case "date":
            case "timestamp":
            case "timestamptz":
                String tv = rs.getString(paramIndex);
                if (tv != null) {
                    insertStatement.setDate(paramIndex, new java.sql.Date(Long.parseLong(tv)));
                } else {
                    insertStatement.setNull(paramIndex, Types.DATE);
                }
                break;
            case "int4":
            case "integer":
                insertStatement.setInt(paramIndex, rs.getInt(paramIndex));
                break;
            case "int8":
            case "bigint":
                insertStatement.setLong(paramIndex, rs.getLong(paramIndex));
                break;
            case "int2":
            case "smallint":
                insertStatement.setShort(paramIndex, rs.getShort(paramIndex));
                break;
            case "bool":
            case "boolean":
                // INTEGER (0/1) to BOOLEAN rs.getInt(paramIndex);
                Integer bV = rs.getInt(paramIndex);
                insertStatement.setBoolean(paramIndex, bV != null && bV > 0);
                break;
            case "float8":
            case "double precision":
            case "numeric":
            case "decimal":
                Double rsDouble = rs.getDouble(paramIndex);
                if(     rsDouble == Double.NaN
                        || rsDouble == Double.NEGATIVE_INFINITY
                        || rsDouble == Double.POSITIVE_INFINITY
                        || rsDouble<=-1.7976931348623157E308
                        || rsDouble>=1.7976931348623157E308){
                    insertStatement.setDouble(paramIndex,0);
                    break;
                }
                if(column.getScale()==0)
                {
                    if(column.getPrecision()<=10)
                    {
                        insertStatement.setInt(paramIndex,  rsDouble.intValue());
                    }
                    else {
                        insertStatement.setLong(paramIndex, rsDouble.longValue());
                    }
                }
                else {
                    insertStatement.setDouble(paramIndex, rsDouble);
                }
                break;
            case "bytea":
                insertStatement.setBytes(paramIndex, rs.getBytes(paramIndex));
                break;
            default:
                insertStatement.setObject(paramIndex, rs.getObject(paramIndex));
        }
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

    public void dropTable(TableMetadata tableMetadata) {
        if (Strings.isBlank(tableMetadata.getSchema())) {
            tableMetadata.setSchema("public");
        }
        try {
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            String dropSql = generateDropTableSql(tableMetadata);
            stmt.execute(dropSql);
            stmt.close();
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

    public String generateDropTableSql(TableMetadata tableMetadata) {
        String schemaName = tableMetadata.getSchema();
        String tableName = tableMetadata.getTableName();
        if (schemaName == null || schemaName.trim().isEmpty()) {
            schemaName = "public";
        }
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name is required");
        }
        String quotedSchemaName = "\"" + schemaName.replace("\"", "\"\"") + "\"";
        String quotedTableName = "\"" + tableName.replace("\"", "\"\"") + "\"";
        String tableFullName = quotedSchemaName + "." + quotedTableName;

        StringBuilder sql = new StringBuilder("DROP TABLE IF EXISTS " + tableFullName + " CASCADE;");

        // Drop associated sequences
        for (ColumnMetadata column : tableMetadata.getColumns()) {
            if (column.getSeqName() != null) {
                // Validate sequence existence
                sql.append("\nDROP SEQUENCE IF EXISTS \"")
                        .append(column.getSeqName())
                        .append("\" CASCADE;");
            }
        }
        return sql.toString();
    }

    public void createTable(TableMetadata tableMetadata, boolean dropIfExists) {
        try {
            if (dropIfExists) {
                dropTable(tableMetadata);
            }
            if (!isTableExist(tableMetadata.getSchema(), tableMetadata.getTableName())) {
                connection.setAutoCommit(true);
                String sql = createSqlFromMetadata(tableMetadata, tableMetadata.getSchema(), tableMetadata.getTableName());
                System.out.println(sql);
                Statement stmt = connection.createStatement();
                stmt.execute(sql);
                stmt.close();
            }
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

        StringBuilder sql = new StringBuilder();
        // Build column definitions
        List<ColumnMetadata> columns = tableMetadata.getColumns();
        //first create sequence
        // Add sequence definitions (if applicable)
        for (ColumnMetadata column : columns) {
            if (column.getSeqName() != null) {
                sql.append("\nCREATE SEQUENCE IF NOT EXISTS ").append(quotedSchemaName).append(".\"").append(column.getSeqName()).append("\"")
                        .append(" START WITH ").append(column.getSeqValue() != null ? column.getSeqValue() : 1).append(";");
            }
        }

        sql.append("CREATE TABLE " + tableFullName + " (\n");

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
                : "";
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
                case "float4":
                case "float8":
                case "double precision":
                    sqlType.append("DOUBLE PRECISION");
                    break;
                case "numeric":
                case "decimal":
                    if(scale==0)
                    {
                        if(precision<=10) {
                            sqlType.append("INTEGER");
                        }
                        else {
                            sqlType.append("BIGINT");
                        }
                    }
                    else {
                        sqlType.append("NUMERIC")
                                .append(precision > 0 ? "(" + precision + "," + scale + ")" : "(10,2)");
                    }
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

    /**
     * 判断postgres是否存在数据库
     *
     * @param databaseName
     * @return
     */
    public boolean isDatabaseExist(String databaseName) {
        String query = "SELECT datname FROM pg_database WHERE datname = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, databaseName);
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            // Check if the database exists
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建数据库
     *
     * @param dbName
     * @param owner
     */
    public boolean createDatabase(String dbName, String owner) {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE DATABASE " + dbName;
            if (Strings.isNotBlank(owner)) {
                sql += " WITH OWNER = " + owner;
            }
            statement.executeUpdate(sql);
            System.out.println("Database '" + dbName + "' created successfully.");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 数据库安装扩展
     *
     * @param extension
     * @return
     */
    public boolean installExtension(String extension) {
        if (Strings.isBlank(extension)) {
            return false;
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT extname FROM pg_extension WHERE extname = 'postgis'")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("PostGIS extension is already installed in '" + db + "'.");
                return true;
            } else {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE EXTENSION " + extension);
                    System.out.println("PostGIS extension added successfully.");
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String string = "-179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        Double d = Double.parseDouble(string);
        System.out.println(d<=1.7976931348623157E308 || d>=1.7976931348623157E308);
        System.out.println(d);
    }
}