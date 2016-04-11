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

import io.github.malapert.jwcs.utility.NumericalUtils;
import java.util.logging.Level;

/**
 * Cylindrical perspective.
 * 
 * <p>
 * The sphere is projected onto a cylinder of radius <code>LAMBDA</code> 
 * spherical radii from points in the equatorial plane of the native system at a
 * distance <code>MU</code> spherical radii measured from the center of the 
 * sphere in the direction opposite the projected surface.
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
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
     * Default MU.
     */
    public static final double DEFAULT_MU = 1;

    /**
     * Default lambda.
     */
    public static final double DEFAULT_LAMBDA = 1;
    
    /**
     * Projection parameter.
     */
    private double mu;
    /**
     * Other projection parameter.
     */
    private double lambda;
    
    /**
     * Creates an instance.
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     */
    public CYP(double crval1, double crval2) {
        this(crval1, crval2, DEFAULT_MU, DEFAULT_LAMBDA);
    }
    
    /**
     * Creates an instance
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     * @param mu Projection parameter
     * @param lambda Other projection Parameter
     * @see <a href="http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf">Representations of celestial coordinates in FITS, chapter 5.2.1</a>
     */
    public CYP(double crval1, double crval2, double mu, double lambda) {
        super(crval1, crval2);
        this.mu = mu;
        this.lambda = lambda;
        check();
    }
    
    /**
     * Checks.
     */
    protected final void check() {
        if (getLambda() < 0) {
            LOG.log(Level.WARNING, "Lambda must be > 0 -- resetting to 1");
            this.lambda = 1;
        }
        if (NumericalUtils.equal(getMu(),-getLambda(), DOUBLE_TOLERANCE)) {
            LOG.log(Level.WARNING, "Mu must not be -lambda -- resetting to 1");
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
    public double[] projectInverse(double phi, double theta) {
        if (phi > Math.PI) {
            phi -= 2*Math.PI;
        } else if (phi < Math.PI) {
            phi += 2*Math.PI;
        }
        double x = Math.toDegrees(getLambda() * phi);
        double y = Math.toDegrees((getMu()+getLambda())/(getMu() + Math.cos(theta)) * Math.sin(theta));
        double[] coord = {x, y};
        return coord;
    }

    /**
     * Returns mu.
     * @return the mu
     */
    protected double getMu() {
        return mu;
    }

    /**
     * Returns lambda.
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

}
