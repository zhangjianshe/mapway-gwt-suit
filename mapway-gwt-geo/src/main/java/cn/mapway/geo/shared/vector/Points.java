package cn.mapway.geo.shared.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Points
 *
 * @author zhang
 */
public class Points extends GeoObject implements Serializable {
    private final List<Point> points;
    Box box;

    public Points() {
        points = new ArrayList<>();
        box = new Box();
    }


    public void addPoint(double x, double y) {
        points.add(new Point(x, y));
        box.expand(x,y);
    }

    public void addPoint(Point point) {
        addPoint(point.x, point.y);
    }

    public int getCount() {
        return points.size();
    }

    public Point getPoint(int index) {
        if (index >= 0 && index < points.size()) {
            return points.get(index);
        }
        return null;
    }


    public Box getExtend() {
        return box;
    }
}
