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
 * Stereographic.
 * 
 * <p>
 * The stereographic projection has the amazing property
 * that it maps all circles on the sphere to circles in the plane of
 * projection, although concentric circles on the sphere are not
 * necessarily concentric in the plane of projection. 
 * This property made it the projection of choice for Arab astronomers in
 * constructing astrolabes. In more recent times it has been used
 * by the Astrogeology Center for maps of the Moon, Mars, and
 * Mercury containing craters, basins, and other circular features.
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class STG extends ZenithalProjection {

    /**
     * Creates an instance
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     */
    public STG(double crval1, double crval2) {
        super(crval1, crval2);
    }

    @Override
    public double[] project(double x, double y) {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double r_theta = Math.sqrt(xr * xr + yr * yr);
        double phi = Math.atan2(xr, -yr);        
        double theta = Math.PI / 2 - 2 * Math.atan(r_theta * 0.5);       
        double[] pos = {phi, theta};
        return pos;       
    }

    @Override
    public double[] projectInverse(double phi, double theta) {
        if (phi > Math.PI) {
            phi -= 2*Math.PI;
        } else if (phi < Math.PI) {
            phi += 2*Math.PI;
        }         
        double r = 360 / Math.PI * Math.tan((Math.toRadians(90)-theta)/2.0d);
        double x = r * Math.sin(phi);
        double y = -r * Math.cos(phi);
        double[] pos = {x,y};
        return pos;
    }

}
