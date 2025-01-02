package cn.mapway.globe.server.gabage;

import cn.mapway.globe.client.core.GlobelConstant;
import jsinterop.annotations.JsType;

import java.util.ArrayList;
import java.util.List;

@JsType(namespace = GlobelConstant.NAMESPACE, name = "Maths")
public class Maths {
    public static final double EPSILON1 = 0.1;
    public static final double EPSILON2 = 0.01;
    public static final double EPSILON3 = 0.001;
    public static final double EPSILON4 = 0.0001;
    public static final double EPSILON5 = 0.00001;
    public static final double EPSILON6 = 0.000001;
    public static final double EPSILON7 = 0.0000001;
    public static final double EPSILON8 = 0.00000001;
    public static final double EPSILON9 = 0.000000001;
    public static final double EPSILON10 = 0.0000000001;
    public static final double EPSILON11 = 0.00000000001;
    public static final double EPSILON12 = 0.000000000001;
    public static final double EPSILON13 = 0.0000000000001;
    public static final double EPSILON14 = 0.00000000000001;
    public static final double EPSILON15 = 0.000000000000001;
    public static final double EPSILON16 = 0.0000000000000001;
    public static final double EPSILON17 = 0.00000000000000001;
    public static final double EPSILON18 = 0.000000000000000001;
    public static final double EPSILON19 = 0.0000000000000000001;
    public static final double EPSILON20 = 0.00000000000000000001;
    public static final double EPSILON21 = 0.000000000000000000001;
    public static final double PI = Math.PI;
    public static final double TWO_PI = 2 * PI;
    public static final double PI_OVER_TWO = PI / 2;
    public static final double PI_OVER_FOUR = PI / 4;
    public static final double DEG_TO_RAD = PI / 180;
    public static final double RAD_TO_DEG = 180 / PI;

    /**
     * @param rad
     * @return
     */
    public static double toDegrees(double rad) {
        return rad * RAD_TO_DEG;
    }

    public static double toRadians(double deg) {
        return deg * DEG_TO_RAD;
    }

    /**
     * 经度正规化
     * normilize longtitude to [-PI, PI]
     *
     * @param lngRad
     * @return
     */
    public static double normalizeLng(double lngRad) {
        double simplified = lngRad - Math.floor(lngRad / TWO_PI) * TWO_PI;
        if (simplified < -Math.PI) {
            return simplified + TWO_PI;
        }
        if (simplified >= Math.PI) {
            return simplified - TWO_PI;
        }
        return simplified;
    }

    /**
     * 纬度正规化
     * normilize latitude to [-PI/2, PI/2]
     *
     * @param latRad
     * @return
     */
    public static double normalizeLat(double latRad) {
        double simplified = latRad - Math.floor(latRad / PI) * PI;
        if (simplified < -Math.PI / 2) {
            return simplified + PI;
        }
        if (simplified >= Math.PI / 2) {
            return simplified - PI;
        }
        return simplified;
    }

    /**
     * 线性插值
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * Computes the linear interpolation of two values.
     *
     * @param min
     * @param max
     * @param value
     * @return
     */
    public static double lerp(double min, double max, double value) {
        return min + (max - min) * value;
    }

    /**
     * Converts a scalar value in the range [rangeMinimum, rangeMaximum] to a scalar in the range [0.0, 1.0]
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double normalize(double value, double min, double max) {
        double maxRange = Math.max(max - min, 0.0);
        if (maxRange == 0.0) {
            return 0.0;
        }
        return clamp((value - min) / maxRange, 0.0, 1.0);
    }


    /**
     * The modulo operation that also works for negative dividends.
     *
     * @param m
     * @param n
     * @return
     */
    public static double mod(double m, double n) {
        assert n != 0;
        if (sign(m) == sign(n) && Math.abs(m) < Math.abs(n)) {
            return m;
        }
        return ((m % n) + n) % n;
    }


    /**
     * 符号
     *
     * @param x
     * @return
     */
    public static int sign(double x) {
        return x > 0 ? 1 : x < 0 ? -1 : 0;
    }


    public static int signNotZero(double x) {
        return x >= 0 ? 1 : -1;
    }


    /**
     * Converts a scalar value in the range [-1.0, 1.0] to a SNORM in the range [0, rangeMaximum]
     *
     * @param x            value The scalar value in the range [-1.0, 1.0]
     * @param rangeMaximum [rangeMaximum=255] The maximum value in the mapped range, 255 by default.
     * @return
     * @returns {number} A SNORM value, where 0 maps to -1.0 and rangeMaximum maps to 1.0.
     */
    public static double toSNorm(double x, double rangeMaximum) {
        if (rangeMaximum <= 0) {
            rangeMaximum = 255;
        }
        return (x + 1.0) / 2.0 * rangeMaximum;
    }


    /**
     * Converts a SNORM value in the range [0, rangeMaximum] to a scalar in the range [-1.0, 1.0].
     *
     * @param x
     * @param rangeMaximum
     * @return
     */
    public static double fromSNorm(double x, double rangeMaximum) {
        if (rangeMaximum <= 0) {
            rangeMaximum = 255;
        }
        return x / rangeMaximum * 2.0 - 1.0;
    }

    /**
     * Computes <code>Math.acos(value)</code>, but first clamps <code>value</code> to the range [-1.0, 1.0]
     *
     * @param value
     * @return
     */
    public static double acosClamped(double value) {
        return Math.acos(clamp(value, -1.0, 1.0));
    }
    /**
     * Computes <code>Math.asin(value)</code>, but first clamps <code>value</code> to the range [-1.0, 1.0]
     *
     * @param value
     * @return
     */
    public static double asinClamped(double value) {
        return Math.asin(clamp(value, -1.0, 1.0));
    }

