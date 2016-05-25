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
import io.github.malapert.jwcs.proj.exception.MathematicalSolutionException;
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import io.github.malapert.jwcs.utility.AiryFunction;
import io.github.malapert.jwcs.utility.NumericalUtility;
import org.apache.commons.math3.util.FastMath;

/**
 * The Airy projection minimizes the error for the region within \u03B8b
 * (Evenden 1991).
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @see <a href="http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf">
 * "Representations of celestial coordinates in FITS", M. R. Calabretta and E.
 * W. Greisen - page 14</a>
 */
public class AIR extends AbstractZenithalProjection {

    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = "Airy projection";

    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "\u03B8b=%s";

    /**
     * Default value for \u03B8b sets to 90&deg;.
     */
    public final static double DEFAULT_VALUE_THETHAB = 90;

    /**
     * \u03B8<SUB>b</SUB> value.
     */
    private double thetab;

    /**
     * The Airy function R<sub>\u03B8</sub> to solve in an iterative way when a
     * point is projected.
     *
     * <p>
     * R<sub>\u03B8</sub> = -2 *
     * (ln(cos\u03B6)/tan\u03B6+ln(cos\u03B6<sub>b</sub>)/tan<sup>2</sup>\u03B6<sub>b</sub>*tan\u03B6)
     * with:
     * <ul>
     * <li>\u03B6 = 0.5 * (HALF_PI - \u03B8)</li>
     * <li>\u03B6<sub>b</sub> = 0.5 * (HALF_PI - \u03B8<sub>b</sub>)</li>
     * </ul>
     * @see io.github.malapert.jwcs.utility.AiryFunction
     */
    private AiryFunction airyFunction;

    /**
     * Creates a new AIR projection based on the celestial longitude and
     * latitude of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     *
     * <p>
     * \u03B8 is set to {@link AIR#DEFAULT_VALUE_THETHAB}.
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point     
     */
    public AIR(final double crval1, final double crval2) {
        this(crval1, crval2, DEFAULT_VALUE_THETHAB);
    }

    /**
     * Creates a new AIR projection based on the celestial longitude and
     * latitude of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @param thetab \u03B4<sub>b</sub>
     */
    public AIR(final double crval1, final double crval2, final double thetab) {
        super(crval1, crval2);
        this.thetab = FastMath.toRadians(thetab);
        this.airyFunction = new AiryFunction(this.thetab);
    }

    @Override
    protected double[] project(final double x, final double y) throws ProjectionException {
        final double xr = FastMath.toRadians(x);
        final double yr = FastMath.toRadians(y);
        final double radius = this.computeRadius(xr, yr);
        getAiryFunction().setRadius(radius);
        final double theta;
        try {
            theta = NumericalUtility.computeFunctionSolution(1000, getAiryFunction(), -NumericalUtility.HALF_PI, NumericalUtility.HALF_PI);
        } catch(MathematicalSolutionException ex) {
            throw new PixelBeyondProjectionException(this, x, y, ex.getMessage(), true);
        }
        final double phi = computePhi(xr, yr, radius);
        return new double[]{phi, theta};
    }

    @Override
    protected double[] projectInverse(final double phi, final double theta) throws ProjectionException {
        final double c = computeC();
        final double zeta = 0.5 * (NumericalUtility.HALF_PI - theta);
        if (NumericalUtility.equal(zeta, 0)) {
            throw new PixelBeyondProjectionException(this, FastMath.toDegrees(phi), FastMath.toDegrees(theta), false);
        }
        final double term1 = FastMath.log(FastMath.cos(zeta)) / FastMath.tan(zeta);
        final double radius = -2 * (term1 + c * FastMath.tan(zeta));
        final double x = computeX(radius, phi);
        final double y = computeY(radius, phi);
        return new double[]{FastMath.toDegrees(x), FastMath.toDegrees(y)};
    }
    
    /**
     * Computes ln(cos\u03B6<sub>b</sub>) / tan<sup>2</sup>\u03B6<sub>b</sub>.
     * 
     * <p>When cos\u03B6<sub>b</sub> = 0, then 0 is returned<br>
     * When cos\u03B6<sub>b</sub> = 1 or tan\u03B6<sub>b</sub> = 0, then c 
     * approaches to its asymptotic value of -0.5<br>
     * Otherwise computes ln(cos\u03B6<sub>b</sub>) / tan<sup>2</sup>\u03B6<sub>b</sub>
     * 
     * @return c, the result of the computation
     */
    private double computeC() {
        final double zetab = 0.5 * (NumericalUtility.HALF_PI - getThetab());
        final double cos_zetab = FastMath.cos(zetab); 
        final double c;
        if (NumericalUtility.equal(cos_zetab, 0)) {
            c = 0d;
        } else if (NumericalUtility.equal(cos_zetab, 1) || NumericalUtility.equal(FastMath.tan(zetab), 0)) {
            c = -0.5d;
        } else {
            c = FastMath.log(FastMath.cos(zetab))/FastMath.pow(FastMath.tan(zetab), 2);
        }         
        return c;
    }    

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtility.round(FastMath.toDegrees(this.getThetab())));
    }

    @Override
    public ProjectionParameter[] getProjectionParameters() {
        final ProjectionParameter p1 = new ProjectionParameter("\u03B8b", AbstractJWcs.PV21, new double[]{-90, 90}, DEFAULT_VALUE_THETHAB);
        return new ProjectionParameter[]{p1};
    }

    /**
     * Returns the Airy function to solve.
     *
     * @return the airyFunction
     */
    private AiryFunction getAiryFunction() {
        return airyFunction;
    }

    /**
     * Returns \u03B8<SUB>b</SUB> in radians.
     *
     * @return the thetab
     */
    public double getThetab() {
        return thetab;
    }

    /**
     * Sets \u03B8<SUB>b</SUB> in radians.
     *
     * @param thetab the thetab to set
     */
    public void setThetab(final double thetab) {
        this.thetab = thetab;
    }

}
