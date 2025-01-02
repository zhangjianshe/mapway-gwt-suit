package cn.mapway.globe.server.gabage;

import cn.mapway.globe.client.core.GlobelConstant;
import elemental2.core.Float32Array;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(namespace = GlobelConstant.NAMESPACE, name = "Vec3")
public class Vec3 {
    public static final Vec3 UNIT_X = Vec3.create(1, 0, 0);
    public static final Vec3 UNIT_Y = Vec3.create(0, 1, 0);
    public static final Vec3 UNIT_Z = Vec3.create(0, 0, 1);
    public double x;
    public double y;
    public double z;

    public Vec3() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
    }

    public static Vec3 create(double x, double y, double z) {
        return new Vec3().set(x, y, z);
    }

    public static void packArray(Vec3[] vectors, Float32Array array, int offset) {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i].pack(array, offset);
            offset += 3;
        }
    }

    public static void unpackArray(Float32Array array, Vec3[] vectors, int offset) {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i].unpack(array, offset);
            offset += 3;
        }
    }

    /**
     * Computes the linear interpolation or extrapolation at t using the provided cartesians.
     *
     * @param min
     * @param max
     * @param value
     * @return
     */
    public static Vec3 lerp(Vec3 min, Vec3 max, double value) {
        double tx = Maths.lerp(min.x, max.x, value);
        double ty = Maths.lerp(min.y, max.y, value);
        double tz = Maths.lerp(min.z, max.z, value);
        return new Vec3().set(tx, ty, tz);
    }

    /**
     * 计算向量之间的夹角
     *
     * @param left
     * @param right
     * @return
     */
    public static double angle(Vec3 left, Vec3 right) {
        Vec3 temp = Vec3Pool.poll().copyFrom(left).normalize();
        Vec3 temp2 = Vec3Pool.poll().copyFrom(right).normalize();


        double cosine = temp.dot(temp2);
        //回收
        double sine = temp.cross(temp2).magnitude();
        Vec3Pool.push(temp).push(temp2);
        return Math.atan2(sine, cosine);
    }

    public Vec3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vec3 clone() {
        return Vec3Pool.poll().set(x, y, z);
    }

    public Vec3 copyFrom(Vec3 v) {
        return set(v.x, v.y, v.z);
    }

    public void copyTo(Vec3 v) {
        v.x = x;
        v.y = y;
        v.z = z;
    }

    public Vec3 pack(Float32Array array, int index) {
        array.setAt(index, x);
        array.setAt(index + 1, y);
        array.setAt(index + 2, z);
        return this;
    }

    public Vec3 unpack(Float32Array array, int index) {
        x = array.getAt(index);
        y = array.getAt(index + 1);
        z = array.getAt(index + 2);
        return this;
    }

    public double maxComponent() {
        return Math.max(Math.max(x, y), z);
    }

    public double minComponent() {
        return Math.min(Math.min(x, y), z);
    }

    /**
     * Compares two Vec2 and computes a Cartesian which contains the minimum components of the supplied Vec2.
     */
    public static Vec3 minByComponent(Vec3 v1, Vec3 v2) {
        double x = Math.min(v1.x, v2.x);
        double y = Math.min(v1.y, v2.y);
        double z = Math.min(v1.z, v2.z);
        return new Vec3().set(x, y, z);
    }

    public static Vec3 maxByComponent(Vec3 v1, Vec3 v2) {
        double x = Math.max(v1.x, v2.x);
        double y = Math.max(v1.y, v2.y);
        double z = Math.max(v1.z, v2.z);
        return new Vec3().set(x, y, z);
    }

    public Vec3 clamp(Vec3 min, Vec3 max) {
        x = Maths.clamp(x, min.x, max.x);
        y = Maths.clamp(y, min.y, max.y);
        z = Maths.clamp(z, min.z, max.z);
        return this;
    }

    /**
     * Computes the provided Cartesian's squared magnitude.
     *
     * @return dx*dx+dy*dy +dz*dz
     */
    public double magnitudeSquared() {

        return x*x+y*y+z*z;
    }

    /**
     * Computes the Cartesian's magnitude (length).
     *
     * @return sqrt(dx * dx + dy * dy + dz * dz)
     */
    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    public double distanceTo(Vec3 other) {

        return Math.sqrt(distanceToSquared(other));
    }

    public double distanceToSquared(Vec3 other) {
        return (x-other.x)*(x-other.x) + (y-other.y)*(y-other.y) + (z-other.z)*(z-other.z);
    }

    public Vec3 normalize() {
        double m = magnitude();
        if (m == 0.0) {
            return this;
        }
        return set(x / m, y / m, z / m);
    }

    public double dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Computes the magnitude of the cross product that would result from implicitly setting the Z coordinate of the input vectors to 0
     *
     * @param right
     * @return
     */
    public Vec3 cross(Vec3 right) {
        double leftX = this.x;
        double leftY = this.y;
        double leftZ = this.z;
        double rightX = right.x;
        double rightY = right.y;
        double rightZ = right.z;

        double x = leftY * rightZ - leftZ * rightY;
        double y = leftZ * rightX - leftX * rightZ;
        double z = leftX * rightY - leftY * rightX;
        return create(x, y, z);
    }

    /**
     * Computes the componentwise product of two Vec2.
     *
     * @param right
     * @return
     */
    public Vec3 multiplyComponents(Vec3 right) {
        return set(x * right.x, y * right.y, z * right.z);
    }

    /**
     * omputes the componentwise quotient of two Vec2.
     *
     * @param right
     * @return
     */
    public Vec3 divideComponents(Vec3 right) {
        assert right.x != 0.0 && right.y != 0.0;

        return set(x / right.x, y / right.y, z / right.z);
    }

    public Vec3 add(Vec3 other) {
        return set(x + other.x, y + other.y, z + other.z);
    }

    /**
     * Computes the componentwise difference of two Vec2.
     *
     * @param other
     * @return
     */
    public Vec3 subtract(Vec3 other) {
        return set(x - other.x, y - other.y, z - other.z);
    }

    public Vec3 scale(double scale) {
        return set(x * scale, y * scale, z * scale);
    }

    public Vec3 negate() {
        return set(-x, -y, -z);
    }

    public Vec3 abs() {
        return set(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    /**
     * 计算角度
     *
     * @param right
     * @return
     */
    public double angleTo(Vec3 right) {
        return angle(this, right);
    }

    public Vec3 zero() {
        return set(0, 0, 0);
    }

    public Vec3 identity() {
        return set(1, 1, 1);
    }

    public String toString() {
        return "[" + x + "," + y + "," + z + "]";
    }

    /**
     * Returns the axis that is most orthogonal to the provided Cartesian.
     *
     * @return
     */
    public Vec3 mostOrthogonalAxis() {
        Vec3 f = Vec3Pool.poll().copyFrom(this);
        f.normalize().abs();

        Vec3 result= Vec3Pool.poll();
        if (f.x <= f.y) {
            if (f.x <= f.z) {
                result.copyFrom(UNIT_X);
            } else {
                result.copyFrom(UNIT_Z);
            }
        } else if (f.y <= f.z) {

            result.copyFrom( UNIT_Y);
        } else {
            result.copyFrom( UNIT_Z);
        }
        Vec3Pool.push(f);
        return result;
    }

    public boolean equals(Vec3 other) {
        return Js.isTripleEqual(x, other.x)
                && Js.isTripleEqual(y, other.y)
                && Js.isTripleEqual(z, other.z)
                ;
    }

    public boolean equalsArray(Float32Array array, int offset) {
        return Js.isTripleEqual(x, array.getAt(offset))
                && Js.isTripleEqual(y, array.getAt(offset + 1))
                && Js.isTripleEqual(z, array.getAt(offset + 2));
    }

    public boolean equalsEpsilon(Vec3 other, double relativeEpsilon, double absoluteEpsilon) {
        return Maths.equalsEpsilon(x, other.x, relativeEpsilon, absoluteEpsilon)
                && Maths.equalsEpsilon(y, other.y, relativeEpsilon, absoluteEpsilon)
                && Maths.equalsEpsilon(z, other.z, relativeEpsilon, absoluteEpsilon);
    }

    public static Vec3 projectTo(Vec3 left,Vec3 right) {
        double scalar = left.dot(right) / right.dot(right);
        return right.clone().scale(scalar);
    }

    public static Vec3 midPoint(Vec3 left, Vec3 right) {
        return left.clone().add(right).scale(0.5);
    }

    /**
     * Returns a Vec3 position from longitude and latitude values given in degrees.
     *
     * @param {number} longitude The longitude, in degrees
     * @param {number} latitude The latitude, in degrees
     * @param {number} [height=0.0] The height, in meters, above the ellipsoid.
     * @param {Ellipsoid} [ellipsoid=Ellipsoid.default] The ellipsoid on which the position lies.
     * @param {Vec3} [result] The object onto which to store the result.
     * @returns {Vec3} The position
     *
     * @example
     * const position = Cesium.Cartesian3.fromDegrees(-115.0, 37.0);
     */
    public static Vec3 fromDegree(double lng, double lat, double height, Ellipsoid ellipsoid)
    {
        return fromRadians(Math.toRadians(lng),Math.toRadians(lat),height,ellipsoid);
    }
    /**
     * Returns a Vec3 position from longitude and latitude values given in radians.
     *
     * @param {number} longitude The longitude, in radians
     * @param {number} latitude The latitude, in radians
     * @param {number} [height=0.0] The height, in meters, above the ellipsoid.
     * @param {Ellipsoid} [ellipsoid=Ellipsoid.default] The ellipsoid on which the position lies.
     * @returns {Vec3} The position
     *
     * @example
     * const position = Cesium.Cartesian3.fromRadians(-2.007, 0.645);
     */
    public static Vec3 fromRadians(double longitude, double latitude, double height, Ellipsoid ellipsoid)
    {
       if(height<=0. ){
           height=0;
       }

       Vec3 radiiSquared = ellipsoid.radiiSquared.clone();

       Vec3 temp= Vec3Pool.poll();
       Vec3 temp2= Vec3Pool.poll();

        double cosLatitude = Math.cos(latitude);
        temp.x = cosLatitude * Math.cos(longitude);
        temp.y = cosLatitude * Math.sin(longitude);
        temp.z = Math.sin(latitude);
        temp.normalize();

        temp2.copyFrom(radiiSquared);
        temp2.multiplyComponents(temp);

        double gamma = Math.sqrt(temp.dot(temp2));
        temp.scale(height);
        temp2.scale( 1/gamma);
        Vec3 clone = temp.add(temp2).clone();

        Vec3Pool.getTempPool().push(temp).push(temp2);
        return clone;
    }

    public static Vec3[] fromDegreesArray(Float32Array array, double height, Ellipsoid ellipsoid)
    {
        int count = array.getLength()/2;

        Vec3[] result = new Vec3[count];
        for(int i=0;i<count;i++){
            result[i]=fromDegree(array.getAt(i*2),array.getAt(i*2+1),height,ellipsoid);
        }
        return result;
    }

    public static Vec3[] fromRadiansArray(Float32Array array, double height, Ellipsoid ellipsoid)
    {
        int count = array.getLength()/2;

        Vec3[] result = new Vec3[count];
        for(int i=0;i<count;i++){
            result[i]=fromRadians(array.getAt(i*2),array.getAt(i*2+1),height,ellipsoid);
        }
        return result;
    }

    public static Vec3[] fromDegreesArrayHeights(double[] array, Ellipsoid ellipsoid)
    {
        int count = array.length/3;
        Vec3[] result = new Vec3[count];
        for(int i=0;i<count;i++){
            result[i]=fromDegree(array[i*3],array[i*3+1],array[i*3+2],ellipsoid);
        }
        return result;
    }

    public static Vec3[] fromRadiansArrayHeights(double[] array, Ellipsoid ellipsoid)
    {
        int count = array.length/3;
        Vec3[] result = new Vec3[count];
        for(int i=0;i<count;i++){
            result[i]=fromRadians(array[i*3],array[i*3+1],array[i*3+2],ellipsoid);
        }
        return result;
    }

    public void discard()
    {
        Vec3Pool.push(this);
    }
}
