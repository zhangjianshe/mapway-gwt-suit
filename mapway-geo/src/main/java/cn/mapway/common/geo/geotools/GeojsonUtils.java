package cn.mapway.common.geo.geotools;

import org.apache.commons.io.FileUtils;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.geometry.jts.WKTWriter2;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析geojson
 * @Author baoshuaiZealot@163.com  2022/1/20
 */
public class GeojsonUtils {

    static GeometryJSON gjson = new GeometryJSON(12);

    static FeatureJSON fjson = new FeatureJSON(gjson);

    static {
        fjson.setEncodeNullValues(true);
    }

    public static Geometry parseGeometry(String geojson) {
        try {
            return gjson.read(geojson);
        } catch (Exception e) {
            return null;
        }
    }

    public static Geometry parseGeometryFromWkt(String wkt) {
        WKTReader2 reader=new WKTReader2();
        if(wkt==null || wkt.length()==0){
            wkt="GEOMETRY EMPTY";
            try {
                return reader.read(wkt);
            } catch (ParseException e) {
                return null;
            }
        }
        if(wkt.startsWith("SRID=")){
            int strat=wkt.indexOf(";");
            String sird=wkt.substring(5,strat);
            String wktData=wkt.substring(strat+1);
            Geometry geometry= null;
            try {
                geometry = reader.read(wktData);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            geometry.setSRID(Integer.parseInt(sird));
            return geometry;
        }
        else {
            Geometry geometry= null;
            try {
                geometry = reader.read(wkt);
            } catch (ParseException e) {
                e.toString();
                return null;
            }
            geometry.setSRID(4326);
            return geometry;
        }
    }

    public static String toWkt(Geometry geom) {
        WKTWriter2 wktWriter2=new WKTWriter2();
        return wktWriter2.write(geom);
    }
    public static String toEWkt(Geometry geom) {
        WKTWriter2 wktWriter2=new WKTWriter2();
        String wkt = wktWriter2.write(geom);
        int srid = geom.getSRID();
        if(srid>0)
        {
            return  "SRID="+srid+";"+wkt;
        }else {
            return wkt;
        }
    }

    public static FeatureCollection parseFeatures(String geojson) {
        try {
            FeatureCollection featureCollection = fjson.readFeatureCollection(geojson);
            return featureCollection;
        } catch (Exception e) {
            return null;
        }
    }

    public static SimpleFeature parseFeature(String geojson) {
        try {
            return fjson.readFeature(geojson);
        } catch (Exception e) {
            return null;
        }
    }

    public static GeometryCollection parseGeometryCollection(String geojson) throws IOException {
        GeometryCollection geometryCollection = gjson.readGeometryCollection(geojson);
        return geometryCollection;
    }

    public static String geometryToGeojson(Geometry geom) {
        try (StringWriter sw = new StringWriter()){
            gjson.write(geom, sw);
            return sw.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String simpleFeatureToGeojson(SimpleFeature geom) {
        try (StringWriter sw = new StringWriter()){
            fjson.writeFeature(geom, sw);
            return sw.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String read(Object obj) {
        try (StringWriter sw = new StringWriter()){
            write(obj, sw);
            return sw.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static void setGeometryDecimals(int decimals){
        gjson = new GeometryJSON(decimals);
        fjson = new FeatureJSON(gjson);
    }

    public static void write(Object obj, Object output) throws IOException {
        if (obj instanceof Geometry) {
            gjson.write((Geometry) obj, output);
        } else if (obj instanceof Feature
                || obj instanceof FeatureCollection
                || obj instanceof CoordinateReferenceSystem
                || obj instanceof SimpleFeatureSource) {

            if (obj instanceof SimpleFeature) {
                fjson.writeFeature((SimpleFeature) obj, output);
            } else if (obj instanceof FeatureCollection) {
                fjson.writeFeatureCollection((FeatureCollection) obj, output);
            } else if (obj instanceof CoordinateReferenceSystem) {
                fjson.writeCRS((CoordinateReferenceSystem) obj, output);
            } else {
                throw new IllegalArgumentException(
                        "Unable able to encode object of type " + obj.getClass());
            }
        }
    }

    public static String write(Object obj) throws IOException {
        StringWriter output = new StringWriter();
        if (obj instanceof Geometry) {
            gjson.write((Geometry) obj, output);
        } else if (obj instanceof Feature
                || obj instanceof FeatureCollection
                || obj instanceof CoordinateReferenceSystem) {

            if (obj instanceof SimpleFeature) {
                fjson.writeFeature((SimpleFeature) obj, output);
            } else if (obj instanceof FeatureCollection) {
                fjson.writeFeatureCollection((FeatureCollection) obj, output);
            } else if (obj instanceof CoordinateReferenceSystem) {
                fjson.writeCRS((CoordinateReferenceSystem) obj, output);
            } else {
                throw new IllegalArgumentException(
                        "Unable able to encode object of type " + obj.getClass());
            }
        }
        return output.toString();
    }


    public static void main(String[] args) throws IOException {

        if(true)
        {
            String wkt="SRID=4326;POLYGON ((93.56286624446513 44.04772094108898, 93.76885989680888 43.97858588318102, 93.45300296321513 43.93706616379512, 93.43927005305888 44.02995105251482, 93.56286624446513 44.04772094108898))";
            Geometry geom = parseGeometryFromWkt(wkt);  //解析wkt
            System.out.println(toEWkt(geom));
            return;
        }
        String readFileToString = FileUtils.readFileToString(new File("D:\\数据\\边界\\xuequ.geojson"), "UTF-8");
        String tStr = FileUtils.readFileToString(new File("D:\\数据\\边界\\t.geojson"), "UTF-8");
        FeatureCollection tsfc = parseFeatures(tStr);
        FeatureIterator features1 = tsfc.features();
        Geometry box = null;
        while (features1.hasNext()){
            SimpleFeature feature = (SimpleFeature) features1.next();
            box = (Geometry) feature.getDefaultGeometry();
        }

        List<SimpleFeature> resultList = new ArrayList<>();
        FeatureCollection featureCollection = parseFeatures(readFileToString);
        FeatureIterator features = featureCollection.features();
        //union
        Geometry union = null;
        while (features.hasNext()){
            SimpleFeature feature = (SimpleFeature) features.next();
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            Geometry intersection = geometry.intersection(box);
            if(!intersection.isEmpty() && intersection.isValid()){
                resultList.add(feature);
                if(union == null){
                    union = intersection;
                } else {
                    union = union.union(intersection);
                }
                feature.setDefaultGeometry(union);
                resultList.clear();
                resultList.add(feature);
            }
        }
        Polygon envelope = (Polygon) union.getEnvelope();
        Coordinate[] coordinates = envelope.getCoordinates();
        Coordinate min = coordinates[3];
        Coordinate max = coordinates[1];
        System.out.println("[" + min.x + "," + min.y + "," + max.x + "," + max.y + "]");


        SimpleFeatureType schema = (SimpleFeatureType) featureCollection.getSchema();
        ListFeatureCollection simpleFeatures = new ListFeatureCollection(schema, resultList);
        String read = read(simpleFeatures);
        FileUtils.writeStringToFile(new File("D:\\数据\\边界\\t1.geojson"), read, "UTF-8");
        System.out.println(featureCollection);
    }
}
