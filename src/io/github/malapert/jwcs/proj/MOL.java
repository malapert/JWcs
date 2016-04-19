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

import io.github.malapert.jwcs.utility.NumericalUtils;

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
    public MOL(double crval1, double crval2) {
        super(crval1, crval2);
        setMaxIter(DEFAULT_MAX_ITER);
        setTolerance(DEFAULT_TOLERANCE);
    }

    @Override
    protected double[] project(double x, double y) {
        //TODO : check algorithm.
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double tol = 1.0e-12;
        double s = 2 - Math.pow(yr, 2);
        double phi;
        if (s < -tol) {
            //erreur
            phi=0;
        } else if (s <= tol) {
            s = 0.0;
            if (Math.abs(xr) > tol) {
                //erreur
            }
            phi = 0;
        } else {
            s = Math.sqrt(s);
            phi = HALF_PI * xr / s;
        }
        double z = yr / Math.sqrt(2);
        if (Math.abs(z) > 1.0) {
            if (Math.abs(z) > 1.0 + tol) {
                //erreur
            }
            z = ((z < 0.0) ? -1.0 : 1.0) + s * yr / Math.PI;
        } else {
            z = NumericalUtils.aasin(z) / HALF_PI + s * yr / Math.PI;
        }

        if (Math.abs(z) > 1.0) {
            if (Math.abs(z) > 1.0 + tol) {
                //erreur
            }
            z = (z < 0.0) ? -1.0 : 1.0;
        }
        double theta = NumericalUtils.aasin(z);

        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    protected double[] projectInverse(double phi, double theta) {
        phi = phiRange(phi);
        double u = Math.PI * Math.sin(theta);
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
        double gamma = v*0.5;
        double x = Math.toDegrees((Math.sqrt(2.0d) / HALF_PI) * phi * Math.cos(gamma));
        double y = Math.toDegrees(Math.sqrt(2.0d) * Math.sin(gamma));
        double[] coord = {x, y};
        return coord;
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
    public final void setTolerance(double tolerance) {
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
    public final void setMaxIter(double maxIter) {
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
