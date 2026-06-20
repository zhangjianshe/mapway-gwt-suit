package cn.mapway.common.geo.geotools.tile.xyz;

import org.geotools.tile.Tile;
import org.geotools.tile.TileIdentifier;
import org.geotools.tile.TileService;
import org.geotools.tile.impl.WebMercatorTileFactory;
import org.geotools.tile.impl.ZoomLevel;

import java.net.URL;

/**
 * 苍灵 XYZ 瓦片，重写 {@link #getUrl()} 以支持任意 URL 模板.
 * <p>
 * 与 {@link org.geotools.tile.impl.osm.OSMTile} 的区别：
 * OSM 使用 {@code baseUrl + code + ".png"}，苍灵使用模板变量替换，
 * 支持任意路径结构、任意扩展名、子域轮换 {s}。
 * </p>
 *
 * @author baoshuaiZealot@163.com
 */
public class CanglingTile extends Tile {

    public static final int DEFAULT_TILE_SIZE = 256;

    /** 子域字符集 */
    private static final char[] SUBDOMAINS = {'a', 'b', 'c'};

    /**
     * 通过坐标创建瓦片.
     */
    public CanglingTile(int x, int y, ZoomLevel zoomLevel, TileService service) {
        this(new CanglingTileIdentifier(x, y, zoomLevel, service.getName()), service);
    }

    /**
     * 通过 TileIdentifier 创建瓦片.
     */
    public CanglingTile(TileIdentifier tileName, TileService service) {
        super(
                tileName,
                WebMercatorTileFactory.getExtentFromTileName(tileName),
                DEFAULT_TILE_SIZE,
                service);
    }

    /**
     * 核心方法：根据模板变量构造瓦片 URL.
     * <p>
     * 将 {@link CanglingService#getUrlTemplate()} 中的
     * {z}/{x}/{y}/{s} 替换为实际值。
     * </p>
     */
    @Override
    public URL getUrl() {
        if (this.service instanceof CanglingService) {
            CanglingService cs = (CanglingService) this.service;
            String template = cs.getUrlTemplate();
            String url = resolveTemplate(template);
            try {
                return new URL(url);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create URL from template: " + template, e);
            }
        }
        // 非 CanglingService 时，回退到 OSM 风格 URL
        String url = this.service.getBaseUrl() + getTileIdentifier().getCode() + ".png";
        try {
            return new URL(url);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create URL from " + url, e);
        }
    }

    /**
     * 模板变量替换.
     * 支持的变量: {z}, {x}, {y}, {s}
     */
    private String resolveTemplate(String template) {
        TileIdentifier id = getTileIdentifier();
        String result = template
                .replace("{z}", String.valueOf(id.getZ()))
                .replace("{x}", String.valueOf(id.getX()))
                .replace("{y}", String.valueOf(id.getY()));

        // 子域轮换: (x + y) % 3 → a/b/c
        if (result.contains("{s}")) {
            int index = Math.abs(id.getX() + id.getY()) % SUBDOMAINS.length;
            result = result.replace("{s}", String.valueOf(SUBDOMAINS[index]));
        }
        return result;
    }
}
