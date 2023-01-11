package cn.mapway.geo.geometry;

import java.util.HashMap;

/**
 * GeoFeature
 *
 * @author zhangjianshe@gmail.com
 */

public class GeoFeature extends GeoObject {
    public GeoObject geometry;
    public HashMap<String, Object> properties;

    public GeoFeature() {
        type = GeoJsonType.GEO_FEATURE;
        geometry = null;
        properties = new HashMap<>();
    }

    public GeoObject getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoObject geometry) {
        this.geometry = geometry;
    }

    @Override
    public GeoExtend getExtend() {
        if (geometry != null) {
            return geometry.getExtend();
        }
        return null;
    }

    public final Object value(String key) {
        if (properties == null) {
            return null;
        }
        return properties.get(key);
    }

    public void put(String key, String value) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(key, value);
    }

    public String toGeoJson() {
        StringBuilder sb = new StringBuilder();

        sb.append("{\"type\":\"").append(type).append("\"");
        if (properties != null) {
            sb.append(",\"properties\":{");
            int index = 0;
            for (String key : properties.keySet()) {
                if (index > 0) {
                    sb.append(",");
                }
                sb.append("\"" + key + "\":");
                Object v = properties.get(key);
                if (v == null) {
                    sb.append("null");
                }
                if (v instanceof String || v instanceof StringBuilder) {
                    sb.append("\"").append(v).append("\"");
                } else {
                    sb.append(v.toString());
                }
                index++;
            }
            sb.append("}");
        }
        if (geometry != null) {
            sb.append(",\"geometry\":");
            sb.append(geometry.toGeoJson());
            sb.append("}");
        }
        return sb.toString();
    }
}
