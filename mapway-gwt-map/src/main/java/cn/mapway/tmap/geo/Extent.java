package cn.mapway.tmap.geo;

import cn.mapway.tmap.tile.Coordinate;
import lombok.Data;

import java.util.List;

@Data
public class Extent {
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    public Extent(){

    }
    public Extent(double minX, double minY, double maxX, double maxY) {
       set(minX,minY,maxX,maxY);
    }

    public static Extent createEmpty()
    {
        Extent extent=new Extent();
        return extent.empty();
    }

    public Extent boundingExtent(Coordinate[] coordinates)
    {
        empty();
        for(int i=0; i<coordinates.length; i++)
        {
            extendCoordinate(coordinates[i]);
        }
        return this;
    }

    public Extent buffer(double bufferSize)
    {
        this.minX-=bufferSize;
        this.minY-=bufferSize;
        this.maxX+=bufferSize;
        this.maxY+=bufferSize;
        return this;
    }

    public Extent clone(){
        return new Extent(minX,minY,maxX,maxY);
    }

    public boolean containsCoordinates(double x,double y)
    {
        return minX <= x && x <= maxX && minY <= y && y <= maxX;
    }
    public boolean containsCoordinates(Coordinate coordinate)
    {
        return containsCoordinates(coordinate.getX(), coordinate.getY());
    }

    /**
     * 返回 坐标与这个范围的关系
     * @param coordinate
     * @return
     */
    public int relation(Coordinate coordinate)
    {
        double x = coordinate.getX();
        double y = coordinate.getY();
        int relationship = Relation.UNKNOWN;
        if (x < minX) {
            relationship = relationship | Relation.LEFT;
        } else if (x > maxX) {
            relationship = relationship | Relation.RIGHT;
        }
        if (y < minY) {
            relationship = relationship | Relation.BELOW;
        } else if (y > maxY) {
            relationship = relationship | Relation.ABOVE;
        }
        if (relationship == Relation.UNKNOWN) {
            relationship = Relation.INTERSECTING;
        }
        return relationship;
    }
    /**
     * Closest squared distance.
     * @param x
     * @param y
     * @return
     */
    public double closestSquaredDistanceXY(double x,double y)
    {
        double dx, dy;
        if (x < this.minX) {
            dx = this.minX - x;
        } else if (this.maxX < x) {
            dx = x - maxX;
        } else {
            dx = 0;
        }
        if (y < minY) {
            dy =minY - y;
        } else if (maxY < y) {
            dy = y - maxY;
        } else {
            dy = 0;
        }
        return dx * dx + dy * dy;
    }
    public static Extent createFromCoordinate(Coordinate coordinate)
    {
        Extent extent=new Extent();
        extent.set(
                coordinate.getX(), coordinate.getY(),
                coordinate.getX(), coordinate.getY()
                );
        return extent;
    }

    public Extent extendCoordinates(List<Coordinate> coordinates)
    {
        for(Coordinate coordinate : coordinates)
        {
            extendCoordinate(coordinate);
        }
        return this;
    }

    public Extent extend(Extent target)
    {
        if (target.minX < this.minX) {
            this.minX = target.minX;
        }
        if (target.minY > this.minY) {
            this.minY = target.minY;
        }
        if (target.maxX < this.maxX) {
            this.maxX = target.maxX;
        }
        if (target.maxY > this.maxY) {
            this.maxY = target.maxY;
        }
        return this;
    }

    public Extent extendCoordinate(Coordinate coordinate)
    {
        if (coordinate.getX() < this.minX) {
            this.minX = coordinate.getX();
        }
        if (coordinate.getX() > this.minX) {
            this.maxX = coordinate.getX();
        }
        if (coordinate.getY() < this.minY) {
            this.minY = coordinate.getY();
        }
        if (coordinate.getY() > this.maxY) {
            this.maxY = coordinate.getY();
        }
        return this;
    }
    public Extent set(double minX,double minY,double maxX,double maxY)
    {
        this.minX=minX;
        this.maxX=maxX;
        this.minY=minY;
        this.maxY=maxY;
        return this;
    }

    public Extent empty() {
        minX=Double.POSITIVE_INFINITY;
        minY=Double.POSITIVE_INFINITY;
        maxX=Double.NEGATIVE_INFINITY;
        maxY=Double.NEGATIVE_INFINITY;
        return this;
    }

    public boolean equals(Extent other) {
        return this.minX==other.minX &&
                this.minY==other.minY &&
                this.maxX==other.maxX &&
                this.maxY==other.maxY;
    }

    public boolean approximatelyEquals(Extent other,double tolerance) {
        return (
                Math.abs(minX - other.minX) < tolerance &&
                Math.abs(minY - other.minY) < tolerance &&
                Math.abs(maxX - other.maxX) < tolerance &&
                Math.abs(maxY - other.maxY) < tolerance
        );
    }
}
