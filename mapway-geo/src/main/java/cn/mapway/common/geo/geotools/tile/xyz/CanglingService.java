package cn.mapway.common.geo.geotools.tile.xyz;

import org.geotools.tile.TileFactory;
import org.geotools.tile.impl.osm.OSMService;

/**
 * 苍灵 XYZ 瓦片服务，支持任意 {z}/{x}/{y} URL 模板.
 * <p>
 * 与 {@link OSMService} 的核心区别：不再写死 OSM 的 baseUrl + code + ".png" 模式，
 * 而是保存完整 URL 模板，由 {@link CanglingTile#getUrl()} 做变量替换。
 * </p>
 *
 * <p>支持的模板变量：</p>
 * <ul>
 *   <li>{@code {z}} — zoom level</li>
 *   <li>{@code {x}} — tile column</li>
 *   <li>{@code {y}} — tile row</li>
 *   <li>{@code {s}} — 子域轮换 (a/b/c)</li>
 * </ul>
 *
 * <p>示例：</p>
 * <pre>{@code
 *   CanglingService service = new CanglingService("my-service",
 *       "https://tile.openstreetmap.org/{z}/{x}/{y}.png");
 * }</pre>
 *
 * @author baoshuaiZealot@163.com
 */
public class CanglingService extends OSMService {

    /** 完整 URL 模板，包含 {z}/{x}/{y} 占位符 */
    private final String urlTemplate;

    /** 每个 Service 实例持有自己的 TileFactory（非 static），因为 URL 模板可能不同 */
    private final TileFactory tileFactory;

    /**
     * 构造苍灵瓦片服务.
     *
     * @param name        服务名称（用于图层标识）
     * @param urlTemplate 完整 URL 模板，如 {@code https://server/{z}/{x}/{y}.png}
     */
    public CanglingService(String name, String urlTemplate) {
        super(name, urlTemplate);
        this.urlTemplate = urlTemplate;
        this.tileFactory = new CanglingTileFactory();
    }

    /**
     * 获取 URL 模板字符串.
     */
    public String getUrlTemplate() {
        return urlTemplate;
    }

    @Override
    public double[] getScaleList() {
        return super.getScaleList();
    }

    @Override
    public TileFactory getTileFactory() {
        return tileFactory;
    }
}
