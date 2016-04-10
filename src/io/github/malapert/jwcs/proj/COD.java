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
 * Conic Equidistant.
 *
 * <p>
 * In the conic equidistant projection the standard parallels are projected at
 * their true length and at their true separation. The other parallels are then
 * drawn as concentric arcs spaced at their true distance from the standard
 * parallels.
 *
 * Reference: "Representations of celestial coordinates in FITS", M. R.
 * Calabretta and E. W. Greisen - page 20
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class COD extends ConicProjection {
    
    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Conic equidistant";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "\u03B8a=%s \u03B7=%s";     

    /**
     * Constructs a COD projection based on the celestial longitude and latitude
     * of the fiducial point (crval1, crval2) and theta_a and eta.
     *
     * @param crval1 celestial longitude in degrees
     * @param crval2 celestial latitude in degrees
     * @param theta_a PV<code>nbAxis</code>_1 in degrees
     * @param eta PV<code>nbAxis</code>_2 in degrees
     */
    public COD(double crval1, double crval2, double theta_a, double eta) {
        super(crval1, crval2, theta_a, eta);
    }

    /**
     * Computes the native spherical coordinates from the projection plane
     * coordinates.
     *
     * @param x projection plane coordinate along X
     * @param y projection plane coordinate along Y
     * @return the native spherical coordinates in radians
     * @throws io.github.malapert.jwcs.proj.exception.BadProjectionParameterException when the projection parameter is wrong
     */
    @Override
    protected double[] project(double x, double y) throws BadProjectionParameterException {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double c = Math.sin(getTheta_a()) * Math.sin(getEta()) / getEta();
        double y0 = getEta() / (Math.tan(getEta()) * Math.tan(getTheta_a()));
        int sign = (getTheta_a() < 0) ? -1 : 1;
        double r_theta = sign * Math.sqrt(Math.pow(xr, 2) + Math.pow(y0 - yr, 2));
        if (NumericalUtils.equal(r_theta, 0, DOUBLE_TOLERANCE)) {
            throw new BadProjectionParameterException("Bad value for sigma: " + this.getEta());
        }
        double phi = NumericalUtils.aatan2(xr / r_theta, (y0 - yr) / r_theta) / c;
        double theta = getTheta_a() + y0 - r_theta;
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    protected double[] projectInverse(double phi, double theta) {
        double c = Math.sin(getTheta_a()) * Math.sin(getEta()) / getEta();
        double y0 = getEta() / (Math.tan(getEta()) * Math.tan(getTheta_a()));
        phi = phiRange(phi);
        double r_theta = getTheta_a() + y0 - theta;
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
