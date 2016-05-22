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
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtility;
import java.util.logging.Level;
import org.apache.commons.math3.util.FastMath;

/**
 * Cylindrical perspective.
 * 
 * <p>The sphere is projected onto a cylinder of radius <code>\u03BB</code> 
 * spherical radii from points in the equatorial plane of the native system at a
 * distance <code>\u03BC</code> spherical radii measured from the center of the 
 * sphere in the direction opposite the projected surface.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class CYP extends AbstractCylindricalProjection {
    
    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = "Cylindrical perspective";
    
    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "\u03BC=%s \u03BB=%s";     

    /**
     * Default value for \u03BC.
     */
    public final static double DEFAULT_MU = 1;

    /**
     * Default value for \u03BB.
     */
    public final static double DEFAULT_LAMBDA = 1;
    
    /**
     * \u03BC: distance in spherical radii from the center of the sphere to the equatorial plane of the native system. 
     */
    private double mu;
    /**
     * \u03BB: radius of the cylinder in spherical radii.
     */
    private double lambda;
    
   /**
     * Constructs a CYP projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     *
     * <p>\u03BC is set to {@link CYP#DEFAULT_MU}.
     * \u03BB is set to {@link CYP#DEFAULT_LAMBDA}.
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     */
    public CYP(final double crval1, final double crval2) {
        this(crval1, crval2, DEFAULT_MU, DEFAULT_LAMBDA);
    }
    
    /**
     * Constructs a CYP projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and \u03BC and \u03BB. 
     *     
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @param mu \u03BC distance measured from the center of the sphere in the direction opposite the projected surface
     * @param lambda \u03BB radius of the cylinder
     * @see <a href="http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf">Representations of celestial coordinates in FITS, chapter 5.2.1</a>
     */
    public CYP(final double crval1, final double crval2, final double mu, final double lambda) {
        super(crval1, crval2);
        this.mu = mu;
        this.lambda = lambda;
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2,mu,lambda)=({0},{1},{2},{3})", new Object[]{crval1, crval2, mu, lambda});        
        check();
    }
    
    /**
     * Checks projection parameters.
     * 
     * <p>Sets \u03BB = 1 when \u03BB &lt; 0 or \u03BC = -\u03BB.
     */
    protected final void check() {
        if (getLambda() < 0 || NumericalUtility.equal(getLambda(), 0)) {
            LOG.log(Level.WARNING, "CYP: Lambda must be > 0 -- resetting to 1");
            this.lambda = 1;
        }
        if (NumericalUtility.equal(getMu(),-getLambda())) {
            LOG.log(Level.WARNING, "CYP: Mu must not be -lambda -- resetting to 1");
            this.mu = 1;
        }              
    }

    @Override
    public double[] project(final double x, final double y) {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                                        
        final double xr = FastMath.toRadians(x);
        final double yr = FastMath.toRadians(y);
        final double phi = xr / getLambda();        
        final double eta = yr / (getMu() + getLambda());
        final double theta = NumericalUtility.aatan2(eta, 1) + NumericalUtility.aasin(getMu() * eta / FastMath.sqrt(FastMath.pow(eta, 2) + 1));       
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                                                                                
        return pos;
    }

    @Override
    public double[] projectInverse(final double phi, final double theta) throws PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                                                                                        
        final double x = getLambda() * phi;
        final double ctheta = FastMath.cos(theta);
        if(NumericalUtility.equal(getMu(), -ctheta)) {
            throw new PixelBeyondProjectionException(this,"theta[deg] = "+FastMath.toDegrees(theta));
        }
        final double y = (getMu()+getLambda())/(getMu() + ctheta) * FastMath.sin(theta);
        final double[] coord = {FastMath.toDegrees(x), FastMath.toDegrees(y)};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{coord[0],coord[1]});                                                                                
        return coord;
    }

    /**
     * Returns \u03BC.
     * 
     * @return the mu
     */
    protected final double getMu() {
        return mu;
    }

    /**
     * Returns \u03BB.
     * 
     * @return the lambda
     */
    protected final double getLambda() {
        return lambda;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtility.round(this.mu), NumericalUtility.round(this.lambda));
    }
    
    @Override
    public ProjectionParameter[] getProjectionParameters() {
        final ProjectionParameter p1 = new ProjectionParameter("\u03BC", AbstractJWcs.PV21, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}, 0);
        final ProjectionParameter p2 = new ProjectionParameter("\u03BB", AbstractJWcs.PV22, new double[]{0, Double.POSITIVE_INFINITY}, 1);
        return new ProjectionParameter[]{p1,p2};        
    }        

}
