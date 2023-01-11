package cn.mapway.geo.geometry;

import cn.mapway.geo.shared.vector.Point;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * GeoPoint
 *
 * @author zhangjianshe@gmail.com
 */
public class GeoPoints extends GeoObject implements IsSerializable {
    List<GeoPoint> pts;

    public GeoPoints() {
        this.type = GeoJsonType.GEO_MULTI_POINT;
        pts = new ArrayList<>();
    }

    public GeoPoints(GeoPoint[] geoPoints) {
        this.type = GeoJsonType.GEO_MULTI_POINT;
        pts = new ArrayList<>();
        for (int i = 0; i < geoPoints.length; i++) {
            GeoPoint geoPoint = geoPoints[i];
            pts.add(geoPoint);
        }
    }

    public GeoPoints(List<GeoPoint> pts) {
        this.type = GeoJsonType.GEO_MULTI_POINT;
        this.pts = pts;
    }

    public GeoPoint center() {
        double xsum = 0.0d;
        double ysum = 0.0d;

        if (pts.size() == 0) {
            return new GeoPoint(xsum, ysum);
        }

        for (GeoPoint pt : pts) {
            xsum += pt.x;
            ysum += pt.y;
        }

        return new GeoPoint(xsum / pts.size(), ysum / pts.size());
    }

    public List<GeoPoint> getPoints() {
        return pts;
    }

    public String toGeoJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"").append(type).append("\",\"coordinates\":[");
        int index = 0;
        for (GeoPoint pt : pts) {
            if (index > 0) {
                sb.append(",");
            }
            sb.append("[");
            sb.append(pt.x).append(",").append(pt.y);
            sb.append("]");
            index++;
        }
        sb.append("]");
        sb.append(getCrsString());
        sb.append("}");
        return sb.toString();
    }
}
