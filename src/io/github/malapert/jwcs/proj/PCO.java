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
import static io.github.malapert.jwcs.utility.NumericalUtils.HALF_PI;
import java.util.logging.Level;

/**
 * Polyconic.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class PCO extends PolyConicProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Polyconic";

    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";

    /**
     * Default tolerance for the approximative solution of the inverse
     * projection.
     */
    public static final double DEFAULT_TOLERANCE = 1E-16;

    /**
     * Default maximum iterations for the approximative solution of the inverse
     * projection.
     */
    public static final int DEFAULT_MAX_ITER = 1000;

    /**
     * Tolerance for the approximative solution.
     */
    private double tolerance;
    /**
     * Number of iterations for the approximative solution.
     */
    private int maxIter;

    /**
     * Constructs a PCO projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     */
    public PCO(final double crval1, final double crval2) {
        super(crval1, crval2, 45);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2)=({0},{1},45)", new Object[]{crval1,crval2});                                        
        setMaxIter(DEFAULT_MAX_ITER);
        setTolerance(DEFAULT_TOLERANCE);
    }

    /**
     * Returns the tolerance.
     *
     * @return the tolerance
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     * Sets the tolerance.
     *
     * @param tolerance the tolerance to set
     */
    public final void setTolerance(final double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * Returns the maximal number of iterations.
     *
     * @return the maxIter
     */
    public int getMaxIter() {
        return maxIter;
    }

    /**
     * Sets the maximal number of iterations.
     *
     * @param maxIter the maxIter to set
     */
    public final void setMaxIter(final int maxIter) {
        this.maxIter = maxIter;
    }

    @Override
    protected double[] project(final double x, final double y) {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                                                                        
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);

        double phi, theta = 0;
        if (NumericalUtils.equal(yr, 0)) {
            phi = xr;
            theta = 0.0;
        } else if (NumericalUtils.equal(yr, HALF_PI)) {
            phi = 0.0;
            theta = (yr < 0.0) ? -HALF_PI : HALF_PI;
        } else {
            double thepos;
            // Iterative solution using weighted division of the interval. 
            if (yr > 0.0) {
                thepos = HALF_PI;
            } else {
                thepos = -HALF_PI;
            }
            double theneg = 0.0;

            double xx = xr * xr;
            double ymthe = yr - thepos;
            double fpos = xx + ymthe * ymthe;
            double fneg = -999.0;
            double tanthe = 1.0;
            for (int j = 0; j < getMaxIter(); j++) {
                if (fneg < -100.0) {

                    // Equal division of the interval. 
                    theta = (thepos + theneg) / 2.0;
                } else {

                    // Weighted division of the interval. 
                    double lambda = fpos / (fpos - fneg);
                    if (lambda < 0.1) {
                        lambda = 0.1;
                    } else if (lambda > 0.9) {
                        lambda = 0.9;
                    }
                    theta = thepos - lambda * (thepos - theneg);
                }

                // Compute the residue. 
                ymthe = yr - theta;
                tanthe = Math.tan(theta);
                double f = xx + ymthe * (ymthe - 2 / tanthe);

                // Check for convergence. 
                if (NumericalUtils.equal(f, 0, getTolerance())) {
                    break;
                }
                if (NumericalUtils.equal(thepos, theneg, getTolerance())) {
                    break;
                }

                // Redefine the interval. 
                if (f > 0.0) {
                    thepos = theta;
                    fpos = f;
                } else {
                    theneg = theta;
                    fneg = f;
                }
            }

            double xp = 1 - ymthe * tanthe;
            double yp = xr * tanthe;
            if (NumericalUtils.equal(xp, 0) && NumericalUtils.equal(yp, 0)) {
                phi = 0.0;
            } else {
                phi = NumericalUtils.aatan2(yp, xp) / Math.sin(theta);
            }
        }

        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                                                                
        return pos;
    }

    @Override
    protected double[] projectInverse(final double phi, final double theta) {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                                                                        
        final double phiCorrect = phiRange(phi);
        double costhe = Math.cos(theta);
        double sinthe = Math.sin(theta);
        double a = phiCorrect * sinthe;
        double x, y;
        if (NumericalUtils.equal(sinthe, 0.0)) {
            x = phiCorrect;
            y = 0.0;
        } else {
            double cotthe = costhe / sinthe;
            x = cotthe * Math.sin(a);
            y = cotthe * (1.0 - Math.cos(a)) + theta;
        }
        double[] coord = {Math.toDegrees(x), Math.toDegrees(y)};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{coord[0],coord[1]});                                                                                                                
        return coord;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public ProjectionParameter[] getProjectionParameters() {
        return new ProjectionParameter[]{};
    }

}
