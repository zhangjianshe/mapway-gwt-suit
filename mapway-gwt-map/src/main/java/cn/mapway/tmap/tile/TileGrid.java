package cn.mapway.tmap.tile;

import lombok.Data;

/**
 * tile grid
 * =========
 * Base class for setting the grid pattern for sources accessing tiled-image servers
 *
 */
@Data
public class TileGrid {

    double minZoom;
    double maxZoom;
    double[] resolutions;
    double zoomFactor=-1;


    /**
     * 构建一个TileGrid
     * @param option
     */
    public TileGrid(TileGridOption option)
    {
        minZoom= option.getMinZoom()!=null ? option.getMinZoom() :0;

        // resolutions must be sorted in descending order
        this.resolutions=option.getResolutions();



        this.maxZoom=this.resolutions.length-1;

    }

}
