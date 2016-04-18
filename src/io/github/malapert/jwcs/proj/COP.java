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

import io.github.malapert.jwcs.proj.exception.BadProjectionParameterException;
import io.github.malapert.jwcs.utility.NumericalUtils;

/**
 * Conic perspective.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class COP extends ConicProjection {
    
    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Conic perspective";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "\u03B8a=%s \u03B7=%s";    

    /**
     * Creates an instance.
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     * @param theta_a (theta1 + theta2) / 2 in degrees
     * @param eta abs(theta1 - theta2) / 2 in degrees
     */
    public COP(double crval1, double crval2, double theta_a, double eta)  {
        super(crval1, crval2, theta_a, eta);
    }

    @Override
    protected double[] project(double x, double y) throws BadProjectionParameterException {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double c = Math.sin(getTheta_a());
        if (NumericalUtils.equal(c, 0, DOUBLE_TOLERANCE)) {
            throw new BadProjectionParameterException("Bad value for theta_a: " + getTheta_a());
        }
        double d = Math.cos(getEta());
        if (NumericalUtils.equal(d, 0, DOUBLE_TOLERANCE)) {
            throw new BadProjectionParameterException("Bad value for eta: " + getEta());
        }
        double y0 = d / Math.tan(getTheta_a());       
        double r_theta = Math.signum(getTheta_a()) * Math.sqrt(Math.pow(xr, 2) + Math.pow(y0 - yr, 2));
        double phi;
        if(NumericalUtils.equal(r_theta, 0, DOUBLE_TOLERANCE)) {
            phi = 0;
        } else {       
            phi = NumericalUtils.aatan2(xr / r_theta, (y0 - yr) / r_theta) / c;              
        }
        double theta = getTheta_a() + Math.atan(1.0/Math.tan(getTheta_a())-r_theta/Math.cos(getEta()));
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    protected double[] projectInverse(double phi, double theta) throws BadProjectionParameterException {
    
        phi = phiRange(phi);
        double c = Math.sin(getTheta_a());
        if (NumericalUtils.equal(c, 0, DOUBLE_TOLERANCE)) {
            throw new BadProjectionParameterException("Bad value for theta_a: " + getTheta_a());
        }
        double a = c*phi;
        double y0 = Math.cos(getEta()) / Math.tan(getTheta_a());      
        double r_theta = y0 - Math.cos(getEta())*Math.tan(theta-getTheta_a());
        if(r_theta < 0) {
            throw new BadProjectionParameterException("r_theta cannot be inferior to 0");
        }
        double x = Math.toDegrees(r_theta *Math.sin(a));
        double y = Math.toDegrees(y0 - r_theta*Math.cos(a));

        double[] coord = {x, y};
        return coord;
    }      

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtils.round(Math.toDegrees(this.getTheta_a())), NumericalUtils.round(Math.toDegrees(this.getEta())));
    }
        

    @Override
    public boolean inside(double lon, double lat) {
        return super.inside(lon, lat);
    }

}
