package cn.mapway.geo.shared.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Polygon
 * 多边形
 *
 * @author zhang
 */
public class Polygon extends GeoObject implements Serializable {
    public List<Line> lines;

    public Polygon() {
        lines = new ArrayList<>();
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    @Override
    public Box getExtend() {
        Box box = new Box();
        for (Line line : lines) {
            box.expand(line.getExtend());
        }
        return box;
    }

    /**
     * 将多边形强制转化为 右手规则
     * 外边框是 逆时针方向 内部是顺时针方向
     * 算法参考 https://github.com/mapbox/geojson-rewind
     * if (rings.length === 0) return;
     * <p>
     * rewindRing(rings[0], outer);
     * for (var i = 1; i < rings.length; i++) {
     * rewindRing(rings[i], !outer);
     * }
     */
    public void forceRightHandRule() {
        if (lines.size() == 0)
            return;
        Line line = lines.get(0);
        rewindRing(line, false);
        for (int i = 1; i < lines.size(); i++) {
            rewindRing(lines.get(i), true);
        }
    }

    /**
     * 将多边形强制转化为 右手规则
     * 外边框是 逆时针方向 内部是顺时针方向
     * 算法参考 https://github.com/mapbox/geojson-rewind
     * function rewindRing(ring, dir) {
     * var area = 0, err = 0;
     * for (var i = 0, len = ring.length, j = len - 1; i < len; j = i++) {
     * var k = (ring[i][0] - ring[j][0]) * (ring[j][1] + ring[i][1]);
     * var m = area + k;
     * err += Math.abs(area) >= Math.abs(k) ? area - m + k : k - m + area;
     * area = m;
     * }
     * if (area + err >= 0 !== !!dir) ring.reverse();
     * }
     *
     * @return
     */
    private void rewindRing(Line line, boolean clockwise) {
        double area = 0d;
        double err = 0d;
        int len = line.getCount();
        int j = line.getCount() - 1;
        for (int i = 0; i < len; j = i++) {
            Point pi = line.getPoint(i);
            Point pj = line.getPoint(j);
            double k = (pi.x - pj.x) * (pj.y + pi.y);
            double m = area + k;
            err += Math.abs(area) >= Math.abs(k) ? (area - m + k) : (k - m + area);
            area = m;
        }
        if (area + err >= 0 != clockwise) {
            line.reverse();
        }
    }
}
