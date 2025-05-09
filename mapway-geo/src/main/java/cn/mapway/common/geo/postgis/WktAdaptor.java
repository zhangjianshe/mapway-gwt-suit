package cn.mapway.common.geo.postgis;

import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.*;
import net.postgis.jdbc.geometry.binary.BinaryParser;
import org.nutz.dao.jdbc.ValueAdaptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static net.postgis.jdbc.geometry.GeometryBuilder.SRIDPREFIX;
import static net.postgis.jdbc.geometry.GeometryBuilder.splitSRID;

@Slf4j
public class WktAdaptor implements ValueAdaptor {
    String emptyGeometryWkt = "";
    Integer sridDefine=0;
    public WktAdaptor(String geometryType) {
        // GEOMETRY(XXX,4326)
        // extract XXX from geometryType
        String type = geometryType.substring(geometryType.indexOf("(") + 1, geometryType.indexOf(","));
        emptyGeometryWkt=type + " EMPTY";
        try {
            String sridString=geometryType.substring(geometryType.indexOf(",") + 1, geometryType.indexOf(")"));
            sridDefine = Integer.parseInt(sridString);
        }
        catch (Exception e) {
            log.warn("无法解析SRID:{}",geometryType);
        }
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
            pGgeometry.setGeometry(geomFromString(emptyGeometryWkt, new BinaryParser(), false,sridDefine));
        } else {
            pGgeometry.setGeometry(geomFromString((String) obj, new BinaryParser(), false,sridDefine));
        }
        stat.setObject(index, pGgeometry, Types.OTHER);
    }

    public static Geometry geomFromString(String value, BinaryParser bp, boolean haveM,Integer sridDefine)
            throws SQLException {
        value = value.trim();

        int srid = sridDefine;

        if (value.startsWith(SRIDPREFIX)) {
            // break up geometry into srid and wkt
            String[] parts = splitSRID(value);
            value = parts[1].trim();
            srid = Geometry.parseSRID(Integer.parseInt(parts[0].substring(5)));
        }


        Geometry result;
        if (value.startsWith("00") || value.startsWith("01")) {
            result = bp.parse(value);
        } else if (value.endsWith("EMPTY")) {
            // Handle empty geometries based on the prefix
            if (value.startsWith("POLYGON")) {
                result = new Polygon(); // Empty Polygon
            } else if (value.startsWith("MULTIPOLYGON")) {
                result = new MultiPolygon(); // Empty MultiPolygon
            } else if (value.startsWith("LINESTRING")) {
                result = new LineString(); // Empty LineString
            } else if (value.startsWith("MULTILINESTRING")) {
                result = new MultiLineString(); // Empty MultiLineString
            } else if (value.startsWith("POINT")) {
                result = new Point(); // Empty Point
            } else if (value.startsWith("MULTIPOINT")) {
                result = new MultiPoint(); // Empty MultiPoint
            } else if (value.startsWith("GEOMETRYCOLLECTION")) {
                result = new GeometryCollection(); // Empty GeometryCollection
            } else {
                result = new GeometryCollection(); // Fallback for unrecognized empty types
            }
        } else if (value.startsWith("MULTIPOLYGON")) {
            result = new MultiPolygon(value);
        } else if (value.startsWith("MULTILINESTRING")) {
            result = new MultiLineString(value, haveM);
        } else if (value.startsWith("MULTIPOINT")) {
            result = new MultiPoint(value   );
        } else if (value.startsWith("POLYGON")) {
            result = new Polygon(value, haveM);
        } else if (value.startsWith("LINESTRING")) {
            result = new LineString(value, haveM);
        } else if (value.startsWith("POINT")) {
            result = new Point(value);
        } else if (value.startsWith("GEOMETRYCOLLECTION")) {
            result = new GeometryCollection(value, haveM);
        } else {
            throw new SQLException("Unknown type: " + value);
        }

        if (srid != Geometry.UNKNOWN_SRID) {
            result.srid = srid;
        }

        return result;
    }

    public static void main(String[] args) {
        WktAdaptor wktAdaptor=new WktAdaptor("GEOMETRY(POLYGON,4326)");
        System.out.println(wktAdaptor.emptyGeometryWkt);
        System.out.println(wktAdaptor.sridDefine);
    }
}
