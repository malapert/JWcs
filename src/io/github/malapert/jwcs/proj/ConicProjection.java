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

import io.github.malapert.jwcs.proj.exception.BadProjectionParameterException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import static io.github.malapert.jwcs.utility.NumericalUtils.HALF_PI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * In conic projections the sphere is thought to be projected onto
 * the surface of a cone which is then opened out. 
 * 
 * <p>
 * The native coordinate system is chosen so that the poles are coincident with
 * the axis of the cone. Native meridians are then projected as uniformly spaced
 * rays that intersect at a point (either directly or by extrapolation), and 
 * parallels are projected as equiangular arcs of concentric circles.
 * </p>
 * <p>
 * Two-standard conic projections are characterized by two
 * latitudes, theta1 and theta2, whose parallels are projected at their true
 * length. In the conic perspective projection these are the latitudes at which 
 * the cone intersects the sphere. One-standard conic projections have 
 * theta1 = theta2 and the cone is tangent to the sphere as shown. 
 * Since conics are designed to minimize distortion in the regions between the
 * two standard parallels they are constructed so that the point on the prime 
 * meridian mid-way between the two standard parallels maps to the reference 
 * point
 * </p>
 * <p>
 * Ref : "Representations of celestial coordinates in FITS", Calabretta, M.R., 
 * and Greisen, E.W., (2002), Astronomy and Astrophysics, 395, 1077-1122. - p11
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public abstract class ConicProjection extends Projection {
    /**
     * Logger.
     */
    protected static final Logger LOG = Logger.getLogger(ConicProjection.class.getName());        
    /**
     * Projection name.
     */
    public static final String NAME = "Conic projections";    
    /**
     * thetaA = (theta1 + theta2) / 2 in radians.
     */
    private final double thetaA;
    /**
     * eta = abs(theta1 - theta2) / 2 in radians.
     */
    private double eta;
    /**
     * Native longitude value in radians for conic projection.
     */
    protected static final double DEFAULT_PHI0 = 0;        
    /**
     * Native longitude in radians of the ﬁducial point for the conic
     * projection.
     */
    private double phio;
    /**
     * Native latitude in radians of the ﬁducial point for the conic projection.
     */
    private double theta0;
    
    /**
     * thetaA - eta.
     */
    protected double theta1;
    
    /**
     * thetaA + eta.
     */
    protected double theta2;
    
    /**
     * Creates a new conic projection.
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     * @param theta_a (theta1 + theta2) / 2 in degrees
     * @param eta abs(theta1 - theta2) / 2 in degrees
     * @throws io.github.malapert.jwcs.proj.exception.BadProjectionParameterException When projection parameters are wrong
     */
    protected ConicProjection(final double crval1, final double crval2, final double theta_a, final double eta) throws BadProjectionParameterException {
        super(crval1, crval2);
        LOG.log(Level.FINER, "INPUTS[deg] (crval1,crval2,theta_a,eta) = ({0},{1},{2},{3})", new Object[]{crval1, crval2, theta_a, eta});
        this.thetaA = Math.toRadians(theta_a);
        this.eta = Math.toRadians(eta);        
        this.theta1 = this.thetaA - this.eta;
        this.theta2 = this.thetaA + this.eta;
        LOG.log(Level.FINEST, "(theta1,theta2)[deg]=({0},{1})", new Object[]{Math.toDegrees(this.theta1),Math.toDegrees(this.theta2)}); 
        checkParameters(theta1, theta2);
        setPhi0(DEFAULT_PHI0);
        setTheta0(this.thetaA);
        setPhip(computeDefaultValueForPhip());
        LOG.log(Level.FINEST, "(phi0,theta0)[DEG]=({0},{1})", new Object[]{Math.toDegrees(DEFAULT_PHI0), Math.toDegrees(this.thetaA)});
        LOG.log(Level.FINEST, "phip[deg]={0}", Math.toDegrees(computeDefaultValueForPhip()));         
    }
    
    /**
     * Checks \u03B8<sub>1</sub>,\u03B8<sub>2</sub> parameters.
     * @param theta1 \u03B8<sub>1</sub> in radians
     * @param theta2 \u03B8<sub>2</sub> in radians
     * @throws BadProjectionParameterException When (theta1,theta2) not in range [-90,90]
     */
    private void checkParameters(final double theta1, final double theta2) throws BadProjectionParameterException {
        final boolean inRangeTheta1 = NumericalUtils.isInInterval(theta1, -HALF_PI, HALF_PI);
        final boolean inRangeTheta2 = NumericalUtils.isInInterval(theta2, -HALF_PI, HALF_PI);
        if(!inRangeTheta1 || !inRangeTheta2) {
            throw new BadProjectionParameterException(this,"(theta1,theta2). Each angle must be -90<=theta1,theta2<=90");
        }
        
    }
    
    @Override
    public String getNameFamily() {
        return NAME;
    }         
    
    /**
     * Computes the native spherical coordinate (\u03D5) in radians along longitude.
     * @param x projection plane coordinate along X
     * @param y projection plane coordinate along Y
     * @param r_theta radius
     * @param y0 y0
     * @param c c
     * @return native spherical coordinate (\u03D5) in radians along longitude
     */
    protected double computePhi(final double x, final double y, final double r_theta, final double y0, final double c) {
        return NumericalUtils.equal(r_theta, 0) ? 0 : NumericalUtils.aatan2(x/r_theta, (y0-y)/r_theta)/c;
    }
    
    /**
     * Computes the projection plane coordinate along X.
     * @param phi the native spherical coordinate (\u03D5) in radians along longitude
     * @param r_theta radius
     * @param c c
     * @return the projection plane coordinate along X
     */
    protected double computeX(final double phi, final double r_theta, final double c) {
        return r_theta * Math.sin(c*phi);
    }

    /**
     * Computes the projection plane coordinate along Y.
     * @param phi the native spherical coordinate (\u03D5) in radians along longitude
     * @param r_theta radius
     * @param c c
     * @param y0 y0
     * @return the projection plane coordinate along Y
     */
    protected double computeY(final double phi, final double r_theta, final double c, final double y0) {
        return -r_theta * Math.cos(c*phi) + y0;
    }    

    @Override
    public final double getPhi0() {
        return phio;
    }

    @Override
    public final double getTheta0() {
        return theta0;
    }  
    
    @Override
    public final void setPhi0(final double phio) {
        this.phio = phio;
    }

    @Override
    public final void setTheta0(final double theta0) {
        this.theta0 = theta0;
    }    

    /**
     * Returns thetaA in radians.
     * @return the thetaA
     */
    protected final double getThetaA() {
        return thetaA;
    }

    /**
     * Returns eta in radians.
     * @return the eta
     */
    protected final double getEta() {
        return eta;
    }
    
    /**
     * Sets eta in radians.
     * @param eta eta
     */
    protected void setEta(final double eta) {
        this.eta = eta;
    }      
    
    @Override
    public boolean inside(final double lon, final double lat) {     
        final double angle = NumericalUtils.distAngle(new double[]{getCrval1(), getCrval2()}, new double[]{lon, lat});
        LOG.log(Level.FINER, "(lont,lat,distAngle)[deg] = ({0},{1}) {2}", new Object[]{Math.toDegrees(lon), Math.toDegrees(lat), angle});
        return NumericalUtils.equal(angle, HALF_PI) || angle <= HALF_PI;
    }   
    
    @Override
    public boolean isLineToDraw(final double[] pos1, final double[] pos2) {
        LOG.log(Level.FINER, "True");
        return true;
    }    

    @Override
    public final Logger getLogger() {
        return LOG;
    }       
}
