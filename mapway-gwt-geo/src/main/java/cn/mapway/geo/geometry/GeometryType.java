package cn.mapway.geo.geometry;

import cn.mapway.ui.client.fonts.Fonts;

/**
 * * enum wkbGeometryType {
 * *     wkbPoint = 1,
 * *     wkbLineString = 2,
 * *     wkbPolygon = 3,
 * *     wkbMultiPoint = 4,
 * *     wkbMultiLineString = 5,
 * *     wkbMultiPolygon = 6,
 * *     wkbGeometryCollection = 7
 * * };
 * 参见 https://libgeos.org/specifications/wkb/
 */
public enum GeometryType {
   //
    GT_GEOMETRY(0, "混合类型", Fonts.GEO_COLLECTION, "Geometry"),
    GT_POINT(1, "点", Fonts.POINT, "Point"),
    GT_LINE(2, "线", Fonts.LINE, "Linestring"),
    GT_POLYGON(3, "多边形", Fonts.POLYGON, "Polygon"),
    GT_MULTI_POINT(4, "点集合", Fonts.POINTS, "MultiPoint"),
    GT_MULTI_LINE(5, "线集合", Fonts.LINES, "MultiLineString"),
    GT_MULTI_POLYGON(6, "多边形集合", Fonts.POLYGONS, "MultiPolygon"),
    GT_GEOMETRY_COLLECTION(7, "集合", Fonts.TABLE_STYLE , "GeometryCollection");

    int code;
    String name;
    String unicode;
    String postGisType;

    public String getName() {
        return name;
    }

    public String getUnicode() {
        return unicode;
    }

    public String getPostGisType() {
        return postGisType;
    }


    public static GeometryType valueOfCode(Integer code) {
        if (code == null) {
            return GT_GEOMETRY;
        }
        for (GeometryType gt : GeometryType.values()) {
            if (gt.code == code) {
                return gt;
            }
        }
        return GT_GEOMETRY;
    }

    public int getCode() {
        return code;
    }

    GeometryType(int code, String name, String unicode, String postGisType) {
        this.code = code;
        this.name = name;
        this.unicode = unicode;
        this.postGisType = postGisType;
    }

    public String toString()
    {
        return name+"("+code+")";
    }
}
