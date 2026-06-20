package cn.mapway.common.geo.geotools.tile.xyz;

import org.geotools.tile.TileIdentifier;
import org.geotools.tile.impl.ZoomLevel;
import org.geotools.tile.impl.osm.OSMTileIdentifier;

/**
 * 苍灵 XYZ 瓦片标识符，遵循 OSM Slippy Map 的 z/x/y 网格逻辑.
 * <p>
 * {@link #getCode()} 返回 {@code "z/x/y"} 格式，用作回退 URL 的路径部分；
 * 主 URL 由 {@link CanglingTile} 通过模板替换生成，不依赖此方法。
 * </p>
 *
 * @author baoshuaiZealot@163.com
 */
public class CanglingTileIdentifier extends OSMTileIdentifier {

    public CanglingTileIdentifier(int x, int y, ZoomLevel zoomLevel, String serviceName) {
        super(x, y, zoomLevel, serviceName);
    }

    @Override
    public String getId() {
        final String separator = "_";
        StringBuilder sb = createGenericCodeBuilder(separator);
        sb.insert(0, separator).insert(0, getServiceName());
        return sb.toString();
    }

    @Override
    public String getCode() {
        final String separator = "/";
        return createGenericCodeBuilder(separator).toString();
    }

    private StringBuilder createGenericCodeBuilder(final String separator) {
        StringBuilder sb = new StringBuilder(50);
        sb.append(getZ()).append(separator).append(getX()).append(separator).append(getY());
        return sb;
    }

    @Override
    public TileIdentifier getRightNeighbour() {
        return new CanglingTileIdentifier(
                TileIdentifier.arithmeticMod(
                        (getX() + 1), getZoomLevel().getMaxTilePerRowNumber()),
                getY(),
                getZoomLevel(),
                getServiceName());
    }

    @Override
    public TileIdentifier getLowerNeighbour() {
        return new CanglingTileIdentifier(
                getX(),
                TileIdentifier.arithmeticMod(
                        (getY() + 1), getZoomLevel().getMaxTilePerRowNumber()),
                getZoomLevel(),
                getServiceName());
    }
}
