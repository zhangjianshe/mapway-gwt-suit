package cn.mapway.common.geo.postgis;

import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.Geometry;
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
                PGgeometry pGgeometry= (PGgeometry) object;
                return toWKT(pGgeometry);
            } else {
                log.warn("不能确定数据类型:{}", object.getClass().toString());
                return "POLYGON EMPTY";
            }
        } else {
            return "POLYGON EMPTY";
        }
    }

    private String toWKT(PGgeometry pGgeometry) {
        StringBuffer stringBuilder = new StringBuffer();
        pGgeometry.getGeometry().outerWKT(stringBuilder);
        return stringBuilder.toString();
    }

    @Override
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            PGgeometry pGgeometry = new PGgeometry();
            pGgeometry.setValue("POLYGON EMPTY");
            stat.setObject(index, pGgeometry, Types.OTHER);
        } else {
            PGgeometry pGgeometry = new PGgeometry();
            pGgeometry.setValue((String) obj);
            stat.setObject(index, pGgeometry, Types.OTHER);
        }
    }
}
