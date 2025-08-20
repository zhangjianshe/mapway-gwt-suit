package cn.mapway.common.geo.gdal;

import cn.mapway.geo.geometry.GeometryType;
import cn.mapway.geo.shared.vector.Box;
import lombok.extern.slf4j.Slf4j;
import org.gdal.ogr.*;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osrConstants;
import org.nutz.castor.Castors;
import org.nutz.lang.Each;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;

import java.io.Closeable;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gdal.ogr.ogrConstants.*;

@Slf4j
public abstract class VectorUtil implements Closeable {
    protected static SpatialReference srcWgs84;
    protected static SpatialReference srcWebMercator;
    static Map<Integer, String> typeMapper;

    static {
        typeMapper = new HashMap<>();
        typeMapper.put(wkbPoint, "点");
        typeMapper.put(wkbMultiPoint, "点集合");
        typeMapper.put(wkbLineString, "线");
        typeMapper.put(wkbMultiLineString, "线集合");
        typeMapper.put(wkbPolygon, "多边形");
        typeMapper.put(wkbMultiPolygon, "多边形集合");
        //TODO  添加其他的类型
    }

    String fileName;
    DataSource source;
    Layer layer;
    int epsgCode;
    int geoType;
    Map<String, Integer> fieldTypes;
    SpatialReference sourceRef;
    GeomTransformer geomTransformer;
    CoordinateTransformation coordinateTransformation;
    private String encoding = "UTF-8";

    public VectorUtil(String fileName, Integer geoType, Integer epsgCode) {
        this.fileName = fileName;
        fieldTypes = new HashMap<>();
        if (epsgCode == null) {
            this.epsgCode = 4326;
        } else {
            this.epsgCode = epsgCode;
        }
        if (geoType == null) {
            this.geoType = wkbMultiPolygon;
        } else {
            this.geoType = geoType;
        }
    }

    public VectorUtil(String path) {
        if (srcWebMercator == null) {
            srcWgs84 = new SpatialReference();
            srcWebMercator = new SpatialReference();
            srcWgs84.ImportFromEPSG(WebMercator.SRC_WGS84);
            srcWgs84.SetAxisMappingStrategy(osrConstants.OAMS_TRADITIONAL_GIS_ORDER);
            srcWebMercator.ImportFromEPSG(WebMercator.SRC_WEB_MERCATOR);
        }


        this.fileName = path;
        fieldTypes = new HashMap<>();

        if (!Files.isFile(new File(fileName))) {
            log.error("Open file {} 不存在", fileName);
        }


    }

    public static int gdalTypeFromGeometryType(GeometryType geometryType) {
        switch (geometryType) {
            case GT_LINE:
                return wkbLineString;
            case GT_POINT:
                return wkbPoint;
            case GT_MULTI_LINE:
                return wkbMultiLineString;
            case GT_MULTI_POINT:
                return wkbMultiPoint;
            case GT_MULTI_POLYGON:
                return wkbMultiPolygon;
            case GT_GEOMETRY_COLLECTION:
            case GT_POLYGON:
            case GT_UNKNOWN:
            default:
                return wkbPolygon;
        }
    }

    public static String readAsString(Feature feature, Integer fieldIndex, String encoding) {
        FieldDefn fieldDefn = feature.GetFieldDefnRef(fieldIndex);
        if (fieldDefn == null) {
            return "字段不存在";
        }
        if (fieldDefn.GetFieldType() == OFTString) {
            return readString(feature, fieldIndex, encoding);
        } else {
            return feature.GetFieldAsString(fieldIndex);
        }
    }

    public static String readAsString(Feature feature, String fieldName, String encoding) {
        FieldDefn fieldDefn = feature.GetFieldDefnRef(fieldName);
        if (fieldDefn == null) {
            return "字段不存在";
        }
        if (fieldDefn.GetFieldType() == OFTString) {
            return readString(feature, fieldName, encoding);
        } else {
            return feature.GetFieldAsString(fieldName);
        }
    }

