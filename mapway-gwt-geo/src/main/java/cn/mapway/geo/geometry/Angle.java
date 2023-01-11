package cn.mapway.geo.geometry;

/**
 * Angle
 *
 * @author zhang
 */
public class Angle {
    public double degree;
    public double minute;
    public double second;

    public Angle(double degree, double minute, double second) {
        this.degree = degree;
        this.minute = minute;
        this.second = second;
    }

    /**
     * 10进制的角度 0-360 转为 度分秒
     *
     * @param deg
     * @param secondPrecision 秒的经度小数点位
     * @return
     */
    public static Angle createFromDecimal(double deg, int secondPrecision) {
        double[] v = split(deg, secondPrecision);
        return new Angle(v[0], v[1], v[2]);
    }

    public static double geoWrapLng(double lng) {
        while (lng > 180) {
            lng -= 360;
        }
        while (lng < -180) {
            lng += 360;
        }
        return lng;
    }

    public static double geoWrapLat(double lat) {
        while (lat > 90) {
            lat -= 180;
        }
        while (lat < -90) {
            lat += 180;
        }
        return lat;
    }

    public static double concat(double d, double m, double s) {
        if (d > 0) {
            return d + m / 60 + s / 3600;
        } else {
            return -(Math.abs(d) + m / 60 + s / 3600);
        }
    }

    public static double concat(double[] v) {
        if (v.length == 3) {
            return concat(v[0], v[1], v[2]);
        } else if (v.length == 2) {
            return concat(v[0], v[1], 0);
        } else if (v.length == 1) {
            return concat(v[0], 0, 0);
        } else {
            return .0;
        }
    }

    public static double[] split(double deg, int secondPrecision) {

        double d = (int) Math.floor(deg);          // make degrees
        double m = (int) Math.floor((deg - d) * 60);    // make minutes
        double s = Math.round(((deg - d) * 60 - m) * 60 * Math.pow(10, secondPrecision)) / Math.pow(10, secondPrecision); // Make sec rounded
        // if seconds rounds to 60 then increment minutes, reset seconds
        if (s == 60) {
            m++;
            s = 0;
        }
        if (m == 60) {
            d++;
            m = 0;
        }
        return new double[]{d, m, s};
    }

    public double toDecimal() {
        return concat(degree, minute, second);
    }

    public String toString() {
        return degree + "°" + minute + "'" + second + "\"";
    }
}
