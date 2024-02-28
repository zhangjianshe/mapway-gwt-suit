package cn.mapway.common.geo.tools;

import cn.mapway.geo.client.raster.BandInfo;
import lombok.Data;
import org.gdal.gdal.Band;

/**
 * BandData
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Data
public class BandData {
    Band band;
    BandInfo info;
    public BandData(Band band, BandInfo info) {
        this.band = band;
        this.info = info;
    }
}
