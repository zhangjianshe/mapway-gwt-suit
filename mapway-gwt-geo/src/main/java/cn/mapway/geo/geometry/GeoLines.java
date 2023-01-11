package cn.mapway.geo.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * GeoLines
 *
 * @author zhangjianshe@gmail.com
 */
public class GeoLines extends GeoObject {
    List<Line> lines;

    public GeoLines() {
        type = "MultiLineString";
        lines = new ArrayList<>();
    }

    public GeoLines(GeoLine[] geoLines) {
        type = "MultiLineString";
        lines = new ArrayList<>();
        for (int i = 0; i < geoLines.length; i++) {
            GeoLine geoLine = geoLines[i];
            lines.add(geoLine.line);
        }
    }

    public GeoExtend getExtend() {
        GeoExtend extend = new GeoExtend();
        for (Line line : lines) {
            extend.extended(line.getExtend());
        }
        return extend;
    }

    public GeoPoint center()
    {
        return getExtend().center();
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public List<Line> getLines() {
        return lines;
    }

    public String toGeoJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"").append(type).append("\",\"coordinates\":[");
        int index = 0;
        for (Line line : lines) {
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
}
