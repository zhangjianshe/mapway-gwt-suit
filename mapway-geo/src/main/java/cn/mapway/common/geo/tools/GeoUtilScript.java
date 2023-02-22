package cn.mapway.common.geo.tools;

import cn.mapway.geo.geometry.*;
import lombok.extern.slf4j.Slf4j;
import org.gdal.ogr.Geometry;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mapl.Mapl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GeoJsonObject
 * Gdal Geometry   GeoObject 转换
 *
 * @author zhangjianshe@gmail.com
 */
@Slf4j
public class GeoUtilScript {

    private static final String FLD_COORDINATES = "coordinates";


    public final static GeoObject parse(String geoJson) {
        Object object = Json.fromJson(geoJson);
        return parseGeoObject(object);
    }

    private static GeoObject parseGeoObject(Object object) {
        String type = (String) Mapl.cell(object, "type");

        if (Strings.isBlank(type)) {
            return null;
        }
        if (Strings.equals(type, GeoJsonType.GEO_FEATURE)) {
            return parseFeature(object);
        } else if (Strings.equals(type, GeoJsonType.GEO_POINT)) {
            return parsePoint(object);
        } else if (Strings.equals(type, GeoJsonType.GEO_MULTI_POINT)) {
            return parseMultiPoint(object);
        } else if (Strings.equals(type, GeoJsonType.GEO_LINE)) {
            return parseLine(object);
        } else if (Strings.equals(type, GeoJsonType.GEO_MULTI_LINE)) {
            return parseMultiLine(object);
        } else if (Strings.equals(type, GeoJsonType.GEO_POLYGON)) {
            return parsePolygon(object);
        } else if (Strings.equals(type, GeoJsonType.GEO_MULTI_POLYGON)) {
            return parseMultiPolygon(object);
        } else if (Strings.equals(type, GeoJsonType.GEO_FEATURE_COLLECTION)) {
            return parseFeatureCollection(object);
        } else if (Strings.equals(type, GeoJsonType.GEO_GEOMETRY_COLLECTION)) {
            return parseGeometryCollection(object);
        }
        return null;
    }

    private static GeoObject parseMultiPolygon(Object object) {
        GeoPolygons geoPolygons = new GeoPolygons();
        Object obj = Mapl.cell(object, FLD_COORDINATES);
        if (obj == null || obj instanceof String) {
            //这可能是一个空的要素
            return null;
        }
        List<List<List<List<Object>>>> data = (List<List<List<List<Object>>>>) Mapl.cell(object, FLD_COORDINATES);
        for (List<List<List<Object>>> polygonData : data) {
            Lines lines = new Lines();
            for (List<List<Object>> linedata : polygonData) {
                Line line = new Line();
                for (List<Object> pt : linedata) {
                    line.add(object2Double(pt.get(0)), object2Double(pt.get(1)));
                }
                lines.add(line);
            }
            geoPolygons.getLiness().add(lines);
        }
        return geoPolygons;
    }

    private static GeoFeatures parseFeatureCollection(Object object) {
        GeoFeatures geoFeatures = new GeoFeatures();
        List<Object> features = (List<Object>) Mapl.cell(object, "features");
        if (features != null) {
            for (Object f : features) {
                GeoFeature feature = parseFeature(f);
                if (feature != null) {
                    geoFeatures.getFeatureList().add(feature);
                }
            }
        }
        return geoFeatures;
    }

    private static GeoCollection parseGeometryCollection(Object object) {
        GeoCollection geoCollection = new GeoCollection();
        Object[] geos = (Object[]) Mapl.cell(object, "geometries");
        if (Lang.isEmpty(geos)) {
            return null;
        }
        for (Object geo : geos) {
            GeoObject o = parseGeoObject(geo);
            if (o != null) {
                geoCollection.getGeometryList().add(parseGeoObject(geo));
            }
        }
        return geoCollection;
    }

    private static GeoPolygon parsePolygon(Object object) {

        GeoPolygon polygon = new GeoPolygon();
        Object obj = Mapl.cell(object, FLD_COORDINATES);
        if (obj == null || obj instanceof String) {
            //这可能是一个空的要素
            return null;
        }
        List<List<List<Object>>> data = (List<List<List<Object>>>) obj;
        for (List<List<Object>> linedata : data) {
            Line line = new Line();
            for (List<Object> pt : linedata) {

                line.add(object2Double(pt.get(0)), object2Double(pt.get(1)));
            }
            // sure close it
            line.close();
            polygon.getLines().add(line);

        }
        return polygon;
    }

    private static GeoLines parseMultiLine(Object object) {
        GeoLines lines = new GeoLines();
        Object obj = Mapl.cell(object, FLD_COORDINATES);
        if (obj == null || obj instanceof String) {
            //这可能是一个空的要素
            return null;
        }

        List<List<List<Object>>> data = (List<List<List<Object>>>) obj;
        for (List<List<Object>> linedata : data) {
            Line line = new Line();
            for (List<Object> pt : linedata) {
                line.add(object2Double(pt.get(0)), object2Double(pt.get(1)));
            }
            lines.addLine(line);
        }
        return lines;
    }

