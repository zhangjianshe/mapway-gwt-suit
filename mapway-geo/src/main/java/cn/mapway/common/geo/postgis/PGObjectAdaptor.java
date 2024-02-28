package cn.mapway.common.geo.postgis;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.jdbc.ValueAdaptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * PGpolygonAdaptor
 * PGObject 对象 我们 将转换为GeoJSON格式
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class PGObjectAdaptor implements ValueAdaptor {
    public PGObjectAdaptor() {
        log.info("自定义转换初始化");
    }

    @Override
    public Object get(ResultSet rs, String colName) throws SQLException {
        log.info("col {}",colName);
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
