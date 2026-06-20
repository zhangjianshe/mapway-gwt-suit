package cn.mapway.common.geo.geotools.tile.xyz;

import org.geotools.tile.Tile;
import org.geotools.tile.TileFactory;
import org.geotools.tile.TileIdentifier;
import org.geotools.tile.TileService;
import org.geotools.tile.impl.WebMercatorTileService;
import org.geotools.tile.impl.ZoomLevel;
import org.geotools.tile.impl.osm.OSMTileFactory;
import org.geotools.tile.impl.osm.OSMTileIdentifier;

/**
 * 苍灵 XYZ 瓦片工厂，创建 {@link CanglingTile} 实例.
 * <p>
 * 继承 {@link OSMTileFactory}，复用其 {@code findTileAtCoordinate}、
 * {@code findRightNeighbour}、{@code findLowerNeighbour} 的坐标计算逻辑，
 * 仅重写 {@link #create(TileIdentifier, TileService)} 返回苍灵瓦片。
 * </p>
 *
 * @author baoshuaiZealot@163.com
 */
public class CanglingTileFactory extends OSMTileFactory {

    @Override
    public Tile create(TileIdentifier identifier, TileService service) {
        return new CanglingTile(identifier, service);
    }

    @Override
    public Tile findTileAtCoordinate(
            double lon, double lat, ZoomLevel zoomLevel, TileService service) {
        lat = TileFactory.normalizeDegreeValue(lat, 90);
        lon = TileFactory.normalizeDegreeValue(lon, 180);

        // 纬度限制在 Web Mercator 有效范围内
        lat = OSMTileFactory.moveInRange(
                lat,
                WebMercatorTileService.MIN_LATITUDE,
                WebMercatorTileService.MAX_LATITUDE);

        int xTile = (int) Math.floor((lon + 180) / 360 * (1 << zoomLevel.getZoomLevel()));
        int yTile =
                (int)
                        Math.floor(
                                (1
                                                - Math.log(
                                                                Math.tan(lat * Math.PI / 180)
                                                                        + 1
                                                                                / Math.cos(
                                                                                        lat
                                                                                                * Math.PI
                                                                                                / 180))
                                                        / Math.PI)
                                        / 2
                                        * (1 << zoomLevel.getZoomLevel()));
        if (yTile < 0) {
            yTile = 0;
        }
        return create(
                new OSMTileIdentifier(xTile, yTile, zoomLevel, service.getName()), service);
    }

    @Override
    public Tile findRightNeighbour(Tile tile, TileService service) {
        return create(tile.getTileIdentifier().getRightNeighbour(), service);
    }

    @Override
    public Tile findLowerNeighbour(Tile tile, TileService service) {
        return create(tile.getTileIdentifier().getLowerNeighbour(), service);
    }
}
