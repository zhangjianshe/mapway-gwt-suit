package cn.mapway.common.geo.postgis;

import org.nutz.dao.jdbc.ValueAdaptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * PGpolygonAdaptor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class PGPointAdaptor implements ValueAdaptor {
    @Override
    public Object get(ResultSet rs, String colName) throws SQLException {
        return rs.getObject(colName);
    }

    @Override
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            stat.setNull(index, Types.NULL);
        } else {
            stat.setObject(index, obj, Types.OTHER);
        }
    }
}
