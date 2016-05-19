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
import io.github.malapert.jwcs.utility.NumericalUtility;
import static io.github.malapert.jwcs.utility.NumericalUtility.HALF_PI;
import java.util.logging.Level;

/**
 * Conic orthomorphic.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class COO extends AbstractConicProjection {

    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = " Conic orthomorphic";
    
    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "\u03B8a=%s \u03B7=%s"; 
    
   /**
     * Constructs a COO projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and 03B8<sub>a</sub> and \u03B7.
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
    public COO(final double crval1, final double crval2, final double theta_a, final double eta) throws BadProjectionParameterException {
        super(crval1, crval2, theta_a, eta);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2,theta_a,eta)=({0},{1},{2},{3})", new Object[]{crval1,crval2,theta_a,eta});                                        
    }

    @Override
    protected double[] project(final double x, final double y) throws BadProjectionParameterException {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                        
        final double xr = Math.toRadians(x);
        final double yr = Math.toRadians(y);
        final double tan1 = Math.tan((HALF_PI - this.theta1) * 0.5);
        final double tan2 = Math.tan((HALF_PI - this.theta2) * 0.5);
        final double c = NumericalUtility.equal(theta1,theta2) ? Math.sin(theta1) : Math.log(Math.cos(theta2) / Math.cos(theta1)) / Math.log(tan2 / tan1);
        if (NumericalUtility.equal(c,0)) {
            throw new BadProjectionParameterException(this,"(theta1,theta2). c must be != 0");
        }
        final double psi = NumericalUtility.equal(tan1,0) ? Math.cos(theta2) / (c * Math.pow(tan2, c)) : Math.cos(theta1) / (c * Math.pow(tan1, c));
        final double y0 = psi * Math.pow(Math.tan((HALF_PI - getThetaA()) * 0.5), c);
        final double r_theta = Math.signum(getThetaA()) * Math.sqrt(Math.pow(xr, 2) + Math.pow(y0 - yr, 2));
        final double phi = computePhi(xr, yr, r_theta, y0, c);            
        final double theta = HALF_PI - 2 * Math.atan(Math.pow(r_theta / psi, 1.0 / c));
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                
        return pos;
    }

    @Override
    protected double[] projectInverse(final double phi, final double theta) throws BadProjectionParameterException {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                        
        final double tan1 = Math.tan((HALF_PI - this.theta1) * 0.5);
        final double tan2 = Math.tan((HALF_PI - this.theta2) * 0.5);
        final double c = NumericalUtility.equal(theta1,theta2) ? Math.sin(theta1) : Math.log(Math.cos(theta2) / Math.cos(theta1)) / Math.log(tan2 / tan1);
        final double psi = NumericalUtility.equal(tan1,0) ? Math.cos(theta2) / (c * Math.pow(tan2, c)) : Math.cos(theta1) / (c * Math.pow(tan1, c));
        if (NumericalUtility.equal(psi,0)) {
            throw new BadProjectionParameterException(this,"(theta_a, eta) = (" + getThetaA() + ", " + getEta()+")");
        }
        final double y0 = psi * Math.pow(Math.tan((HALF_PI - getThetaA()) * 0.5), c);
        final double r_theta = psi * Math.pow(Math.tan((HALF_PI - theta) * 0.5), c);       
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
        LOG.log(Level.FINER, "(lon,lat)=({0},{1}) {2}",new Object[]{Math.toDegrees(lon),Math.toDegrees(lat),super.inside(lon, lat) && !NumericalUtility.equal(lat, -HALF_PI)});
        return super.inside(lon, lat) && !NumericalUtility.equal(lat, -HALF_PI);
    }

    @Override
    public ProjectionParameter[] getProjectionParameters() {
        final ProjectionParameter p1 = new ProjectionParameter("theta_a", AbstractJWcs.PV21, new double[]{-90, 90}, -45);
        final ProjectionParameter p2 = new ProjectionParameter("eta", AbstractJWcs.PV22, new double[]{0, 90}, 0);
        return new ProjectionParameter[]{p1,p2};    
    }    
}
