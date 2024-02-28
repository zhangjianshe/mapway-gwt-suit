package cn.mapway.geo.geometry;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * GeoLine
 *
 * @author zhangjianshe@gmail.com
 */
public class GeoPolygon extends GeoObject implements IsSerializable {

    Lines lines;

    public GeoPolygon() {
        type = GeoJsonType.GEO_POLYGON;
        lines = new Lines();
    }

    public GeoPoint center() {
        return getExtend().center();
    }

    public GeoExtend getExtend() {
        return lines.getExtend();
    }

    public Lines getLines() {
        return lines;
    }

    public String toGeoJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"").append(type).append("\",");
        sb.append("\"coordinates\":[");
        int index = 0;
        for (Line line : lines.lines) {
            if (index > 0) {
                sb.append(",");
            }
            sb.append(line.toString());
            index++;
        }
        sb.append("]");
        sb.append(getCrsString());
        sb.append("}");
        return sb.toString();
    }

    /**
     * 强制右手规则
     */
    public void forceRightHandRule() {
        lines.forceRightHandRule();
    }
}
