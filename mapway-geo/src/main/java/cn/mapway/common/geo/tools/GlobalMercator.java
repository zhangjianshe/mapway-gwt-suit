package cn.mapway.common.geo.tools;

import cn.mapway.common.geo.sfile.TileNo;
import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;
import lombok.extern.slf4j.Slf4j;

/**
 * GlobalMercator 墨卡托信息
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 * <p>
 * TMS Global Mercator Profile
 * ---------------------------
 * <p>
 * Functions necessary for generation of tiles in Spherical Mercator projection,
 * EPSG:3857.
 * <p>
 * Such tiles are compatible with Google Maps, Bing Maps, Yahoo Maps,
 * UK Ordnance Survey OpenSpace API, ...
 * and you can overlay them on top of base maps of those web mapping applications.
 * <p>
 * Pixel and tile coordinates are in TMS notation (origin [0,0] in bottom-left).
 * <p>
 * What coordinate conversions do we need for TMS Global Mercator tiles::
 * <p>
 * LatLon      <->       Meters      <->     Pixels    <->       Tile
 * <p>
 * WGS84 coordinates   Spherical Mercator  Pixels in pyramid  Tiles in pyramid
 * lat/lon            XY in meters     XY pixels Z zoom      XYZ from TMS
 * EPSG:4326           EPSG:3875
 * .----.              ---------               --                TMS
 * /      \     <->     |       |     <->     /----/    <->      Google
 * \      /             |       |           /--------/          QuadTree
 * -----               ---------         /------------/
 * KML, public         WebMapService         Web Clients      TileMapService
 * <p>
 * What is the coordinate extent of Earth in EPSG:3857?
 * <p>
 * [-20037508.342789244, -20037508.342789244, 20037508.342789244, 20037508.342789244]
 * Constant 20037508.342789244 comes from the circumference of the Earth in meters,
 * which is 40 thousand kilometers, the coordinate origin is in the middle of extent.
 * In fact you can calculate the constant as: 2 * math.pi * 6378137 / 2.0
 * $ echo 180 85 | gdaltransform -s_srs EPSG:4326 -t_srs EPSG:3857
 * Polar areas with abs(latitude) bigger then 85.05112878 are clipped off.
 * <p>
 * What are zoom level constants (pixels/meter) for pyramid with EPSG:3857?
 * <p>
 * whole region is on top of pyramid (zoom=0) covered by 256x256 pixels tile,
 * every lower zoom level resolution is always divided by two
 * initialResolution = 20037508.342789244 * 2 / 256 = 156543.03392804062
 * <p>
 * What is the difference between TMS and Google Maps/QuadTree tile name convention?
 * <p>
 * The tile raster itself is the same (equal extent, projection, pixel size),
 * there is just different identification of the same raster tile.
 * Tiles in TMS are counted from [0,0] in the bottom-left corner, id is XYZ.
 * Google placed the origin [0,0] to the top-left corner, reference is XYZ.
 * Microsoft is referencing tiles by a QuadTree name, defined on the website:
 * http://msdn2.microsoft.com/en-us/library/bb259689.aspx
 * <p>
 * The lat/lon coordinates are using WGS84 datum, yes?
 * <p>
 * Yes, all lat/lon we are mentioning should use WGS84 Geodetic Datum.
 * Well, the web clients like Google Maps are projecting those coordinates by
 * Spherical Mercator, so in fact lat/lon coordinates on sphere are treated as if
 * the were on the WGS84 ellipsoid.
 * <p>
 * From MSDN documentation:
 * To simplify the calculations, we use the spherical form of projection, not
 * the ellipsoidal form. Since the projection is used only for map display,
 * and not for displaying numeric coordinates, we don't need the extra precision
 * of an ellipsoidal projection. The spherical projection causes approximately
 * 0.33 percent scale distortion in the Y direction, which is not visually
 * noticeable.
 * <p>
 * How do I create a raster in EPSG:3857 and convert coordinates with PROJ.4?
 * <p>
 * You can use standard GIS tools like gdalwarp, cs2cs or gdaltransform.
 * All of the tools supports -t_srs 'epsg:3857'.
 * <p>
 * For other GIS programs check the exact definition of the projection:
 * More info at http://spatialreference.org/ref/user/google-projection/
 * The same projection is designated as EPSG:3857. WKT definition is in the
 * official EPSG database.
 * <p>
 * Proj4 Text:
 * +proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0
 * +k=1.0 +units=m +nadgrids=@null +no_defs
 * <p>
 * Human readable WKT format of EPSG:3857:
 * PROJCS["Google Maps Global Mercator",
 * GEOGCS["WGS 84",
 * DATUM["WGS_1984",
 * SPHEROID["WGS 84",6378137,298.257223563,
 * AUTHORITY["EPSG","7030"]],
 * AUTHORITY["EPSG","6326"]],
 * PRIMEM["Greenwich",0],
 * UNIT["degree",0.0174532925199433],
 * AUTHORITY["EPSG","4326"]],
 * PROJECTION["Mercator_1SP"],
 * PARAMETER["central_meridian",0],
 * PARAMETER["scale_factor",1],
 * PARAMETER["false_easting",0],
 * PARAMETER["false_northing",0],
 * UNIT["metre",1,
 * AUTHORITY["EPSG","9001"]]]
 */