    private static GeoLine parseLine(Object object) {
        GeoLine line = new GeoLine();
        Object obj = Mapl.cell(object, FLD_COORDINATES);
        if (obj == null || obj instanceof String) {
            //这可能是一个空的要素
            return null;
        }
        List<List<Object>> data = (List<List<Object>>) obj;
        for (List<Object> p : data) {
            line.getLine().add(object2Double(p.get(0)), object2Double(p.get(1)));
        }
        return line;
    }

    private static GeoPoints parseMultiPoint(Object object) {
        GeoPoints points = new GeoPoints();
        Object obj = Mapl.cell(object, FLD_COORDINATES);
        if (obj == null || obj instanceof String) {
            //这可能是一个空的要素
            return null;
        }
        List<List<Object>> data = (List<List<Object>>) obj;
        for (List<Object> p : data) {
            points.getPoints().add(new GeoPoint(object2Double(p.get(0)), object2Double(p.get(1))));
        }
        return points;
    }

    private static GeoPoint parsePoint(Object object) {
        GeoPoint point = new GeoPoint();
        Object obj = Mapl.cell(object, FLD_COORDINATES);
        if (obj == null || obj instanceof String) {
            //这可能是一个空的要素
            return null;
        }
        List<Object> data = (List<Object>) obj;
        point.setData(object2Double(data.get(0)), object2Double(data.get(1)));
        return point;
    }

    /**
     * 解析 feature
     *
     * @param object
     * @return
     */
    private static GeoFeature parseFeature(Object object) {
        GeoFeature feature = new GeoFeature();
        Object obj = Mapl.cell(object, "geometry");
        if (obj != null) {
            feature.geometry = parseGeoObject(obj);
        }
        if (feature.geometry == null) {
            return null;
        }
        Map<String, Object> properties = (Map<String, Object>) Mapl.cell(object, "properties");
        feature.properties = new HashMap<>(properties);
        return feature;
    }

    public static void main(String[] args) {
        String geoJson1 = "{\"type\":\"Feature\",\"properties\":{\"a\":123},\"geometry\":{\"type\":\"Point\",\"coordinates\":[39.63650465011597,50.40165209456205]}}";
        String point = "{\"type\":\"Point\",\"coordinates\":[39.63650465011597,50.40165209456205]}}";
        String points = "{\"type\":\"MultiPoint\",\"coordinates\":[[39.63650465011597,50.40165209456205],[39.63650465011597,50.40165209456205]]}}";
        String line = "{\"type\":\"LineString\",\"coordinates\":[[39.63650465011597,50.40165209456205],[39.63650465011597,50.40165209456205],[39.63650465011597,50.40165209456205]]}}";
        String lines = "{\"type\":\"MultiLineString\",\"coordinates\":[[[39.63650465011597,50.40165209456205],[39.63650465011597,50.40165209456205]],[[39.63650465011597,50.40165209456205],[39.63650465011597,50.40165209456205]]]}}";
        String errors = "{\"type\": \"FeatureCollection\", \"name\": \"BoxArea\", \"features\": [{\"type\": \"Feature\", \"properties\": {}, \"geometry\": {\"type\": \"Polygon\", \"coordinates\": \"\", \"category\": \"\", \"score\": \"\", \"area\": \"\"}}]}";
        GeoObject obj = GeoUtilScript.parse(errors);
        System.out.println(obj.toGeoJson());
    }

    public static GeoObject GeoObjectFromGdalGeometry(Geometry geometry) {
        if (!geometry.IsValid() || geometry.IsEmpty()) {
            log.warn("geometry is not valid");
            return null;
        }
        Object geoJson = Json.fromJson(geometry.ExportToJson());
        return parsePolygon(geoJson);
    }

    private static Double object2Double(Object o) {
        if (o instanceof Double) {
            return (Double) o;
        } else if (o instanceof BigDecimal) {
            BigDecimal d = (BigDecimal) o;
            return d.doubleValue();
        } else if (o instanceof Integer) {
            Integer i = (Integer) o;
            return i.doubleValue();
        } else if (o instanceof String) {
            String s = (String) o;
            return Double.parseDouble(s);
        }
        log.warn("类型转换错误{} to Double ", o.getClass().getName());
        return 0.0;

    }

    public static double wrapLat(double v) {
        while (v > 90) {
            v -= 180;
        }
        while (v < -90) {
            v += 180;
        }
        return v;
    }

    public static double wrapLng(double v) {
        while (v > 180) {
            v -= 360;
        }
        while (v < -180) {
            v += 360;
        }
        return v;
    }


}
