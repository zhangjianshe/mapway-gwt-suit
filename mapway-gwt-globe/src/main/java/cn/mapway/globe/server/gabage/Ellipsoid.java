package cn.mapway.globe.server.gabage;

import elemental2.core.Float32Array;

/**
 * 椭球定义
 */
public class Ellipsoid {
    Vec3 radii;
    Vec3 radiiSquared;
    Vec3 radiiToTheFourth;
    Vec3 oneOverRadii;
    Vec3 oneOverRadiiSquared;
    double minimumRadius;
    double maximumRadius;
    double centerToleranceSquared;
    double squaredXOverSquaredZ;
    /**
     * 根据直角坐标系中 XYZ三方向的半径长度　定义椭球
     *  (x / a)^2 + (y / b)^2 + (z / c)^2 = 1
     * @param x
     * @param y
     * @param z
     */
    public Ellipsoid(double x,double y,double z) {
        createFrom(x, y, z);
    }

    public void createFrom(double x,double y,double z)
    {
        radii = Vec3.create(x, y, z);
        radiiSquared = Vec3.create(x * x, y * y, z * z);
        radiiToTheFourth =  Vec3.create(
                x * x * x * x,
                y * y * y * y,
                z * z * z * z
        );

        oneOverRadii = Vec3.create(
                x == 0.0 ? 0.0 : 1.0 / x,
                y == 0.0 ? 0.0 : 1.0 / y,
                z == 0.0 ? 0.0 : 1.0 / z
        );

        oneOverRadiiSquared = Vec3.create(
                x == 0.0 ? 0.0 : 1.0 / (x * x),
                y == 0.0 ? 0.0 : 1.0 / (y * y),
                z == 0.0 ? 0.0 : 1.0 / (z * z)
        );

        minimumRadius = radii.minComponent();
        maximumRadius = radii.maxComponent();

        centerToleranceSquared = Maths.EPSILON1;
        if (radiiSquared.z != 0) {
            squaredXOverSquaredZ =
                    radiiSquared.x / radiiSquared.z;
        }
    }

    public  Ellipsoid clone()
    {
        return new Ellipsoid(radii.x, radii.y, radii.z);
    }

    public static Ellipsoid create(Vec3 param)
    {
        return new Ellipsoid(param.x, param.y, param.z);
    }

    public void pack(Float32Array array, int index) {
       radii.pack(array, index);
    }
    public void unpack(Float32Array array, int index) {
        radii.unpack(array, index);
        createFrom(radii.x, radii.y, radii.z);
    }

    /**
     * WGS84椭球体
     */
    public static final Ellipsoid WGS84_ELLIPSOID   = new Ellipsoid(6378137.0, 6378137.0, 6356752.3142451793);
    /**
     * 单位球
     */
    public static final Ellipsoid UNIT_ELLIPSOID   = new Ellipsoid(1,1,1);

    /**
     * 月球椭球体
     */
    public static final Ellipsoid MOON_ELLIPSOID   = new Ellipsoid(11737400.0, 1737400.0, 1737400.0);
}