    public  String readAsString(Feature feature, Integer fieldIndex) {
        String value = readAsString(feature, fieldIndex, getEncoding());
        return value;
    }
    public  String readAsString(Feature feature, String fileName) {
        String value = readAsString(feature, fileName, getEncoding());
        return value;
    }

    public static String readString(Feature feature, Integer fieldIndex, String encoding) {
        byte[] d = feature.GetFieldAsBinary(fieldIndex);
        if (d == null) {
            return "";
        }
        String value = "";
        try {
            value = new String(d, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String readString(Feature feature, String fieldName, String encoding) {
        byte[] d = feature.GetFieldAsBinary(fieldName);
        if (d == null) {
            return "";
        }
        String value = "";
        try {
            value = new String(d, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取字段定义
     *
     * @return
     */
    public List<FieldDefn> getFields() {
        List<FieldDefn> fields;
        FeatureDefn featureDefn = layer.GetLayerDefn();
        fields = new ArrayList<>(featureDefn.GetFieldCount() + 1);
        for (int i = 0; i < featureDefn.GetFieldCount(); i++) {
            fields.add(featureDefn.GetFieldDefn(i));
        }
        return fields;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * 记录数量
     *
     * @return
     */
    public long count() {
        if (layer == null) {
            return 0L;
        }
        return layer.GetFeatureCount();
    }

    public Feature getFeature(int index) {
        layer.SetNextByIndex(index);
        return layer.GetNextFeature();
    }

    public Feature getFeatureById(long fid) {
        return layer.GetFeature(fid);
    }

    /*
     *  WGS84范围
     */
    public Box getExtendBox() {
        Geometry geometry = getExtend();
        return toBox(geometry);
    }

    private Box extendBox() {
        if (layer == null) {
            return null;
        }
        Box box = new Box();
        double[] extend = layer.GetExtent();
        box.setValue(extend[0], extend[2], extend[1], extend[3]);
        return box;
    }

    /**
     * WGS 84
     *
     * @return
     */
    public Geometry getExtend() {
        Box box = extendBox();
        Geometry geometry = toGeometry(box, layer.GetSpatialRef());
        return toWgs84(geometry);
    }

    public SpatialReference getSpatialRef() {
        return layer.GetSpatialRef();
    }

    /**
     * 循环每个要素
     *
     * @param eachFeatureHandler
     */
    public void each(Each<Feature> eachFeatureHandler) {
        if (layer == null) {

            return;
        }
        layer.ResetReading();
        Feature feature = layer.GetNextFeature();
        int index = 0;
        while (feature != null) {
            if (eachFeatureHandler != null) {
                eachFeatureHandler.invoke(index++, feature, -1);
            }
            feature = layer.GetNextFeature();
        }
        log.warn("feature select is " + index);
    }

    public FieldDefn addStringField(String name, int length, String defaultValue) {
        if (layer == null) {
            return null;
        }
        FieldDefn fieldDefn = new FieldDefn();
        fieldDefn.SetWidth(length);
        fieldDefn.SetName(name);
        fieldDefn.SetType(OFTString);
        if (Strings.isNotBlank(defaultValue)) {
            fieldDefn.SetDefault(defaultValue);
        }
        layer.CreateField(fieldDefn);
        fieldTypes.put(name, OFTString);
        return fieldDefn;
    }

    public FieldDefn addIntField(String name, Integer defaultValue) {
        if (layer == null) {
            return null;
        }
        FieldDefn fieldDefn = new FieldDefn();
        fieldDefn.SetName(name);
        fieldDefn.SetType(OFTInteger);
        fieldTypes.put(name, OFTInteger);
        if (defaultValue != null) {
            fieldDefn.SetDefault(defaultValue.toString());
        }
        layer.CreateField(fieldDefn);
        return fieldDefn;
    }

    public FieldDefn addDateField(String name, int i) {
        if (layer == null) {
            return null;
        }
        FieldDefn fieldDefn = new FieldDefn();
        fieldDefn.SetName(name);
        fieldDefn.SetType(OFTDate);
        fieldTypes.put(name, OFTDate);
        layer.CreateField(fieldDefn);
        return fieldDefn;
    }

    public FieldDefn addDoubleField(String name, Double defaultValue) {
        if (layer == null) {
            return null;
        }
        FieldDefn fieldDefn = new FieldDefn();
        fieldDefn.SetName(name);
        fieldDefn.SetType(OFTReal);
        fieldTypes.put(name, OFTReal);
        if (defaultValue != null) {
            fieldDefn.SetDefault(defaultValue.toString());
        }
        layer.CreateField(fieldDefn);
        return fieldDefn;
    }

    public int getGeoType() {
        return layer.GetGeomType();
    }

    public String getGeoTypeAsString() {

        int geoType = getGeoType();
        switch (geoType) {
            case wkbPoint:
                return "点";
            case wkbPointM:
                return "点M";
            case wkbPointZM:
                return "点ZM";
            case wkbPoint25D:
                return "点25D";
            case wkbMultiPoint:
                return "点集合";
            case wkbMultiPointM:
                return "点集合M";
            case wkbMultiPointZM:
                return "点集合ZM";
            case wkbMultiPoint25D:
                return "点集合25D";
            case wkbLineString:
                return "线";
            case wkbLineStringM:
                return "线M";
            case wkbLineStringZM:
                return "线ZM";
            case wkbLineString25D:
                return "线25D";
            case wkbMultiLineString:
                return "线集合";
            case wkbMultiLineStringM:
                return "线集合M";
            case wkbMultiLineStringZM:
                return "线集合ZM";
            case wkbMultiLineString25D:
                return "线集合25D";
            case wkbPolygon:
                return "多边形";
            case wkbPolygonM:
                return "多边形M";
            case wkbPolygonZM:
                return "多边形ZM";
            case wkbPolygon25D:
                return "多边形25D";
            case wkbMultiPolygon:
                return "多边形集合";
            case wkbMultiPolygonM:
                return "多边形集合M";
            case wkbMultiPolygonZM:
                return "多边形集合ZM";
            case wkbMultiPolygon25D:
                return "多边形集合25D";
            default:
                return "未知类型" + geoType;
        }
    }


    public GeomFieldDefn addGeometry(String name) {
        return addGeometry(name, null);
    }

    public GeomFieldDefn addGeometry(String name, Integer type) {
        if (layer == null) {
            return null;
        }
        if (type == null) {
            type = geoType;
        }
        GeomFieldDefn geomFieldDefn = new GeomFieldDefn();
        geomFieldDefn.SetName(name);
        geomFieldDefn.SetType(type);

        SpatialReference spatialReference = new SpatialReference();
        spatialReference.ImportFromEPSG(epsgCode);
        geomFieldDefn.SetSpatialRef(spatialReference);
        layer.CreateGeomField(geomFieldDefn);

        return geomFieldDefn;
    }

    public Feature createRecord(Layer layer, Map<String,Object> attributes) {
          return null;
    }

    public Feature createFeature(Geometry geometry, Map<String, Object> attributes) {
        if (layer == null) {
            return null;
        }
        FeatureDefn featureDefn = layer.GetLayerDefn();
        Feature feature = new Feature(featureDefn);
        feature.SetGeometry(geometry);
        for (String key : attributes.keySet()) {
            Object data = attributes.get(key);
            Integer type = fieldTypes.get(key);
            if (type != null) {
                if (type == OFTString) {
                    if (data == null) {
                        data = "";
                    }
                    feature.SetField(key, data.toString());
                } else if (type == OFTReal) {
                    double v = Castors.me().castTo(data, Double.class);
                    feature.SetField(key, v);
                } else if (type == OFTInteger) {
                    Integer v = Castors.me().castTo(data, Integer.class);
                    feature.SetField(key, v);
                }
            }

        }
        layer.CreateFeature(feature);
        return feature;
    }

    public void close() {
        if(source!=null) {
            source.Close();
            source = null;
        }
    }

    public Attrs createAttrs() {
        Attrs attrs = new Attrs();
        return attrs;
    }

    public DataSource getDataSource() {
        return source;
    }

    public Geometry toGeometry(Box box, SpatialReference spatialReference) {
        Geometry geometry = new Geometry(wkbPolygon);
        if (spatialReference != null) {
            geometry.AssignSpatialReference(spatialReference);
        } else {
            geometry.AssignSpatialReference(srcWgs84);
        }
        Geometry ring = new Geometry(wkbLinearRing);
        ring.AddPoint(box.xmin, box.ymax);
        ring.AddPoint(box.xmax, box.ymax);
        ring.AddPoint(box.xmax, box.ymin);
        ring.AddPoint(box.xmin, box.ymin);
        ring.CloseRings();
        geometry.AddGeometry(ring);
        return geometry;
    }


    public Box toBox(Geometry geometry) {
        Box box = new Box();
        // minX, maxX, minY, maxY
        double[] v = new double[4];
        geometry.GetEnvelope(v);
        box.setValue(v[0], v[2], v[1], v[3]);
        return box;
    }


    public Geometry toWgs84(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry.GetSpatialReference() == null) {
            return null;
        }
        CoordinateTransformation coordinateTransformation = new CoordinateTransformation(geometry.GetSpatialReference(), srcWgs84);
        GeomTransformer geomTransformer = new GeomTransformer(coordinateTransformation);
        return geomTransformer.Transform(geometry);
    }

    public GeomTransformer getGeomTransformerToWgs84() {

        if (getSpatialRef() == null) {
            return null;
        }
        CoordinateTransformation coordinateTransformation = new CoordinateTransformation(getSpatialRef(), srcWgs84);
        GeomTransformer geomTransformer = new GeomTransformer(coordinateTransformation);
        return geomTransformer;
    }

    public GeomTransformer getGeomTransformerToWebMercator() {

        if (getSpatialRef() == null) {
            return null;
        }
        CoordinateTransformation coordinateTransformation = new CoordinateTransformation(getSpatialRef(), srcWebMercator);
        GeomTransformer geomTransformer = new GeomTransformer(coordinateTransformation);
        return geomTransformer;
    }


    @Deprecated
    public Geometry toWebMercator(Geometry geometry) {
        if (geometry == null) {
            return geometry;
        }
        if (geomTransformer != null) {
            return geomTransformer.Transform(geometry);
        }
        return geometry;
    }

    /**
     * 过滤与 tileBox 相交的要素
     *
     * @param tileBox
     */
    public void filter(Box tileBox) {
        if (layer == null) {
            return;
        }
        Geometry filterBox = toGeometry(tileBox, srcWgs84);
        if (sourceRef.IsSame(srcWgs84) == 0) {
            //数据的坐标参考 与 tile过滤WGS84不一样
            //转换坐标范围
            CoordinateTransformation coordinateTransformation = new CoordinateTransformation(srcWgs84, sourceRef);
            GeomTransformer geomTransformer = new GeomTransformer(coordinateTransformation);
            filterBox = geomTransformer.Transform(filterBox);
        }
        layer.SetSpatialFilter(filterBox);
    }

    public boolean isSuccess() {
        return layer != null;
    }


    protected abstract Driver getDriver();

    public static class Attrs extends HashMap<String, Object> {
        public Attrs add(String key, Object v) {
            put(key, v);
            return this;
        }
    }

    public static boolean isShapeFile(File file) {
        return file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(".shp");
    }
    public static boolean isGeoPackage(File file) {
        return file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(".gpkg");
    }
    public static boolean isOpenFileGdb(File file) {
        return file.exists() && file.isDirectory() && file.getName().toLowerCase().endsWith(".gdb");
    }

    public Layer getLayer()
    {
        return layer;
    }
    /**
     * 打开矢量数据
     * @param filename
     * @return
     */
    public static VectorUtil openFile(String filename) {
            File file = new File(filename);
            if (isShapeFile(file)) {
                return new ShapeUtil(file.getAbsolutePath());
            } else if (isGeoPackage(file)) {
                return new GeoPackageUtil(file.getAbsolutePath());
            } else if (isOpenFileGdb(file)) {
                return new OpenFileGdbUtil(file.getAbsolutePath());
            }
            return null;
    }
}
