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
 * Conic orthomorphic.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class COO extends ConicProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = " Conic orthomorphic";
    
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
    public COO(double crval1, double crval2, double theta_a, double eta) {
        super(crval1, crval2, theta_a, eta);
    }

    /**
     * Computes the native spherical coordinates from the projection plane
     * coordinates.
     *
     *
     * @param x projection plane coordinate along X
     * @param y projection plane coordinate along Y
     * @return the native spherical coordinates in radians
     * @throws
     * io.github.malapert.jwcs.proj.exception.BadProjectionParameterException when a projection parameter is wrong
     */
    @Override
    protected double[] project(double x, double y) throws BadProjectionParameterException {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double theta1 = getTheta_a() - Math.abs(getEta());
        double theta2 = getTheta_a() + Math.abs(getEta());
        double tan1 = Math.tan((HALF_PI - theta1) * 0.5);
        double tan2 = Math.tan((HALF_PI - theta2) * 0.5);
        double c = (NumericalUtils.equal(theta1,theta2,DOUBLE_TOLERANCE)) ? Math.sin(theta1) : Math.log(Math.cos(theta2) / Math.cos(theta1)) / Math.log(tan2 / tan1);
        if (NumericalUtils.equal(c,0,DOUBLE_TOLERANCE)) {
            throw new BadProjectionParameterException("Projection parameters: sin(theta1) + sin(theta2) = 0");
        }
        double psi = (NumericalUtils.equal(tan1,0, DOUBLE_TOLERANCE)) ? Math.cos(theta2) / (c * Math.pow(tan2, c)) : Math.cos(theta1) / (c * Math.pow(tan1, c));
        double y0 = psi * Math.pow(Math.tan((HALF_PI - getTheta_a()) * 0.5), c);
        int sign = (getTheta_a() < 0) ? -1 : 1;
        double r_theta = sign * Math.sqrt(Math.pow(xr, 2) + Math.pow(y0 - yr, 2));
        double phi = NumericalUtils.aatan2(xr / r_theta, (y0 - yr) / r_theta) / c;
        double theta = HALF_PI - 2 * Math.atan(Math.pow(r_theta / psi, 1.0 / c));
        double[] pos = {phi, theta};
        return pos;
    }

    /**
     * Computes the projection plane coordinates from the native spherical
     * coordinates.
     *
     * @param phi native spherical coordinate in radians along longitude
     * @param theta native spherical coordinate in radians along latitude
     * @return the projection plane coordinates
     * @throws io.github.malapert.jwcs.proj.exception.BadProjectionParameterException when a projection parameter is wrong
     */
    @Override
    protected double[] projectInverse(double phi, double theta) throws BadProjectionParameterException {
        double theta1 = getTheta_a() - Math.abs(getEta());
        double theta2 = getTheta_a() + Math.abs(getEta());
        double tan1 = Math.tan((HALF_PI - theta1) * 0.5);
        double tan2 = Math.tan((HALF_PI - theta2) * 0.5);
        double c = (NumericalUtils.equal(theta1,theta2,DOUBLE_TOLERANCE)) ? Math.sin(theta1) : Math.log(Math.cos(theta2) / Math.cos(theta1)) / Math.log(tan2 / tan1);
        double psi = (NumericalUtils.equal(tan1,0,DOUBLE_TOLERANCE)) ? Math.cos(theta2) / (c * Math.pow(tan2, c)) : Math.cos(theta1) / (c * Math.pow(tan1, c));
        if (NumericalUtils.equal(psi,0,DOUBLE_TOLERANCE)) {
            throw new BadProjectionParameterException(
                    "Projection parameters: theta_a, eta = " + getTheta_a() + ", " + getEta());
        }
        double y0 = psi * Math.pow(Math.tan((HALF_PI - getTheta_a()) * 0.5), c);
        phi = phiRange(phi);
        double r_theta = psi * Math.pow(Math.tan((HALF_PI - theta) * 0.5), c);
        double x = Math.toDegrees(r_theta * Math.sin(c * phi));
        double y = Math.toDegrees(-r_theta * Math.cos(c * phi) + y0);
        double[] coord = {x, y};
        return coord;
    }
    
    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, this.getTheta_a(), this.getEta());
    }

}
