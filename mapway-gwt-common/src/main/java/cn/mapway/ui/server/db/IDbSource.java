package cn.mapway.ui.server.db;

import cn.mapway.ui.shared.db.TableMetadata;
import org.nutz.lang.Each;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDbSource {
     void eachRow(TableMetadata tableMetadata, Each<ResultSet> consumer) throws SQLException;
}
