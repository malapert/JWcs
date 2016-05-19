/*
 * Copyright (C) 2014-2016 Jean-Christophe Malapert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.malapert.jwcs.utility;

import io.github.malapert.jwcs.proj.exception.JWcsError;
import java.text.DecimalFormat;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * NumericalUtility class.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public final class NumericalUtility {
    
    /**
     * Private constructor
     */
    private NumericalUtility() {
        //not called
    }

    /**
     * Double tolerance for numerical precision operations sets to 1e-12.
     */
    public final static double DOUBLE_TOLERANCE = 1e-12;

    /**
     * Half PI value.
     */
    public final static double HALF_PI = Math.PI * 0.5d;

    /**
     * Two Pi value.
     */
    public final static double TWO_PI = Math.PI * 2.0d;

    /**
     * Compares two doubles.
     *
     * @param val1 first double
     * @param val2 second double
     * @param precision precision for the comparison
     * @return True when <code>val1</code> and <code>val2</code> are equals.
     */
    public final static boolean equal(final double val1, final double val2, final double precision) {
        return Math.abs(val2 - val1) <= precision;
    }

    /**
     * Projects ra/dec on cartesian reference system.
     *
     * @param pos position in the sky
     * @return the position in the cartesian reference system
     */
    public final static double[] radec2xyz(final double pos[]) {
        final double[] xyz = new double[3];
        xyz[0] = Math.cos(pos[1]) * Math.cos(pos[0]);
        xyz[1] = Math.cos(pos[1]) * Math.sin(pos[0]);
        xyz[2] = Math.sin(pos[1]);
        return xyz;
    }

    /**
     * Norm a vector.
     *
     * @param pos position in the sky
     * @return the norm of the position.
     */
    public final static double normVector(final double[] pos) {
        return Math.hypot(pos[0], pos[1]);
    }

    /**
     * Distance between two angles
     *
     * @param pos1 first angle
     * @param pos2 second angle.
     * @return the distance between two angles
     */
    public final static double distAngle(final double[] pos1, final double[] pos2) {
        final double[] xyzPos1 = radec2xyz(pos1);
        final double[] xyzPos2 = radec2xyz(pos2);
        double dot = xyzPos1[0] * xyzPos2[0] + xyzPos1[1] * xyzPos2[1] + xyzPos1[2] * xyzPos2[2];
        if (NumericalUtility.equal(dot, 0, 1e-13)) {
            dot = 0;
        }
        return NumericalUtility.aacos(dot / (normVector(xyzPos1) * normVector(xyzPos2)));
    }

    /**
     * Atan2 operation
     *
     * @param n the ordinate coordinate
     * @param d the abscissa coordinate
     * @return the theta component of the point (r, theta) in polar coordinates
     * that corresponds to the point (x, y) in Cartesian coordinates.
     */
    public final static double aatan2(final double n, final double d) {
        return aatan2(n, d, Double.NaN);
    }

    /**
     * Atan2 operation
     *
     * @param n the ordinate coordinate
     * @param d the abscissa coordinate
     * @param defaultValue DefaultValue when atan2 is undefined
     * @return the theta component of the point (r, theta) in polar coordinates
     * that corresponds to the point (x, y) in Cartesian coordinates.
     */
    public final static double aatan2(final double n, final double d, final double defaultValue) {
        return Math.abs(n) < DOUBLE_TOLERANCE && Math.abs(d) < DOUBLE_TOLERANCE ? defaultValue : Math.atan2(n, d);
    }

    /**
     * Asin operation.
     *
     * Returns the arc sine of a value; the returned angle is in the range -pi/2
     * through pi/2. Special cases:
     * <ul>
     * <li>If the argument is NaN or its absolute value is greater than 1, then
     * the result is NaN.</li>
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.</li>
     * </ul>
     *
     * @param v the value whose arc sine is returned
     * @return the arc sine of the argument.
     */
    public final static double aasin(final double v) {
        if (equal(v, 1, DOUBLE_TOLERANCE)) {
            return Math.PI / 2;
        } else if (equal(v, -1, DOUBLE_TOLERANCE)) {
            return -Math.PI / 2;
        } else {
            return Math.asin(v);
        }
    }

    /**
     * Acos operation.
     *
     * @param v the value whose arc cosine is to be returned.
     * @return the value whose arc cosine is to be returned
     */
    public final static double aacos(final double v) {
        if (Math.abs(v) > 1.) {
            return v < 0.0 ? Math.PI : 0.0;
        }
        return Math.acos(v);
    }

    /**
     * Normalizes the latitude
     *
     * @param angle latitude in radians
     * @return the angle from -half_PI to half_PI
     */
    public final static double normalizeLatitude(final double angle) {
        double resut = angle;
        if (Double.isInfinite(resut) || Double.isNaN(resut)) {
            throw new JWcsError("Infinite latitude");
        }
        if (Math.abs(resut - HALF_PI) < DOUBLE_TOLERANCE) {
            return HALF_PI;
        }
        if (Math.abs(resut + HALF_PI) < DOUBLE_TOLERANCE) {
            return -HALF_PI;
        }

        if (resut > Math.PI) {
            resut -= TWO_PI;
        }

        if (resut < -Math.PI) {
            resut += TWO_PI;
        }
        return resut;
    }

    /**
     * normalizes longitude angle in radians
     *
     * @param angle longitude in radians
     * @return the angle from 0 to 2PI
     */
    public final static double normalizeLongitude(final double angle) {
        double result = angle;
        if (Double.isInfinite(angle) || Double.isNaN(angle)) {
            throw new JWcsError("Infinite longitude");
        }

        // avoid instable computations with very small numbers: if the
        // angle is very close to the graticule boundary, return +/-PI.
        // Bernhard Jenny, May 25 2010.
        if (Math.abs(result - 0) < DOUBLE_TOLERANCE) {
            return 0;
        }
        if (Math.abs(result - TWO_PI) < DOUBLE_TOLERANCE) {
            return TWO_PI;
        }

        while (result > TWO_PI) {
            result -= TWO_PI;
        }
        while (result < 0) {
            result += TWO_PI;
        }
        return result;
    }

    /**
     * Formats the number with a precision with 3 digits after the comma.
     *
     * @param number the number to format
     * @return the formatted number
     */
    public final static String round(final double number) {
        final DecimalFormat df = new DecimalFormat("0.###");
        return df.format(number);
    }

    /**
     * Checks if the number is included in [min,max] with a numerical precision.
     *
     * @param number number to test
     * @param min minimum value
     * @param max maximum value
     * @param precision numerical precision
     * @return True when number is included in [min,max] otherwise False.
     */
    public final static boolean isInInterval(final double number, final double min, final double max, final double precision) {
        if (NumericalUtility.equal(number, min, precision)) {
            return true;
        }
        if (NumericalUtility.equal(number, max, precision)) {
            return true;
        }
        return min < number && number < max;
    }

    /**
     * Compares two doubles with a numerical precision of
     * {@link NumericalUtility#DOUBLE_TOLERANCE}.
     *
     * @param val1 first double
     * @param val2 second double
     * @return True when <code>val1</code> and <code>val2</code> are equals.
     */
    public static boolean equal(final double val1, final double val2) {
        return equal(val1, val2, DOUBLE_TOLERANCE);
    }

    /**
     * Checks if the number is included in [min,max] with a numerical precision
     * of {@link NumericalUtility#DOUBLE_TOLERANCE}.
     *
     * @param number number to test
     * @param min minimum value
     * @param max maximum value
     * @return True when number is included in [min,max] otherwise False.
     */
    public static boolean isInInterval(final double number, final double min, final double max) {
        return isInInterval(number, min, max, DOUBLE_TOLERANCE);
    }

    /**
     * Calculates the matrix that represents a 3d rotation around the X axis.
     *
     * Reference: ---------- Diebel, J. 2006, Stanford University, Representing
     * Attitude: Euler angles, Unit Quaternions and Rotation Vectors.
     * http://ai.stanford.edu/~diebel/attitude.html
     *
     * Notes: ------ Return the rotation matrix for a rotation around the X
     * axis. This is a rotation in the YZ plane. Note that we construct a new
     * vector with: xnew = R1.x In the literature, this rotation is usually
     * called R1
     *
     * @param angle Rotation angle in degrees
     * @return A 3x3 matrix representing the rotation about angle around X axis.
     */
    public final static RealMatrix rotX(final double angle) {
        final double angleRadians = Math.toRadians(angle);
        final double[][] array = {
            {1, 0, 0},
            {0, Math.cos(angleRadians), Math.sin(angleRadians)},
            {0, -Math.sin(angleRadians), Math.cos(angleRadians)}
        };
        return createRealMatrix(array);
    }

    /**
     * Calculates the matrix that represents a 3d rotation around the Y axis.
     *
     * Reference: ---------- Diebel, J. 2006, Stanford University, Representing
     * Attitude: Euler angles, Unit Quaternions and Rotation Vectors.
     * http://ai.stanford.edu/~diebel/attitude.html
     *
     * Notes: ------ Return the rotation matrix for a rotation around the X
     * axis. This is a rotation in the YZ plane. Note that we construct a new
     * vector with: xnew = R1.x In the literature, this rotation is usually
     * called R1
     *
     * @param angle Rotation angle in degrees
     * @return A 3x3 matrix representing the rotation about angle around Y axis.
     */
    public final static RealMatrix rotY(final double angle) {
        final double angleRadians = Math.toRadians(angle);
        final double[][] array = {
            {Math.cos(angleRadians), 0, -Math.sin(angleRadians)},
            {0, 1, 0},
            {Math.sin(angleRadians), 0, Math.cos(angleRadians)}
        };
        return createRealMatrix(array);
    }

    /**
     * Calculates the matrix that represents a 3d rotation around the Z axis.
     *
     * @param angle Rotation angle in degrees
     * @return A 3x3 matrix representing the rotation about angle around Z axis.
     */
    public final static RealMatrix rotZ(final double angle) {
        final double angleRadians = Math.toRadians(angle);
        final double[][] array = {
            {Math.cos(angleRadians), Math.sin(angleRadians), 0},
            {-Math.sin(angleRadians), Math.cos(angleRadians), 0},
            {0, 0, 1}
        };
        return createRealMatrix(array);
    }

    /**
     * Creates an identity matrix
     *
     * @param dimension matrix dimension
     * @return a matrix of dimension
     */
    public final static RealMatrix createRealIdentityMatrix(final int dimension) {
        return (RealMatrix) MatrixUtils.createRealIdentityMatrix(3);
    }

    /**
     * A RealMatrix whose entries are the the values in the the input array.
     *
     * @param data input array
     * @return RealMatrix containing the values of the array
     */
    public final static RealMatrix createRealMatrix(final double[][] data) {
        return (RealMatrix) MatrixUtils.createRealMatrix(data);
    }

    /**
     * Inverse matrix.
     * @param matrix the matrix to inverse
     * @return the inverse matrix
     */
    public final static RealMatrix inverse(final RealMatrix matrix) {
        return (RealMatrix) MatrixUtils.inverse(matrix);
    }  
}
