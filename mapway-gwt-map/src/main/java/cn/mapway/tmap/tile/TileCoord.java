package cn.mapway.tmap.tile;

import lombok.Data;

/**
 * An array of three numbers representing the location of a tile in a tile
 * grid. The order is `z` (zoom level), `x` (column), and `y` (row).
 */
@Data
public class TileCoord {
    private double x;
    private double y;
    private double z;
    public TileCoord(double x, double y, double z)
    {
        update(x,y,z);
    }

    public TileCoord update(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    public String getKeyZXY()
    {
        return z + "/" + x + "/" + y;
    }

    public String getKey(){
        return getKeyZXY();
    }
}
