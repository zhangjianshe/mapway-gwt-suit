package cn.mapway.geo.shared.vector;

import java.io.Serializable;

/**
 * Point
 * 点
 *
 * @author zhang
 */
public class Point extends GeoObject implements Serializable {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public int getXAsInt() {
        return (int) x;
    }
    public int getYAsInt() {
        return (int) y;
    }

    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public void copyFrom(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String toString() {
        return x+","+y;
    }

    @Override
    public Box getExtend() {
        Box box = new Box();
        box.expand(x, y);
        return box;
    }

    public String toWKT() {
        return "POINT("+x+" "+y+")";
    }
    public Point clone() {
        return new Point(x, y);
    }

    public boolean equals(Object pt) {
        if (pt == null) {
            return false;
        }
        if (pt == this) {
            return true;
        }
        if (pt instanceof Point) {
            Point pt0 = (Point) pt;
            return pt0.x == this.x && pt0.y == this.y;
        } else {
            return false;
        }
    }
}
