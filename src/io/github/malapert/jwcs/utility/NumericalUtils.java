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
        return Math.sqrt(pos[0]*pos[0]+pos[1]*pos[1]+pos[2]*pos[2]);
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
        return Math.acos(dot/(normVector(xyzPos1)*normVector(xyzPos2)));
    }
    
}
