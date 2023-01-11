package cn.mapway.geo.shared.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Polygons
 *
 * @author zhang
 */
public class Polygons extends GeoObject implements Serializable {
    public List<Polygon> polygons;

    public Polygons() {
        polygons = new ArrayList<>();
    }

    public Box getExtend() {
        Box box = new Box();
        for (Polygon polygon : polygons) {
            box.expand(polygon.getExtend());
        }
        return box;
    }

    /**
     * 强制右手规则
     */
    public void forceRightHandRule() {
        for (Polygon polygon : polygons) {
            polygon.forceRightHandRule();
        }
    }
}
