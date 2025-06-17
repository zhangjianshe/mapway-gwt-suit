package cn.mapway.geo.geometry;


import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;

import java.io.Serializable;

/**
 * GeoExtend
 *
 * @author zhangjianshe@gmail.com
 */
@JsType
public class GeoExtend implements Serializable, IsSerializable {
    public Double minLng = Double.MAX_VALUE;
    public Double maxLng = -Double.MAX_VALUE;
    public Double minLat = Double.MAX_VALUE;
    public Double maxLat = -Double.MAX_VALUE;

    public void set(Double t, Double r, Double b, Double l) {
        this.minLat = b;
        this.maxLat = t;
        this.maxLng = r;
        this.minLng = l;
    }

    public GeoPoint center() {
        return new GeoPoint((minLng + maxLng) / 2, (maxLat + minLat) / 2);
    }

    public Double width() {
        return maxLng - minLng;
    }

    public Double height() {
        return maxLat - minLat;
    }

    /**
     * 扩展这个范围
     *
     * @param point
     */
    public void extendPoint(GeoPoint point) {
        if (point.x < minLng) {
            minLng = point.x;
        }
        if (point.x > maxLng) {
            maxLng = point.x;
        }
        if (point.y < minLat) {
            minLat = point.y;
        }
        if (point.y > maxLat) {
            maxLat = point.y;
        }
    }

    public void extended(GeoExtend extend) {
        if (extend == null) {
            return;
        }
        if (this.minLat > extend.minLat) {
            this.minLat = extend.minLat;
        }
        if (this.maxLat < extend.maxLat) {
            this.maxLat = extend.maxLat;
        }
        if (this.minLng > extend.minLng) {
            this.minLng = extend.minLng;
        }
        if (this.maxLng < extend.maxLng) {
            this.maxLng = extend.maxLng;
        }
    }

    /**
     * 扩展经纬度范围
     * @param dlng
     * @param dlat
     */
    public void expand(double dlng,double dlat){
        minLng -= dlng;
        maxLng += dlng;
        minLat -= dlat;
        maxLat += dlat;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(minLng).append(" ").append(minLat).append(",").append(maxLng).append(" ").append(maxLat).append("]");
        return sb.toString();
    }

    public String toJSON() {
        StringBuilder sb = new StringBuilder(200);
        sb.append("{\"minLat\":").append(minLat).append(",");
        sb.append("\"minLng\":").append(minLng).append(",");
        sb.append("\"maxLng\":").append(maxLng).append(",");
        sb.append("\"maxLat\":").append(maxLat);
        sb.append("}");
        return sb.toString();
    }

    public GeoObject toPolygon() {
        GeoPolygon polygon = new GeoPolygon();
        Line line = new Line();
        line.add(minLng, minLat);
        line.add(minLng, maxLat);
        line.add(maxLng, maxLat);
        line.add(maxLng, minLat);
        line.add(minLng, minLat);
        polygon.getLines().add(line);
        return polygon;
    }

    public String toWKT(GeometryType type)
    {
        StringBuilder sb = new StringBuilder();
        if(type==GeometryType.GT_MULTI_POLYGON) {
            sb.append("MULTIPOLYGON (((");
        }
        else if(type==GeometryType.GT_POLYGON) {
            sb.append("POLYGON ((");
        }
        else {
            sb.append("POLYGON EMPTY");
        }
        sb.append(minLng);
        sb.append(" ");
        sb.append(minLat);
        sb.append(" , ");
        sb.append(maxLng);
        sb.append(" ");
        sb.append(minLat);
        sb.append(" , ");
        sb.append(maxLng);
        sb.append(" ");
        sb.append(maxLat);
        sb.append(" , ");
        sb.append(minLng);
        sb.append(" ");
        sb.append(maxLat);
        sb.append(" , ");
        sb.append(minLng);
        sb.append(" ");
        sb.append(minLat);
        if(type==GeometryType.GT_MULTI_POLYGON) {
            sb.append(")))");
        }
        else if(type==GeometryType.GT_POLYGON){
            sb.append("))");
        }
        return sb.toString();
    }

}
