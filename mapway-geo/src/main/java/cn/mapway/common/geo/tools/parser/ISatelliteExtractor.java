package cn.mapway.common.geo.tools.parser;


import cn.mapway.geo.client.raster.ImageInfo;

/**
 * ISatelliteExtractor
 * 卫星数据提取接口
 * @author zhang
 */
public interface ISatelliteExtractor {
    boolean extract(String fileName, ImageInfo imageInfo);
}
