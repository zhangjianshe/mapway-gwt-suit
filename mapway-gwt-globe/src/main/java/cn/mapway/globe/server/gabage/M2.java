package cn.mapway.globe.server.gabage;

import cn.mapway.globe.client.core.GlobelConstant;
import elemental2.core.Float32Array;
import elemental2.core.JsArray;
import jsinterop.annotations.JsType;

/**
 * 2X2维矩阵
 * 构造函数是　行优先
 * 存储列优先
 */
@JsType(namespace = GlobelConstant.NAMESPACE, name = "M2")
public class M2 extends JsArray<Double> {
    public static final M2 IDENTITY = new M2(1, 0, 0, 1);
    public static final M2 ZERO = new M2(0, 0, 0, 0);

    public M2(double m00, double m01, double m10, double m11) {
        setAt(0, m00);
        setAt(1, m10);
        setAt(2, m01);
        setAt(3, m11);
    }

    public static Float32Array packArray(M2[] matrices, Float32Array array, int offset) {
        assert (array.length - offset) >= matrices.length * 4;
        for (int i = 0; i < matrices.length; i++) {
            matrices[i].pack(array, offset);
            offset += 4;
        }
        return array;
    }

    public static M2[] unpackArray(Float32Array array, M2[] matrices, int offset) {
        for (int i = 0; i < matrices.length; i++) {
            matrices[i] = new M2(1, 0, 0, 1);
            matrices[i].unpack(array, offset);
            offset += 4;
        }
        return matrices;
    }

    public M2 pack(Float32Array array, int offset) {
        array.setAt(offset, getAt(0));
        array.setAt(offset + 1, getAt(1));
        array.setAt(offset + 2, getAt(2));
        array.setAt(offset + 3, getAt(3));
        return this;
    }

    public M2 unpack(Float32Array array, int offset) {
        setAt(0, array.getAt(offset));
        setAt(1, array.getAt(offset + 1));
        setAt(2, array.getAt(offset + 2));
        setAt(3, array.getAt(offset + 3));
        return this;
    }

    public M2 set(double m00, double m01, double m10, double m11) {
        setAt(0, m00);
        setAt(1, m10);
        setAt(2, m01);
        setAt(3, m11);
        return this;
    }

    public M2 clone() {
        return M2Pool.poll().set(getAt(0), getAt(2), getAt(1), getAt(3));
    }

    public String toString() {
        return "(" + getAt(0) + ", " + getAt(1) + ",\n " + getAt(2) + ", " + getAt(3) + ")";
    }

    /**
     * Creates a Matrix2 instance from a column-major order array
     *
     * @param array
     * @return
     */
    public M2 fromArray(double[] array) {
        setAt(0, array[0]);
        setAt(1, array[1]);
        setAt(2, array[2]);
        setAt(3, array[3]);
        return this;
    }

    /**
     * Creates a Matrix2 instance from a row-major order array.
     * The resulting matrix will be in column-major order.
     *
     * @param array
     * @return
     */
    public M2 fromRowMajorArray(double[] array) {
        setAt(0, array[0]);
        setAt(1, array[2]);
        setAt(2, array[1]);
        setAt(3, array[3]);
        return this;
    }

    /**
     * Computes a M2 instance representing a non-uniform scale.
     *
     * @param scale
     * @return // Creates
     * //   [7.0, 0.0]
     * //   [0.0, 8.0]
     * M2 m = M2.fromScale(Vec2.create(7.0, 8.0));
     */
    public M2 fromScale(Vec2 scale) {
        setAt(0, scale.x);
        setAt(1, 0.);
        setAt(2, 0.);
        setAt(3, scale.y);
        return this;
    }

    /**
     * Computes a M2 instance representing a non-uniform scale.
     *
     * @param scale
     * @return // Creates
     * //   [2.0, 0.0]
     * //   [0.0, 2.0]
     * M2 m = M2.fromScale(2.0);
     */
    public M2 fromUniformScale(double scale) {
        setAt(0, scale);
        setAt(1, 0.);
        setAt(2, 0.);
        setAt(3, scale);
        return this;
    }

    /**
     * Creates a rotation matrix.
     *
     * @param angle The angle, in radians, of the rotation.  Positive angles are counterclockwise.
     * @return
     */
    public M2 formRotation(double angle) {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        setAt(0, cosAngle);
        setAt(1, sinAngle);
        setAt(2, -sinAngle);
        setAt(3, cosAngle);
        return this;
    }

    /**
     * Creates an Array from the provided Matrix2 instance.
     * The array will be in column-major order.
     *
     * @return
     */
    public double[] toArray() {
        return new double[]{getAt(0), getAt(1), getAt(2), getAt(3)};
    }

    /**
     * Computes the array index of the element at the provided row and column.
     *
     * @param column
     * @param row
     * @return
     */
    public int getElementIndex(int column, int row) {
        assert column >= 0 && column <= 1;
        assert row >= 0 && row <= 1;
        return column * 2 + row;
    }

