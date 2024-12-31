package cn.mapway.globe.server.gabage;

import cn.mapway.globe.client.core.GlobelConstant;
import elemental2.core.Float32Array;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(namespace = GlobelConstant.NAMESPACE, name = "Vec4")
public class Vec4 {
    public static final Vec4 UNIT_X = Vec4.create(1, 0, 0,0);
    public static final Vec4 UNIT_Y = Vec4.create(0, 1, 0,0);
    public static final Vec4 UNIT_Z = Vec4.create(0, 0, 1,0);
    public static final Vec4 UNIT_W = Vec4.create(0, 0, 0,1);
    public double x;
    public double y;
    public double z;
    public double w;

    public Vec4() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
        w=0.0;
    }

    public static Vec4 create(double x, double y, double z,double w) {
        return Vec4Pool.poll().set(x, y, z,w);
    }

    public static void packArray(Vec4[] vectors, Float32Array array, int offset) {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i].pack(array, offset);
            offset += 4;
        }
    }

    public static void unpackArray(Float32Array array, Vec4[] vectors, int offset) {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i].unpack(array, offset);
            offset += 4;
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
    public static Vec4 lerp(Vec4 min, Vec4 max, double value) {
        double tx = Maths.lerp(min.x, max.x, value);
        double ty = Maths.lerp(min.y, max.y, value);
        double tz = Maths.lerp(min.z, max.z, value);
        double tw = Maths.lerp(min.w, max.w, value);
        return Vec4Pool.poll().set(tx, ty, tz,tw);
    }


    public Vec4 set(double x, double y, double z,double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w=w;
        return this;
    }

    public Vec4 clone() {
        return Vec4Pool.poll().set(x, y, z,w);
    }

    public Vec4 copyFrom(Vec4 v) {
        return set(v.x, v.y, v.z,v.w);
    }

    public void copyTo(Vec4 v) {
        v.x = x;
        v.y = y;
        v.z = z;
        v.w=w;
    }

    public Vec4 pack(Float32Array array, int index) {
        array.setAt(index, x);
        array.setAt(index + 1, y);
        array.setAt(index + 2, z);
        array.setAt(index + 3, w);
        return this;
    }

    public Vec4 unpack(Float32Array array, int index) {
        x = array.getAt(index);
        y = array.getAt(index + 1);
        z = array.getAt(index + 2);
        w = array.getAt(index + 3);
        return this;
    }

    /**
     * Creates a Vec4 instance from a {@link Color}. <code>red</code>, <code>green</code>, <code>blue</code>,
     *  * and <code>alpha</code> map to <code>x</code>, <code>y</code>, <code>z</code>, and <code>w</code>, respectively.@param color
     *  *@return
     */
    public Vec4 fromColor(Color color)
    {
        return set(color.red,color.green,color.blue,color.alpha);
    }

    public double maxComponent() {
        return Math.max(Math.max(Math.max(x, y), z),w);
    }

    public double minComponent() {
        return Math.min(Math.min(Math.min(x, y), z),w);
    }

    /**
     * Compares two Vec2 and computes a Cartesian which contains the minimum components of the supplied Vec2.
     */
    public static Vec4 minByComponent(Vec4 v1, Vec4 v2) {
        double x = Math.min(v1.x, v2.x);
        double y = Math.min(v1.y, v2.y);
        double z = Math.min(v1.z, v2.z);
        double w = Math.min(v1.w, v2.w);
        return Vec4Pool.poll().set(x, y, z,w);
    }

    public static Vec4 maxByComponent(Vec4 v1, Vec4 v2) {
        double x = Math.max(v1.x, v2.x);
        double y = Math.max(v1.y, v2.y);
        double z = Math.max(v1.z, v2.z);
        double w = Math.max(v1.w, v2.w);
        return Vec4Pool.poll().set(x, y, z,w);
    }

    public  Vec4  clamp(Vec4 min, Vec4 max) {
         x = Maths.clamp(x, min.x, max.x);
         y = Maths.clamp(y, min.y, max.y);
         z = Maths.clamp(z, min.z, max.z);
         w = Maths.clamp(w, min.w, max.w);
        return this;
    }

    /**
     * Computes the provided Cartesian's squared magnitude.
     *
     * @return dx*dx+dy*dy +dz*dz +dw*dw
     */
    public double magnitudeSquared() {
        return   x*x+y*y+z*z+w*w;
    }

    /**
     * Computes the Cartesian's magnitude (length).
     *
     * @return sqrt(dx * dx + dy * dy + dz * dz + dw * dw)
     */
    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    public double distanceTo(Vec4 other) {
        return Math.sqrt(distanceToSquared(other));
    }

    public double distanceToSquared(Vec4 other) {
        return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z)+ (w - other.w) * (w - other.w);
    }

    public Vec4 normalize() {
        double m = magnitude();
        if (m == 0.0) {
            return this;
        }
        return set(x / m, y / m, z / m,w/m);
    }

    public double dot(Vec4 other) {
        return x * other.x + y * other.y + z * other.z+ w * other.w;
    }



    /**
     * Computes the componentwise product of two Vec2.
     *
     * @param right
     * @return
     */
    public Vec4 multiplyComponents(Vec4 right) {

        return set(x * right.x, y * right.y, z * right.z,w*right.w);
    }

    /**
     * omputes the componentwise quotient of two Vec2.
     *
     * @param right
     * @return
     */
    public Vec4 divideComponents(Vec4 right) {
        assert right.x != 0.0 && right.y != 0.0;

        return set(x / right.x, y / right.y, z / right.z,w/right.w);
    }

    public Vec4 add(Vec4 other) {
        return set(x + other.x, y + other.y, z + other.z,w+other.w);
    }

    /**
     * Computes the componentwise difference of two Vec2.
     *
     * @param other
     * @return
     */
    public Vec4 subtract(Vec4 other) {
        return set(x - other.x, y - other.y, z - other.z,w-other.w);
    }

    public Vec4 scale(double scale) {
        return set(x * scale, y * scale, z * scale,w*scale);
    }

    public Vec4 negate() {
        return set(-x, -y, -z,-w);
    }

    public Vec4 abs() {
        return set(Math.abs(x), Math.abs(y), Math.abs(z),Math.abs(w));
    }


    public Vec4 zero() {
        return set(0, 0, 0,0);
    }

    public Vec4 identity() {
        return set(1, 1, 1,1);
    }

    public String toString() {
        return "[" + x + "," + y + "," + z +"," + w+ "]";
    }

    /**
     * Returns the axis that is most orthogonal to the provided Cartesian.
     *
     * @return
     */
    public Vec4 mostOrthogonalAxis() {
        Vec4 f = Vec4Pool.poll().copyFrom(this);
        f.normalize().abs();
        Vec4 result= Vec4Pool.poll();

        if (f.x <= f.y) {
            if (f.x <= f.z) {
                if (f.x <= f.w) {
                    result.copyFrom(UNIT_X);
                } else {
                    result.copyFrom(UNIT_W);
                }
            } else if (f.z <= f.w) {
                result.copyFrom(UNIT_Z);
            } else {
                result.copyFrom(UNIT_W);
            }
        } else if (f.y <= f.z) {
            if (f.y <= f.w) {
                result.copyFrom(UNIT_Y);
            } else {
                result.copyFrom(UNIT_W);
            }
        } else if (f.z <= f.w) {
            result.copyFrom(UNIT_Z);
        } else {
            result.copyFrom(UNIT_W);
        }

        Vec4Pool.push(f);
        return result;
    }

    public boolean equals(Vec4 other) {
        return Js.isTripleEqual(x, other.x)
                && Js.isTripleEqual(y, other.y)
                && Js.isTripleEqual(z, other.z)
                && Js.isTripleEqual(w, other.w)
                ;
    }

    public boolean equalsArray(Float32Array array, int offset) {
        return Js.isTripleEqual(x, array.getAt(offset))
                && Js.isTripleEqual(y, array.getAt(offset + 1))
                && Js.isTripleEqual(z, array.getAt(offset + 2))
                && Js.isTripleEqual(w, array.getAt(offset + 3))

                ;
    }

    public boolean equalsEpsilon(Vec4 other, double relativeEpsilon, double absoluteEpsilon) {
        return Maths.equalsEpsilon(x, other.x, relativeEpsilon, absoluteEpsilon)
                && Maths.equalsEpsilon(y, other.y, relativeEpsilon, absoluteEpsilon)
                && Maths.equalsEpsilon(z, other.z, relativeEpsilon, absoluteEpsilon)
                && Maths.equalsEpsilon(w, other.w, relativeEpsilon, absoluteEpsilon);
    }


}
