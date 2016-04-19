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

import io.github.malapert.jwcs.proj.exception.BadProjectionParameterException;
import io.github.malapert.jwcs.utility.NumericalUtils;

/**
 * Conic orthomorphic.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
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
     * Constructs a COO projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and 03B8<sub>a</sub> and \u03B7.
     *
     * \u03B8<sub>a</sub> is set by the FITS keyword PV<code>nbAxis</code>_1 in degrees.
     * \u03B7 is set by the FITS keyword PV<code>nbAxis</code>_2 in degrees.
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @param theta_a \u03B8<sub>a</sub> in degrees and defined as \u03B8<sub>a</sub>=(\u03B8<sub>1</sub>+\u03B8<sub>2</sub>)/2
     * @param eta \u03B7 in degrees and defined as \u03B7=|\u03B8<sub>1</sub>-\u03B8<sub>2</sub>|/2
     */
    public COO(double crval1, double crval2, double theta_a, double eta) {
        super(crval1, crval2, theta_a, eta);
    }

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
            throw new BadProjectionParameterException("COO : Projection parameters: sin(theta1) + sin(theta2) = 0");
        }
        double psi = (NumericalUtils.equal(tan1,0, DOUBLE_TOLERANCE)) ? Math.cos(theta2) / (c * Math.pow(tan2, c)) : Math.cos(theta1) / (c * Math.pow(tan1, c));
        double y0 = psi * Math.pow(Math.tan((HALF_PI - getTheta_a()) * 0.5), c);
        double r_theta = Math.signum(getTheta_a()) * Math.sqrt(Math.pow(xr, 2) + Math.pow(y0 - yr, 2));
        double phi;
        if (NumericalUtils.equal(r_theta, 0, DOUBLE_TOLERANCE)) {
            phi = 0;
        } else {
            phi = NumericalUtils.aatan2(xr / r_theta, (y0 - yr) / r_theta) / c;
        }             
        double theta = HALF_PI - 2 * Math.atan(Math.pow(r_theta / psi, 1.0 / c));
        double[] pos = {phi, theta};
        return pos;
    }

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
                    "COO : Projection parameters: theta_a, eta = " + getTheta_a() + ", " + getEta());
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
        return String.format(DESCRIPTION, NumericalUtils.round(Math.toDegrees(this.getTheta_a())), NumericalUtils.round(Math.toDegrees(this.getEta())));
    }
        
    @Override
    public boolean inside(double lon, double lat) {
        return super.inside(lon, lat) && !NumericalUtils.equal(lat, -HALF_PI, DOUBLE_TOLERANCE);
    }

}
