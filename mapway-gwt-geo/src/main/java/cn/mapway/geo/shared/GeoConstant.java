package cn.mapway.geo.shared;

public class GeoConstant {
    public static final String MQTT_TOPIC_TYPE_DIR_INDEX = "dir_index";

    //投影表示
    public static String EPSG = "EPSG:";
    public static String EPSG4326 = "EPSG:4326";
    public static String EPSG3857 = "EPSG:3857";
    public static int SRID_WGS84 = 4326;
    public static int SRID_WEB_MERCATO = 3857;
    public static int SRID_CGCS2000 = 4490;
    public static int SRID_NULL = 0;


    //支持的图像格式
    public static final String SUPPORT_FILE_FORMATS = "tiff|tif|dat|bsq|img|shp";
    public static final String SUPPORT_MAP_FORMATS = "tiff|tif|dat|bsq|img|jp2|vrt";
}
