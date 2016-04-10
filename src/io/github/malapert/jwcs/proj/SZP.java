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
 * Slant zenithal perspective.
 *
 * <p>
 * While the generalization of the AZP projection to tilted planes of projection
 * is useful for certain applications it does have a number of drawbacks, in
 * particular, unequal scaling at the reference point.
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class SZP extends ZenithalProjection {
    
    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Slant zenithal perspective";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "\u03BC=%s \u03C6c=%s \u03B8c=%s";      

    private static final double NO_VALUE = 101;

    private final double mu;
    private final double thetac;
    private final double phic;

    /**
     * Default value for mu0
     */
    public static final double DEFAULT_VALUE_MU = 0;

    /**
     * Default value for PHIC
     */
    public static final double DEFAULT_VALUE_PHIC = 0;

    /**
     * Default value for THETAC
     */
    public static final double DEFAULT_VALUE_THETAC = 90;

    /**
     * Create an instance.
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     */
    public SZP(double crval1, double crval2) {
        super(crval1, crval2);
        this.mu = DEFAULT_VALUE_MU;
        this.thetac = Math.toRadians(DEFAULT_VALUE_THETAC);
        this.phic = Math.toRadians(DEFAULT_VALUE_PHIC);
    }

    /**
     * Creates an instance.
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     * @param mu Parameter projection
     * @param phic Parameter projection
     * @param thetac Parameter projection
     */
    public SZP(double crval1, double crval2, double mu, double phic, double thetac) {
        super(crval1, crval2);
        this.mu = mu;
        this.thetac = Math.toRadians(thetac);
        this.phic = Math.toRadians(phic);
        check();        
    }

    /**
     * Check.
     */
    protected final void check() {
        if ((getPhi0() != 0) || (getTheta0() != HALF_PI)) {
            throw new IllegalArgumentException("Non-standard PVi_1 and/or PVi_2 values");
        }
    }

    @Override
    public double[] project(double x, double y) throws PixelBeyondProjectionException {

        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);

        double xp = -mu * Math.cos(thetac) * Math.sin(phic);
        double yp = mu * Math.cos(thetac) * Math.cos(phic);
        double zp = mu * Math.sin(thetac) + 1;

        double X = xr;
        double Y = yr;
        double X1 = (X - xp) / zp;
        double Y1 = (Y - yp) / zp;
        double a = X1 * X1 + Y1 * Y1 + 1;
        double b = X1 * (X - X1) + Y1 * (Y - Y1);
        double c = (X - X1) * (X - X1) + (Y - Y1) * (Y - Y1) - 1;
        double sol1 = (-b - Math.sqrt(b * b - a * c)) / a;
        double sol2 = (-b + Math.sqrt(b * b - a * c)) / a;
        double theta1 = NO_VALUE, theta2 = NO_VALUE;        
        if (Math.abs(sol1) < 1 + DOUBLE_TOLERANCE) {
            if (NumericalUtils.equal(sol1, 1, DOUBLE_TOLERANCE)) {
                theta1 = HALF_PI;
            } else if(NumericalUtils.equal(sol1, -1, DOUBLE_TOLERANCE)){
                theta1 = -HALF_PI;
            } else {
                theta1 = NumericalUtils.aasin(sol1);
            }
        }
        if (Math.abs(sol2) < 1 + DOUBLE_TOLERANCE) {
            if (NumericalUtils.equal(sol1, 1, DOUBLE_TOLERANCE)) {
                theta2 = HALF_PI;
            } else if(NumericalUtils.equal(sol1, -1, DOUBLE_TOLERANCE)){
                theta2 = -HALF_PI;
            } else {            
                theta2 = NumericalUtils.aasin(sol2);
            }
        }
        double theta;
        if (theta1 > NO_VALUE - 1 && theta2 > NO_VALUE - 1) {
            throw new PixelBeyondProjectionException("SZP: (x,y) = (" + x
                    + ", " + y + ")");
        } else if (theta1 > NO_VALUE - 1) {
            theta = theta2;
        } else if (theta2 > NO_VALUE - 1) {
            theta = theta1;
        } else {
            if (Math.abs(theta1 - HALF_PI) > Math.abs(theta2 - HALF_PI)) {
                theta = theta2;
            } else {
                theta = theta1;
            }
        }
        double phi = NumericalUtils.aatan2(X - X1 * (1 - Math.sin(theta)), -(Y - Y1 * (1 - Math.sin(theta))));
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    public double[] projectInverse(double phi, double theta) throws PixelBeyondProjectionException {
        phi = phiRange(phi);
        double xp = -mu * Math.cos(thetac) * Math.sin(phic);
        double yp = mu * Math.cos(thetac) * Math.cos(phic);
        double zp = mu * Math.sin(thetac) + 1;
        double denom = zp - (1 - Math.sin(theta));
        if (NumericalUtils.equal(zp, 0, DOUBLE_TOLERANCE)) {
            throw new PixelBeyondProjectionException("SZP: theta = " + theta);
        }
        double x = (zp * Math.cos(theta) * Math.sin(phi) - xp * (1 - Math.sin(theta)))/denom;
        x = Math.toDegrees(x);
        double y = -(zp * Math.cos(theta) * Math.cos(phi) + yp * (1 - Math.sin(theta)))/denom;
        y = Math.toDegrees(y);
        double[] coord = {x, y};
        return coord;
    }  
    
    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, this.mu, Math.toDegrees(this.phic), Math.toDegrees(this.thetac));
    }

}
