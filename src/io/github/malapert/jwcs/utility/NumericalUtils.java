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

/**
 * NumericalUtils class.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public abstract class NumericalUtils {
    
    /**
     * Double tolerance for numerical precision operations sets to 1e-12.
     */
    protected static final double DOUBLE_TOLERANCE = 1e-12;

    /**
     * Half PI value.
     */
    public static final double HALF_PI = Math.PI * 0.5d;

    /**
     * Two Pi value.
     */
    public static final double TWO_PI = Math.PI * 2.0d;    

    /**
     * Compares two doubles.
     *
     * @param val1 first double
     * @param val2 second double
     * @param precision precision for the comparison
     * @return True when <code>val1</code> and <code>val2</code> are equals.
     */
    public final static boolean equal(double val1, double val2, double precision) {
        return Math.abs(val2 - val1) <= precision;
    }

    /**
     * Projects ra/dec on cartesian reference system.
     *
     * @param pos position in the sky
     * @return the position in the cartesian reference system
     */
    public final static double[] radec2xyz(double pos[]) {
        double[] xyz = new double[3];
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
    public final static double normVector(double[] pos) {
        return Math.hypot(pos[0], pos[1]);
    }

    /**
     * Distance between two angles
     *
     * @param pos1 first angle
     * @param pos2 second angle.
     * @return the distance between two angles
     */
    public final static double distAngle(double[] pos1, double[] pos2) {
        double[] xyzPos1 = radec2xyz(pos1);
        double[] xyzPos2 = radec2xyz(pos2);
        double dot = xyzPos1[0] * xyzPos2[0] + xyzPos1[1] * xyzPos2[1] + xyzPos1[2] * xyzPos2[2];
        if (NumericalUtils.equal(dot, 0, 1e-13)) {
            dot = 0;
        }
        return NumericalUtils.aacos(dot / (normVector(xyzPos1) * normVector(xyzPos2)));
    }

    /**
     * Atan2 operation
     * @param n the ordinate coordinate
     * @param d the abscissa coordinate
     * @return the theta component of the point (r, theta) in polar coordinates 
     * that corresponds to the point (x, y) in Cartesian coordinates.
     */
    public final static double aatan2(double n, double d) {
        return aatan2(n, d, Double.NaN);
    }
    
    /**
     * Atan2 operation
     * @param n the ordinate coordinate
     * @param d the abscissa coordinate
     * @param defaultValue DefaultValue when atan2 is undefined
     * @return the theta component of the point (r, theta) in polar coordinates 
     * that corresponds to the point (x, y) in Cartesian coordinates.
     */
    public final static double aatan2(double n, double d, double defaultValue) {
        return ((Math.abs(n) < DOUBLE_TOLERANCE && Math.abs(d) < DOUBLE_TOLERANCE) ? defaultValue : Math.atan2(n, d));
    }    
    
    /**
     * Asin operation.
     * 
     * Returns the arc sine of a value; the returned angle is in the range -pi/2 through pi/2. Special cases:
     * <ul>
     *   <li>If the argument is NaN or its absolute value is greater than 1, then the result is NaN.</li>
     *   <li>If the argument is zero, then the result is a zero with the same sign as the argument.</li>
     * </ul>     
     * 
     * @param v the value whose arc sine is returned
     * @return the arc sine of the argument. 
     */
    public final static double aasin(double v) {       
        if (equal(v, 1, DOUBLE_TOLERANCE)) {
            return Math.PI/2;
        } else if (equal(v, -1, DOUBLE_TOLERANCE)) {
            return -Math.PI/2;
        } else {
            return Math.asin(v);
        }        
    }

    /**
     * Acos operation.
     * @param v the value whose arc cosine is to be returned.
     * @return the value whose arc cosine is to be returned
     */
    public final static double aacos(double v) {
        if (Math.abs(v) > 1.) {
            return v < 0.0 ? Math.PI : 0.0;
        }
        return Math.acos(v);
    }

    /**
     * Normalizes the latitude
     * @param angle latitude in radians
     * @return the angle from -half_PI to half_PI
     */
    public final static double normalizeLatitude(double angle) {
        if (Double.isInfinite(angle) || Double.isNaN(angle)) {
            throw new JWcsError("Infinite latitude");
        }
        if (Math.abs(angle - HALF_PI) < DOUBLE_TOLERANCE) {
            return HALF_PI;
        }
        if (Math.abs(angle + HALF_PI) < DOUBLE_TOLERANCE) {
            return -HALF_PI;
        }

        if (angle > Math.PI) {
            angle -= TWO_PI;
        }
        
        if (angle < -Math.PI) {
            angle += TWO_PI;
        }
        return angle;
    }

    /**
     * normalizes longitude angle in radians
     *
     * @param angle longitude in radians
     * @return the angle from 0 to 2PI
     */
    public final static double normalizeLongitude(double angle) {
        if (Double.isInfinite(angle) || Double.isNaN(angle)) {
            throw new JWcsError("Infinite longitude");
        }

        // avoid instable computations with very small numbers: if the
        // angle is very close to the graticule boundary, return +/-PI.
        // Bernhard Jenny, May 25 2010.
        if (Math.abs(angle - 0) < DOUBLE_TOLERANCE) {
            return 0;
        }
        if (Math.abs(angle - TWO_PI) < DOUBLE_TOLERANCE) {
            return TWO_PI;
        }

        while (angle > TWO_PI) {
            angle -= TWO_PI;
        }
        while (angle < 0) {
            angle += TWO_PI;
        }
        return angle;
    }

    /**
     * Formats the number with a precision with 3 digits after the comma.
     * @param number the number to format
     * @return the formatted number
     */
    public final static String round(double number) {
        DecimalFormat df = new DecimalFormat("0.###");
        return df.format(number);
    }
    
    /**
     * Checks if the number is included in [min,max] with a numerical precision.
     * @param number number to test
     * @param min minimum value
     * @param max maximum value
     * @param precision numerical precision
     * @return True when number is included in [min,max] otherwise False.
     */
    public final static boolean isInInterval(double number, double min, double max, double precision) {
        if (NumericalUtils.equal(number, min, precision)) {
            return true;
        }
        if (NumericalUtils.equal(number, max, precision)) {
            return true;
        }        
        return min < number && number < max;
    } 

    /**
     * Compares two doubles with a numerical precision of {@link NumericalUtils#DOUBLE_TOLERANCE}.
     *
     * @param val1 first double
     * @param val2 second double
     * @return True when <code>val1</code> and <code>val2</code> are equals.
     */    
    public static boolean equal(double val1, double val2) {
        return equal(val1, val2, DOUBLE_TOLERANCE);
    }

    /**
     * Checks if the number is included in [min,max] with a numerical precision of {@link NumericalUtils#DOUBLE_TOLERANCE}.
     * @param number number to test
     * @param min minimum value
     * @param max maximum value
     * @return True when number is included in [min,max] otherwise False.
     */    
    public static boolean isInInterval(double number, double min, double max) {
        return isInInterval(number, min, max, DOUBLE_TOLERANCE);
    }
}
