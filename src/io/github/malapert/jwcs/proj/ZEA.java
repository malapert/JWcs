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

import io.github.malapert.jwcs.utility.NumericalUtils;

/**
 * Zenithal equal-area.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class ZEA extends ZenithalProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Zenithal equal-area";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";     
    
    /**
     * Tolerance for numerical precision.
     */
    private final static double TOLERANCE = 1.0e-13;

   /**
     * Constructs a ZEA projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     */
    public ZEA(double crval1, double crval2) {
        super(crval1, crval2);
    }

    @Override
    public double[] project(double x, double y) {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double r_theta = Math.sqrt(xr * xr + yr * yr);
        double phi;
        if (NumericalUtils.equal(r_theta, 0.0, TOLERANCE)) {
            phi = 0;
        } else {
            phi = NumericalUtils.aatan2(xr, -yr);
        }
        
        double theta;
	if (NumericalUtils.equal(r_theta, 2, DOUBLE_TOLERANCE)) {
	    theta = -HALF_PI;
	} else {
	    theta = HALF_PI - 2*NumericalUtils.aasin(r_theta * 0.5);
	}        
        double[] pos = {phi, theta};
        return pos;      
    }

    @Override
    public double[] projectInverse(double phi, double theta) {
        phi = phiRange(phi);
        double r = 2 * Math.sin((HALF_PI-theta)*0.5d);
        double x = Math.toDegrees(r * Math.sin(phi));
        double y = Math.toDegrees(-r * Math.cos(phi));
        double[] pos = {x,y};
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
    public ProjectionParameter[] getProjectionParameters(){
        return new ProjectionParameter[]{};
    }
}
