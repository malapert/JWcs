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
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;

/**
 * Zenithal perspective.
 *
 * <p>
 * Zenithal (azimuthal) perspective projections are generated from a point and
 * carried through the sphere to the plane of projection.
 *
 * @see <a href="http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf">
 * "Representations of celestial coordinates in FITS"</a>, M. R. Calabretta and
 * E. W. Greisen - page 10
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class AZP extends ZenithalProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Zenithal perspective";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "\u03BC=%s \u0263=%s";

    private double gamma;
    private double mu;
    private static final double DEFAULT_VALUE = 0;

    /**
     * Creates a new AZC projection based on the celestial longitude and
     * latitude of the fiducial point (crval1, crval2) and mu and gamma.
     *
     * @param crval1 celestial longitude of the fiducial point in degrees
     * @param crval2 celestial latitude of the fiducial point in degrees
     */
    public AZP(double crval1, double crval2) {
        this(crval1, crval2, DEFAULT_VALUE, DEFAULT_VALUE);
    }

    /**
     * Creates a new AZC projection based on the celestial longitude and
     * latitude of the fiducial point (crval1, crval2) and mu and gamma.
     *
     * @param crval1 celestial longitude of the fiducial point in degrees
     * @param crval2 celestial latitude of the fiducial point in degrees
     * @param gamma PV<code>nbAxis</code>_2 in degrees
     * @param mu PV<code>nbAxis</code>_1 in radians
     */
    public AZP(double crval1, double crval2, double mu, double gamma) {
        super(crval1, crval2);
        this.gamma = Math.toRadians(gamma);
        this.mu = mu;
    }

    /**
     * Computes the native spherical coordinates from the projection plane
     * coordinates.
     *
     * @param x projection plane coordinate along X
     * @param y projection plane coordinate along Y
     * @return the native spherical coordinates in radians
     * @throws
     * io.github.malapert.jwcs.proj.exception.BadProjectionParameterException
     * when a projection parameter is wrong
     * @throws
     * io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException
     * When the pixel is beyond the visible projection
     */
    @Override
    public double[] project(double x, double y) throws BadProjectionParameterException, PixelBeyondProjectionException {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double r = Math.sqrt(xr * xr + yr * yr * Math.cos(gamma) * Math.cos(gamma));
        double phi, theta;
        if (NumericalUtils.equal(r, 0, DOUBLE_TOLERANCE)) {
            phi = 0;
        } else {
            phi = NumericalUtils.aatan2(xr, -yr * Math.cos(gamma));
        }

        double c = (mu + 1) + yr * Math.sin(gamma);
        if (c == 0) {
            throw new BadProjectionParameterException("mu = " + mu + " , gamma = " + gamma);
        }
        double rhau = r / c;
        double s = rhau * mu / Math.sqrt(rhau * rhau + 1);
        if (Math.abs(s) > 1 + DOUBLE_TOLERANCE) {
            throw new PixelBeyondProjectionException("AZP: (x,y) = (" + x
                    + ", " + y + ")");
        } else if (Math.abs(s) > 1) {
            double tmp = (s < 0.0) ? -Math.abs(HALF_PI) : Math.abs(HALF_PI);
            theta = NumericalUtils.aatan2(1.0, rhau) - tmp;
        } else {
            theta = NumericalUtils.aatan2(1.0, rhau) - NumericalUtils.aasin(s);
        }
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
     * @throws
     * io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException
     * When the pixel is beyond the visible projection
     */
    @Override
    public double[] projectInverse(double phi, double theta) throws PixelBeyondProjectionException {
//        double thetax;
//        if (NumericalUtils.equal(mu, 0, DOUBLE_TOLERANCE)) {
//            thetax = 0;
//        } else if (Math.abs(mu) > 1) {
//            thetax = Math.asin(-1 / mu);
//        } else {
//            thetax = Math.asin(-mu);
//        }
        phi = phiRange(phi);

        double denom;
        if (NumericalUtils.equal(gamma, 0, DOUBLE_TOLERANCE)) {
            denom = mu + Math.sin(theta);
        } else {
            denom = mu + Math.sin(theta) + Math.cos(theta) * Math.cos(phi) * Math.tan(gamma);
        }

//        if (NumericalUtils.equal(denom, 0, DOUBLE_TOLERANCE) || theta < thetax) {
//            throw new PixelBeyondProjectionException("AZP: theta = " + theta);
//        }
        if (NumericalUtils.equal(denom, 0, DOUBLE_TOLERANCE)) {
            throw new PixelBeyondProjectionException("AZP: theta = " + theta);
        }

        double r = (mu + 1) * Math.cos(theta) / denom;
        r = Math.toDegrees(r);
        double x = r * Math.sin(phi);
        double y = -r * Math.cos(phi) / Math.cos(gamma);
        double[] pos = {x, y};
        return pos;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }
    
    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, this.mu, Math.toDegrees(this.gamma));
    }
}
