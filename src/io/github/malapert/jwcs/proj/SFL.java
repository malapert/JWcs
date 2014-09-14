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
package io.github.malapert.jwcs.proj;

/**
 * Sanson-Flamsteed.
 * 
 * <p>
 * Bonne's projection reduces to the pseudocylindrical Sanson-Flamsteed16 
 * projection when theta1 = 0. Parallels are equispaced and projected at 
 * their true length which makes it an equal area projection.
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class SFL extends CylindricalProjection {

    /**
     * Creates an instance.
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     */
    public SFL(double crval1, double crval2) {
        super(crval1, crval2);
    }

    @Override
    protected double[] project(double x, double y) {        
        double theta = Math.toRadians(y);
        double cosTheta = Math.cos(theta);
        double phi;
        if(cosTheta == 0) {
            phi = 0;
        } else {
            phi = Math.toRadians(x) / Math.cos(theta);
        }
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    protected double[] projectInverse(double phi, double theta) {
        phi = phiRange(phi);
        double x = Math.toDegrees(phi * Math.cos(theta));
        double y = Math.toDegrees(theta);
        double[] coord = {x, y};
        return coord;        
    }
}
