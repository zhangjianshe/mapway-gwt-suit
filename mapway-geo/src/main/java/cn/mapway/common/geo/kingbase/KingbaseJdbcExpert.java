package cn.mapway.common.geo.kingbase;

import cn.mapway.common.geo.postgis.*;
import cn.mapway.geo.geometry.GeoObject;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.jdbc.AbstractJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.Pojos;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.postgresql.geometric.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class KingbaseJdbcExpert extends AbstractJdbcExpert {

    private static final Log log = Logs.get();

    public KingbaseJdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    @Override
    public String getDatabaseType() {
        return "KINGBASE";
    }

    @Override
    public void formatQuery(Pojo pojo) {
        Pager pager = pojo.getContext().getPager();
        if (null != pager && pager.getPageNumber() > 0)
            pojo.append(Pojos.Items.wrapf(" LIMIT %d OFFSET %d",
                    pager.getPageSize(),
                    pager.getOffset()));
    }

    @Override
    public void formatQuery(Sql sql) {
        Pager pager = sql.getContext().getPager();
        if (null != pager && pager.getPageNumber() > 0) {
            sql.setSourceSql(sql.getSourceSql() + String.format(" LIMIT %d OFFSET %d",
                    pager.getPageSize(),
                    pager.getOffset()));
        }
    }

    @Override
    public String evalFieldType(MappingField mf) {
        if (mf.getCustomDbType() != null)
            return mf.getCustomDbType();

        switch (mf.getColumnType()) {
            case PSQL_JSON:
            case MYSQL_JSON:
                return "JSON"; // 完美适配 JSON 字段，解决你的报错
            case BOOLEAN:
                return "BOOLEAN";
            case TEXT:
                return "TEXT";
            case INT:
                if (mf.getWidth() > 0)
                    return "NUMERIC(" + mf.getWidth() + ")";
                return "INT";
            case FLOAT:
                if (mf.getWidth() > 0 && mf.getPrecision() > 0) {
                    return "NUMERIC(" + mf.getWidth() + "," + mf.getPrecision() + ")";
                }
                if (mf.getMirror().isDouble())
                    return "NUMERIC(15,10)";
                return "NUMERIC";
            case BINARY:
                return "BYTEA";
            case DATETIME:
            case TIMESTAMP:
                return "TIMESTAMP";
            default:
                break;
        }
        return super.evalFieldType(mf);
    }

    @Override
    public boolean createEntity(Dao dao, Entity<?> en) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + en.getTableName() + "(");
        for (MappingField mf : en.getMappingFields()) {
            if (mf.isReadonly())
                continue;

            sb.append('\n').append(safeColumnName(mf.getColumnNameInSql())).append(" ");

            if (mf.isId() && mf.isAutoIncreasement()) {
                sb.append(mf.getWidth() > 10 ? "BIGSERIAL" : "SERIAL");
            } else {
                sb.append(evalFieldType(mf));
                if (mf.isName() && en.getPkType() != PkType.NAME) {
                    sb.append(" UNIQUE NOT NULL");
                } else {
                    if (mf.isUnsigned())
                        sb.append(" UNSIGNED");
                    if (mf.isNotNull())
                        sb.append(" NOT NULL");
                    if (mf.hasDefaultValue())
                        addDefaultValue(sb, mf);
                }
            }
            sb.append(',');
        }

        List<MappingField> pks = en.getPks();
        if (!pks.isEmpty()) {
            sb.append('\n');
            sb.append(String.format("CONSTRAINT %s_pkey PRIMARY KEY (",
                    en.getTableName().replace('.', '_').replace('"', '_')));
            for (MappingField pk : pks) {
                sb.append(safeColumnName(pk.getColumnNameInSql())).append(',');
            }
            sb.setCharAt(sb.length() - 1, ')');
            sb.append("\n ");
        }

        sb.setCharAt(sb.length() - 1, ')');

        dao.execute(Sqls.create(sb.toString()));
        dao.execute(createIndexs(en).toArray(new Sql[0]));
        createRelation(dao, en);
        addComment(dao, en);

        return true;
    }

    /**
     * 修正关键字包裹逻辑：适配 Kingbase 的双引号规则
     */
    @Override
    public String wrapKeyword(String columnName, boolean force) {
        if (columnName == null) {
            return null;
        }
        if (force || (keywords != null && keywords.contains(columnName.toUpperCase()))) {
            return "\"" + columnName + "\"";
        }
        return null;
    }

    protected String safeColumnName(String columnName) {
        String str = wrapKeyword(columnName, Daos.FORCE_WRAP_COLUMN_NAME);
        return str == null ? columnName : str;
    }

    @Override
    public void checkDataSource(Connection conn) throws SQLException {
        if (log.isDebugEnabled()) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT version()")) {
                if (rs.next()) {
                    log.debug("Kingbase Version: " + rs.getString(1));
                }
            }
        }
    }

    @Override
    public List<String> getIndexNames(Entity<?> en, Connection conn) throws SQLException {
        String tableName = en.getTableName();
        // 兼容处理带 schema 的表名
        if (tableName.contains(".")) {
            tableName = tableName.substring(tableName.indexOf(".") + 1);
        }
        tableName = tableName.replace("\"", "");

        String sql = "SELECT indexname FROM pg_indexes WHERE tablename='" + tableName.toLowerCase() + "'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ArrayList<String> indexNames = new ArrayList<>(17);
            while (rs.next()) {
                indexNames.add(rs.getString("indexname"));
            }
            return indexNames;
        }
    }


    @Override
    public ValueAdaptor getAdaptor(MappingField ef) {
        //重载系统的转换器
        if (ColType.PSQL_JSON == ef.getColumnType()) {
            return new PGObject2JsonObjectAdaptor(ef.getType());
        }
        else if (ef.getMirror().isOf(GeoObject.class)) {
            return new GeoObjectAdaptor();
        }
        else if (ef.getMirror().isOf(PGpolygon.class)) {
            return new PGpolygonAdaptor();
        }
        else if (ef.getMirror().isOf(PGline.class)) {
            return new PGLineAdaptor();
        }
        else if (ef.getMirror().isOf(PGpoint.class)) {
            return new PGPointAdaptor();
        }
        else if (ef.getMirror().isOf(PGpath.class)) {
            return new PGPathAdaptor();
        }
        else if (ef.getMirror().isOf(PGcircle.class)) {
            return new PGCircleAdaptor();
        }
        else if (ef.getMirror().isOf(PGbox.class)) {
            return new PGBoxAdaptor();
        } else if(ef.getColumnType().equals(ColType.VARCHAR)){
            if(ef.getCustomDbType()!=null && ef.getCustomDbType().startsWith("GEOMETRY")){
                return new KingBaseWktAdaptor(ef.getCustomDbType());
            }
            else {
                return super.getAdaptor(ef);
            }
        }
        else {
            return super.getAdaptor(ef);
        }
    }
}