@Slf4j
public class GlobalMercator {
    private static GlobalMercator GLOBAL_MERCATOR = null;
    private static byte[] white;
    public final int MAXZOOMLEVEL = 32;
    // 地球半径 单位m
    public final long EARTH_RADIUS = 6378137;
    int tileSize;
    //Level 0 m每个像素的地面长度 单位m
    double initialResolution;
    // 原点的偏移量
    double originShift;

    public GlobalMercator(int tileSize) {
        this.tileSize = tileSize;
        this.initialResolution = 2 * Math.PI * EARTH_RADIUS / tileSize;
        this.originShift = 2 * Math.PI * EARTH_RADIUS / 2.0;
    }

    public static void main(String[] args) {
        GlobalMercator app = new GlobalMercator(256);
        String t = "[(-63.61813817899997,15.666064122000023)(331,467)]" +
                "[(-63.61818975899996,15.666549322000037)(331,467)]" +
                "[(-63.61755476299999,15.668319613000051)(331,467)]" +
                "[(-63.61766059599995,15.670166874000074)(331,467)]" +
                "[(-63.61718915999995,15.670243843000037)(331,467)]" +
                "[(-63.61687166199994,15.666693639000073)(331,467)]";

    }

    /**
     * 全局获取墨卡托参数信息
     *
     * @return
     */
    public static synchronized GlobalMercator get() {
        if (GLOBAL_MERCATOR == null) {
            GLOBAL_MERCATOR = new GlobalMercator(256);
        }
        return GLOBAL_MERCATOR;
    }

    /**
     * 获取一个波段数据
     *
     * @return
     */
    public synchronized byte[] getWhiteBand() {
        if (white == null) {
            white = new byte[tileSize * tileSize];
            for (int i = 0; i < tileSize * tileSize; i++) {
                white[i] = (byte) 0xff;
            }
        }
        return white;
    }

    /**
     * "Maximal scaledown zoom of the pyramid closest to the pixelSize."
     * 墨卡托 以米为单位的坐标 转化为WGS84经纬度坐标
     *
     * @param mx
     * @param my
     * @return
     */
    public Point meterTolngLat(double mx, double my) {
        double lon = (mx / originShift) * 180.0;
        double lat = (my / originShift) * 180.0;

        lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
        return new Point(lon, lat);
    }

