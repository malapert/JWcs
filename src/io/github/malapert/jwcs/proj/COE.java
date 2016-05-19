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
import io.github.malapert.jwcs.proj.exception.BadProjectionParameterException;
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtility;
import java.util.logging.Level;

/**
 * Conic equal area.
 *
 * <p>The standard parallels in Alber's conic equal area projection are projected
 * as concentric arcs at their true length and separated so that the area
 * between them is the same as the corresponding area on the sphere. The other
 * parallels are then drawn as concentric arcs spaced so as to preserve the
 * area.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class COE extends AbstractConicProjection {

    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = "Conic equal area";
    
    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "\u03B8a=%s \u03B7=%s"; 
        
    /**
     * Constant in radians.
     *
     * <p>This constant is equal to sin\u03B8<sub>a</sub>*     
     */
    private final double y0;
    
    /**
     * Constant in radians.
     * 
     * <p>This constant is equal to sin\u03B8<sub>1</sub>+sin\u03B8<sub>2</sub>
     */
    private final double gamma;
    
    /**
     * Constant of the cone in radians.
     * 
     * <p>This constant is equal to 0.5*\u0263
     */
    private final double c;    
    
   /**
     * Constructs a COE projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and \u03B8<sub>a</sub> and \u03B7.
     *
     * <p>\u03B8<sub>a</sub> is set by the FITS keyword PV<code>nbAxis</code>_1 in degrees.
     * \u03B7 is set by the FITS keyword PV<code>nbAxis</code>_2 in degrees.
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @param theta_a \u03B8<sub>a</sub> in degrees and defined as \u03B8<sub>a</sub>=(\u03B8<sub>1</sub>+\u03B8<sub>2</sub>)/2
     * @param eta \u03B7 in degrees and defined as \u03B7=|\u03B8<sub>1</sub>-\u03B8<sub>2</sub>|/2
     * @throws io.github.malapert.jwcs.proj.exception.BadProjectionParameterException When projection parameters are wrong
     */
    public COE(final double crval1, final double crval2, final double theta_a, final double eta) throws BadProjectionParameterException {
        super(crval1, crval2, theta_a, eta);  
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2,theta_a,eta)=({0},{1},{2},{3})", new Object[]{crval1,crval2,theta_a,eta});                                
        gamma = Math.sin(getTheta1()) + Math.sin(getTheta2());
        c = gamma * 0.5;
        if (NumericalUtility.equal(c, 0)) {
            throw new BadProjectionParameterException(this,"(theta1,theta2). sin(theta1) + sin(theta2) must be != 0");
        }         
        y0 = Math.sqrt(1.0d + Math.sin(getTheta1()) * Math.sin(getTheta2()) - gamma * Math.sin((getTheta1()+getTheta2())*0.5)) / c;
    }

    @Override
    protected double[] project(final double x, final double y) throws BadProjectionParameterException, PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                
        final double xr = Math.toRadians(x);
        final double yr = Math.toRadians(y);                              
        final double r_theta = Math.signum(getThetaA()) * Math.sqrt(Math.pow(xr, 2) + Math.pow(y0 - yr, 2));
        final double phi = computePhi(xr, yr, r_theta, y0, c);                   
        final double w = 1.0d / gamma + Math.sin(getTheta1()) * Math.sin(getTheta2()) / gamma - gamma * Math.pow(r_theta * 0.5, 2);
        final double theta = NumericalUtility.aasin(w);
        if (Double.isNaN(theta)) {
            throw new PixelBeyondProjectionException(this,"(x,y) = ("+x+","+y+")");
        }        
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                        
        return pos;
    }

    @Override
    protected double[] projectInverse(final double phi, final double theta) {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                
        final double r_theta = Math.sqrt(1.0d + Math.sin(getTheta1()) * Math.sin(getTheta2()) - gamma * Math.sin(theta)) / c;      
        final double x = computeX(phi, r_theta, c);
        final double y = computeY(phi, r_theta, c, y0);
        final double[] coord = {Math.toDegrees(x), Math.toDegrees(y)};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                        
        return coord;
    }
    
    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtility.round(Math.toDegrees(this.getThetaA())), NumericalUtility.round(Math.toDegrees(this.getEta())));
    }   
    
    @Override
    public boolean inside(final double lon, final double lat) {
        LOG.log(Level.FINER, "True");        
        return true;
    }    
    
    @Override
    public ProjectionParameter[] getProjectionParameters() {
        final ProjectionParameter p1 = new ProjectionParameter("theta_a", AbstractJWcs.PV21, new double[]{-90, 90}, -45);
        final ProjectionParameter p2 = new ProjectionParameter("eta", AbstractJWcs.PV22, new double[]{0, 90}, 0);
        return new ProjectionParameter[]{p1,p2};    
    }    

}
