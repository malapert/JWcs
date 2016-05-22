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
package io.github.malapert.jwcs.utility;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;

/**   
 * The Airy function R<sub>\u03B8</sub> to solve in an iterative way when a
 * point is projected.
 *
 * <p>R<sub>\u03B8</sub> + 2 *
 * (ln(cos\u03B6)/tan\u03B6+ln(cos\u03B6<sub>b</sub>)/tan<sup>2</sup>\u03B6<sub>b</sub>*tan\u03B6) = 0
 * with:
 * <ul>
 * <li>\u03B6 = 0.5 * (HALF_PI - \u03B8)</li>
 * <li>\u03B6<sub>b</sub> = 0.5 * (HALF_PI - \u03B8<sub>b</sub>)</li>
 * </ul>     
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public class AiryFunction implements UnivariateFunction {
    
    /**
     * \u03B8<sub>b</sub>.
     */
    private final double thetab;
    
    /**
     * Radius.
     */
    private double radius;
    
    /**
     * Initialize the function with \u03B8<sub>b</sub> with radius = 0.
     * 
     * <p>\u03B8<sub>b</sub> is a fixed parameter. Only the radius changes
     * according to (x,y) values.
     * 
     * @param thetab \u03B8<sub>b</sub>.
     */
    public AiryFunction(final double thetab) {
        this.thetab = thetab;
        this.radius = 0;
    }

    @Override
    public double value(final double theta) {
        final double zetab = 0.5 * (NumericalUtility.HALF_PI - getThetab());
        final double zeta = 0.5 * (NumericalUtility.HALF_PI - theta);
        final double lncZeta = FastMath.log(FastMath.cos(zeta));
        final double lncZetab = FastMath.log(FastMath.cos(zetab));
        final double tanZeta = FastMath.tan(zeta);
        return 2 * (lncZeta / tanZeta + lncZetab / FastMath.pow(tanZeta, 2)) * tanZeta + getRadius();
    }

    /**
     * Returns \u03B8<sub>b</sub>.
     * @return the thetab
     */
    public double getThetab() {
        return thetab;
    }

    /**
     * Returns the radius.
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius.
     * @param radius the radius to set
     */
    public void setRadius(final double radius) {
        this.radius = radius;
    }
    
}
