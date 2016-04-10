/*
 * Copyright (C) 2014 Jean-Christophe Malapert
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

import io.github.malapert.jwcs.proj.Projection;
import io.github.malapert.jwcs.proj.exception.JWcsError;
import io.github.malapert.jwcs.proj.exception.ProjectionException;

/**
 * NumericalUtils class.
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public abstract class NumericalUtils {
    
    /**
     * Compares two doubles.
     * @param val1 first double
     * @param val2 second double
     * @param precision precision for the comparison
     * @return True when <code>val1</code> and <code>val2</code> are equals.
     */
    public static boolean equal(double val1, double val2, double precision) {
        return Math.abs(val2-val1) <= precision;
    }
    
    /**
     * Projects ra/dec on cartesian reference system.
     * @param pos position in the sky
     * @return the position in the cartesian reference system
     */
    public static double[] radec2xyz(double pos[]) {
        double[] xyz = new double[3];
        xyz[0] = Math.cos(pos[1])*Math.cos(pos[0]);
        xyz[1] = Math.cos(pos[1])*Math.sin(pos[0]);
        xyz[2] = Math.sin(pos[1]);
        return xyz;
    }
    
    /**
     * Norm a vector.
     * @param pos position in the sky
     * @return the norm of the position.
     */
    public static double normVector(double[] pos) {
        return Math.hypot(pos[0], pos[1]);
    }
    
    /**
     * Distance between two angles
     * @param pos1 first angle
     * @param pos2 second angle.
     * @return the distance between two angles
     */
    public static double distAngle(double[] pos1, double[] pos2) {
        double[] xyzPos1 = radec2xyz(pos1);
        double[] xyzPos2 = radec2xyz(pos2);
        double dot = xyzPos1[0]*xyzPos2[0]+xyzPos1[1]*xyzPos2[1]+xyzPos1[2]*xyzPos2[2];        
        if (NumericalUtils.equal(dot, 0, 1e-13)) {
            dot = 0;
        }
        return NumericalUtils.aacos(dot/(normVector(xyzPos1)*normVector(xyzPos2)));
    }     
    
    public static double aatan2(double n, double d) {
        final double ATOL = 1.0e-15;
        return ((Math.abs(n) < ATOL && Math.abs(d) < ATOL) ? 0 : Math.atan2(n, d));
    }

    public static double aasin(double v) {
        final double ONE_TOL = 1.00000000000001;
        double av = Math.abs(v);
        if (av >= 1) {
            if (av > ONE_TOL) {
                return Double.NaN;
            }
            return v < 0 ? -Math.PI / 2 : Math.PI / 2;
        }
        return Math.asin(v);
    } 
    
    public static double aacos(double v) {
        if (Math.abs(v) > 1.) {
            return v < 0.0 ? Math.PI : 0.0;
        }
        return Math.acos(v);
    }
//
    public static double normalizeLatitude(double angle) {
        if (Double.isInfinite(angle) || Double.isNaN(angle)) {
            throw new JWcsError("Infinite latitude");
        }
        if (Math.abs(angle - Projection.HALF_PI) < 1e-15) {
            return Projection.HALF_PI;
        }
        if (Math.abs(angle + Projection.HALF_PI) < 1e-15) {
            return -Projection.HALF_PI;
        }   
        if(angle > Projection.HALF_PI) {
            angle = Projection.HALF_PI;
        }
        
        if(angle < -Projection.HALF_PI) {
            angle = -Projection.HALF_PI;
        }        

        return angle;
//		return Math.IEEEremainder(angle, Math.PI);
    }

    /**
     * normalize longitude angle in radians
     * @param angle
     * @return
     */
    public static double normalizeLongitude(double angle) {
        if (Double.isInfinite(angle) || Double.isNaN(angle)) {
            throw new JWcsError("Infinite longitude");
        }

        // avoid instable computations with very small numbers: if the
        // angle is very close to the graticule boundary, return +/-PI.
        // Bernhard Jenny, May 25 2010.
        if (Math.abs(angle - 0) < 1e-15) {
            return 0;
        }
        if (Math.abs(angle - Projection.TWO_PI) < 1e-15) {
            return Projection.TWO_PI;
        }

        while (angle > Projection.TWO_PI) {
            angle -= Projection.TWO_PI;
        }
        while (angle < 0) {
            angle += Projection.TWO_PI;
        }
        return angle;
    }
    
    public static double normalizeLongitudeD(double angle) {
        if (Double.isInfinite(angle) || Double.isNaN(angle)) {
            throw new JWcsError("Infinite longitude");
        }

        if (Math.abs(angle%360 - 0) < 1e-15) {
            return 0;
        }

        return angle%360;
    }    
//
//    public static double normalizeAngle(double angle) {
//        if (Double.isInfinite(angle) || Double.isNaN(angle)) {
//            throw new ProjectionException("Infinite angle");
//        }
//        while (angle > TWOPI) {
//            angle -= TWOPI;
//        }
//        while (angle < 0) {
//            angle += TWOPI;
//        }
//        return angle;
//    }
    
    
}