    /**
     * "Converts lon/lat to pixel coordinates in given zoom of the EPSG:4326 pyramid"
     * 经纬度 转化为 以mi为单位的坐标
     *
     * @param lng
     * @param lat
     * @return
     */
    public Point lngLatToMeter(double lng, double lat) {
        double mx = lng * originShift / 180.0;
        double my = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);
        my = my * originShift / 180.0;
        return new Point(mx, my);
    }

    /**
     * 经纬度所在的tile编号
     *
     * @param lng
     * @param lat
     * @return
     */
    public Point lngLatToTile(double lng, double lat, int zoom) {
        // 经纬度->墨卡托米
        Point meter = lngLatToMeter(lng, lat);
        // 墨卡托米->像素
        Point meterToPixels = meterToPixels(meter.getX(), meter.getY(), zoom);

        //像素->tile
        Point tilePoint = pixelToTile(meterToPixels.getX(), meterToPixels.getY());
        return tilePoint;
    }

    /**
     * "Converts pixel coordinates in given zoom level of pyramid to EPSG:3857"
     * tile (tx,ty)对应的经纬度范围
     * 墨卡托 tile编码规则 左上角 为（0,0） 向右向下生长
     * 墨卡托坐标单位 为 mi  坐标原点为 将 中央子午线和赤道的交点 (0,0)
     * 这个方法 将 像素坐标 转化为 以mi为单位的 墨卡托坐标
     * <p>
     * (0,0)
     * +-------------------------------
     * |               |
     * |               |
     * |               |
     * |---------------|(0,0)---------------  py*res
     * |               |
     * |               |
     *
     * @param px
     * @param py
     * @param zoom
     */
    public Point pixelsToMeters(double px, double py, int zoom) {
        //zoom级别 每个像素对应的地面米数 该值只在赤道上是准确的 其他地方都有偏差，但是这个偏差只是为了对位置的一个描述
        double res = resolution(zoom);
        //    墨卡托 米     离左上角的米数       墨卡托的坐标原点X
        double mx = px * res - originShift;
        //     墨卡托 米       离左上角的米数 Y方向      墨卡托的坐标原点Y
        double my = -(py * res - originShift);
        return new Point(mx, my);
    }

    /**
     * 给定一个经纬度坐标范围 返回一个 墨卡托坐标系下 zoom级别中对应的 瓦片范围
     *
     * @param lngLat
     * @param zoom
     * @return
     */
    public Box lngLatBoxToTileBox(Box lngLat, int zoom) {

        // 影像平铺到WEB MOCATOR平面上，主要是为了展示
        // 当前影像的分辨率 与某个级别的分辨率不一样，所以 输出瓦片的时候要进行采样
        // 一般都会选取 分辨率比图像分辨率高的层级进行输出
        // 根据图像经纬度 可以找到其所在级别瓦片的比那好
        Point tileBL = lngLatToTile(lngLat.getXmin(), lngLat.getYmin(), zoom);
        Point tileTR = lngLatToTile(lngLat.getXmax(), lngLat.getYmax(), zoom);
        return new Box(tileBL.getX(), tileBL.getY(), tileTR.getX(), tileTR.getY());
    }

    /**
     * 给定瓦片的范围 范围其经纬度信息范围
     *
     * @param tileNo
     * @param zoom
     * @return
     */
    public Box tileNoBoxToLngLatBox(Box tileNo, int zoom) {

        Box lt = tileLngLatBounds((long) tileNo.getXmin(), (long) tileNo.getYmin(), zoom);
        Box br = tileLngLatBounds((long) tileNo.getXmax(), (long) tileNo.getYmax(), zoom);

        return new Box(lt.getXmin(), br.getYmin(), br.getXmax(), lt.getYmax());
    }

    /**
     * Converts EPSG:3857 to pyramid pixel coordinates in given zoom level"
     *
     * @param mx
     * @param my
     * @param zoom
     * @return
     */
    public Point meterToPixels(double mx, double my, int zoom) {
        double res = resolution(zoom);
        double px = (mx + originShift) / res;
        double py = (originShift - my) / res;
        return new Point(px, py);
    }

    /**
     * "Returns a tile covering region in given pixel coordinates"
     *
     * @param px
     * @param py
     * @return
     */
    public Point pixelToTile(double px, double py) {
        double tx = Math.ceil(px / tileSize) - 1;
        double ty = Math.ceil(py / tileSize) - 1;
        return new Point(tx, ty);
    }

    /**
     * "Resolution (meters/pixel) for given zoom level (measured at Equator)"
     *
     * @param zoom
     * @return
     */
    public double resolution(int zoom) {
        return initialResolution / (Math.pow(2, zoom));
    }

    /**
     * "Move the origin of pixel coordinates to top-left corner"
     *
     * @param px
     * @param py
     * @param zoom
     * @return
     */
    public Point pixelsToRaster(double px, double py, int zoom) {
        double mapSize = tileSize << zoom;
        return new Point(px, mapSize - py);
    }

    /**
     * 地理坐标 转化为 tile内像素坐标 (256*256)
     * 坐标系为 左下角为坐标原点
     *
     * @param lng
     * @param lat
     * @param zoom
     * @return
     */
    public Point geoToTile(double lng, double lat, double tx, double ty, int zoom) {

        Point meter = lngLatToMeter(lng, lat);
        TileNo tile = tileNoFromWgs84(lat, lng, zoom);
        // 这里的像素坐标为 是以左下角为原点的坐标空间
        Point pixel = meterToPixels(meter.getX(), meter.getY(), zoom);
        // 像素坐标必须转换为以左下角为原点的像素坐标
        double tilex = pixel.getX() - tile.getTileX() * tileSize;
        double tiley = pixel.getY() - tile.getTileY() * tileSize;
        Point result = new Point(tilex, tiley);
        return result;
    }

    /**
     * 墨卡托坐标下 tile的编号规则 左下角的编号为 (0,0)  向右 向上增加
     * WGS坐标 统一称做 gps坐标
     *
     * @param tx   墨卡托坐标下的X方向瓦片编号
     * @param ty   墨卡托坐标下的Y方向瓦片编号
     * @param zoom 图像LEVEL
     * @return
     */
    public Box tileLngLatBounds(long tx, long ty, int zoom) {
        // 瓦片左上角 和右下角的 以米为单位的坐标
        Point topLeftMi = pixelsToMeters(tx * tileSize, ty * tileSize + tileSize, zoom);
        Point bottomRightMi = pixelsToMeters(tx * tileSize + tileSize, ty * tileSize, zoom);

        //瓦片左上角 和右下角的 经纬度坐标
        Point topLeftGPS = meterTolngLat(topLeftMi.getX(), topLeftMi.getY());
        Point bottomRightGPS = meterTolngLat(bottomRightMi.getX(), bottomRightMi.getY());
        Box box = new Box(topLeftGPS.getX(), bottomRightGPS.getY(), bottomRightGPS.getX(), topLeftGPS.getY());
        return box;
    }

    /**
     * "Returns bounds of the given tile in EPSG:3857 coordinates"
     *
     * @param tx
     * @param ty
     * @param zoom
     * @return
     */
    public Box tileBounds(double tx, double ty, int zoom) {
        Point minPoint = pixelsToMeters(tx * tileSize, ty * tileSize, zoom);
        // log.info("{}-{} meter min {}", tx, ty, minPoint.toString());
        minPoint = meterTolngLat(minPoint.getX(), minPoint.getY());
        // log.info("{}-{} lng min {}", tx, ty, minPoint.toString());

        Point maxPoint = pixelsToMeters((tx + 1) * tileSize, (ty + 1) * tileSize, zoom);
        // log.info("{}-{} meter max {}", tx, ty, maxPoint.toString());
        maxPoint = meterTolngLat(maxPoint.getX(), maxPoint.getY());
        // log.info("{}-{} lng max {}", tx, ty, maxPoint.toString());
        Box box = new Box(minPoint.getX(), minPoint.getY(), maxPoint.getX(), maxPoint.getY());
        return box;
    }

    /**
     * 经纬度坐标 所在的tile索引
     * 参考 https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
     *
     * @param lat
     * @param lng
     * @param zoom
     * @return
     */
    public static TileNo tileNoFromWgs84(double lat, double lng, int zoom) {
        int xtile = (int) Math.floor((lng + 180) / 360 * (1 << zoom));
        double d0=Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat)));
        double d2=(1-d0/Math.PI)/2;
        int ytile = (int) Math.floor(d2* (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        TileNo tileNo = new TileNo();
        tileNo.setTileX(xtile);
        tileNo.setTileY(ytile);
        tileNo.setZoom(zoom);
        return tileNo;
    }
    /**
     * tile (tx,ty)对应的经纬度范围
     * 墨卡托 tile编码规则 左上角 为（0,0） 向右向下生长
     * 墨卡托坐标单位 为 mi  坐标原点为 将 中央子午线和赤道的交点 (0,0)
     *
     * @param tx
     * @param ty
     * @param zoom
     * @return
     */
    public Box tileBoundWgs84(double tx, double ty, int zoom) {
        //左上角 对应的墨卡托坐标
        Point LT = pixelsToMeters(tx * tileSize, ty * tileSize, zoom);
        //墨卡托转为 经纬度
        LT = meterTolngLat(LT.getX(), LT.getY());

        //右下角 对应的墨卡托坐标
        Point RB = pixelsToMeters(tx * tileSize + tileSize, ty * tileSize + tileSize, zoom);
        //墨卡托转为 经纬度
        RB = meterTolngLat(RB.getX(), RB.getY());

        return new Box(LT.getX(), RB.getY(), RB.getX(), LT.getY());
    }

    /**
     * 某一个级别下的瓦片编号 tx ty ,计算其WEB墨卡托的像素坐标
     * (tx ty) 左上角为 (0,0)  向右 向下 增加
     * WEB墨卡托 所坐标系定义  （地图中心点为坐标原点）
     * (tx*512 , ty*512)是一个世界坐标系
     * 第0级是一个 512*512图片 对应一个地球表面 赤道上 每个像素对应的地面距离为 RES= 2*PI*r/512
     * 第n级赤道上每个像素对应的地面距离为 RES/2^n
     *
     * @param tx
     * @param ty
     * @param zoom
     * @return
     */
    public Box tileBoundsWebMercator(double tx, double ty, int zoom) {
        Point lt = pixelsToMeters(tx * tileSize, ty * tileSize, zoom);
        Point rb = pixelsToMeters((tx + 1) * tileSize, (ty + 1) * tileSize, zoom);
        Box box = new Box(lt.getX(), rb.getY(), rb.getX(), lt.getY());
        return box;
    }

    /**
     * 墨卡托坐标 转化为经纬度
     *
     * @param meter
     * @return
     */
    public Box meterToGeo(Box meter) {
        Point pt1 = meterTolngLat(meter.getXmin(), meter.getYmin());
        Point pt2 = meterTolngLat(meter.getXmax(), meter.getYmax());
        return new Box(pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY());
    }


    /**
     * "Maximal scaledown zoom of the pyramid closest to the pixelSize."
     *
     * @param pixelSize
     * @return
     */
    public int zoomForPixelSize(double pixelSize) {
        for (int i = 0; i < MAXZOOMLEVEL; i++) {
            if (pixelSize > resolution(i)) {
                if (i >= 1) {
                    return i - 1;
                }
                return 0;
            }
        }
        return 0;
    }

    /**
     * 墨卡托坐标 在 tile下的像素坐标
     * @param mx
     * @param my
     * @param tileX
     * @param tileY
     * @param zoom
     * @return
     */
    public Point meterToTile(double mx, double my, long tileX,long tileY,int zoom) {
        Box tileBound = tileBoundsWebMercator(tileX, tileY, zoom);
        double x =  tileSize * (mx - tileBound.getXmin()) / (tileBound.getXmax() - tileBound.getXmin());
        double y =  tileSize * ( tileBound.getYmax()-my) / (tileBound.ymax-tileBound.ymin);
        return new Point(x,y);
    }
}
