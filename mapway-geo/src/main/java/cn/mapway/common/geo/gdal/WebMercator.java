package cn.mapway.common.geo.gdal;

import cn.mapway.common.geo.sfile.TileNo;
import cn.mapway.geo.shared.vector.Box;

/**
 * WebMercator
 * tools for web mercator
 * https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 * some information about tile server
 * https://www.maptiler.com/google-maps-coordinates-tile-bounds-projection/#0/-140.84/67.89
 *
 * @author zhang
 */
public class WebMercator {
    public final static int SRC_WEB_MERCATOR = 3857;
    public final static int SRC_WGS84 = 4326;
    // 地球半径 单位m
    public static final long EARTH_RADIUS = 6378137;
    public static final double initialResolution = 2 * Math.PI * EARTH_RADIUS / 256;
    public static final double originShift = 2 * Math.PI * EARTH_RADIUS / 2.0;
    /**
     * 赤道一个像素的分辨率率 zoom=0
     * 40075.016686 * 1000 / 256 ≈ 6378137.0 * 2 * pi / 256 ≈ 156543.03
     */
    static double RESOLUTION0 = 156543.03d;
    static double EARTH_EQUATOR_LENGTH = 40075016.686d;

    /**
     * zoom level 的分辨率 单位米
     *
     * @param zoom
     * @return
     */
    public static double zoomResolution(double latitude, int zoom) {
        return RESOLUTION0 * Math.cos(latitude) / Math.pow(2, zoom);
    }

    public static double resolution(int zoom) {
        return initialResolution / (Math.pow(2, zoom));
    }

    /**
     * tile的wgs84 范围
     *
     * @param tileNo
     * @return
     */
    public static Box tileBoundWgs84(TileNo tileNo) {
        return tileBoundWgs84(tileNo.getTileX(), tileNo.getTileY(), tileNo.getZoom());
    }

    /**
     * tile的范围
     *
     * @param tileX
     * @param tileY
     * @param zoom
     * @return
     */
    public static Box tileBoundWgs84(long tileX, long tileY, int zoom) {

        Box box = new Box();
        box.ymax = tile2lat(tileY, zoom);
        box.ymin = tile2lat(tileY + 1, zoom);
        box.xmin = tile2lon(tileX, zoom);
        box.xmax = tile2lon(tileX + 1, zoom);
        return box;
    }

    /**
     * tile 左上角的 墨卡托坐标
     *            |
     *            |
     *            |
     *     -------+-------   EQUATOR_LENGTH
     *            |
     *            |
     *            |
     *
     *
     * @param tileX
     * @param tileY
     * @param zoom
     * @return
     */
    public static Box tileBoundMercator(long tileX, long tileY, int zoom) {
        double half = EARTH_EQUATOR_LENGTH / 2;
        // length / tile
        double tileLength = EARTH_EQUATOR_LENGTH / Math.pow(2.0, zoom);
        double left = tileX * tileLength - half;
        double top = -(tileY * tileLength - half);
        double right = left + tileLength;
        double bottom = top - tileLength;
        return new Box(left, bottom, right, top);
    }

    static double tile2lon(long x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    static double tile2lat(long y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
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
}
