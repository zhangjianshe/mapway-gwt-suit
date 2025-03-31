package cn.mapway.common.geo.postgis;

import cn.mapway.geo.geometry.GeoObject;
import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeo;
import net.postgis.jdbc.PGgeometry;
import org.nutz.dao.jdbc.ValueAdaptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Slf4j
public class WktAdaptor implements ValueAdaptor {
    @Override
    public Object get(ResultSet rs, String colName) throws SQLException {
        Object object = rs.getObject(colName);
        if (object != null) {
            if (object instanceof PGgeometry) {
                return ((PGgeometry) object).getGeometry().getValue();
            } else {
                log.warn("不能确定数据类型:{}", object.getClass().toString());
            }
        }
        else {
            return "POLYGON EMPTY";
        }
    }

    @Override
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            stat.setString(index, "POLYGON EMPTY");
        } else {
            assert obj instanceof String;
            stat.setObject(index, obj, Types.VARCHAR);
        }
    }
}
