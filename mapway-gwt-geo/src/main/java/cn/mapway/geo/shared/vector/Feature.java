package cn.mapway.geo.shared.vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Feature
 *
 * @author zhang
 */
public class Feature extends GeoObject  {
    public Map<String, Object> properties;
    GeoObject geometry;

    public Feature(GeoObject geoObject) {
        properties = new HashMap<>();
        this.geometry = geoObject;
    }
    public GeoObject getGeometry() {
        return geometry;
    }
    public void setGeometry(GeoObject geometry) {
        this.geometry = geometry;
    }

    public Box getExtend() {
        return this.geometry.getExtend();
    }

}
