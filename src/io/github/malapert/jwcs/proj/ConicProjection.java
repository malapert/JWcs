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
 * @version 1.0
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
     * theta_a = (theta1 + theta2) / 2 in radians.
     */
    private final double theta_a;
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
     * Creates a new conic projection.
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     * @param theta_a (theta1 + theta2) / 2 in degrees
     * @param eta abs(theta1 - theta2) / 2 in degrees
     */
    protected ConicProjection(double crval1, double crval2, double theta_a, double eta) {
        super(crval1, crval2);
        LOG.log(Level.FINER, "theta_a[deg]", theta_a);
        LOG.log(Level.FINER, "eta[deg]", eta);
        double[] angles = fixInputParameters(theta_a, eta);
        this.theta_a = Math.toRadians(angles[0]);
        this.eta = Math.toRadians(angles[1]);        
        setPhi0(DEFAULT_PHI0);
        setTheta0(this.theta_a);
    }
    
    @Override
    public String getNameFamily() {
        return NAME;
    }     
    
    /**
     * Fix Theta_a and eta.
     * 
     * <p>
     * Sets theta_a to 45 when theta_a = 0. Sets eta to 0 when eta is
     * too large     
     * </p>
     * @param theta_a in degrees
     * @param eta in degrees
     * @return corrected values of (theta_a, eta)
     */
    private double[] fixInputParameters(double theta_a, double eta) {
        double theta_fixed;
        double eta_fixed;
        if (theta_a == 0) {
            LOG.log(Level.WARNING, "ThetaA=0 not allowed -- defaulting to 45 deg", theta_a);
            theta_fixed = 45;
        } else {
            theta_fixed = theta_a;
        }
        
        if ((theta_a-Math.abs(eta) < -180) || (theta_a+Math.abs(eta) > 180)) {
            LOG.log(Level.WARNING, "Eta too large, set eta to 0");
            eta_fixed = 0;
        } else {
            eta_fixed = eta;
        }
        double angles[] = {theta_fixed, eta_fixed};
        return angles;
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
    public final void setPhi0(double phio) {
        this.phio = phio;
    }

    @Override
    public final void setTheta0(double theta0) {
        this.theta0 = theta0;
    }    

    /**
     * Returns theta_a in radians.
     * @return the theta_a
     */
    protected final double getTheta_a() {
        return theta_a;
    }

    /**
     * Returns eta in radians.
     * @return the eta
     */
    protected double getEta() {
        return eta;
    }
    
    /**
     * Sets eta in radians.
     * @param eta eta
     */
    protected void setEta(double eta) {
        this.eta = eta;
    }    
    
    @Override
    protected double[] computeCelestialSpherical(double phi, double theta, double alphap, double deltap, double phip) {
        double[] posNativePole = computeCoordNativePole(phip);
        LOG.log(Level.FINER, "(alphap, deltap) of native pole", posNativePole);
        return super.computeCelestialSpherical(phi, theta, posNativePole[0], posNativePole[1], phip);
    }

    @Override
    protected double[] computeNativeSpherical(double ra, double dec, double ra_p, double dec_p, double phi_p) {
        double[] posNativePole = computeCoordNativePole(phi_p);
        LOG.log(Level.FINER, "(alphap, deltap) of coordinate native pole", posNativePole);
        return super.computeNativeSpherical(ra, dec, posNativePole[0], posNativePole[1], phi_p);
    }    
    
    @Override
    public boolean inside(double lon, double lat) {      
       return true;      
    }     
}
