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
import io.github.malapert.jwcs.utility.GammaFunction;
import io.github.malapert.jwcs.utility.NumericalUtility;
import static io.github.malapert.jwcs.utility.NumericalUtility.HALF_PI;
import java.util.logging.Level;

/**
 * Mollweide's.
 *
 * <p>In Mollweide's pseudocylindrical projection17, the meridians are projected as
 * ellipses that correctly divide the equator and the parallels are spaced so as
 * to make the projection equal area
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class MOL extends AbstractCylindricalProjection {

    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = "Mollweide’s";

    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "no limits";

    /**
     * Default tolerance for the iterative solution.
     */
    public final static double DEFAULT_TOLERANCE = 1E-15;

    /**
     * Default maximum iteration for the iterative solution.
     */
    public final static int DEFAULT_MAX_ITER = 1000;

    /**
     * Tolerance for the iterative solution.
     */
    private double tolerance;
    /**
     * Maximum iteration for the iterative solution.
     */
    private int maxIter;
    
    /**
     * GammaFunction function to solve.
     */
    private final GammaFunction gammaFunction;

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
        this.gammaFunction = new GammaFunction();
    }

    @Override
    protected double[] project(final double x, final double y) throws PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x, y});
        final double xr = Math.toRadians(x);
        final double yr = Math.toRadians(y);        
        final double[] phis = computePhiAndS(xr, yr);
        final double phi = phis[0];
        final double s = phis[1];
        final double theta = computeTheta(xr, yr, s);
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi), Math.toDegrees(theta)});
        return pos;
    }

    /**
     * Computes the native spherical coordinate (\u03B8) in radians along
     * latitude.
     *
     * @param xr projection plane coordinate along X in radians
     * @param yr projection plane coordinate along Y in radians
     * @param s s
     * @return the native spherical coordinate (\u03B8) in radians along
     * latitude
     * @throws PixelBeyondProjectionException Solution not defined
     */
    private double computeTheta(final double xr, final double yr, final double s) throws PixelBeyondProjectionException {
        double z = yr / Math.sqrt(2);
        if (NumericalUtility.equal(Math.abs(z), 1)) {
            z = (z < 0.0 ? -1.0 : 1.0) + s * yr / Math.PI;
        } else if (Math.abs(1) > 1) {
            throw new PixelBeyondProjectionException(this, "MOL: Solution not defined for y: " + Math.toDegrees(yr));
        } else {
            z = NumericalUtility.aasin(z) / HALF_PI + s * yr / Math.PI;
        }
        if (NumericalUtility.equal(Math.abs(z), 1)) {
            z = z < 0.0 ? -1.0 : 1.0;
        } else if (Math.abs(1) > 1) {
            throw new PixelBeyondProjectionException(this, "MOL: Solution not defined for x,y: " + Math.toDegrees(xr) + ", " + Math.toDegrees(yr));
        }
        final double theta = NumericalUtility.aasin(z);
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
     * @return the native spherical coordinate (\u03D5) in radians along
     * longitude and s
     * @throws PixelBeyondProjectionException Solution not defined
     */
    private double[] computePhiAndS(final double xr, final double yr) throws PixelBeyondProjectionException {
        final double tol = 1.0e-12;        
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
     * Computes gamma by an iterative solution. 
     * 
     * <p>Solves <code>v - PI*sin(theta) + sin(v) = 0</code><br>
     * with <code>gamma = 0.5 * v</code>
     *
     * @param theta the native spherical coordinate (\u03B8) in radians along
     * latitude
     * @return gamma
     * @see GammaFunction
     */
    private double computeGamma(final double theta) {
        this.gammaFunction.setTheta(theta);
        return NumericalUtility.computeFunctionSolution(this.getMaxIter(), this.gammaFunction, -Math.PI, Math.PI) * 0.5;
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
     * Returns the maximal number of iterations of the approximative solution of
     * the inverse projection.
     *
     * @return the number maximum of iteration
     */
    public int getMaxIter() {
        return maxIter;
    }

    /**
     * Sets the maximal number of iterations.
     *
     * @param maxIter the maximum number of iteration to set
     */
    public final void setMaxIter(final int maxIter) {
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
