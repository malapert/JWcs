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
import io.github.malapert.jwcs.utility.NumericalUtility;
import java.util.logging.Level;
import org.apache.commons.math3.util.FastMath;

/**
 * Gnomonic.
 *
 * <p>The zenithal perspective projection with mu = 0, the gnomonic projection, is
 * widely used in optical astronomy and was given its own code within the AIPS
 * convention, namely TAN.
 *
 * <p>Ref : http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf page 12
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class TAN extends AbstractZenithalProjection {

    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = "Gnomonic";
    
    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "no limits";     
    

   /**
     * Constructs a TAN projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     */
    public TAN(final double crval1, final double crval2) {
        super(crval1, crval2);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2)=({0},{1})", new Object[]{crval1,crval2});                                
        
    }

    @Override
    public double[] project(final double x, final double y) throws PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                                                                                                        
        final double xr = FastMath.toRadians(x);
        final double yr = FastMath.toRadians(y);
        final double r_theta = computeRadius(xr, yr);        
        final double phi = computePhi(x, y, r_theta);       
        final double theta = NumericalUtility.aatan2(1, r_theta);        
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                                                                                                                                                        
        return pos;
    }

    @Override
    public double[] projectInverse(final double phi, final double theta) throws PixelBeyondProjectionException {        
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                                                                                                                                                                
        final double s = FastMath.sin(theta);
        if (NumericalUtility.equal(s, 0)) {
            throw new PixelBeyondProjectionException(this, "theta = " + FastMath.toDegrees(theta));
        }
        final double r_theta = FastMath.cos(theta) / s;
        final double x = computeX(r_theta, phi);
        final double y = computeY(r_theta, phi);
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
        return DESCRIPTION;
    }
    
    @Override
    public boolean inside(final double lon, final double lat) {  
       return super.inside(lon, lat) && !NumericalUtility.equal(FastMath.abs(lat), 0);
    }     

    @Override
    public ProjectionParameter[] getProjectionParameters() {
        return new ProjectionParameter[]{};
    }
}
