package cn.mapway.common.geo.postgis;

import cn.mapway.geo.geometry.*;
import lombok.extern.slf4j.Slf4j;
import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.*;
import org.nutz.dao.jdbc.ValueAdaptor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * GeoObjectAdaptor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class GeoObjectAdaptor implements ValueAdaptor {
    @Override
    public GeoObject get(ResultSet rs, String colName) throws SQLException {
        GeoObject obj = new GeoObject();
        Object object = rs.getObject(colName);
        if (object != null) {
            if (object instanceof PGgeometry) {
                obj = parseGeometry((PGgeometry) object);
            } else {
                log.warn("不能确定数据类型:{}", object.getClass().toString());
            }
        }
        return obj;
    }

    private GeoObject parseGeometry(PGgeometry object) {
        PGgeometry geometry = object;
        switch (geometry.getGeoType()) {
            case Geometry.POLYGON: {
                Polygon polygon = (Polygon) geometry.getGeometry();
                GeoPolygon geoPolygon = toPolygon(polygon);
                return geoPolygon;
            }
            case Geometry.MULTIPOLYGON: {
                MultiPolygon multiPolygon = (MultiPolygon) geometry.getGeometry();
                GeoPolygons geoPolygons = toMultiPolygon(multiPolygon);
                return geoPolygons;
            }
            case Geometry.LINESTRING: {
                LineString lineString = (LineString) geometry.getGeometry();
                GeoLine geoPolygons = toGeoLine(lineString);
                return geoPolygons;
            }
            case Geometry.MULTILINESTRING: {
                MultiLineString multiLineString = (MultiLineString) geometry.getGeometry();
                GeoLines geoLines = toGeoLines(multiLineString);
                return geoLines;
            }
            case Geometry.POINT: {
                Point point = (Point) geometry.getGeometry();
                GeoPoint geoPoint = toGeoPoint(point);
                return geoPoint;
            }
            case Geometry.MULTIPOINT: {
                MultiPoint multiPoint = (MultiPoint) geometry.getGeometry();
                GeoPoints geoPoints = toGeoPoints(multiPoint);
                return geoPoints;
            }
            default:
                log.warn("没有实现对{}的解析", Geometry.getTypeString(geometry.getGeoType()));
                return new GeoObject();
        }
    }

    private GeoPoints toGeoPoints(MultiPoint multiPoint) {
        Point[] points = multiPoint.getPoints();
        int length = points.length;
        List<GeoPoint> pts = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Point point = points[i];
            pts.add(new GeoPoint(point.getX(), point.getY()));
        }
        return new GeoPoints(pts);
    }


    /**
     * POSTGIS Polygon -> GeoPolygon
     *
     */
    private GeoPolygon toPolygon(Polygon polygon) {
        GeoPolygon geoPolygon = new GeoPolygon();
        for (int i = 0; i < polygon.numRings(); i++) {
            LinearRing ring = polygon.getRing(i);
            net.postgis.jdbc.geometry.Point[] points = ring.getPoints();
            Line line = new Line();
            for (int j = 0; j < points.length; j++) {
                net.postgis.jdbc.geometry.Point point = points[j];
                line.add(point.x, point.y);
            }
            geoPolygon.getLines().add(line);
        }
        return geoPolygon;
    }

    /**
     * POSTGIS multiPolygon -> GeoPolygons
     *
     * @param multiPolygon
     * @return
     */
    private GeoPolygons toMultiPolygon(MultiPolygon multiPolygon) {
        Polygon[] polygons = multiPolygon.getPolygons();
        GeoPolygon[] geoPolygons = new GeoPolygon[polygons.length];
        for (int i = 0; i < polygons.length; i++) {
            geoPolygons[i] = toPolygon(polygons[i]);
        }
        return new GeoPolygons(geoPolygons);
    }

    /**
     * POSTGIS LineString -> GeoLine
     *
     * @param lineString
     * @return
     */
    private GeoLine toGeoLine(LineString lineString) {
        Point[] points = lineString.getPoints();
        Line line = new Line();
        for (int i = 0; i < points.length; i++) {
            line.add(new GeoPoint(points[i].getX(), points[i].getY()));
        }
        return new GeoLine(line);
    }

    /**
     * POSTGIS MultiLineString -> GeoLines
     *
     * @param multiLineString
     * @return
     */
    private GeoLines toGeoLines(MultiLineString multiLineString) {
        LineString[] lines = multiLineString.getLines();
        int length = lines.length;
        GeoLine[] geoLines = new GeoLine[length];
        for (int i = 0; i < length; i++) {
            geoLines[i] = toGeoLine(lines[i]);
        }
        return new GeoLines(geoLines);
    }

    /**
     * POSTGIS point -> GeoPoint
     *
     * @param point
     * @return
     */
    private GeoPoint toGeoPoint(Point point) {
        return new GeoPoint(point.getX(), point.getY());
    }

    @Override
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            stat.setNull(index, Types.NULL);
        } else {
            if (obj instanceof GeoObject) {
                stat.setObject(index, toGeometry(obj), Types.OTHER);
            } else {
                log.warn("不能插入数据格式{}", obj.getClass().getName());
                stat.setNull(index, Types.NULL);
            }

        }
    }


    private PGgeometry toGeometry(Object obj) {
        GeoObject geoObject = (GeoObject) obj;
        if (geoObject.getType().equals(GeoJsonType.GEO_POLYGON)) {
            Polygon polygon = fromPolygon(geoObject.asGeoPolygon());
            return new PGgeometry(polygon);
        } else if (geoObject.getType().equals(GeoJsonType.GEO_MULTI_POLYGON)) {
            MultiPolygon polygon = fromMultiPolygon(geoObject.asGeoPolygons());
            return new PGgeometry(polygon);
        } else if (geoObject.getType().equals(GeoJsonType.GEO_LINE)) {
            LineString lineString = fromLineString(geoObject.asGeoLine());
            return new PGgeometry(lineString);
        } else if (geoObject.getType().equals(GeoJsonType.GEO_MULTI_LINE)) {
            MultiLineString multiLineString = fromMultiLineString(geoObject.asGeoLines());
            return new PGgeometry(multiLineString);
        } else if (geoObject.getType().equals(GeoJsonType.GEO_POINT)) {
            Point point = fromPoint(geoObject.asGeoPoint());
            return new PGgeometry(point);
        } else if (geoObject.getType().equals(GeoJsonType.GEO_MULTI_POINT)) {
            MultiPoint points = fromMultiPoint(geoObject.asGeoPoints());
            return new PGgeometry(points);
        }
        return null;
    }


    private Polygon fromPolygon(GeoPolygon asGeoPolygon) {
        Lines lines = asGeoPolygon.getLines();
        Polygon polygon = fromPolygon(lines);
        if (asGeoPolygon.getSrid() != null) {
            polygon.setSrid(asGeoPolygon.getSrid());
        }
        return polygon;
    }

    private Polygon fromPolygon(Lines lines) {
        LinearRing[] rings = new LinearRing[lines.getSize()];
        int ringIndex = 0;
        for (int i = 0; i < lines.getSize(); i++) {
            Line line = lines.getLine(i);
            net.postgis.jdbc.geometry.Point[] points = new net.postgis.jdbc.geometry.Point[line.getPoints().size()];
            Integer[] pointIndex = {0};
            line.getPoints().forEach((t) -> {
                points[pointIndex[0]++] = new net.postgis.jdbc.geometry.Point(t.x,t.y);
            });
            rings[ringIndex++] = new LinearRing(points);
        }
        Polygon polygon = new Polygon(rings);
        return polygon;
    }

    private MultiPolygon fromMultiPolygon(GeoPolygons asGeoPolygon) {
        List<Lines> liness = asGeoPolygon.getLiness();
        int size = liness.size();
        Polygon[] polygons = new Polygon[size];
        for (int i = 0; i < size; i++) {
            polygons[i] = fromPolygon(liness.get(i));
        }
        MultiPolygon data = new MultiPolygon(polygons);
        if (asGeoPolygon.getSrid() != null) {
            data.setSrid(asGeoPolygon.getSrid());
        }
        return data;
    }

    private LineString fromLineString(GeoLine asGeoLine) {
        Line line = asGeoLine.getLine();
        return fromLineString(line);
    }

    private LineString fromLineString(Line line) {
        net.postgis.jdbc.geometry.Point[] points = new net.postgis.jdbc.geometry.Point[line.getPoints().size()];
        Integer[] pointIndex = {0};
        line.getPoints().forEach((t) -> {
            points[pointIndex[0]++] = new net.postgis.jdbc.geometry.Point(t.getX(), t.getY());
        });
        return new LineString(points);
    }

    private MultiLineString fromMultiLineString(GeoLines asGeoLines) {
        List<Line> lines = asGeoLines.getLines();
        int size = lines.size();
        LineString[] lineStrings = new LineString[size];
        for (int i = 0; i < size; i++) {
            lineStrings[i] = fromLineString(lines.get(i));
        }
        return new MultiLineString(lineStrings);
    }

    private Point fromPoint(GeoPoint pt) {
        net.postgis.jdbc.geometry.Point point = new net.postgis.jdbc.geometry.Point(pt.getX(), pt.getY());
        return point;
    }

    private MultiPoint fromMultiPoint(GeoPoints asGeoPoints) {
        List<GeoPoint> geoPoints = asGeoPoints.getPoints();
        int size = geoPoints.size();
        net.postgis.jdbc.geometry.Point[] points = new net.postgis.jdbc.geometry.Point[size];
        for (int i = 0; i < size; i++) {
            GeoPoint t = geoPoints.get(i);
            points[i] = new net.postgis.jdbc.geometry.Point(t.getX(), t.getY());
        }
        return new MultiPoint(points);
    }
}
