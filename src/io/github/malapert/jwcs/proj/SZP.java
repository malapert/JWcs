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
import io.github.malapert.jwcs.proj.exception.JWcsError;
import io.github.malapert.jwcs.proj.exception.JWcsException;
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtility;
import static io.github.malapert.jwcs.utility.NumericalUtility.HALF_PI;
import java.util.logging.Level;
import org.apache.commons.math3.util.FastMath;

/**
 * Slant zenithal perspective.
 *
 * <p>While the generalization of the AZP projection to tilted planes of projection
 * is useful for certain applications it does have a number of drawbacks, in
 * particular, unequal scaling at the reference point. 
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class SZP extends AbstractZenithalProjection {
    
    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = "Slant zenithal perspective";
    
    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "\u03BC=%s \u03C6c=%s \u03B8c=%s";      

    /**
     * \u03BC : Distance in spherical radii from the center of the sphere to the source of the projection.
     */
    private final double mu;
    /**
     * Intersection of the line PO with the sphere at the \u03D5<sub>c</sub> coordinate.
     */
    private final double thetac;
    /**
     * Intersection of the line PO with the sphere at the \u03B8<sub>c</sub> coordinate.
     */    
    private final double phic;
    
    /**
     * X coordinate of P.
     */
    private final double xp;
    
    /**
     * Y coordinate of P.
     */
    private final double yp;
    
    /**
     * Z coordinate of P.
     */
    private final double zp;

    /**
     * Default value for \u03BC.
     */
    public final static double DEFAULT_VALUE_MU = 0;

    /**
     * Default value for \u03D5<sub>c</sub>.
     */
    public final static double DEFAULT_VALUE_PHIC = 0;

    /**
     * Default value for \u03B8<sub>c</sub>.
     */
    public final static double DEFAULT_VALUE_THETAC = 90;

   /**
     * Constructs a SZP projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * <p>\u03D5<sub>c</sub> is set to {@link SZP#DEFAULT_VALUE_PHIC}.
     * \u03B8<sub>c</sub> is set to {@link SZP#DEFAULT_VALUE_THETAC}.
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @throws io.github.malapert.jwcs.proj.exception.BadProjectionParameterException When projection parameters are wrong
     */
    public SZP(final double crval1, final double crval2) throws BadProjectionParameterException {
        this(crval1, crval2, DEFAULT_VALUE_MU, DEFAULT_VALUE_THETAC, DEFAULT_VALUE_PHIC);
    }

   /**
     * Constructs a SZP projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @param mu \u03BC parameter projection
     * @param phic \u03B8<sub>c</sub> parameter projection
     * @param thetac \u03D5<sub>c</sub> parameter projection
     * @throws io.github.malapert.jwcs.proj.exception.BadProjectionParameterException When projection parameters are wrong
     */
    public SZP(final double crval1, final double crval2, final double mu, final double phic, final double thetac) throws BadProjectionParameterException {
        super(crval1, crval2);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2,mu,phic,thetac)=({0},{1},{2},{3},{4})", new Object[]{crval1,crval2,mu,phic,thetac});                                                                                                                                                
        this.mu = mu;
        this.thetac = FastMath.toRadians(thetac);
        this.phic = FastMath.toRadians(phic);
        this.xp = -this.mu * FastMath.cos(this.thetac) * FastMath.sin(this.phic);
        this.yp = this.mu * FastMath.cos(this.thetac) * FastMath.cos(this.phic);
        this.zp = this.mu * FastMath.sin(this.thetac) + 1;        
        check();        
    }

    /**
     * Check.
     * 
     * @throws io.github.malapert.jwcs.proj.exception.BadProjectionParameterException When projection parameters are wrong
     * @throws JWcsError Non-standard phi0 or theta0 values
     */
    protected final void check() throws BadProjectionParameterException {
        if (!NumericalUtility.equal(getPhi0(), 0) || !NumericalUtility.equal(getTheta0(),HALF_PI)) {
            throw new JWcsError("Non-standard phi0 or theta0 values");
        }
        if (NumericalUtility.equal(this.zp, 0)) {
            throw new BadProjectionParameterException(this,"zp = 0. It must be !=0");
        }
    }

    @Override
    public double[] project(final double x, final double y) throws PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                                                                                                
        final double xr = FastMath.toRadians(x);
        final double yr = FastMath.toRadians(y);        
        final double X = xr;
        final double Y = yr;
        final double X1 = (X - xp) / zp;
        final double Y1 = (Y - yp) / zp;
        final double a = X1 * X1 + Y1 * Y1 + 1;
        final double b = X1 * (X - X1) + Y1 * (Y - Y1);
        final double c = (X - X1) * (X - X1) + (Y - Y1) * (Y - Y1) - 1;
        final double theta;
        try {
            theta = NumericalUtility.computeQuatraticSolution(new double[]{c,b*2,a});
        } catch (JWcsException ex) {
            throw new PixelBeyondProjectionException(this,"(x,y) = (" + x
                    + ", " + y + ")");
        }
        final double phi = computePhi(X - X1 * (1 - FastMath.sin(theta)), Y - Y1 * (1 - FastMath.sin(theta)), 1);
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                                                                                                                                                
        return pos;
    }

    @Override
    public double[] projectInverse(final double phi, final double theta) throws PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                                                                                                                                                        
        final double denom = zp - (1 - FastMath.sin(theta));
        if (NumericalUtility.equal(denom, 0)) {
            throw new PixelBeyondProjectionException(this, "theta = " + FastMath.toDegrees(theta));
        }
        final double x = (zp * FastMath.cos(theta) * FastMath.sin(phi) - xp * (1 - FastMath.sin(theta)))/denom;
        final double y = -(zp * FastMath.cos(theta) * FastMath.cos(phi) + yp * (1 - FastMath.sin(theta)))/denom;
        final double[] coord = {FastMath.toDegrees(x), FastMath.toDegrees(y)};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{coord[0],coord[1]});                                                                                                                                        
        return coord;
    }  
    
    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtility.round(this.mu), NumericalUtility.round(FastMath.toDegrees(this.phic)), NumericalUtility.round(FastMath.toDegrees(this.thetac)));
    }
    
    @Override
    public ProjectionParameter[] getProjectionParameters() {
        final ProjectionParameter p1 = new ProjectionParameter("mu", AbstractJWcs.PV21, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}, 0);
        final ProjectionParameter p2 = new ProjectionParameter("phic", AbstractJWcs.PV22, new double[]{0, 360}, 0);                
        final ProjectionParameter p3 = new ProjectionParameter("thetac", AbstractJWcs.PV23, new double[]{0, 90}, 90);
        return new ProjectionParameter[]{p1,p2,p3};    
    }    

}