    /**
     * 计算角度对应圆半径radius的弦长
     * @param angle
     * @param radius
     * @return
     */
    public static double chordLength(double angle,double radius)
    {
        return 2.0*radius*Math.sin(angle*0.5);
    }

    public static double cubeRoot(double value) {
        return Math.cbrt(value);
    }

    /**
     * Determines if two values are equal using an absolute or relative tolerance test. This is useful
     * to avoid problems due to roundoff error when comparing floating-point values directly. The values are
     * first compared using an absolute tolerance test. If that fails, a relative tolerance test is performed.
     * Use this test if you are unsure of the magnitudes of left and right.
     *
     * @param {number} left The first value to compare.
     * @param {number} right The other value to compare.
     * @param {number} [relativeEpsilon=0] The maximum inclusive delta between <code>left</code> and <code>right</code> for the relative tolerance test.
     * @param {number} [absoluteEpsilon=relativeEpsilon] The maximum inclusive delta between <code>left</code> and <code>right</code> for the absolute tolerance test.
     * @returns {boolean} <code>true</code> if the values are equal within the epsilon; otherwise, <code>false</code>.
     * @example const a = Maths.equalsEpsilon(0.0, 0.01, Math.EPSILON2); // true
     * const b = Math.equalsEpsilon(0.0, 0.1, Math.EPSILON2);  // false
     * const c = Math.equalsEpsilon(3699175.1634344, 3699175.2, Math.EPSILON7); // true
     * const d = Math.equalsEpsilon(3699175.1634344, 3699175.2, Math.EPSILON9); // false
     */
    public static boolean equalsEpsilon(double left, double right, double relativeEpsilon, double absoluteEpsilon) {
        relativeEpsilon = relativeEpsilon < 0 ? 0.0 : Math.abs(relativeEpsilon);
        absoluteEpsilon = absoluteEpsilon <= 0 ? relativeEpsilon : Math.abs(absoluteEpsilon);

        double absDiff = Math.abs(left - right);
        return (
                absDiff <= absoluteEpsilon ||
                        absDiff <= relativeEpsilon * Math.max(Math.abs(left), Math.abs(right))
        );
    }

    /**
     * Determines if the left value is less than the right value. If the two values are within
     * @param left
     * @param right
     * @param absoluteEpsilon
     * @return
     */
    public static boolean lessThan(double left, double right,double absoluteEpsilon) {
         return left - right < -absoluteEpsilon;
    }

    /**
     * Determines if the left value is less than or equal to the right value. If the two values are within
     * <code>absoluteEpsilon</code> of each other, they are considered equal and this function returns true.
     * @param left
     * @param right
     * @param absoluteEpsilon
     * @return
     */
    public static boolean lessThanOrEqual(double left, double right,double absoluteEpsilon) {
        return left - right < absoluteEpsilon;
    }

    /**
     * Determines if the left value is greater the right value. If the two values are within
     * <code>absoluteEpsilon</code> of each other, they are considered equal and this function returns false.
     * @param left
     * @param right
     * @param absoluteEpsilon
     * @return
     */
    public static boolean greatThan(double left, double right,double absoluteEpsilon) {
        return left - right > absoluteEpsilon;
    }

    /**
     * Determines if the left value is greater than or equal to the right value. If the two values are within
     * <code>absoluteEpsilon</code> of each other, they are considered equal and this function returns true.
     * @param left
     * @param right
     * @param absoluteEpsilon
     * @return
     */
    public static boolean greatThanOrEqual(double left, double right,double absoluteEpsilon) {
        return left - right > -absoluteEpsilon;
    }
    static List<Double> factorials = new ArrayList<>();
    static {
        factorials.add(1.0);
    }

    /**
     * Computes the factorial of the provided number.
     * @param n
     * @return
     */
    public static double factorial(int n)
    {
       int length = factorials.size();
        if (n >= length) {
            double sum = factorials.get(length - 1);
            for (int i = length; i <= n; i++) {
                double  next = sum * i;
                factorials.add(next);
                sum = next;
            }
        }
        return factorials.get(n);
    }

    /**
     * Increments a number with a wrapping to a minimum value if the number exceeds the maximum value.
     * @example
     * const n = Maths.incrementWrap(5, 10, 0); // returns 6
     * const m = Maths.incrementWrap(10, 10, 0); // returns 0
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double incrementWrap(double value, double min, double max) {
        ++value;
        if (value > max) {
            value = min;
        }
        return value;
    }

    /**
     * Determines if a non-negative integer is a power of two.
     * The maximum allowed input is (2^32)-1 due to 32-bit bitwise operator limitation in Javascript.
     * @param value The integer to test in the range [0, (2^32)-1].
     * @return
     */
    public static boolean isPowerOfTwo(long value) {
        assert value >= 0 && value < 4294967295L;
        return (value & (value - 1)) == 0;
    }

    /**
     * Computes the next power-of-two integer greater than or equal to the provided non-negative integer.
     * The maximum allowed input is 2^31 due to 32-bit bitwise operator limitation in Javascript.
     * @param value
     * @return
     */
    public static long nextPowerOfTwo(long value) {
        long v = value;
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }

    /**
     * Computes the previous power-of-two integer less than or equal to the provided non-negative integer.
     * The maximum allowed input is (2^32)-1 due to 32-bit bitwise operator limitation in Javascript.
     * @param value
     * @return
     */
    public static long previousPowerOfTwo(long value) {
        long n = value;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n |= n >> 32;

        // The previous bitwise operations implicitly convert to signed 32-bit. Use `>>>` to convert to unsigned
        n = (n >>> 0) - (n >>> 1);
        return n;
    }

    public static double random() {
        return Math.random();
    }
    public static double randomBetween(double min,double max){

        return Math.random()*(max-min)+min;
    }
}
