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

import io.github.malapert.jwcs.AbstractJWcs;
import io.github.malapert.jwcs.proj.exception.BadProjectionParameterException;
import io.github.malapert.jwcs.proj.exception.MathematicalSolutionException;
import io.github.malapert.jwcs.proj.exception.JWcsError;
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtility;
import static io.github.malapert.jwcs.utility.NumericalUtility.HALF_PI;
import java.util.logging.Level;
import org.apache.commons.math3.util.FastMath;

/**
 * Zenithal perspective.
 *
 * <p>Zenithal (azimuthal) perspective projections are generated from a point and
 * carried through the sphere to the plane of projection.
 *
 * @see <a href="http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf">
 * "Representations of celestial coordinates in FITS", M. R. Calabretta and
 * E. W. Greisen - page 10</a>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class AZP extends AbstractZenithalProjection {

    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = "Zenithal perspective";
    
    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "\u03BC=%s \u0263=%s";

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
    private final static double DEFAULT_VALUE = 0;

    /**
     * Creates a new AZC projection based on the celestial longitude and
     * latitude of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * <p>\u03BC and \u0263 are set to {@link AZP#DEFAULT_VALUE}.
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the fiducial point
     * @throws io.github.malapert.jwcs.proj.exception.BadProjectionParameterException Gamma must be different +/- 90°
     */
    public AZP(final double crval1, final double crval2) throws BadProjectionParameterException {
        this(crval1, crval2, DEFAULT_VALUE, DEFAULT_VALUE);
    }

    /**
     * Creates a new AZP projection based on the celestial longitude and
     * latitude of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and \u03BC and \u0263.
     * 
     * <p><img alt="AZP projection" src="doc-files/azpProjection.png">
     * <br>The picture shows alternate geometries of slant zenithal perspective 
     * projections with \u0263=2 and \u03BC=30&deg;
     * 
     * <p>\u03BC is in degrees while \u0263 is set in radii.
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the fiducial point
     * @param gamma \u03BC Angle in degrees between the camera's optical axis and the line to the center of the planet
     * @param mu \u0263 Distance in radii from the center of the sphere to the source of projectionPV<code>nbAxis</code>_1 in radians
     * @throws io.github.malapert.jwcs.proj.exception.BadProjectionParameterException Gamma must be different +/- 90°
     */
    public AZP(final double crval1, final double crval2, final double mu, final double gamma) throws BadProjectionParameterException {
        super(crval1, crval2);
        this.gamma = FastMath.toRadians(gamma);
        this.mu = mu;
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2,mu,gamma)=({0},{1},{2},{3})", new Object[]{crval1,crval2,mu,gamma});        
        checkParameters(this.gamma);
    }
    
    /**
     * Checks gamma parameter.
     * 
     * @param gamma value to check
     * @throws BadProjectionParameterException Gamma must be different +/- HALF_PI
     */
    private void checkParameters(final double gamma) throws BadProjectionParameterException {
        if(NumericalUtility.equal(FastMath.abs(gamma), HALF_PI)) {
            throw new BadProjectionParameterException(this, "gamma="+gamma+". Gamma must be != +/-HALF_PI");
        }
    }
    
    /**
     * Computes the plane coordinate along X based on parameter projection.
     * 
     * <p>X coordinate is computed as <code>xr = x</code>
     * 
     * @param x plane coordinate along X in radians.
     * @return the plane coordinate along X with application of parameter projection
     */
    private double computeXr(final double x) {
        return x;
    }
    
    /**
     * Computes the plane coordinate along Y based on parameter projection.
     * 
     * <p>Y coordinate is computed as <code>yr = y * cos\u03BC</code>
     * 
     * @param y plane coordinate along Y in radians.
     * @return the plane coordinate along Y with application of parameter projection
     */    
    private double computeYr(final double y) {
        return y * FastMath.cos(getGamma());
    }       
    
    /**
     * Computes \u03C1.
     * 
     * <p>\u03C1 is computed as:
     * <br><code>\u03C1 = radius / (\u03BC + 1 + y * tan\u0263)</code>
     * 
     * @param x plane coordinate in radians along X
     * @param y plane coordinate in radians along Y
     * @param radius Radius
     * @return rho
     * @throws BadProjectionParameterException When (x,y) has no solution
     */
    private double computeRho(final double x, final double y, final double radius) throws BadProjectionParameterException {
        final double denom = getMu() + 1 + y * FastMath.tan(getGamma());
        if (NumericalUtility.equal(denom,0)) {
            throw new BadProjectionParameterException(this,"(mu,gamma) = (" + getMu() + ", " + getGamma()+"). (mu + 1) + y * tan(gamma) must be !=0");
        }    
        return radius / denom;        
    }    
    
    /**
     * Computes theta in radians.
     * 
     * @param x plane coordinate in radians along X
     * @param y plane coordinate in radians along Y
     * @param radius radius
     * @return the \u03B8 native spherical coordinates in radians
     * @throws BadProjectionParameterException When projection parameters are wrong
     * @throws PixelBeyondProjectionException When (x,y) has no solution
     */
    private double computeTheta(final double x, final double y, final double radius) throws BadProjectionParameterException, PixelBeyondProjectionException {   
        final double rho = computeRho(x, y, radius);
        
        //computes omega
        final double val = getMu()*rho/FastMath.sqrt(FastMath.pow(rho, 2)+1);
        final double omega = NumericalUtility.aasin(val);
        if (Double.isNaN(omega)) {
            throw new PixelBeyondProjectionException(this, x, y, true);
        }
        
        //computes psi
        final double psi = NumericalUtility.aatan2(1.0, rho);
        if(Double.isNaN(psi)) {
            throw new PixelBeyondProjectionException(this, x, y, true);            
        }
        
        //computes the valid theta solution
        final double theta1 = NumericalUtility.normalizeLatitude(psi - omega);
        final double theta2 = NumericalUtility.normalizeLatitude(psi + omega + FastMath.PI);        
        final double theta;        
        if(FastMath.abs(getMu()) < 1) {
            try {
                theta = findTheValidSolution(theta1, theta2);
            } catch (MathematicalSolutionException ex) {
                throw new PixelBeyondProjectionException(this, x, y, ex.getMessage(), true);
            }
        } else {
            theta = findTheSolutionNearestNorthPole(theta1, theta2);
        }
        return theta;
    }
    
    /**
     * Finds the right solution among theta1 and theta2.
     * 
     * <p>The right solution is this one which is the nearest from the north pole.
     * 
     * @param theta1 First solution
     * @param theta2 Second solution
     * @return theta1 or theta2
     */
    private double findTheSolutionNearestNorthPole(final double theta1, final double theta2) {
        final double theta;
        final double diffTheta1With90 = FastMath.abs(theta1-HALF_PI);
        final double diffTheta2With90 = FastMath.abs(theta2-HALF_PI);
        if (diffTheta1With90 > diffTheta2With90) {
            theta = theta2; 
        } else {
            theta = theta1;
        }    
        return theta;
    }
    
    /**
     * Finds the valid solution among the two solutions.
     * 
     * <p>Only one theta is valid. A valid theta is a theta in which the value
     * is included in [-HalfPi, HalfPi].
     * 
     * @param theta1 First solution
     * @param theta2 Second solution
     * @return theta1 or theta2
     * @throws MathematicalSolutionException No valid solution
     */
    private double findTheValidSolution(final double theta1, final double theta2) {
        final double theta;
        final boolean isTheta1Valid = NumericalUtility.isInInterval(theta1, -HALF_PI, HALF_PI);
        final boolean isTheta2Valid = NumericalUtility.isInInterval(theta2, -HALF_PI, HALF_PI);
        if(isTheta1Valid && isTheta2Valid) {
            throw new MathematicalSolutionException("No valid solution");
        } else if (isTheta1Valid) {
            theta = theta1;
        } else if (isTheta2Valid) {
            theta = theta2;
        } else {
            throw new MathematicalSolutionException("No valid solution");
        }
        return theta;
    }

    @Override
    public double[] project(final double x, final double y) throws BadProjectionParameterException, PixelBeyondProjectionException {
        final double xr = computeXr(FastMath.toRadians(x));
        final double yr = computeYr(FastMath.toRadians(y));
        final double r = computeRadius(xr, yr);
        final double theta = computeTheta(xr, yr, r);
        final double phi = computePhi(xr, yr, r);              
        final double[] pos = {phi, theta};
        return pos;
    }

    @Override
    public double[] projectInverse(final double phi, final double theta) throws PixelBeyondProjectionException {
        final double r = computeRadiusFrom(phi, theta);
        final double x = r * FastMath.sin(phi);
        final double y = -r * FastMath.cos(phi) / FastMath.cos(getGamma());
        final double[] pos = {FastMath.toDegrees(x), FastMath.toDegrees(y)};
        return pos;
    }
    
    /**
     * Computes the radius from (\u03D5, \u03B8).
     * 
     * <p>To compute (x,y), first we need to compute R, which is given by :
     * <br>R = (\u03BC + 1) * cos\u03B8 / (\u03BC + sin\u03B8 + cos\u03B8 * cos\u03D5 * tan\u0263)
     * 
     * @param phi the native spherical coordinate (\u03D5) in radians along longitude
     * @param theta the native spherical coordinate (\u03B8) in radians along latitude
     * @return the radius
     * @throws PixelBeyondProjectionException \u03B8 is beyond the projection
     */
    private double computeRadiusFrom(final double phi, final double theta) throws PixelBeyondProjectionException {
        final double thetax;
        if (NumericalUtility.equal(getMu(), 0)) {
            thetax = 0;
        } else if (FastMath.abs(getMu()) > 1) {
            thetax = NumericalUtility.aasin(-1 / getMu());
        } else {
            thetax = NumericalUtility.aasin(-getMu());
        }

        final double denom = getMu() + FastMath.sin(theta) + FastMath.cos(theta) * FastMath.cos(phi) * FastMath.tan(getGamma());

        if (NumericalUtility.equal(denom, 0) || theta < thetax) {
            throw new PixelBeyondProjectionException(this, FastMath.toDegrees(phi), FastMath.toDegrees(theta), false);
        }

        return (getMu() + 1) * FastMath.cos(theta) / denom;          
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }
    
    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, this.getMu(), NumericalUtility.round(FastMath.toDegrees(this.getGamma())));
    }

    @Override
    public ProjectionParameter[] getProjectionParameters() {
        final ProjectionParameter p1 = new ProjectionParameter("\u03BC", AbstractJWcs.PV21, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}, 0);
        final ProjectionParameter p2 = new ProjectionParameter("\u0263", AbstractJWcs.PV22, new double[]{0, 360}, 0);
        return new ProjectionParameter[]{p1,p2};        
    }

    /**
     * Returns \u0263 in radians.
     * @return the gamma
     */
    public double getGamma() {
        return gamma;
    }

    /**
     * Sets \u0263 in radians.
     * @param gamma the gamma to set
     * @throws BadProjectionParameterException Gamma must be != +/-HALF_PI
     */
    public void setGamma(final double gamma) throws BadProjectionParameterException {
        this.gamma = gamma;
        checkParameters(gamma);
    }

    /**
     * Returns \u03BC.
     * @return the mu
     */
    public double getMu() {
        return mu;
    }

    /**
     * Sets \u03BC.
     * @param mu the mu to set
     */
    public void setMu(double mu) {
        this.mu = mu;
    }
}
