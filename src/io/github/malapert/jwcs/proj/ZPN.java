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
 * Zenithal polynomial.
 *
 * <p>
 * The zenithal polynomial projection, ZPN, generalizes the ARC projection by
 * adding polynomial terms up to a large degree in the zenith distance
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public final class ZPN extends ZenithalProjection {

    /**
     * Default numerical tolerance for double comparison or iterative solution .
     */
    public static final double DEFAULT_TOLERANCE = 1E-13;
    /**
     * Default maximum iteration for iterative solution.
     */
    public static final double DEFAULT_MAX_ITER = 1000;
    /**
     * Tolerance to apply.
     */
    private double tolerance;
    /**
     * Maximum iteration for iterative solution.
     */
    private double maxIter;
    /**
     * Projection parameters.
     */
    private final double[] PV;

    /**
     * The highest PV coefficient not equal to 0.
     */
    private transient final int n;

    /**
     * The point of inflection closest to the pole.
     *
     * coeff[0] Co-latitude of the first point of inflection (N > 2) coeff[1]
     * Radius of the first point of inflection (N > 2)
     */
    private final transient double[] coeff;

    /**
     * Creates a projection based on crval1, crval2 and the projection
     * parameters.
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     * @param PV projection parameters
     * @throws
     * io.github.malapert.jwcs.proj.exception.BadProjectionParameterException when a parameter projection is wrong
     */
    public ZPN(double crval1, double crval2, double[] PV) throws BadProjectionParameterException {
        super(crval1, crval2);
        this.PV = PV;
        setMaxIter(DEFAULT_MAX_ITER);
        setTolerance(DEFAULT_TOLERANCE);
        check();
        n = findTheHighestPVNoNull(PV);
        coeff = findTheInflectionPointClosestFromPole(n, PV);
    }

    /**
     * Finds the inflection point the closest from pole.
     * @param highestPV highest PV order
     * @param PV projection parameters
     * @return the inflection point the closest from pole
     * @throws BadProjectionParameterException 
     */
    private double[] findTheInflectionPointClosestFromPole(int highestPV, double[] PV) throws BadProjectionParameterException {
        // Find the point of inflection closest to the pole. 
        double d, d1, d2, r, zd, zd1, zd2;
        int i;
        zd1 = zd2 = d2 = zd = 0.0;
        d1 = PV[1];
        if (d1 <= 0.0) {
            throw new BadProjectionParameterException("p[1] <= 0");
        }

        // Find the point where the derivative first goes negative. 
        for (i = 0; i < 180; i++) {
            zd2 = i * Math.PI / 180;
            d2 = 0.0;
            for (int j = highestPV; j > 0; j--) {
                d2 = d2 * zd2 + j * PV[j];
            }

            if (d2 <= 0.0) {
                break;
            }
            zd1 = zd2;
            d1 = d2;
        }

        if (i == 180) {

            // No negative derivative -> no point of inflection. 
            zd = Math.PI;
        } else {

            // Find where the derivative is zero. 
            for (i = 1; i <= 10; i++) {
                zd = zd1 - d1 * (zd2 - zd1) / (d2 - d1);

                d = 0.0;
                for (int j = highestPV; j > 0; j--) {
                    d = d * zd + j * PV[j];
                }

                if (Math.abs(d) < getTolerance()) {
                    break;
                }

                if (d < 0.0) {
                    zd2 = zd;
                    d2 = d;
                } else {
                    zd1 = zd;
                    d1 = d;
                }
            }
        }

        r = 0.0;
        for (int j = highestPV; j >= 0; j--) {
            r = r * zd + PV[j];
        }
        return new double[]{zd, r};
    }

    /**
     * Searches the highest PV index <code>i</code> where <code>PV[i]> != 0.
     *
     * @param projection parameters
     * @return the highest PV index
     * @throws BadProjectionParameterException
     */
    @SuppressWarnings("empty-statement")
    private int findTheHighestPVNoNull(double[] pv) throws BadProjectionParameterException {
        int i;
        for (i = pv.length - 1; i >= 0 && pv[i] == 0.0; i--);
        if (i < 0) {
            throw new BadProjectionParameterException("All coefficients = 0");
        }
        return i;
    }

    /**
     * Checks validity of parameters.
     */
    private void check() {
        if ((getPhi0() != 0) || (getTheta0() != HALF_PI)) {
            throw new IllegalArgumentException("Non-standard PVi_1 and/or PVi_2 values");
        }
        if (this.PV.length < 10) {
            throw new ArrayIndexOutOfBoundsException("Need at least 10 projection parameters");
        }
    }

    /**
     * Gets the tolerance of the numerical resolution of the double and iterative solution.
     *
     * @return the tolerance
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     * Sets the tolerance of the numerical resolution of the double and iterative solution.
     *
     * @param tolerance the tolerance to set
     */
    public final void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * Gets the number of maximum iteration for the iterative solution.
     *
     * @return the maxIter
     */
    public double getMaxIter() {
        return maxIter;
    }

    /**
     * Sets the number of maximum iteration for the iterative solution.
     *
     * @param maxIter the maxIter to set
     */
    public final void setMaxIter(double maxIter) {
        this.maxIter = maxIter;
    }


    /**
     * The highest PV coefficient not equal to 0
     * @return the n
     */
    public int getN() {
        return n;
    }

    /**
     * The point of inflection closest to the pole
     * @return the coeff
     */
    public double[] getCoeff() {
        return coeff;
    }
    
    /**
     * Computes a polynomial where orders are given by pv.
     *
     * @param x value
     * @param pv polynomial order
     * @return the value of the polynomial
     */
    private double polyEval(double x, double[] pv) {
        int lastElt = pv.length - 1;
        double y = 0;
        double result;
        for (int i = lastElt; i >= 0; i--) {
            y = y * x + pv[i];
        }
        result = y;

        return result;
    }

    /**
     * Computes the solution for a linear equation.
     * @param r radius
     * @param pv projection parameters
     * @return the solution for a linear equation
     */
    private double linearSolution(double r, double[] pv) {
        return (r - pv[0]) / pv[1];
    }

    /**
     * Computes the solution for a quadratic equation.
     * @param r radius
     * @param pv projection parameters
     * @return the solution for a quadratic equation
     */  
    private double quadraticSolution(double r, double[] pv) throws PixelBeyondProjectionException {
        double a = pv[2];
        double b = pv[1];
        double c = pv[0] - r;
        double d = b * b - 4.0 * a * c;
        if (d < 0.0) {
            throw new PixelBeyondProjectionException();
        }
        d = Math.sqrt(d);
        // Choose solution closest to pole. 
        double x1 = (-b + d) / (2.0 * a);
        double x2 = (-b - d) / (2.0 * a);
        double x = (x1 < x2) ? x1 : x2;
        if (x < -getTolerance()) {
            x = (x1 > x2) ? x1 : x2;
        }
        if (x < 0.0) {
            if (x < -getTolerance()) {
                throw new PixelBeyondProjectionException();
            }
            x = 0.0;
        } else if (x > Math.PI) {
            if (x > Math.PI + getTolerance()) {
                throw new PixelBeyondProjectionException();
            }
            x = Math.PI;
        }
        return x;
    }

    /**
     * Computes an iterative solution for an equation where order > 2.
     * <p>
     * The end of the iterative solution is given by the expected numerical precision
     * or the maximal number of iterations.
     * </p>
     * @param r radius
     * @param pv projection parameters
     * @return the solution for an equation where order > 2
     */    
    private double iterativeSolution(double r, double[] pv, double[] coeff) throws PixelBeyondProjectionException {
        double zd1 = 0.0;
        double r1 = pv[0];
        double zd2 = coeff[0];
        double r2 = coeff[1];
        double zd;
        if (r < r1) {
            if (r < r1 - getTolerance()) {
                throw new PixelBeyondProjectionException();
            }
            zd = zd1;
        } else if (r > r2) {
            if (r > r2 + getTolerance()) {
                throw new PixelBeyondProjectionException();
            }
            zd = zd2;
        } else {
            zd = 0;
            // Disect the interval. 
            for (int j = 0; j < getMaxIter(); j++) {
                double lambda = (r2 - r) / (r2 - r1);
                if (lambda < 0.1) {
                    lambda = 0.1;
                } else if (lambda > 0.9) {
                    lambda = 0.9;
                }

                zd = zd2 - lambda * (zd2 - zd1);

                double rt = 0.0;
                for (int i = getN(); i >= 0; i--) {
                    rt = (rt * zd) + pv[i];
                }

                if (rt < r) {
                    if (r - rt < getTolerance()) {
                        break;
                    }
                    r1 = rt;
                    zd1 = zd;
                } else {
                    if (rt - r < getTolerance()) {
                        break;
                    }
                    r2 = rt;
                    zd2 = zd;
                }

                if (Math.abs(zd2 - zd1) < getTolerance()) {
                    break;
                }
            }
        }
        return zd;
    }
    
    /**
     * Compute the solution for whatever polynomial equation.
     * @param r radius
     * @param pv projection parameters
     * @return the solution for whatever polynomial equation
     * @throws PixelBeyondProjectionException 
     */
    private double computeSolution(double r, double[] pv) throws PixelBeyondProjectionException {
        double result;
        if (getN() == 1) {
            result = linearSolution(r, pv);
        } else if (getN()==2) {
            result = quadraticSolution(r, pv);
        } else {
            result = iterativeSolution(r, pv, getCoeff());
        }
        return result;
    }

    @Override
    protected double[] project(double x, double y) throws PixelBeyondProjectionException {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);        
        double r_theta = Math.sqrt(xr * xr + yr * yr);
        double phi;
	if (NumericalUtils.equal(r_theta, 0.0, getTolerance())) {
	    phi = 0.0;
	} else {
	    phi = Math.atan2(xr, -yr);
	}
        double theta = HALF_PI - computeSolution(r_theta, PV);
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    protected double[] projectInverse(double phi, double theta) {
        double r_theta = polyEval(HALF_PI - theta, PV);
        double x = Math.toDegrees(r_theta * Math.sin(phi));
        double y = Math.toDegrees(-r_theta * Math.cos(phi));
        double[] coord = {x, y};
        return coord;
    }

}
