package cn.mapway.geo.shared.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Line
 * 自己拥有点的拷贝
 *
 * @author zhang
 */
public class Line extends GeoObject implements Serializable {
    private final List<Point> points;
    private final Box box;

    public Line() {
        points = new ArrayList<>();
        box = new Box();
    }

    public void addPoint(double x, double y) {
        points.add(new Point(x, y));
        box.expand(x, y);
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

    public void closeLine() {
        if (getCount() > 2) {
            Point start = points.get(0);
            Point end = points.get(points.size() - 1);
            if (!start.equals(end)) {
                points.add(start.clone());
            }
        }
    }

    @Override
    public Box getExtend() {
        return box;
    }

    /**
     * 坐标顺序颠倒
     */
    public void reverse() {
        Collections.reverse(points);
    }
}
