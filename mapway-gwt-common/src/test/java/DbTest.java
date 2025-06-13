import cn.mapway.ui.server.db.SqliteTools;

import java.sql.SQLException;

public class DbTest {
    public static void main(String[] args) {
        SqliteTools tools= null;
        try {
            tools = SqliteTools.create("/ibbackup/sys_region.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(tools.isTableExist("sys_region"));
    }
}
