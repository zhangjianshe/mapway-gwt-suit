package cn.mapway.geo.geometry;

/**
 * GeoLine
 *
 * @author zhangjianshe@gmail.com
 */
public class GeoLine extends GeoObject {

    Line line;

    public GeoLine() {
        type = "LineString";
        line = new Line();
    }

    public GeoLine(Line line) {
        type = "LineString";
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public String toGeoJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"").append(type).append("\",\"coordinates\":");
        sb.append(line.toString());
        sb.append(getCrsString());
        sb.append("}");
        return sb.toString();
    }

    public GeoPoint center()
    {
        return line.center();
    }

}
