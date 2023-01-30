package cn.mapway.tmap.tile;

import lombok.Data;

/**
 * 构造 TileGird的参数信息
 */
@Data
public class TileGridOption {
    Double minZoom;
    double[] resolutions;
}
