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
package io.github.malapert.jwcs.proj;

import static io.github.malapert.jwcs.utility.NumericalUtils.HALF_PI;

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
 * @version 2.0
 */
public class STG extends ZenithalProjection {
    
    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Stereographic";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";    

   /**
     * Constructs a STG projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     */
    public STG(double crval1, double crval2) {
        super(crval1, crval2);
    }

    @Override
    public double[] project(double x, double y) {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double r_theta = computeRadius(xr, yr);
        double phi = computePhi(x, y, r_theta);        
        double theta = HALF_PI - 2 * Math.atan(r_theta * 0.5);       
        double[] pos = {phi, theta};
        return pos;       
    }

    @Override
    public double[] projectInverse(double phi, double theta) {
        phi = phiRange(phi);        
        double r = 2 * Math.tan((HALF_PI-theta)*0.5d);
        double x = r * Math.sin(phi);
        double y = -r * Math.cos(phi);
        double[] pos = {Math.toDegrees(x),Math.toDegrees(y)};
        return pos;
    } 
    
    @Override
    public String getName() {
        return NAME_PROJECTION;
    }
    
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }    

    @Override
    public ProjectionParameter[] getProjectionParameters() {
        return new ProjectionParameter[]{};
    }
}
