package cn.mapway.globe.server.gabage;

import cn.mapway.globe.client.core.GlobelConstant;
import elemental2.core.Float32Array;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(namespace = GlobelConstant.NAMESPACE, name = "Vec2")
public class Vec2 {
    public static final Vec2 UNIT_X = Vec2.create(1, 0);
    public static final Vec2 UNIT_Y = Vec2.create(0, 1);
    public double x;
    public double y;

    public Vec2() {
        x = 0.0;
        y = 0.0;
    }

    public static Vec2 create(double x, double y) {
        return Vec2Pool.poll().set(x, y);
    }

    public static void packArray(Vec2[] vectors, Float32Array array, int offset) {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i].pack(array, offset);
            offset += 2;
        }
    }

    public static void unpackArray(Float32Array array, Vec2[] vectors, int offset) {
        for (int i = 0; i < vectors.length; i++) {
            vectors[i].unpack(array, offset);
            offset += 2;
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
    public static Vec2 lerp(Vec2 min, Vec2 max, double value) {
        double tx = Maths.lerp(min.x, max.x, value);
        double ty = Maths.lerp(min.y, max.y, value);
        return Vec2Pool.poll().set(tx, ty);
    }

    public static double angle(Vec2 left, Vec2 right) {
        Vec2 temp = Vec2Pool.poll().copyFrom(left).normalize();
        Vec2 temp2 = Vec2Pool.poll().copyFrom(right).normalize();
        double dot = temp.dot(temp2);
        //回收
        Vec2Pool.push(temp).push(temp2);
        return Maths.acosClamped(dot);
    }

    public Vec2 set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vec2 clone() {
        return Vec2Pool.poll().set(x, y);
    }

    public Vec2 copyFrom(Vec2 v) {
        return set(v.x, v.y);
    }

    public void copyTo(Vec2 v) {
        v.x = x;
        v.y = y;
    }

    public Vec2 pack(Float32Array array, int index) {
        array.setAt(index, x);
        array.setAt(index + 1, y);
        return this;
    }

    public Vec2 unpack(Float32Array array, int index) {
        x = array.getAt(index);
        y = array.getAt(index + 1);
        return this;
    }

    public double maxComponent() {
        return Math.max(x, y);
    }

    public double minComponent() {
        return Math.min(x, y);
    }

    /**
     * Compares two Vec2 and computes a Cartesian which contains the minimum components of the supplied Vec2.
     */
    public static Vec2 minByComponent(Vec2 v1, Vec2 v2) {
        double x = Math.min(v1.x, v2.x);
        double y = Math.min(v1.y, v2.y);
        return Vec2Pool.poll().set(x, y);
    }

    public static Vec2 maxByComponent(Vec2 v1, Vec2 v2) {
        double  x = Math.max(v1.x, v2.x);
        double y = Math.max(v1.y, v2.y);
        return Vec2Pool.poll().set(x,y);
    }

    public Vec2 clamp(Vec2 min, Vec2 max) {
        x = Maths.clamp(x, min.x, max.x);
        y = Maths.clamp(y, min.y, max.y);
        return this;
    }

    /**
     * Computes the provided Cartesian's squared magnitude.
     *
     * @return dx*dx+dy*dy
     */
    public double magnitudeSquared() {
        return x * x + y * y;
    }
    /**
     * Computes the Cartesian's magnitude (length).
     *
     * @return sqrt(dx * dx + dy * dy)
     */
    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }

    public double distanceTo(Vec2 other) {
        return Math.sqrt(distanceToSquared(other));
    }

    public double distanceToSquared(Vec2 other) {
        return (x-other.x)*(x-other.x) + (y-other.y)*(y-other.y);
    }

    public Vec2 normalize() {
        double m = magnitude();
        if (m == 0.0) {
            return this;
        }
        return set(x / m, y / m);
    }

    public double dot(Vec2 other) {
        return x * other.x + y * other.y;
    }

    /**
     * Computes the magnitude of the cross product that would result from implicitly setting the Z coordinate of the input vectors to 0
     *
     * @param other
     * @return
     */
    public double cross(Vec2 other) {
        return x * other.y - y * other.x;
    }

    /**
     * Computes the componentwise product of two Vec2.
     *
     * @param right
     * @return
     */
    public Vec2 multiplyComponents(Vec2 right) {
        return set(x * right.x, y * right.y);
    }

    /**
     * omputes the componentwise quotient of two Vec2.
     *
     * @param right
     * @return
     */
    public Vec2 divideComponents(Vec2 right) {
        assert right.x != 0.0 && right.y != 0.0;

        return set(x / right.x, y / right.y);
    }

    public Vec2 add(Vec2 other) {
        return set(x + other.x, y + other.y);
    }

    /**
     * Computes the componentwise difference of two Vec2.
     *
     * @param other
     * @return
     */
    public Vec2 subtract(Vec2 other) {
        return set(x - other.x, y - other.y);
    }

    public Vec2 scale(double scale) {
        return set(x * scale, y * scale);
    }

    public Vec2 negate() {
        return set(-x, -y);
    }

    public Vec2 abs() {
        return set(Math.abs(x), Math.abs(y));
    }

    /**
     * 计算角度
     *
     * @param right
     * @return
     */
    public double angleTo(Vec2 right) {
        return angle(this, right);
    }

    public Vec2 zero() {
        return set(0, 0);
    }

    public Vec2 identity() {
        return set(1, 1);
    }

    public String toString()
    {
        return "[" + x + "," + y + "]";
    }

    /**
     * Returns the axis that is most orthogonal to the provided Cartesian.
     * @return
     */
    public Vec2 mostOrthogonalAxis()
    {
        Vec2 temp = Vec2Pool.poll().copyFrom(this);
        temp.normalize().abs();

        boolean nearToX=temp.x <= temp.y;
        Vec2Pool.push(temp);
        if (nearToX) {
          return   UNIT_X.clone();
        } else {
          return   UNIT_Y.clone();
        }
    }

    public boolean equals(Vec2 other) {
        return Js.isTripleEqual(x, other.x) && Js.isTripleEqual(y, other.y);
    }

    public boolean equalsArray(Float32Array array, int offset) {
        return Js.isTripleEqual(x, array.getAt(offset)) && Js.isTripleEqual(y, array.getAt(offset + 1));
    }

    public boolean equalsEpsilon(Vec2 other, double relativeEpsilon,double  absoluteEpsilon) {
            return Maths.equalsEpsilon(x, other.x, relativeEpsilon, absoluteEpsilon)
                    && Maths.equalsEpsilon(y, other.y, relativeEpsilon, absoluteEpsilon);
    }

}
