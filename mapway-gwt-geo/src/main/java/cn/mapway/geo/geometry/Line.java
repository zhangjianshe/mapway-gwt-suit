package cn.mapway.geo.geometry;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Line
 *
 * @author zhangjianshe@gmail.com
 */
public class Line implements Serializable, IsSerializable {
    List<GeoPoint> points;
    GeoExtend extend;

    public Line() {
        points = new ArrayList<>();
        extend = new GeoExtend();
    }

    public GeoExtend getExtend() {
        return extend;
    }

    public void add(GeoPoint point) {
        points.add(point);
        extend.extendPoint(point);
    }

    public GeoPoint center() {
        return getExtend().center();
    }

    public void add(Double x, Double y) {
        GeoPoint point = new GeoPoint(x, y);
        extend.extendPoint(point);
        points.add(point);
    }

    public void head(Double x, Double y) {
        GeoPoint point = new GeoPoint(x, y);
        extend.extendPoint(point);
        points.add(0, point);
    }

    public void reverse() {
        Collections.reverse(points);
    }

    public List<GeoPoint> getPoints() {
        return points;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int index = 0;
        for (GeoPoint pt : points) {
            if (index > 0) {
                sb.append(",");
            }
            sb.append("[").append(pt.toString()).append("]");
            index++;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 确保 line is Closed
     */
    public void close() {
        GeoPoint ptFirst = points.get(0);
        GeoPoint ptLast = points.get(points.size() - 1);
        if (!ptFirst.equals(ptLast)) {
            points.add(ptFirst);
        }
    }
}
