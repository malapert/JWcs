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

import io.github.malapert.jwcs.utility.NumericalUtils;
import static io.github.malapert.jwcs.utility.NumericalUtils.HALF_PI;
import java.util.logging.Level;

/**
 * Zenithal equal-area.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class ZEA extends ZenithalProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Zenithal equal-area";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";         

   /**
     * Constructs a ZEA projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     */
    public ZEA(final double crval1, final double crval2) {
        super(crval1, crval2);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2)=({0},{1})", new Object[]{crval1,crval2});                                        
    }

    @Override
    public double[] project(final double x, final double y) {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                                                                                                                
        final double xr = Math.toRadians(x);
        final double yr = Math.toRadians(y);
        final double r_theta = computeRadius(xr, yr);
        final double phi = computePhi(xr, yr, r_theta);        
        final double theta;
	if (NumericalUtils.equal(r_theta, 2)) {
	    theta = -HALF_PI;
	} else {
	    theta = HALF_PI - 2*NumericalUtils.aasin(r_theta * 0.5);
	}        
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                                                                                                                
        return pos;      
    }

    @Override
    public double[] projectInverse(final double phi, final double theta) {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                                                                                                                        
        final double phiCorrect = phiRange(phi);
        final double r = 2 * Math.sin((HALF_PI-theta)*0.5d);
        final double x = computeX(r, phiCorrect);
        final double y = computeY(r, phiCorrect);
        final double[] pos = {Math.toDegrees(x),Math.toDegrees(y)};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{pos[0],pos[1]});                                                                                                                                                        
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
    public ProjectionParameter[] getProjectionParameters(){
        return new ProjectionParameter[]{};
    }
}
