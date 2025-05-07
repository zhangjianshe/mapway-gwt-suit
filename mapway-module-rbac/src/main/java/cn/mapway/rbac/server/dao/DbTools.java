package cn.mapway.rbac.server.dao;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.trans.Trans;

import java.util.ArrayList;
import java.util.List;

public class DbTools {
    Dao dao;
    public DbTools(Dao dao)
    {
        this.dao=dao;
    }

    public void execute(List<String> list) {
        List<Sql> sqlList = new ArrayList<Sql>();
        for (String sql : list) {
            sqlList.add(Sqls.create(sql));
        }
        executeSqls(sqlList);
    }

    public void executeSqls(List<Sql> sqlList) {
        //需要按順序執行操作
        //在一个事物中执行这个操作
        Trans.exec(() -> {
            for (Sql sql : sqlList) {
                sql.setCallback((t, r, w) -> null);
                dao.execute(sql);
            }
        });
    }
}
