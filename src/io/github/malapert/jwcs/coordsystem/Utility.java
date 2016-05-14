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
package io.github.malapert.jwcs.coordsystem;

import io.github.malapert.jwcs.utility.NumericalUtils;
import static io.github.malapert.jwcs.utility.NumericalUtils.createRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * TimeUtils class for handling the rotation matrix.
 *
 * The methods in this class have been traduced from Python to JAVA.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 * @see <a href="http://www.astro.rug.nl/software/kapteyn/">The original code in
 * Python</a>
 */
public class Utility {

//In header:                           In wcs structure:
//
//EPOCH       EQUINOX     RADECSYS     epoch     equinox    radecsys
//1950        none        none         date-obs  1950       FK4
//2000        none        none         date-obs  2000       FK5
//date<1980   none        none         date-obs  date       FK4
//date>1980   none        none         date-obs  date       FK5
//none        none        none         date-obs  2000       FK5
//none        1950        none         date-obs  1950       FK4
//none        2000        none         date-obs  2000       FK5
//0           none        none         date-obs  1950       FK4
//0           1950        none         date-obs  1950       FK4
//0           2000        none         date-obs  2000       FK5    




    /**
     * Given two angles in longitude and latitude returns corresponding
     * Cartesian coordinates x,y,z.
     *
     * Notes: ------ The three coordinate axes x, y and z, the set of
     * right-handed Cartesian axes that correspond to the usual celestial
     * spherical coordinate system. The xy-plane is the equator, the z-axis
     * points toward the north celestial pole, and the x-axis points toward the
     * origin of right ascension.
     *
     * @param longitude longitude in decimal degree
     * @param latitude latitude in decimal degree
     * @return Corresponding values of x,y,z in same order as input
     */
    public final static RealMatrix longlat2xyz(double longitude, double latitude) {
        return Utility.longlatRad2xyz(Math.toRadians(longitude), Math.toRadians(latitude));
    }

    /**
     * Given two angles in longitude and latitude returns corresponding
     * Cartesian coordinates x,y,z.
     *
     * Notes: ------ The three coordinate axes x, y and z, the set of
     * right-handed Cartesian axes that correspond to the usual celestial
     * spherical coordinate system. The xy-plane is the equator, the z-axis
     * points toward the north celestial pole, and the x-axis points toward the
     * origin of right ascension.
     *
     * @param longitudeRad longitude in radians
     * @param latitudeRad latitude in radians
     * @return Corresponding values of x,y,z in same order as input
     */
    public final static RealMatrix longlatRad2xyz(double longitudeRad, double latitudeRad) {
        double x = Math.cos(longitudeRad) * Math.cos(latitudeRad);
        double y = Math.sin(longitudeRad) * Math.cos(latitudeRad);
        double z = Math.sin(latitudeRad);
        double[][] array = {
            {x},
            {y},
            {z}
        };
        return createRealMatrix(array);
    }

    /**
     * Given Cartesian x,y,z return corresponding longitude and latitude in
     * degrees.
     *
     * Notes: ------ Note that one can expect strange behavior for the values of
     * the longitudes very close to the pole. In fact, at the poles itself, the
     * longitudes are meaningless.
     *
     * @param xyz Vector with values for x,y,z
     * @return The same number of positions (longitude, latitude and in the same
     * order as the input.
     */
    public final static double[] xyz2longlat(final RealMatrix xyz) {
        double[] vec = xyz.getColumn(0);
        double len = Math.sqrt(Math.pow(vec[0], 2)+Math.pow(vec[1], 2)+Math.pow(vec[2], 2));
        double x = vec[0]/len;
        double y = vec[1]/len;
        double z = vec[2]/len;
        double longitude = Math.toDegrees(NumericalUtils.aatan2(y, x, 0));
        longitude = (longitude < 0) ? longitude + 360.0d : longitude;
        double latitude = Math.toDegrees(NumericalUtils.aasin(z));
        double coord[] = {longitude, latitude};
        return coord;
    }
}
