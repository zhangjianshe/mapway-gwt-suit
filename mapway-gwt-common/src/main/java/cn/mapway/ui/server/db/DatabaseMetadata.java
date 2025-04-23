package cn.mapway.ui.server.db;

import cn.mapway.ui.shared.db.TableMetadata;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.lang.Strings;

import java.util.List;

/**
 * 数据库元数据　指向一个　SQLITEdb
 */
public class DatabaseMetadata {
    NutDao dao;

    /**
     * 创建一个SQLITE数据源
     *
     * @param sqliteDbPath
     */
    public DatabaseMetadata(String sqliteDbPath) {
        SimpleDataSource dataSource = new SimpleDataSource();
        dataSource.setJdbcUrl("jdbc:sqlite:" + sqliteDbPath);
        dao = new NutDao(dataSource);

        init();
    }

    public boolean init() {
        if (!dao.exists(TableMetadata.class)) {
            dao.create(TableMetadata.class, true);
        }
        return true;
    }

    /**
     * 所有表格
     *
     * @return
     */
    public List<TableMetadata> listTable(String db) {
        Cnd where;
        if (Strings.isNotBlank(db)) {
            where = Cnd.where("db", "=", db);
        } else {
            where = Cnd.NEW();
        }
        where.asc("db").asc("schema").asc("table_name");
        return dao.query(TableMetadata.class, where);
    }

    public void insert(TableMetadata tableMetadata) {
        dao.insert(tableMetadata);
    }
}
