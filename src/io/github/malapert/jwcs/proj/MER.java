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

/**
 * Mercator.
 * 
 * <p>
 * Since the meridians and parallels of all cylindrical projections
 * intersect at right angles the requirement for conformality.
 * This projection has been widely used in navigation since it has the 
 * property that lines of constant bearing (known asrhumb lines orloxodromes)
 * are projected as straight lines. 
 * This is a direct result of its conformality and the fact that its meridians 
 * do not converge.
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class MER extends CylindricalProjection {
    
    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Mercator";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";       

    /**
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     */
    public MER(double crval1, double crval2) {
        super(crval1, crval2);
    }

    @Override
    protected double[] project(double x, double y) {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double phi = xr;
        double theta = 2*Math.atan(Math.exp(yr)) - HALF_PI;
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    protected double[] projectInverse(double phi, double theta) {
        phi = phiRange(phi);
        double x = phi;
        double y = Math.log(Math.tan((HALF_PI + theta) * 0.5d));
        x = Math.toDegrees(x);
        y = Math.toDegrees(y);
        double[] coord = {x, y};
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

}
