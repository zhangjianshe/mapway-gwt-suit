package cn.mapway.geo.shared;

import lombok.Data;

@Data
public class GeoTile {
    long tileX;
    long tileY;
    int zoom;
}
