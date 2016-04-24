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

import io.github.malapert.jwcs.JWcs;
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import java.util.logging.Level;

/**
 * Cylindrical perspective.
 * 
 * <p>
 * The sphere is projected onto a cylinder of radius <code>\u03BB</code> 
 * spherical radii from points in the equatorial plane of the native system at a
 * distance <code>\u03BC</code> spherical radii measured from the center of the 
 * sphere in the direction opposite the projected surface.
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class CYP extends CylindricalProjection {
    
    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Cylindrical perspective";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "\u03BC=%s \u03BB=%s";     

    /**
     * Default value for \u03BC.
     */
    public static final double DEFAULT_MU = 1;

    /**
     * Default value for \u03BB.
     */
    public static final double DEFAULT_LAMBDA = 1;
    
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
     * \u03BC is set to {@link CYP#DEFAULT_MU}.
     * \u03BB is set to {@link CYP#DEFAULT_LAMBDA}.
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     */
    public CYP(double crval1, double crval2) {
        this(crval1, crval2, DEFAULT_MU, DEFAULT_LAMBDA);
    }
    
    /**
     * Constructs a CYP projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and \u03BC and \u03BB. 
     *
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @param mu \u03BC distance measured from the center of the sphere in the direction opposite the projected surface
     * @param lambda \u03BB radius of the cylinder
     * @see <a href="http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf">Representations of celestial coordinates in FITS, chapter 5.2.1</a>
     */
    public CYP(double crval1, double crval2, double mu, double lambda) {
        super(crval1, crval2);
        this.mu = mu;
        this.lambda = lambda;
        check();
    }
    
    /**
     * Checks projection parameters.
     * 
     * Sets \u03BB = 1 when \u03BB &lt; 0 or \u03BC = -\u03BB.
     */
    protected final void check() {
        if (getLambda() < 0 || NumericalUtils.equal(getLambda(), 0)) {
            LOG.log(Level.WARNING, "CYP: Lambda must be > 0 -- resetting to 1");
            this.lambda = 1;
        }
        if (NumericalUtils.equal(getMu(),-getLambda())) {
            LOG.log(Level.WARNING, "CYP: Mu must not be -lambda -- resetting to 1");
            this.mu = 1;
        }              
    }

    @Override
    public double[] project(double x, double y) {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double phi = xr / getLambda();        
        double eta = yr / (getMu() + getLambda());
        double theta = NumericalUtils.aatan2(eta, 1) + NumericalUtils.aasin(getMu() * eta / Math.sqrt(Math.pow(eta, 2) + 1));       
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    public double[] projectInverse(double phi, double theta) throws PixelBeyondProjectionException {
        phi = phiRange(phi);
        double x = getLambda() * phi;
        double ctheta = Math.cos(theta);
        if(NumericalUtils.equal(getMu(), -ctheta)) {
            throw new PixelBeyondProjectionException(this,"theta = "+theta);
        }
        double y = (getMu()+getLambda())/(getMu() + ctheta) * Math.sin(theta);
        double[] coord = {Math.toDegrees(x), Math.toDegrees(y)};
        return coord;
    }

    /**
     * Returns \u03BC.
     * @return the mu
     */
    protected double getMu() {
        return mu;
    }

    /**
     * Returns \u03BB.
     * @return the lambda
     */
    protected double getLambda() {
        return lambda;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtils.round(this.mu), NumericalUtils.round(this.lambda));
    }
    
    @Override
    public ProjectionParameter[] getProjectionParameters() {
        ProjectionParameter p1 = new ProjectionParameter("mu", JWcs.PV21, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}, 0);
        ProjectionParameter p2 = new ProjectionParameter("lambda", JWcs.PV22, new double[]{0, Double.POSITIVE_INFINITY}, 1);
        return new ProjectionParameter[]{p1,p2};        
    }        

}
