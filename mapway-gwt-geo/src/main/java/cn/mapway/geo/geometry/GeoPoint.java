package cn.mapway.geo.geometry;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * GeoPoint
 *
 * @author zhangjianshe@gmail.com
 */
public class GeoPoint extends GeoObject implements IsSerializable {
    public double x;
    public double y;

    public GeoPoint() {
        type = "Point";
        x = 0.0;
        y = 0.0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public GeoPoint setX(double x) {
        this.x = x;
        return this;
    }
    public GeoPoint setY(double y) {
        this.y = y;
        return this;
    }

    public GeoPoint(double x, double y) {
        type = "Point";
        this.x = x;
        this.y = y;
    }

    public GeoPoint clone() {
        return this;
    }

    public void copyFrom(GeoPoint pt) {
        this.x = pt.x;
        this.y = pt.y;
    }

    public void setData(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public String toGeoJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"").append(type).append("\",\"coordinates\":[");
        sb.append(x).append(",").append(y);
        sb.append("]");
        sb.append(getCrsString());
        sb.append("}");
        return sb.toString();
    }
}
