package cn.mapway.geo.geometry;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

import static cn.mapway.geo.geometry.GeoJsonType.GEO_GEOMETRY_EMPTY;


/**
 * GeoObject
 *
 * @author zhangjianshe@gmail.com
 */
public class GeoObject implements Serializable, IsSerializable {
    public String type;
    protected Integer srid;

    public GeoObject() {
        setType(GEO_GEOMETRY_EMPTY);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GeoPoint asGeoPoint() {
        if (GeoJsonType.GEO_POINT.equals(type)) {
            return (GeoPoint) this;
        }
        return null;
    }

    public Integer getSrid() {
        return srid;
    }

    public void setSrid(int epsgId) {
        srid = epsgId;
    }

    public void setCrsWgs84() {
        setSrid(4326);
    }

    protected String getCrsString() {
        if (srid != null) {
            return ",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:" + srid + "\"}}";
        } else {
            return "";
        }
    }

    public GeoExtend getExtend() {
        return null;
    }

    public GeoPoint center() {
        return null;
    }

    public GeoPoints asGeoPoints() {
        if (GeoJsonType.GEO_MULTI_POINT.equals(type)) {
            return (GeoPoints) this;
        }
        return null;
    }

    public GeoLine asGeoLine() {
        if (GeoJsonType.GEO_LINE.equals(type)) {
            return (GeoLine) this;
        }
        return null;
    }

    public GeoLines asGeoLines() {
        if (GeoJsonType.GEO_MULTI_LINE.equals(type)) {
            return (GeoLines) this;
        }
        return null;
    }

    public GeoPolygon asGeoPolygon() {
        if (GeoJsonType.GEO_POLYGON.equals(type)) {
            return (GeoPolygon) this;
        }
        return null;
    }

    public GeoPolygons asGeoPolygons() {
        if (GeoJsonType.GEO_MULTI_POLYGON.equals(type)) {
            return (GeoPolygons) this;
        }
        return null;
    }

    public GeoFeature asGeoFeature() {
        if (GeoJsonType.GEO_FEATURE.equals(type)) {
            return (GeoFeature) this;
        }
        return null;
    }

    public GeoFeatures asGeoFeatures() {
        if (GeoJsonType.GEO_FEATURE_COLLECTION.equals(type)) {
            return (GeoFeatures) this;
        }
        return null;
    }

    public GeoCollection asGeoCollection() {
        if (GeoJsonType.GEO_GEOMETRY_COLLECTION.equals(type)) {
            return (GeoCollection) this;
        }
        return null;
    }

    public boolean isEmpty() {
        return GEO_GEOMETRY_EMPTY.equals(type);
    }

    public String toGeoJson() {
        String type = getType();
        if (GeoJsonType.GEO_MULTI_POINT.equals(type)) {
            return this.toGeoJson();
        } else if (GeoJsonType.GEO_LINE.equals(type)) {
            return this.toGeoJson();
        } else if (GeoJsonType.GEO_MULTI_LINE.equals(type)) {
            return this.toGeoJson();
        } else if (GeoJsonType.GEO_POLYGON.equals(type)) {
            return this.toGeoJson();
        } else if (GeoJsonType.GEO_MULTI_POLYGON.equals(type)) {
            return this.toGeoJson();
        } else if (GeoJsonType.GEO_FEATURE.equals(type)) {
            return this.toGeoJson();
        } else if (GeoJsonType.GEO_FEATURE_COLLECTION.equals(type)) {
            return this.toGeoJson();
        } else if (GeoJsonType.GEO_GEOMETRY_COLLECTION.equals(type)) {
            return this.toGeoJson();
        }
        return "";
    }

}
