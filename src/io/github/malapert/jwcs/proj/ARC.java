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

import static io.github.malapert.jwcs.utility.NumericalUtility.HALF_PI;
import java.util.logging.Level;
import org.apache.commons.math3.util.FastMath;

/**
 * Zenithal equidistant.
 *
 * <p>The zenithal equidistant projection ﬁrst appeared in Greisen (1983) as ARC.
 * It is widely used as the approximate projection of Schmidt telescopes.
 * The native meridians are uniformly divided to give equispaced parallels.
 *  This projection was also known in antiquity.
 * 
 * @see <a href="http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf">
 * "Representations of celestial coordinates in FITS", M. R. Calabretta and 
 * E. W. Greisen - page 14</a> 
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class ARC extends AbstractZenithalProjection {
    
    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = "Zenithal equidistant";
    
    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "no limits";

    /**
     * Creates a new ARC projection based on the celestial longitude and 
     * latitude of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the fiducial point
     */    
    public ARC(final double crval1, final double crval2) {
        super(crval1, crval2);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2)=({0},{1})", new Object[]{crval1,crval2});        
        
    }
 
    @Override
    protected double[] project(final double x, final double y) {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});        
        final double xr = FastMath.toRadians(x);
        final double yr = FastMath.toRadians(y);
        final double r_theta = computeRadius(xr, yr);
        final double phi = computePhi(xr, yr, r_theta);
        final double theta = HALF_PI - r_theta;
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                
        return pos;
    }

    @Override
    protected double[] projectInverse(final double phi, final double theta) {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                        
        final double r = FastMath.toDegrees(HALF_PI - theta);  // theta between [-HALF_PI, HALF_PI] => no need to test
        final double x = computeX(r, phi);
        final double y = computeY(r, phi);
        final double[] pos = {x, y};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                
        return pos;
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
    public ProjectionParameter[] getProjectionParameters() {
        return new ProjectionParameter[]{};
    }
}
