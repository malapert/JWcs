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

import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;

/**
 * Gnomonic.
 *
 * <p>
 * The zenithal perspective projection with mu = 0, the gnomonic projection, is
 * widely used in optical astronomy and was given its own code within the AIPS
 * convention, namely TAN.
 *
 * Ref : http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf page 12
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class TAN extends ZenithalProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Gnomonic";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";     
    
    /**
     * Tolerance for numerical precision.
     */
    protected final static double TOLERANCE = 1.0e-13;

    /**
     * Creates an instance
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     */
    public TAN(double crval1, double crval2) {
        super(crval1, crval2);
    }

    @Override
    public double[] project(double x, double y) throws PixelBeyondProjectionException {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double r_theta = Math.sqrt(xr * xr + yr * yr);
        double phi;
        if (NumericalUtils.equal(r_theta, 0.0, DOUBLE_TOLERANCE)) {
            phi = 0;
        } else {
            phi = NumericalUtils.aatan2(xr, -yr);
        }
        double theta = Math.atan2(1.0d, r_theta);
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    public double[] projectInverse(double phi, double theta) throws PixelBeyondProjectionException {        
        phi = phiRange(phi);
        double s = Math.sin(theta);
        if (NumericalUtils.equal(s, 0, 1e-13)) {
            throw new PixelBeyondProjectionException("TAN: theta = " + theta);
        }
        double r;
        if (theta <= 0+DOUBLE_TOLERANCE) {
            r = theta;
        } else {
            r = 1 / Math.tan(theta);//Math.cos(theta)/s;
        }
        double x = Math.toDegrees(r * Math.sin(phi));
        double y = Math.toDegrees(-r * Math.cos(phi));
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
    
    @Override
    public boolean inside(double lon, double lat) {  
       return super.inside(lon, lat) && !NumericalUtils.equal(Math.abs(lat), 0, DOUBLE_TOLERANCE);
    }     
}
