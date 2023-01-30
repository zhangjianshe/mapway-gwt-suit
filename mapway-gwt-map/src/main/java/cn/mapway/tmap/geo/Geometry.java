package cn.mapway.tmap.geo;


import cn.mapway.tmap.tile.Coordinate;
import lombok.Data;

/**
 * Abstract base class; normally only used for creating subclasses and not
 * instantiated in apps.
 * Base class for vector geometries.
 *
 * To get notified of changes to the geometry, register a listener for the
 * generic `change` event on your geometry instance.
 */
@Data
public abstract class Geometry {
    Extent extent;
    int extentRevision=-1;
    double simplifiedGeometryMaxMinSquaredTolerance=0.0;
    double simplifiedGeometryRevision=0;
    public Geometry() {
        extent = Extent.createEmpty();
    }

    public abstract Geometry clone() ;

    public  abstract  Coordinate closestPointXY(double x, double y,double minSquaredDistance);

    public  boolean contains(double x, double y)
    {
        Coordinate coordinate=getClosestPoint(x,y);
        return coordinate.getX()==x && coordinate.getY()==y;
    }


    public Coordinate getClosestPoint(double x, double y)
    {
        return closestPointXY(x,y,Double.POSITIVE_INFINITY);
    }
}