    /**
     * Retrieves a copy of the matrix column at the provided index as a Vec2 instance.
     *
     * @param column
     * @return
     */
    public Vec2 getColumn(int column) {
        assert column >= 0 && column <= 1;
        int index = column * 2;
        return Vec2Pool.poll().set(getAt(index), getAt(index + 1));
    }

    /**
     * Computes a new matrix that replaces the specified column in the provided matrix with the provided Vec2 instance.
     *
     * @param column
     * @param vec
     * @return
     */
    public M2 setColumn(int column, Vec2 vec) {
        assert column >= 0 && column <= 1;
        int index = column * 2;
        setAt(index, vec.x);
        setAt(index + 1, vec.y);
        return this;
    }

    /**
     * Retrieves a copy of the matrix row at the provided index as a Vec2 instance.
     *
     * @param row
     * @return
     */
    public Vec2 getRow(int row) {
        assert row >= 0 && row <= 1;
        return Vec2Pool.poll().set(getAt(row), getAt(row + 2));
    }

    /**
     * Computes a new matrix that replaces the specified row in the provided matrix with the provided Vec2 instance.
     *
     * @param row
     * @param vec
     * @return
     */
    public M2 setRow(int row, Vec2 vec) {
        assert row >= 0 && row <= 1;
        setAt(row, vec.x);
        setAt(row + 2, vec.y);
        return this;
    }

    /**
     * Extracts the non-uniform scale assuming the matrix is an affine transformation.
     *
     * @return
     */
    public Vec2 getScale() {
        Vec2 result = Vec2Pool.poll();
        result.x = getColumn(0).magnitude();
        result.y = getColumn(1).magnitude();
        return result;
    }

    /**
     * Computes a new matrix that replaces the scale with the provided scale.
     * This assumes the matrix is an affine transformation.
     * //TODO i don not know what it does for
     *
     * @param scale
     * @return
     */
    public M2 setScale(Vec2 scale) {
        Vec2 existingScale = getScale();
        double scaleRatioX = scale.x / existingScale.x;
        double scaleRatioY = scale.y / existingScale.y;
        setAt(0, getAt(0) * scaleRatioX);
        setAt(1, getAt(1) * scaleRatioX);
        setAt(2, getAt(2) * scaleRatioY);
        setAt(3, getAt(3) * scaleRatioY);
        Vec2Pool.push(existingScale);
        return this;
    }

    /**
     * Computes a new matrix that replaces the scale with the provided uniform scale.
     * This assumes the matrix is an affine transformation.
     *
     * @param scale
     * @return
     */
    public M2 setUniformScale(double scale) {
        Vec2 existingScale = getScale();
        double scaleRatioX = scale / existingScale.x;
        double scaleRatioY = scale / existingScale.y;
        setAt(0, getAt(0) * scaleRatioX);
        setAt(1, getAt(1) * scaleRatioX);
        setAt(2, getAt(2) * scaleRatioY);
        setAt(3, getAt(3) * scaleRatioY);
        Vec2Pool.push(existingScale);
        return this;
    }

    /**
     * Computes the maximum scale assuming the matrix is an affine transformation.
     * The maximum scale is the maximum length of the column vectors.
     *
     * @return
     */
    public double getMaximumScale() {
        Vec2 scale = getScale();
        double v = Math.max(scale.x, scale.y);
        Vec2Pool.push(scale);
        return v;
    }

    /**
     * Extracts the rotation matrix assuming the matrix is an affine transformation.
     *
     * @return
     */
    public M2 getRotation() {
        Vec2 scale = getScale();

        M2 result = M2Pool.poll();
        result.setAt(0, getAt(0) / scale.x);
        result.setAt(1, getAt(1) / scale.x);
        result.setAt(2, getAt(2) / scale.y);
        result.setAt(3, getAt(3) / scale.y);
        Vec2Pool.push(scale);
        return result;
    }

    /**
     * Sets the rotation assuming the matrix is an affine transformation.
     *
     * @param rotationMatrix
     * @return
     */
    public M2 setRotation(M2 rotationMatrix) {
        Vec2 scale = getScale();

        setAt(0, rotationMatrix.getAt(0) * scale.x);
        setAt(1, rotationMatrix.getAt(1) * scale.x);
        setAt(2, rotationMatrix.getAt(2) * scale.y);
        setAt(3, rotationMatrix.getAt(3) * scale.y);

        Vec2Pool.push(scale);
        return this;
    }

