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

import io.github.malapert.jwcs.utility.NumericalUtils;

/**
 * Parabolic.
 * 
 * <p>
 * The meridians are projected as parabolic arcs
 * which intersect the poles and correctly divide the equator, and
 * the parallels of latitude are spaced so as to make it an equal
 * area projection.
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class PAR extends CylindricalProjection {
    
    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Parabolic";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";    

    /**
     * Creates an instance.
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     */
    public PAR(double crval1, double crval2) {
        super(crval1, crval2);
    }

    @Override
    protected double[] project(double x, double y) {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double theta = 3 * NumericalUtils.aasin(yr / Math.PI);
        double phi = xr / (1 - 4*Math.pow(yr / Math.PI, 2));
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    protected double[] projectInverse(double phi, double theta) {        
        if (phi > Math.PI) {
            phi -= 2*Math.PI;
        } else if (phi < Math.PI) {
            phi += 2*Math.PI;
        }         
        double y = Math.PI * Math.toDegrees(Math.sin(theta / 3));
        double x = Math.toDegrees(phi * (2*Math.cos(2*theta/3) - 1));
        double[] coord = {x, y};        
        return coord;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

}
