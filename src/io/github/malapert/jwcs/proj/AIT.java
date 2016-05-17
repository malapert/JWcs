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
import java.util.logging.Level;

/**
 * The Hammer-Aitoff projection.
 * 
 * <p>
 * This projection is developed from the equatorial case of the zenithal equal
 * area projection by doubling the equatorial scale and longitude coverage.
 * The whole sphere is mapped thereby while preserving the equal. The whole 
 * sphere is mapped thereby while preserving the equal.
 * 
 * This projection reduces distortion in the polar regions compared to 
 * pseudo cylindrical by making the meridians and parallels more nearly 
 * orthogonal. Together with its equal area property this makes it one of 
 * most commonly used all-sky projections.
 * 
 * @see <a href="http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf">
 * "Representations of celestial coordinates in FITS"</a>, 
 * M. R. Calabretta and E. W. Greisen - page 18
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class AIT extends CylindricalProjection {
    
    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Hammer-Aitoff";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";    
        
    /**
     * Creates a new AIT projection based on the celestial longitude and 
     * latitude of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>)
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the fiducial point
     */
    public AIT(final double crval1, final double crval2) {
        super(crval1, crval2);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2)=({0},{1})", new Object[]{crval1,crval2});        

    }

    @Override
    public double[] project(final double x, final double y) throws PixelBeyondProjectionException  {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});
        final double xr = Math.toRadians(x);
        final double yr = Math.toRadians(y);
        double z = 1 - Math.pow(xr / 4, 2) - Math.pow(yr / 2, 2);
        if (z < 0) {
            throw new PixelBeyondProjectionException(this,"(x,y)= (" + x + ", " + y+")");
        }
        z = Math.sqrt(z);      
        final double phi = 2 * NumericalUtils.aatan2(z * xr / 2, 2 * Math.pow(z, 2) - 1);
        final double theta = NumericalUtils.aasin(yr * z);         
        if(Double.isNaN(theta)) {
            throw new PixelBeyondProjectionException(this,"(x,y)= (" + x + ", " + y+")");
        }
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});        
        return pos;
    }

    /**
     * Computes the projection plane coordinates from the native spherical
     * coordinates. 
     *
     * @param phi native spherical coordinate in radians along longitude
     * @param theta native spherical coordinate in radians along latitude
     * @return the projection plane coordinates
     * @throws io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException When (phi,theta) has no solution
     */    
    @Override
    public double[] projectInverse(final double phi, final double theta) throws PixelBeyondProjectionException {         
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                
        final double phiCorrect = phiRange(phi);         
        final double d = 1 + Math.cos(theta) * Math.cos(phiCorrect * 0.5d);
        if (NumericalUtils.equal(d, 0)) {
            throw new PixelBeyondProjectionException(this,"(phi,theta)=(" + Math.toDegrees(phiCorrect) + ", " + Math.toDegrees(theta)+")");
        }
        final double gamma = Math.toDegrees(Math.sqrt(2.0d / d));        
        final double x = 2 * gamma * Math.cos(theta) * Math.sin(phiCorrect * 0.5d);
        final double y = gamma * Math.sin(theta);
        final double[] coord = {x, y};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});        
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
