package cn.mapway.globe.server.gabage;

import cn.mapway.globe.client.core.GlobelConstant;
import elemental2.core.Float32Array;
import elemental2.core.JsArray;
import jsinterop.annotations.JsType;

/**
 * A 3x3 matrix, indexable as a column-major order array.
 */
@JsType(namespace = GlobelConstant.NAMESPACE, name = "M3")
public class M3 extends JsArray<Double> {
    int[] rowVal = new int[]{1, 0, 0};
    int[] colVal = new int[]{2, 2, 1};

    /**
     * Constructor parameters are in row-major order for code readability.
     *
     * @param m00 The value for column 0, row 0.
     * @param m01 The value for column 0, row 1.
     * @param m02 The value for column 0, row 2.
     * @param m10 The value for column 1, row 0.
     * @param m11 The value for column 1, row 1.
     * @param m12 The value for column 1, row 2.
     * @param m20 The value for column 2, row 0.
     * @param m21 The value for column 2, row 1.
     * @param m22 The value for column 2, row 2.
     */
    public M3(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
        set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    public static Float32Array packArray(M3[] matrices, Float32Array array, int offset) {
        assert (array.length - offset) >= matrices.length * 9;
        for (int i = 0; i < matrices.length; i++) {
            matrices[i].pack(array, offset);
            offset += 9;
        }
        return array;
    }

    public static M3[] unpackArray(Float32Array array, M3[] matrices, int offset) {
        for (int i = 0; i < matrices.length; i++) {
            matrices[i] = new M3(1, 0, 0, 1, 0, 0, 0, 1, 0);
            matrices[i].unpack(array, offset);
            offset += 9;
        }
        return matrices;
    }

    public M3 pack(Float32Array array, int offset) {
        array.setAt(offset, getAt(0));
        array.setAt(offset + 1, getAt(1));
        array.setAt(offset + 2, getAt(2));
        array.setAt(offset + 3, getAt(3));
        array.setAt(offset + 4, getAt(4));
        array.setAt(offset + 5, getAt(5));
        array.setAt(offset + 6, getAt(6));
        array.setAt(offset + 7, getAt(7));
        array.setAt(offset + 8, getAt(8));
        return this;
    }

    public M3 unpack(Float32Array array, int offset) {
        setAt(0, array.getAt(offset));
        setAt(1, array.getAt(offset + 1));
        setAt(2, array.getAt(offset + 2));
        setAt(3, array.getAt(offset + 3));
        setAt(4, array.getAt(offset + 4));
        setAt(5, array.getAt(offset + 5));
        setAt(6, array.getAt(offset + 6));
        setAt(7, array.getAt(offset + 7));
        setAt(8, array.getAt(offset + 8));
        return this;
    }

    public M3 set(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
        setAt(0, m00);
        setAt(1, m10);
        setAt(2, m20);
        setAt(3, m01);
        setAt(4, m11);
        setAt(5, m21);
        setAt(6, m02);
        setAt(7, m12);
        setAt(8, m22);
        return this;
    }

    public M3 clone() {
        return M3Pool.poll().set(getAt(0), getAt(3), getAt(6), getAt(1), getAt(4), getAt(7), getAt(2), getAt(5), getAt(8));
    }


    public M3 fromColumnMajorArray(double[] array) {
        setAt(0, array[0]);
        setAt(1, array[1]);
        setAt(2, array[2]);
        setAt(3, array[3]);
        setAt(4, array[4]);
        setAt(5, array[5]);
        setAt(6, array[6]);
        setAt(7, array[7]);
        setAt(8, array[8]);
        return this;
    }

    public M3 fromRowMajorArray(double[] array) {
        setAt(0, array[0]);
        setAt(1, array[3]);
        setAt(2, array[6]);
        setAt(3, array[1]);
        setAt(4, array[4]);
        setAt(5, array[7]);
        setAt(6, array[2]);
        setAt(7, array[5]);
        setAt(8, array[8]);
        return this;
    }

    public double[] toColumnMajorArray() {
        double[] array = new double[9];
        array[0] = getAt(0);
        array[1] = getAt(1);
        array[2] = getAt(2);
        array[3] = getAt(3);
        array[4] = getAt(4);
        array[5] = getAt(5);
        array[6] = getAt(6);
        array[7] = getAt(7);
        array[8] = getAt(8);
        return array;
    }

    public double[] toRowMajorArray() {
        double[] array = new double[9];
        array[0] = getAt(0);
        array[1] = getAt(3);
        array[2] = getAt(6);
        array[3] = getAt(1);
        array[4] = getAt(4);
        array[5] = getAt(7);
        array[6] = getAt(2);
        array[7] = getAt(5);
        array[8] = getAt(8);
        return array;
    }

    public M3 identity() {
        setAt(0, 1.);
        setAt(1, 0.);
        setAt(2, 0.);
        setAt(3, 0.);
        setAt(4, 1.);
        setAt(5, 0.);
        setAt(6, 0.);
        setAt(7, 0.);
        setAt(8, 1.);
        return this;
    }

    public M3 zero() {
        setAt(0, 0.);
        setAt(1, 0.);
        setAt(2, 0.);
        setAt(3, 0.);
        setAt(4, 0.);
        setAt(5, 0.);
        setAt(6, 0.);
        setAt(7, 0.);
        setAt(8, 0.);
        return this;
    }

    public M3 transpose() {

        double tmp = getAt(1);
        setAt(1, getAt(3));
        setAt(3, tmp);
        tmp = getAt(2);
        setAt(2, getAt(6));
        setAt(6, tmp);
        tmp = getAt(5);
        setAt(5, getAt(7));
        setAt(7, tmp);
        return this;
    }

    /**
     * Computes a 3x3 rotation matrix from the provided headingPitchRoll. (see http://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles )
     *
     * @param headingPitchRoll
     * @return
     */
    public M3 fromHeadingPitchRoll(HeadingPitchRoll headingPitchRoll) {
        double cosTheta = Math.cos(-headingPitchRoll.pitch);
        double cosPsi = Math.cos(-headingPitchRoll.heading);
        double cosPhi = Math.cos(headingPitchRoll.roll);
        double sinTheta = Math.sin(-headingPitchRoll.pitch);
        double sinPsi = Math.sin(-headingPitchRoll.heading);
        double sinPhi = Math.sin(headingPitchRoll.roll);
        double m00 = cosTheta * cosPsi;
        double m01 = -cosPhi * sinPsi + sinPhi * sinTheta * cosPsi;
        double m02 = sinPhi * sinPsi + cosPhi * sinTheta * cosPsi;
        double m10 = cosTheta * sinPsi;
        double m11 = cosPhi * cosPsi + sinPhi * sinTheta * sinPsi;
        double m12 = -sinPhi * cosPsi + cosPhi * sinTheta * sinPsi;
        double m20 = -sinTheta;
        double m21 = sinPhi * cosTheta;
        double m22 = cosPhi * cosTheta;
        set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
        return this;
    }

    /**
     * Computes a Matrix3 instance representing a non-uniform scale.
     *
     * @param scale
     * @return
     */
    public M3 fromScale(Vec3 scale) {
        setAt(0, scale.x);
        setAt(1, 0.);
        setAt(2, 0.);
        setAt(3, 0.);
        setAt(4, scale.y);
        setAt(5, 0.);
        setAt(6, 0.);
        setAt(7, 0.);
        setAt(8, scale.z);
        return this;
    }

    /**
     * Computes a Matrix3 instance representing a uniform scale.
     *
     * @param scale
     * @return
     */
    public M3 fromUniformScale(double scale) {
        setAt(0, scale);
        setAt(1, 0.);
        setAt(2, 0.);
        setAt(3, 0.);
        setAt(4, scale);
        setAt(5, 0.);
        setAt(6, 0.);
        setAt(7, 0.);
        setAt(8, scale);
        return this;
    }

    /**
     * Computes a Matrix3 instance representing the cross product equivalent matrix of a Vec3 vector.
     *
     * @param v
     * @return // Creates
     * //   [0.0, -9.0,  8.0]
     * //   [9.0,  0.0, -7.0]
     * //   [-8.0, 7.0,  0.0]
     */
    public M3 fromCrossProduct(Vec3 v) {
        setAt(0, 0.);
        setAt(1, v.z);
        setAt(2, -v.y);
        setAt(3, -v.z);
        setAt(4, 0.);
        setAt(5, v.x);
        setAt(6, v.y);
        setAt(7, -v.x);
        setAt(8, 0.);
        return this;
    }

    /**
     * Creates a rotation matrix around the x-axis.
     *
     * @param angle The angle, in radians, of the rotation.  Positive angles are counterclockwise.
     * @return
     * @example // Rotate a point 45 degrees counterclockwise around the x-axis.
     * Vec3 p = new Vec3.create(5, 6, 7);
     * M3 m = M3.fromRotationX(Cesium.Math.toRadians(45.0));
     * M3 rotated = M3.multiplyByVector(m, p);
     */
    public M3 fromRotationX(double angle) {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        setAt(0, 1.0);
        setAt(1, 0.0);
        setAt(2, 0.0);
        setAt(3, 0.0);
        setAt(4, cosAngle);
        setAt(5, sinAngle);
        setAt(6, 0.0);
        setAt(7, -sinAngle);
        setAt(8, cosAngle);
        return this;
    }

    /**
     * Creates a rotation matrix around the y-axis.
     *
     * @param angle The angle, in radians, of the rotation.  Positive angles are counterclockwise.
     * @return
     * @example // Rotate a point 45 degrees counterclockwise around the y-axis.
     * Vec3 p = new Vec3.create(5, 6, 7);
     * M3 m = M3.fromRotationY(Cesium.Math.toRadians(45.0));
     * M3 rotated = M3.multiplyByVector(m, p);
     */
    public M3 fromRotationY(double angle) {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        setAt(0, cosAngle);
        setAt(1, 0.0);
        setAt(2, -sinAngle);
        setAt(3, 0.0);
        setAt(4, 1.0);
        setAt(5, 0.0);
        setAt(6, sinAngle);
        setAt(7, 0.0);
        setAt(8, cosAngle);
        return this;
    }

    /**
     * Creates a rotation matrix around the z-axis.
     *
     * @param angle The angle, in radians, of the rotation.  Positive angles are counterclockwise.
     * @return
     * @example // Rotate a point 45 degrees counterclockwise around the z-axis.
     * Vec3 p = new Vec3.create(5, 6, 7);
     * M3 m = M3.fromRotationZ(Cesium.Math.toRadians(45.0));
     * M3 rotated = M3.multiplyByVector(m, p);
     */
    public M3 fromRotationZ(double angle) {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        setAt(0, cosAngle);
        setAt(1, sinAngle);
        setAt(2, 0.0);
        setAt(3, -sinAngle);
        setAt(4, cosAngle);
        setAt(5, 0.0);
        setAt(6, 0.0);
        setAt(7, 0.0);
        setAt(8, 1.0);
        return this;
    }

    /**
     * Creates an Array from the Matrix3 instance.
     *
     * @param array
     * @return
     */
    public double[] toArray(double[] array) {
        if (array == null || array.length != 9) {
            array = new double[9];
        }
        array[0] = getAt(0);
        array[1] = getAt(1);
        array[2] = getAt(2);
        array[3] = getAt(3);
        array[4] = getAt(4);
        array[5] = getAt(5);
        array[6] = getAt(6);
        array[7] = getAt(7);
        array[8] = getAt(8);
        return array;
    }

    /**
     * Computes the array index of the element at the provided row and column.
     *
     * @param column
     * @param row
     * @return
     */
    public int getElementIndex(int column, int row) {
        assert column >= 0 && column <= 2;
        assert row >= 0 && row <= 2;
        return column * 3 + row;
    }

    /**
     * Retrieves a copy of the matrix column at the provided index as a Cartesian3 instance.
     *
     * @param column
     * @return
     */
    public Vec3 getColumn(int column) {
        assert column >= 0 && column <= 2;
        Vec3 v = Vec3Pool.poll();
        int startIndex = column * 3;
        double x = getAt(startIndex);
        double y = getAt(startIndex + 1);
        double z = getAt(startIndex + 2);
        v.set(x, y, z);
        return v;
    }

    /**
     * Computes a new matrix that replaces the specified column in the provided matrix with the provided Cartesian3 instance.
     *
     * @param column
     * @param vec
     * @return
     */
    public M3 setColumn(int column, Vec3 vec) {
        assert column >= 0 && column <= 2;
        int index = column * 3;
        setAt(index, vec.x);
        setAt(index + 1, vec.y);
        setAt(index + 2, vec.z);
        return this;
    }

    /**
     * Retrieves a copy of the matrix row at the provided index as a Cartesian3 instance.
     *
     * @param row
     * @return
     */
    public Vec3 getRow(int row) {
        assert row >= 0 && row <= 2;
        Vec3 v = Vec3Pool.poll();
        int startIndex = row;
        double x = getAt(startIndex);
        double y = getAt(startIndex + 3);
        double z = getAt(startIndex + 6);
        v.set(x, y, z);
        return v;
    }

    /**
     * Computes a new matrix that replaces the specified row in the provided matrix with the provided Cartesian3 instance.
     *
     * @param row
     * @param vec
     * @return
     */
    public M3 setRow(int row, Vec3 vec) {
        assert row >= 0 && row <= 2;
        int index = row;
        setAt(index, vec.x);
        setAt(index + 3, vec.y);
        setAt(index + 6, vec.z);
        return this;
    }

    public M3 setUniformScale(double scale) {
        Vec3 temp = Vec3Pool.poll().set(scale, scale, scale);
        setScale(temp);
        temp.discard();
        return this;
    }

    public Vec3 getScale() {
        Vec3 result = Vec3Pool.poll();
        Vec3 temp = getColumn(0);
        result.x = temp.magnitude();
        temp.discard();

        temp = getColumn(1);
        result.y = temp.magnitude();
        temp.discard();

        temp = getColumn(2);
        result.z = temp.magnitude();
        temp.discard();
        return result;
    }

    public M3 setScale(Vec3 scale) {
        Vec3 existingScale = getScale();
        double scaleRatioX = scale.x / existingScale.x;
        double scaleRatioY = scale.y / existingScale.y;
        double scaleRatioZ = scale.z / existingScale.z;

        setAt(0, getAt(0) * scaleRatioX);
        setAt(1, getAt(1) * scaleRatioX);
        setAt(2, getAt(2) * scaleRatioX);
        setAt(3, getAt(3) * scaleRatioY);
        setAt(4, getAt(4) * scaleRatioY);
        setAt(5, getAt(5) * scaleRatioY);
        setAt(6, getAt(6) * scaleRatioZ);
        setAt(7, getAt(7) * scaleRatioZ);
        setAt(8, getAt(8) * scaleRatioZ);
        existingScale.discard();
        return this;
    }

    public double getMaximumScale() {
        Vec3 scale = getScale();
        double v = scale.maxComponent();
        scale.discard();
        return v;
    }

    public double getMinimumScale() {
        Vec3 scale = getScale();
        double v = scale.minComponent();
        scale.discard();
        return v;
    }

    public M3 getRotation() {
        Vec3 scale = getScale();
        M3 result = M3Pool.poll();
        result.setAt(0, getAt(0) / scale.x);
        result.setAt(1, getAt(1) / scale.x);
        result.setAt(2, getAt(2) / scale.x);
        result.setAt(3, getAt(3) / scale.y);
        result.setAt(4, getAt(4) / scale.y);
        result.setAt(5, getAt(5) / scale.y);
        result.setAt(6, getAt(6) / scale.z);
        result.setAt(7, getAt(7) / scale.z);
        result.setAt(8, getAt(8) / scale.z);
        scale.discard();
        return result;
    }

    /**
     * Sets the rotation assuming the matrix is an affine transformation.
     *
     * @param rotation
     * @return
     */
    public M3 setRotation(M3 rotation) {
        Vec3 scale = getScale();
        setAt(0, rotation.getAt(0) * scale.x);
        setAt(1, rotation.getAt(1) * scale.x);
        setAt(2, rotation.getAt(2) * scale.x);
        setAt(3, rotation.getAt(3) * scale.y);
        setAt(4, rotation.getAt(4) * scale.y);
        setAt(5, rotation.getAt(5) * scale.y);
        setAt(6, rotation.getAt(6) * scale.z);
        setAt(7, rotation.getAt(7) * scale.z);
        setAt(8, rotation.getAt(8) * scale.z);
        scale.discard();
        return this;
    }

    /**
     * Computes the product of two matrices.
     *
     * @param right
     * @return
     */
    public M3 multiply(M3 right) {

        double column0Row0 = getAt(0) * right.getAt(0) + getAt(3) * right.getAt(1) + getAt(6) * right.getAt(2);
        double column1Row0 = getAt(1) * right.getAt(0) + getAt(4) * right.getAt(1) + getAt(7) * right.getAt(2);
        double column2Row0 = getAt(2) * right.getAt(0) + getAt(5) * right.getAt(1) + getAt(8) * right.getAt(2);
        double column0Row1 = getAt(0) * right.getAt(3) + getAt(3) * right.getAt(4) + getAt(6) * right.getAt(5);
        double column1Row1 = getAt(1) * right.getAt(3) + getAt(4) * right.getAt(4) + getAt(7) * right.getAt(5);
        double column2Row1 = getAt(2) * right.getAt(3) + getAt(5) * right.getAt(4) + getAt(8) * right.getAt(5);
        double column0Row2 = getAt(0) * right.getAt(6) + getAt(3) * right.getAt(7) + getAt(6) * right.getAt(8);
        double column1Row2 = getAt(1) * right.getAt(6) + getAt(4) * right.getAt(7) + getAt(7) * right.getAt(8);
        double column2Row2 = getAt(2) * right.getAt(6) + getAt(5) * right.getAt(7) + getAt(8) * right.getAt(8);
        setAt(0, column0Row0);
        setAt(1, column0Row1);
        setAt(2, column0Row2);
        setAt(3, column1Row0);
        setAt(4, column1Row1);
        setAt(5, column1Row2);
        setAt(6, column2Row0);
        setAt(7, column2Row1);
        setAt(8, column2Row2);
        return this;
    }

    /**
     * Computes the sum of two matrices.
     *
     * @param right
     * @return
     */
    public M3 add(M3 right) {
        setAt(0, getAt(0) + right.getAt(0));
        setAt(1, getAt(1) + right.getAt(1));
        setAt(2, getAt(2) + right.getAt(2));
        setAt(3, getAt(3) + right.getAt(3));
        setAt(4, getAt(4) + right.getAt(4));
        setAt(5, getAt(5) + right.getAt(5));
        setAt(6, getAt(6) + right.getAt(6));
        setAt(7, getAt(7) + right.getAt(7));
        setAt(8, getAt(8) + right.getAt(8));
        return this;
    }

    /**
     * Computes the difference of two matrices.
     *
     * @param right
     * @return
     */
    public M3 subtract(M3 right) {
        setAt(0, getAt(0) - right.getAt(0));
        setAt(1, getAt(1) - right.getAt(1));
        setAt(2, getAt(2) - right.getAt(2));
        setAt(3, getAt(3) - right.getAt(3));
        setAt(4, getAt(4) - right.getAt(4));
        setAt(5, getAt(5) - right.getAt(5));
        setAt(6, getAt(6) - right.getAt(6));
        setAt(7, getAt(7) - right.getAt(7));
        setAt(8, getAt(8) - right.getAt(8));
        return this;
    }

    /**
     * Computes the product of two matrices.
     *
     * @param right
     * @return
     */
    Vec3 multiplyByVec3(Vec3 right) {
        Vec3 result = Vec3Pool.poll();
        double vX = right.x;
        double vY = right.y;
        double vZ = right.z;
        result.x = getAt(0) * vX + getAt(3) * vY + getAt(6) * vZ;
        result.y = getAt(1) * vX + getAt(4) * vY + getAt(7) * vZ;
        result.z = getAt(2) * vX + getAt(5) * vY + getAt(8) * vZ;
        return result;
    }

    /**
     * Computes the product of a matrix and a scalar.
     *
     * @param scalar
     * @return
     */
    public M3 multiplyByScalar(double scalar) {
        setAt(0, getAt(0) * scalar);
        setAt(1, getAt(1) * scalar);
        setAt(2, getAt(2) * scalar);
        setAt(3, getAt(3) * scalar);
        setAt(4, getAt(4) * scalar);
        setAt(5, getAt(5) * scalar);
        setAt(6, getAt(6) * scalar);
        setAt(7, getAt(7) * scalar);
        setAt(8, getAt(8) * scalar);
        return this;
    }

    /**
     * Computes the product of a matrix times a (non-uniform) scale, as if the scale were a scale matrix.
     */
    public M3 multiplyByScale(Vec3 scale) {
        setAt(0, getAt(0) * scale.x);
        setAt(1, getAt(1) * scale.x);
        setAt(2, getAt(2) * scale.y);
        setAt(3, getAt(3) * scale.y);
        setAt(4, getAt(4) * scale.z);
        setAt(5, getAt(5) * scale.z);
        setAt(6, getAt(6) * scale.z);
        setAt(7, getAt(7) * scale.z);
        setAt(8, getAt(8) * scale.z);
        return this;
    }

    /**
     * Computes the product of a matrix times a uniform scale, as if the scale were a scale matrix.
     *
     * @param scale
     * @return
     */
    public M3 multiplyByUniformScale(double scale) {
        setAt(0, getAt(0) * scale);
        setAt(1, getAt(1) * scale);
        setAt(2, getAt(2) * scale);
        setAt(3, getAt(3) * scale);
        setAt(4, getAt(4) * scale);
        setAt(5, getAt(5) * scale);
        setAt(6, getAt(6) * scale);
        setAt(7, getAt(7) * scale);
        setAt(8, getAt(8) * scale);
        return this;
    }

    /**
     * Creates a negated copy of the provided matrix.
     *
     * @return
     */
    public M3 negate() {
        setAt(0, -getAt(0));
        setAt(1, -getAt(1));
        setAt(2, -getAt(2));
        setAt(3, -getAt(3));
        setAt(4, -getAt(4));
        setAt(5, -getAt(5));
        setAt(6, -getAt(6));
        setAt(7, -getAt(7));
        setAt(8, -getAt(8));
        return this;
    }

    public double computeFrobeniusNorm() {
        double norm = 0.0;
        for (int i = 0; i < 9; ++i) {
            double temp = getAt(i);
            norm += temp * temp;
        }
        return Math.sqrt(norm);
    }

    public double offDiagonalFrobeniusNorm() {
        // Computes the "off-diagonal" Frobenius norm.
        // Assumes matrix is symmetric.

        double norm = 0.0;
        for (int i = 0; i < 3; ++i) {
            double temp = getAt(getElementIndex(colVal[i], rowVal[i]));
            norm += 2.0 * temp * temp;
        }
        return Math.sqrt(norm);
    }

    public M3 shurDecomposition() {
        // This routine was created based upon Matrix Computations, 3rd ed., by Golub and Van Loan,
        // section 8.4.2 The 2by2 Symmetric Schur Decomposition.
        //
        // The routine takes a matrix, which is assumed to be symmetric, and
        // finds the largest off-diagonal term, and then creates
        // a matrix (result) which can be used to help reduce it

        double tolerance = Maths.EPSILON15;

        double maxDiagonal = 0.0;
        int rotAxis = 1;

        // find pivot (rotAxis) based on max diagonal of matrix
        for (int i = 0; i < 3; ++i) {
            double temp = Math.abs(
                    getAt(getElementIndex(colVal[i], rowVal[i]))
            );
            if (temp > maxDiagonal) {
                rotAxis = i;
                maxDiagonal = temp;
            }
        }

        double c = 1.0;
        double s = 0.0;

        int p = rowVal[rotAxis];
        int q = colVal[rotAxis];

        if (Math.abs(getAt(getElementIndex(q, p))) > tolerance) {
            double qq = getAt(getElementIndex(q, q));
            double pp = getAt(getElementIndex(p, p));
            double qp = getAt(getElementIndex(q, p));

            double tau = (qq - pp) / 2.0 / qp;
            double t;

            if (tau < 0.0) {
                t = -1.0 / (-tau + Math.sqrt(1.0 + tau * tau));
            } else {
                t = 1.0 / (tau + Math.sqrt(1.0 + tau * tau));
            }

            c = 1.0 / Math.sqrt(1.0 + t * t);
            s = t * c;
        }
        M3 result = M3Pool.poll().identity();

        result.setAt(getElementIndex(p, p), c);
        result.setAt(getElementIndex(q, q), c);
        result.setAt(getElementIndex(q, p), s);
        result.setAt(getElementIndex(p, q), -s);
        return result;
    }

    /**
     * Computes a matrix, which contains the absolute (unsigned) values of the provided matrix's elements.
     * @return
     */
    public M3 abs(){
        setAt(0, Math.abs(getAt(0)));
        setAt(1, Math.abs(getAt(1)));
        setAt(2, Math.abs(getAt(2)));
        setAt(3, Math.abs(getAt(3)));
        setAt(4, Math.abs(getAt(4)));
        setAt(5, Math.abs(getAt(5)));
        setAt(6, Math.abs(getAt(6)));
        setAt(7, Math.abs(getAt(7)));
        setAt(8, Math.abs(getAt(8)));
        return this;
    }

    /**
     * Computes the determinant of the provided matrix.
     * @return
     */
    public double determinant()
    {
      double m11 = getAt(0);
      double m21 = getAt(3);
      double m31 = getAt(6);
      double m12 = getAt(1);
      double m22 = getAt(4);
      double m32 = getAt(7);
      double m13 = getAt(2);
      double m23 = getAt(5);
      double m33 = getAt(8);

        return (
                m11 * (m22 * m33 - m23 * m32) +
                        m12 * (m23 * m31 - m21 * m33) +
                        m13 * (m21 * m32 - m22 * m31)
        );
    }

    /**
     * Computes the inverse of the provided matrix.
     * @return
     */
    public M3 inverse() {
        double m11 = getAt(0);
        double m21 = getAt(1);
        double m31 = getAt(2);
        double m12 = getAt(3);
        double m22 = getAt(4);
        double m32 = getAt(5);
        double m13 = getAt(6);
        double m23 = getAt(7);
        double m33 = getAt(8);
        double determinant = determinant();
        if (Math.abs(determinant) <= Maths.EPSILON15) {
            throw new RuntimeException("matrix is not invertible");
        }
        M3 result= M3Pool.poll();
        result.setAt(0, (m22 * m33 - m23 * m32) / determinant);
        result.setAt(1, (m23 * m31 - m21 * m33) / determinant);
        result.setAt(2, (m21 * m32 - m22 * m31) / determinant);
        result.setAt(3, (m13 * m32 - m12 * m33) / determinant);
        result.setAt(4, (m11 * m33 - m13 * m31) / determinant);
        result.setAt(5, (m12 * m31 - m11 * m32) / determinant);
        result.setAt(6, (m12 * m23 - m13 * m22) / determinant);
        result.setAt(7, (m13 * m21 - m11 * m23) / determinant);
        result.setAt(8, (m11 * m22 - m12 * m21) / determinant);
        return result;
    }

    /**
     * Computes the inverse transpose of a matrix.
     * @return
     */
    public M3 inverseTranspose() {
        M3 temp=transpose();
        M3 inverse = temp.inverse();
        temp.discard();
        return inverse;
    }

    private void discard() {
        M3Pool.push(this);
    }

    public boolean equals(M3 other) {
        return getAt(0) == other.getAt(0) && getAt(1) == other.getAt(1) && getAt(2) == other.getAt(2) && getAt(3) == other.getAt(3)
                && getAt(4) == other.getAt(4) && getAt(5) == other.getAt(5) && getAt(6) == other.getAt(6) && getAt(7) == other.getAt(7)
                && getAt(8) == other.getAt(8);
    }

    public boolean equalsEpsilon(M3 other, double epsilon) {
        return Math.abs(getAt(0) - other.getAt(0)) <= epsilon
                && Math.abs(getAt(1) - other.getAt(1)) <= epsilon
                && Math.abs(getAt(2) - other.getAt(2)) <= epsilon
                && Math.abs(getAt(3) - other.getAt(3)) <= epsilon
                && Math.abs(getAt(4) - other.getAt(4)) <= epsilon
                && Math.abs(getAt(5) - other.getAt(5)) <= epsilon
                && Math.abs(getAt(6) - other.getAt(6)) <= epsilon
                && Math.abs(getAt(7) - other.getAt(7)) <= epsilon
                && Math.abs(getAt(8) - other.getAt(8)) <= epsilon;
    }

    public boolean equalsArray(Float32Array array, int offset) {
        return getAt(0) == array.getAt(offset) && getAt(1) == array.getAt(offset + 1) && getAt(2) == array.getAt(offset + 2) && getAt(3) == array.getAt(offset + 3)
                && getAt(4) == array.getAt(offset + 4) && getAt(5) == array.getAt(offset + 5) && getAt(6) == array.getAt(offset + 6) && getAt(7) == array.getAt(offset + 7)
                && getAt(8) == array.getAt(offset + 8);
    }

    public String toString() {
        return "("
                +this.getAt(0)+", "+this.getAt(1)+", "+this.getAt(2)+",\n"
                +this.getAt(3)+"," +this.getAt(4)+", "+this.getAt(5)+", \n"
                +this.getAt(6)+", "+this.getAt(7)+", "+this.getAt(8)+")";
    }
}