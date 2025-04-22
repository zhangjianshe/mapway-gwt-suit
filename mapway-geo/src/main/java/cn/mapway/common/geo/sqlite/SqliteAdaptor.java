package cn.mapway.common.geo.sqlite;

import cn.mapway.common.geo.postgis.*;
import cn.mapway.geo.geometry.GeoObject;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.jdbc.sqlite.SQLiteJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Strings;
import org.postgresql.geometric.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class SqliteAdaptor extends SQLiteJdbcExpert {
    private static final Logger log = LoggerFactory.getLogger(SqliteAdaptor.class);

    public SqliteAdaptor(JdbcExpertConfigFile conf) {
        super(conf);
        log.info("Sqlite 转换初始化");
    }

    public void addDefaultValue(StringBuilder sb, MappingField mf) {
        String defaultValue = this.getDefaultValue(mf);
        if (Strings.isNotBlank(defaultValue)) {
            if (defaultValue.contains("::character")) {
                sb.append(" DEFAULT ").append(defaultValue);
            } else {
                super.addDefaultValue(sb, mf);
            }
        }

    }

    @Override
    public boolean createEntity(Dao dao, Entity<?> en) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + en.getTableName() + "(");
        if (en.getPks().size() > 1 && en.getPkType() == PkType.ID) {
            return false;
        }
        // 创建字段
        boolean mPks = en.getPks().size() > 1;
        for (MappingField mf : en.getMappingFields()) {
            if (mf.isReadonly())
                continue;
            sb.append('\n').append(mf.getColumnNameInSql());
            // Sqlite的整数型主键,一般都是自增的,必须定义为(PRIMARY KEY
            // AUTOINCREMENT),但这样就无法定义多主键!!
            if (mf.isId() && en.getPkType() == PkType.ID) {
                sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
                continue;
            } else
                sb.append(' ').append(evalFieldType(mf));
            // 非主键的 @Name，应该加入唯一性约束
            if (mf.isName() && en.getPkType() != PkType.NAME) {
                sb.append(" UNIQUE NOT NULL");
            }
            // 普通字段
            else {
                if (mf.isUnsigned())
                    sb.append(" UNSIGNED");
                if (mf.isNotNull())
                    sb.append(" NOT NULL");
                if (mf.isPk() && !mPks) {// 复合主键需要另外定义
                    sb.append(" PRIMARY KEY");
                }
                if (mf.hasDefaultValue())
                    addDefaultValue(sb, mf);
            }
            sb.append(',');
        }
        // 创建主键
        List<MappingField> pks = en.getPks();
        if (mPks) {
            sb.append('\n');
            sb.append("constraint pk_").append(en.getTableName()).append(" PRIMARY KEY (");
            for (MappingField pk : pks) {
                sb.append(pk.getColumnNameInSql()).append(',');
            }
            sb.setCharAt(sb.length() - 1, ')');
            sb.append("\n ");
        }

        // 结束表字段设置
        sb.setCharAt(sb.length() - 1, ')');

        // 执行创建语句
        dao.execute(Sqls.create(sb.toString()));

        // 创建索引
        dao.execute(createIndexs(en).toArray(new Sql[0]));
        // 创建关联表
        createRelation(dao, en);

        return true;
    }

    public ValueAdaptor getAdaptor(MappingField ef) {
        if (ColType.PSQL_JSON == ef.getColumnType()) {
            return new SqliteJsonAdaptor(ef.getType());
        } else if (ef.getMirror().isOf(GeoObject.class)) {
            return new GeoObjectAdaptor();
        } else if (ef.getMirror().isOf(PGpolygon.class)) {
            return new PGpolygonAdaptor();
        } else if (ef.getMirror().isOf(PGline.class)) {
            return new PGLineAdaptor();
        } else if (ef.getMirror().isOf(PGpoint.class)) {
            return new PGPointAdaptor();
        } else if (ef.getMirror().isOf(PGpath.class)) {
            return new PGPathAdaptor();
        } else if (ef.getMirror().isOf(PGcircle.class)) {
            return new PGCircleAdaptor();
        } else  {
            return super.getAdaptor(ef);
        }
    }

    public String evalFieldType(MappingField mf) {
        if (Objects.requireNonNull(mf.getColumnType()) == ColType.FLOAT) {
            if (mf.getWidth() > 0 && mf.getPrecision() > 0) {
                return "NUMERIC(" + mf.getWidth() + "," + mf.getPrecision() + ")";
            } else {
                if (mf.getMirror().isDouble()) {
                    return "NUMERIC(53,10)";
                }

                return "NUMERIC";
            }
        }
        if (mf.getColumnName().compareToIgnoreCase("geom") == 0) {
            return "TEXT";
        }
        return super.evalFieldType(mf);
    }
}
