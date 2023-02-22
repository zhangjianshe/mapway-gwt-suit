package cn.mapway.common.geo.tools;

import cn.mapway.geo.client.raster.ImageInfo;
import org.gdal.gdal.Dataset;

/**
 * IExtractTile
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public interface ITileExtractor {
    /**
     * 从原始影像中提取一个Tile
     *
     * @param imageInfo
     * @param tileX
     * @param tileY
     * @param zoom
     * @param targetDataset
     */
    boolean extractTileToTarget(ImageInfo imageInfo, long tileX, long tileY, int zoom, Dataset sourceDataset, Dataset targetDataset);
}
