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
    String emptyGeometryWkt = "";
    public WktAdaptor(String geometryType) {
        // GEOMETRY(XXX,4326)
        // extract XXX from geometryType
        String type = geometryType.substring(geometryType.indexOf("(") + 1, geometryType.indexOf(","));
        emptyGeometryWkt=type + " EMPTY";
    }
    @Override
    public Object get(ResultSet rs, String colName) throws SQLException {
        Object object = rs.getObject(colName);
        if (object != null) {
            if (object instanceof PGgeometry) {
                PGgeometry pGgeometry= (PGgeometry) object;
                return toWKT(pGgeometry);
            } else {
                log.warn("不能确定数据类型:{}", object.getClass().toString());
                return emptyGeometryWkt;
            }
        } else {
            return emptyGeometryWkt;
        }
    }

    private String toWKT(PGgeometry pGgeometry) {
        StringBuffer stringBuilder = new StringBuffer();
        pGgeometry.getGeometry().outerWKT(stringBuilder);
        return stringBuilder.toString();
    }

    @Override
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        PGgeometry pGgeometry = new PGgeometry();
        if (null == obj) {
            pGgeometry.setValue(emptyGeometryWkt);
        } else {
            pGgeometry.setValue((String) obj);
        }
        stat.setObject(index, pGgeometry, Types.OTHER);
    }
}
