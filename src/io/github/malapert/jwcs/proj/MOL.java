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

import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import static io.github.malapert.jwcs.utility.NumericalUtils.HALF_PI;
import java.util.logging.Level;

/**
 * Mollweide's.
 *
 * <p>
 * In Mollweide's pseudocylindrical projection17, the meridians are projected as
 * ellipses that correctly divide the equator and the parallels are spaced so as
 * to make the projection equal area
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class MOL extends CylindricalProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Mollweide’s";

    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";

    /**
     * Default tolerance for the iterative solution.
     */
    public static final double DEFAULT_TOLERANCE = 1E-15;

    /**
     * Default maximum iteration for the iterative solution.
     */
    public static final double DEFAULT_MAX_ITER = 100;

    /**
     * Tolerance for the iterative solution.
     */
    private double tolerance;
    /**
     * Maximum iteration for the iterative solution.
     */
    private double maxIter;

    /**
     * Constructs a MOL projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     */
    public MOL(final double crval1, final double crval2) {
        super(crval1, crval2);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2)=({0},{1})", new Object[]{crval1, crval2});
        setMaxIter(DEFAULT_MAX_ITER);
        setTolerance(DEFAULT_TOLERANCE);
    }

    @Override
    protected double[] project(final double x, final double y) throws PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x, y});
        final double xr = Math.toRadians(x);
        final double yr = Math.toRadians(y);
        final double tol = 1.0e-12;
        final double[] phis = computePhiAndS(xr, yr, tol);
        final double phi = phis[0];
        final double s = phis[1];
        final double theta = computeTheta(xr, yr, s, tol);
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi), Math.toDegrees(theta)});
        return pos;
    }

    /**
     * Computes the native spherical coordinate (\u03B8) in radians along
     * latitude
     *
     * @param xr projection plane coordinate along X in radians
     * @param yr projection plane coordinate along Y in radians
     * @param s s
     * @param tol tolerance for comparing double
     * @return the native spherical coordinate (\u03B8) in radians along
     * latitude
     * @throws PixelBeyondProjectionException Solution not defined
     */
    private double computeTheta(final double xr, final double yr, final double s, final double tol) throws PixelBeyondProjectionException {
        double z = yr / Math.sqrt(2);
        if (NumericalUtils.equal(Math.abs(z), 1)) {
            z = (z < 0.0 ? -1.0 : 1.0) + s * yr / Math.PI;
        } else if (Math.abs(1) > 1) {
            throw new PixelBeyondProjectionException(this, "MOL: Solution not defined for y: " + Math.toDegrees(yr));
        } else {
            z = NumericalUtils.aasin(z) / HALF_PI + s * yr / Math.PI;
        }
        if (NumericalUtils.equal(Math.abs(z), 1)) {
            z = z < 0.0 ? -1.0 : 1.0;
        } else if (Math.abs(1) > 1) {
            throw new PixelBeyondProjectionException(this, "MOL: Solution not defined for x,y: " + Math.toDegrees(xr) + ", " + Math.toDegrees(yr));
        }
        final double theta = NumericalUtils.aasin(z);
        if (Double.isNaN(theta)) {
            throw new PixelBeyondProjectionException(this, "(x,y)=(" + Math.toDegrees(xr) + "," + Math.toDegrees(yr) + ")");
        }
        return theta;
    }

    /**
     * Computes the native spherical coordinate (\u03D5) in radians along
     * longitude and s.
     *
     * @param xr projection plane coordinate along X in radians
     * @param yr projection plane coordinate along Y in radians
     * @param tol tolerance to compare a double
     * @return the native spherical coordinate (\u03D5) in radians along
     * longitude and s
     * @throws PixelBeyondProjectionException Solution not defined
     */
    private double[] computePhiAndS(final double xr, final double yr, final double tol) throws PixelBeyondProjectionException {
        double s = 2 - Math.pow(yr, 2);
        final double phi;
        if (s <= tol) {
            if (s < -tol) {
                throw new PixelBeyondProjectionException(this,
                        "MOL: Solution not defined for y: " + Math.toDegrees(yr));
            }
            s = 0.0;
            if (Math.abs(xr) > tol) {
                throw new PixelBeyondProjectionException(this, "MOL: Solution not defined for x: " + Math.toDegrees(xr));
            }
            phi = 0;
        } else {
            s = Math.sqrt(s);
            phi = HALF_PI * xr / s;
        }
        return new double[]{phi, s};
    }

    @Override
    protected double[] projectInverse(final double phi, final double theta) {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi), Math.toDegrees(theta)});
        final double gamma = computeGamma(theta);
        final double x = Math.toDegrees((Math.sqrt(2.0d) / HALF_PI) * phi * Math.cos(gamma));
        final double y = Math.toDegrees(Math.sqrt(2.0d) * Math.sin(gamma));
        final double[] coord = {x, y};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{x, y});
        return coord;
    }

    /**
     * Computes gamma by an iterative solution. Solves v - PI*sin(theta) +
     * sin(v) = 0 with v = 2*gamma
     *
     * @param theta the native spherical coordinate (\u03B8) in radians along
     * latitude
     * @return gamma
     */
    private double computeGamma(final double theta) {
        final double u = Math.PI * Math.sin(theta);
        double v0 = -Math.PI;
        double v1 = Math.PI;
        double v = u;
        int nIter = 0;
        double diff;
        do {
            nIter++;
            if (nIter != 1) {
                v = (v0 + v1) / 2.0;
            }
            diff = (v - u) + Math.sin(v);
            if (diff < 0.0) {
                v0 = v;
            } else {
                v1 = v;
            }
        } while (Math.abs(diff) > getTolerance() && nIter < getMaxIter());
        return v * 0.5;
    }

    /**
     * Returns the tolerance of the approximative solution of the inverse
     * projection.
     *
     * @return the tolerance
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     * Sets the tolerance of the approximative solution.
     *
     * @param tolerance the tolerance to set
     */
    public final void setTolerance(final double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * Returns the number maximal of iterations of the approximative solution of
     * the inverse projection.
     *
     * @return the maxIter
     */
    public double getMaxIter() {
        return maxIter;
    }

    /**
     * Sets the number maximal of iterations.
     *
     * @param maxIter the maxIter to set
     */
    public final void setMaxIter(final double maxIter) {
        this.maxIter = maxIter;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
