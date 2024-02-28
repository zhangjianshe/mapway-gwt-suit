package cn.mapway.common.geo.sqlite;

import org.gdal.ogr.Geometry;

import java.awt.*;

/**
 * DrawTile
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public interface IDrawTile {
    void draw(Graphics2D g, double tx, double ty, int zoom, Geometry filter);
}
