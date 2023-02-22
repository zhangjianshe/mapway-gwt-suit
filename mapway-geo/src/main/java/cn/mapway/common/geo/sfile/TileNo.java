package cn.mapway.common.geo.sfile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TileNo
 * 瓦片的编号
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TileNo {
    long tileX;
    long tileY;
    int zoom;
}
