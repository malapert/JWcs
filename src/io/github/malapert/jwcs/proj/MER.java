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

import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import static io.github.malapert.jwcs.utility.NumericalUtils.HALF_PI;

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
 * @version 2.0
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
     * Constructs a MER projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
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
    protected double[] projectInverse(double phi, double theta) throws PixelBeyondProjectionException  {
        phi = phiRange(phi);
        double x = phi;
        if (NumericalUtils.equal(Math.abs(theta), HALF_PI)) {
            throw new PixelBeyondProjectionException(this,"theta = "+theta);            
        }
        double angle = (HALF_PI + theta) * 0.5d;
        if(NumericalUtils.equal(Math.abs(angle), HALF_PI)) {
            throw new PixelBeyondProjectionException(this,"theta = "+theta);        
        }
        double d = Math.tan(angle);
        if (d<0 || NumericalUtils.equal(d, 0)) {
            throw new PixelBeyondProjectionException(this,"theta = "+theta);        
        }
        double y = Math.log(d);
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
        

    @Override
    public boolean inside(double lon, double lat) {
        return super.inside(lon, lat) && !NumericalUtils.equal(Math.abs(lat), HALF_PI);   
    }

}
