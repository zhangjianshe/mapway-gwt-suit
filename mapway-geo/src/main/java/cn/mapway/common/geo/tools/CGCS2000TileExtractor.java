package cn.mapway.common.geo.tools;

import lombok.extern.slf4j.Slf4j;

/**
 * CGCS2000 TileExtractor(EPSG:4490)
 * 偷个懒  直接使用 WGS84坐标转换代替，在我们的精度范围内，可以认为他们是一样的,未来有时间再进行补充完整
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class CGCS2000TileExtractor extends WGS84TileExtractor {

}
