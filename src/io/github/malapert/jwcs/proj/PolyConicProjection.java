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
 * Polyconic projection.
 * 
 * <p>
 * Polyconics are generalizations of the standard conic projections; 
 * the parallels of latitude are projected as circular arcs which may or may 
 * not be concentric, and meridians are curved rather than straight as in the 
 * standard conics.
 * </p> 
 * <p>
 * Ref : "Representations of celestial coordinates in FITS", Calabretta, M.R., 
 * and Greisen, E.W., (2002), Astronomy and Astrophysics, 395, 1077-1122. - p21
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public abstract class PolyConicProjection extends Projection {
    /**
     * Logger.
     */
    protected static final Logger LOG = Logger.getLogger(PolyConicProjection.class.getName());  
    
    /**
     * Projection name.
     */
    public static final String NAME = "Polyconic and pseudoconic projections";
    
    /**
     * Native longitude value in radians for cylindrical projection.
     */
    protected static final double DEFAULT_PHI0 = 0;
    /**
     * Native latitude value in radians for cylindrical projection.
     */
    protected static final double DEFAULT_THETA0 = 0;
    
    private final double theta1;
    /**
     * Native longitude in radians of the ﬁducial point for the polyconic
     * Projection.
     */    
    private double phio;
    /**
     * Native latitude in radians of the ﬁducial point for the polyconic
     * Projection.
     */    
    private double theta0;

    /**
     * Creates a new polyconic projection
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     * @param theta1 theta1 in degrees
     */
    protected PolyConicProjection(double crval1, double crval2, double theta1) {
        super(crval1, crval2);
        LOG.log(Level.FINER, "theta1[deg]", theta1);
        if (theta1 == 0) {
            LOG.log(Level.WARNING, "ThetaA=0 not allowed -- defaulting to 45 deg", theta1);
            theta1 = 45;
        }        
        this.theta1 = Math.toRadians(theta1);
        setPhi0(DEFAULT_PHI0);
        setTheta0(DEFAULT_THETA0);
    }
    
    @Override
    public String getNameFamily() {
        return NAME;
    }     

    @Override
    public double getPhi0() {
        return phio;
    }

    @Override
    public double getTheta0() {
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

    /**
     * Returns theta1 in radians.
     * @return the theta1
     */
    protected double getTheta1() {
        return theta1;
    }
    
    @Override
    public boolean inside(double lon, double lat) {      
       return true;      
    }       
}
