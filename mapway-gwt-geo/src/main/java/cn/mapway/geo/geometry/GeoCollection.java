package cn.mapway.geo.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * GeoCollection
 *
 * @author zhangjianshe@gmail.com
 */
public class GeoCollection extends GeoObject {
    List<GeoObject> geometryList;

    public GeoCollection() {
        type = GeoJsonType.GEO_GEOMETRY_COLLECTION;
        geometryList = new ArrayList<GeoObject>();
    }

    public List<GeoObject> getGeometryList() {
        return geometryList;
    }

    public String toGeoJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"").append(type).append("\",\"geometries\":[");
        int index = 0;
        for (GeoObject geo : geometryList) {
            if (index > 0) {
                sb.append(",");
            }
            index++;
            sb.append(geo.toGeoJson());
        }
        sb.append("]");
        sb.append(getCrsString());
        sb.append("}");
        return sb.toString();
    }
}
