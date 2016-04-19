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
 * @version 2.0
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

    /**
     * \u0263 is the angle between the camera's optical axis and the line to the center of the planet.
     */
    private double gamma;
    /**
     * \u03BC is the distance from the center of the sphere to the source of projection.
     * \u03BC increases in the direction away from the plane of projection.
     */
    private double mu;
    /**
     * Default value for \u0263 and \u03BC.
     */
    private static final double DEFAULT_VALUE = 0;

    /**
     * Creates a new AZC projection based on the celestial longitude and
     * latitude of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * \u03BC and \u0263 are set to {@link AZP#DEFAULT_VALUE}.
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the fiducial point
     */
    public AZP(double crval1, double crval2) {
        this(crval1, crval2, DEFAULT_VALUE, DEFAULT_VALUE);
    }

    /**
     * Creates a new AZP projection based on the celestial longitude and
     * latitude of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and \u03BC and \u0263.
     * 
     * \u03BC is set by the FITS keyword PV<code>nbAxis</code>_2 in degrees. 
     * \u0263 is set by the FITS keyword PV<code>nbAxis</code>_1 in radii.
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the fiducial point
     * @param gamma \u03BC Angle in degrees between the camera's optical axis and the line to the center of the planet
     * @param mu \u0263 Distance in radii from the center of the sphere to the source of projectionPV<code>nbAxis</code>_1 in radians
     */
    public AZP(double crval1, double crval2, double mu, double gamma) {
        super(crval1, crval2);
        this.gamma = Math.toRadians(gamma);
        this.mu = mu;
    }

    @Override
    public double[] project(double x, double y) throws BadProjectionParameterException, PixelBeyondProjectionException {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double r = Math.sqrt(Math.pow(xr,2) + Math.pow(yr, 2) * Math.pow(Math.cos(gamma),2));
        double phi, theta;
        if (NumericalUtils.equal(r, 0, DOUBLE_TOLERANCE)) {
            phi = 0;
        } else {
            phi = NumericalUtils.aatan2(xr, -yr * Math.cos(gamma));
        }       
        
        double c = (mu + 1) + yr * Math.sin(gamma);
        if (NumericalUtils.equal(c,0,DOUBLE_TOLERANCE)) {
            throw new BadProjectionParameterException("AZP: Bad projection parameter for (mu,gamma): " + mu + ", " + gamma);
        }
        double rhau = r / c;
        double s = rhau * mu / Math.sqrt(rhau * rhau + 1);
        if (Math.abs(s) > 1 + DOUBLE_TOLERANCE) {
            throw new PixelBeyondProjectionException("AZP: Solution not defined for (x,y) = (" + x
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

    @Override
    public double[] projectInverse(double phi, double theta) throws PixelBeyondProjectionException {
        double thetax;
        if (NumericalUtils.equal(mu, 0, DOUBLE_TOLERANCE)) {
            thetax = 0;
        } else if (Math.abs(mu) > 1) {
            thetax = NumericalUtils.aasin(-1 / mu);
        } else {
            thetax = NumericalUtils.aasin(-mu);
        }
        phi = phiRange(phi);

        double denom;
        if (NumericalUtils.equal(gamma, 0, DOUBLE_TOLERANCE)) {
            denom = mu + Math.sin(theta);
        } else {
            denom = mu + Math.sin(theta) + Math.cos(theta) * Math.cos(phi) * Math.tan(gamma);
        }

        if (NumericalUtils.equal(denom, 0, DOUBLE_TOLERANCE) || theta < thetax) {
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
        return String.format(DESCRIPTION, this.mu, NumericalUtils.round(Math.toDegrees(this.gamma)));
    }
}