    /**
     * Computes the product of two matrices.
     *
     * @param other
     * @return
     */
    public M2 multiply(M2 other) {
        double column0Row0 = getAt(0) * other.getAt(0) + getAt(2) * other.getAt(1);
        double column1Row0 = getAt(0) * other.getAt(2) + getAt(2) * other.getAt(3);
        double column0Row1 = getAt(1) * other.getAt(0) + getAt(3) * other.getAt(1);
        double column1Row1 = getAt(1) * other.getAt(2) + getAt(3) * other.getAt(3);

        setAt(0, column0Row0);
        setAt(1, column0Row1);
        setAt(2, column1Row0);
        setAt(3, column1Row1);
        return this;
    }

    /**
     * Computes the sum of two matrices.
     *
     * @param other
     * @return
     */
    public M2 add(M2 other) {
        setAt(0, getAt(0) + other.getAt(0));
        setAt(1, getAt(1) + other.getAt(1));
        setAt(2, getAt(2) + other.getAt(2));
        setAt(3, getAt(3) + other.getAt(3));
        return this;
    }

    /**
     * Computes the difference of two matrices.
     *
     * @param other
     * @return
     */
    public M2 subtract(M2 other) {
        setAt(0, getAt(0) - other.getAt(0));
        setAt(1, getAt(1) - other.getAt(1));
        setAt(2, getAt(2) - other.getAt(2));
        setAt(3, getAt(3) - other.getAt(3));
        return this;
    }

    /**
     * Computes the product of a matrix and a column vector.
     *
     * @param vec
     * @return
     */
    public Vec2 multiplyByVec2(Vec2 vec) {
        Vec2 result = Vec2Pool.poll();
        double x = getAt(0) * vec.x + getAt(2) * vec.y;
        double y = getAt(1) * vec.x + getAt(3) * vec.y;
        result.set(x, y);
        return result;
    }

    /**
     * Computes the product of a matrix and a scalar.
     *
     * @param scalar
     * @return
     */
    public M2 multiplyByScalar(double scalar) {
        setAt(0, getAt(0) * scalar);
        setAt(1, getAt(1) * scalar);
        setAt(2, getAt(2) * scalar);
        setAt(3, getAt(3) * scalar);
        return this;
    }

    /**
     * Computes the product of a matrix times a (non-uniform) scale, as if the scale were a scale matrix.
     *
     * @param scale
     * @return
     */
    public M2 multiplyByScale(Vec2 scale) {
        setAt(0, getAt(0) * scale.x);
        setAt(1, getAt(1) * scale.x);
        setAt(2, getAt(2) * scale.y);
        setAt(3, getAt(3) * scale.y);
        return this;
    }

    /**
     * Computes the product of a matrix times a uniform scale, as if the scale were a scale matrix.
     *
     * @param scale
     * @return
     */
    public M2 multiplyByUniformScale(double scale) {
        setAt(0, getAt(0) * scale);
        setAt(1, getAt(1) * scale);
        setAt(2, getAt(2) * scale);
        setAt(3, getAt(3) * scale);
        return this;
    }

    /**
     * Creates a negated copy of the provided matrix.
     *
     * @return
     */
    public M2 negate() {
        setAt(0, -getAt(0));
        setAt(1, -getAt(1));
        setAt(2, -getAt(2));
        setAt(3, -getAt(3));
        return this;
    }

    /**
     * Computes the transpose of the provided matrix.
     *
     * @return
     */
    public M2 transpose() {
        double tmp = getAt(1);
        setAt(1, getAt(2));
        setAt(2, tmp);
        return this;
    }

    /**
     * Computes a matrix, which contains the absolute (unsigned) values of the provided matrix's elements.
     *
     * @return
     */
    public M2 abs() {
        setAt(0, Math.abs(getAt(0)));
        setAt(1, Math.abs(getAt(1)));
        setAt(2, Math.abs(getAt(2)));
        setAt(3, Math.abs(getAt(3)));
        return this;
    }

    public boolean equals(M2 other) {
        return getAt(0) == other.getAt(0) && getAt(1) == other.getAt(1) && getAt(2) == other.getAt(2) && getAt(3) == other.getAt(3);
    }

    public boolean equalsArray(Float32Array array, int offset) {
        return getAt(0) == array.getAt(offset) && getAt(1) == array.getAt(offset + 1) && getAt(2) == array.getAt(offset + 2) && getAt(3) == array.getAt(offset + 3);
    }

    public boolean equalsEpsilon(M2 other, double epsilon) {
        return Math.abs(getAt(0) - other.getAt(0)) <= epsilon
                && Math.abs(getAt(1) - other.getAt(1)) <= epsilon
                && Math.abs(getAt(2) - other.getAt(2)) <= epsilon
                && Math.abs(getAt(3) - other.getAt(3)) <= epsilon;
    }

    public M2 zero() {
        setAt(0, 0.);
        setAt(1, 0.);
        setAt(2, 0.);
        setAt(3, 0.);
        return this;
    }

    public M2 identity() {
        setAt(0, 1.);
        setAt(1, 0.);
        setAt(2, 0.);
        setAt(3, 1.);
        return this;
    }

    public void discard()
    {
        M2Pool.push(this);
    }

}
