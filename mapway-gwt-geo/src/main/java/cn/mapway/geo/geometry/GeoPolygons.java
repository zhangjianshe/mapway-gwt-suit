package cn.mapway.geo.geometry;

import cn.mapway.geo.shared.vector.Point;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * GeoLine
 *
 * @author zhangjianshe@gmail.com
 */
public class GeoPolygons extends GeoObject implements IsSerializable {

    List<Lines> liness;

    public GeoPolygons() {
        type = "MultiPolygon";
        liness = new ArrayList<>();
    }

    public GeoPolygons(GeoPolygon[] geoPolygons) {
        type = "MultiPolygon";
        liness = new ArrayList<>();
        for (int i = 0; i < geoPolygons.length; i++) {
            GeoPolygon geoPolygon = geoPolygons[i];
            if (geoPolygon != null) {
                Lines lines = geoPolygon.getLines();
                liness.add(lines);
            }
        }
    }

    public GeoPoint center() {
        if (liness.size() == 0) {
            return new GeoPoint(0.0d, 0.0d);
        }
        GeoExtend extend = liness.get(0).getExtend();
        for (int i = 1; i < liness.size(); i++) {
            GeoExtend extend1 = liness.get(i).getExtend();
            extend.extended(extend1);
        }
        return extend.center();
    }

    /**
     * 强制右手规则
     */
    public void forceRightHandRule() {
        for (Lines lines : liness) {
            lines.forceRightHandRule();
        }
    }

    public List<Lines> getLiness() {
        return liness;
    }

    public String toGeoJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"").append(type).append("\",\"coordinates\":[");
        int out = 0;
        for (Lines lines : liness) {
            if (out > 0) {
                sb.append(",");
            }
            sb.append("[");
            int index = 0;
            for (Line line : lines.lines) {
                if (index > 0) {
                    sb.append(",");
                }
                sb.append(line.toString());
                index++;
            }
            sb.append("]");
            out++;
        }

        sb.append("]");
        sb.append(getCrsString());
        sb.append("}");
        return sb.toString();
    }
}
