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
import io.github.malapert.jwcs.proj.exception.JWcsError;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import static io.github.malapert.jwcs.utility.NumericalUtils.HALF_PI;
import static io.github.malapert.jwcs.utility.NumericalUtils.TWO_PI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Conversion of intermediate world coordinates (or projection plane
 * coordinates) to celestial coordinates (\u03B1, \u03B4).
 *
 *
 * Projection plane is given and must be computed from pixels coordinates by
 * applying a linear transformation.
 *
 * The conversion is organized by a pipeline doing the following steps:
 * <ul>
 * <li>computes the projection plane coordinates (x,y) to native spherical coordinates
 * (\u03D5, \u03B8) through a spherical projection</li>
 * <li>computes the native spherical coordinates (\u03D5, \u03B8) to celestial spherical
 * coordinates through a spherical coordinate rotation.</li>
 * </ul>
 *
 *
 * <p>
 * Ref : "Representations of celestial coordinates in FITS", Calabretta, M.R.,
 * and Greisen, E.W., (2002), Astronomy and Astrophysics, 395, 1077-1122.
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public abstract class Projection {

    /**
     * Default native latitude of the celestial pole (\u03B8<sub>p</sub>) sets to {@link NumericalUtils#HALF_PI}.
     */
    public static final double DEFAULT_THETAP = HALF_PI;

    /**
     * Default native longitude of the celestial pole (\u03D5<sub>p</sub>) sets to 0.     
     */
    public static final double DEFAULT_PHIP = 0;
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(Projection.class.getName());

    /**
     * Native longitude in radians of the celestial pole for \u03B4<sub>0</sub>
     * &lt; \u03B8<sub>0</sub>.
     */
    protected static final double LONPOLE_PI = Math.PI;

    /**
     * Native longitude in radians of the celestial pole for \u03B4<sub>0</sub>
     * &ge; \u03B8<sub>0</sub>.
     */
    protected static final double LONPOLE_0 = 0;

    /**
     * Celestial longitude \u03B1<sub>0</sub> in radians of the fiducial point.
     */
    private final double crval1;
    /**
     * Celestial longitude \u03B4<sub>0</sub> in radians of the fiducial point.
     */
    private final double crval2;
    /**
     * Initializes the native longitude of the celestial pole (\u03D5<sub>p</sub>) to {@link Projection#DEFAULT_PHIP}. 
     */
    private double phip = DEFAULT_PHIP;
    /**
     * Initializes the native latitude of the celestial pole (\u03B8<sub>p</sub>) to {@link NumericalUtils#HALF_PI}. 
     */
    private double thetap = HALF_PI;    
    /**
     * Celestial longitude and latitude of the native pole (\u03B1<sub>p</sub>, \u03B4<sub>p</sub>).
     */
    private double[] coordNativePole;

    /**
     * Creates an instance of projection by given sky position coordinates.
     * 
     * Initializes {@link Projection#crval1} with crval1, {@link Projection#crval2} with crval2 and {@link Projection#thetap} to {@link Projection#DEFAULT_THETAP}
     *
     * @param crval1 Celestial longitude (\u03B1<sub>0</sub>) in degrees of the ﬁducial point
     * @param crval2 Celestial latitude (\u03B4<sub>0</sub>) in degrees of the ﬁducial point
     */
    protected Projection(double crval1, double crval2) {
        this.crval1 = Math.toRadians(crval1);
        this.crval2 = Math.toRadians(crval2);
        LOG.log(Level.FINER, "INPUTS: crval1[deg]={0} crval2[deg]={1}", new Object[]{crval1,crval2});        
        LOG.log(Level.FINEST, "Theta_p[deg]={0}", new Object[]{Math.toDegrees(DEFAULT_THETAP)});
        setThetap(DEFAULT_THETAP);
    }

    /**
     * Computes the native spherical coordinates (\u03D5, \u03B8) from the projection plane
     * coordinates (x, y).
     *
     * @param x projection plane coordinate along X
     * @param y projection plane coordinate along Y
     * @return the native spherical coordinates (\u03D5, \u03B8) in radians
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException when
     * an error happens while the projection
     */
    protected abstract double[] project(double x, double y) throws ProjectionException;

    /**
     * Computes the projection plane coordinates (x, y) from the native spherical
     * coordinates (\u03D5, \u03B8).
     *
     * @param phi the native spherical coordinate (\u03D5) in radians along longitude
     * @param theta the native spherical coordinate (\u03B8) in radians along latitude
     * @return the projection plane coordinates
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException when
     * an error happens while the projection
     */
    protected abstract double[] projectInverse(double phi, double theta) throws ProjectionException;

    /**
     * Returns the native longitude of the fiducial point (\u03D5<sub>0</sub>) in radians.
     *
     * @return \u03D5<sub>0</sub> in radians.
     */
    public abstract double getPhi0();

    /**
     * Sets the native longitude of the ﬁducial point (\u03D5<sub>0</sub>) in radians.
     *
     * @param phio the native longitude in radians of the ﬁducial point (\u03D5<sub>0</sub>)
     */
    public abstract void setPhi0(double phio);

    /**
     * Returns the native latitude of the ﬁducial point (\u03B8<sub>0</sub>) in radians.
     *
     * @return \u03B8<sub>0</sub> in radians.
     */
    public abstract double getTheta0();

    /**
     * Sets the native latitude in radians of the ﬁducial point (\u03B8<sub>0</sub>).
     *
     * @param theta0 the native latitude in radians of the ﬁducial point (\u03B8<sub>0</sub>)
     */
    public abstract void setTheta0(double theta0);

    /**
     * Computes the default value for \u03D5<sub>p</sub>. The default value of
     * \u03D5<sub>p</sub> will be {@link Projection#LONPOLE_0} for \u03B4<sub>0</sub> &ge;
     * \u03B8<sub>0</sub> or {@link Projection#LONPOLE_PI} for \u03B4<sub>0</sub> &lt; \u03B8<sub>0</sub>.
     *
     * @return \u03D5<sub>p</sub> in radians.
     */
    protected final double computeDefaultValueForPhip() {
        double phi_p;
        if (NumericalUtils.equal(getCrval2(), getTheta0())) {
            phi_p = LONPOLE_0;
        } else if (getCrval2() > getTheta0()) {
            phi_p = LONPOLE_0;
        } else {
            phi_p = LONPOLE_PI;
        }
        return phi_p;
    }

    /**
     * Computes the celestial spherical coordinates (\u03B1, \u03B4) from the
     * native spherical coordinates (\u03D5, \u03B8). 
     * 
     * The computation is performed by applying the spherical coordinate rotation.
     *
     * general case:<br>
     * ------------<br>
     * \u03B1 = \u03B1<sub>p</sub> + arg(sin\u03B8cos\u03B4<sub>p</sub> -
     * cos\u03B8sin\u03B4<sub>p</sub>cos(\u03D5-\u03D5<sub>p</sub>),-cos\u03B8sin(\u03D5-\u03D5<sub>p</sub>))<br>
     * \u03B4 = asin(sin\u03B8sin\u03B4<sub>p</sub> +
     * cos\u03B8cos\u03B4<sub>p</sub>cos(\u03D5-\u03D5<sub>p</sub>))<br>
     * <br>
     * Special cases:<br>
     * -------------<br>
     * if \u03B4<sub>p</sub> = HALF_PI<br>
     * \u03B1 = \u03B1<sub>p</sub> + \u03D5 - \u03D5<sub>p</sub> - PI<br>
     * \u03B4 = \u03B8<br>
     * if \u03B4<sub>p</sub> = -HALF_PI<br>
     * \u03B1 = \u03B1<sub>p</sub> - \u03D5 + \u03D5<sub>p</sub><br>
     * \u03B4 = -\u03B8<br>
     *
     *
     * @param phi Native longitude (\u03D5) in radians
     * @param theta Native latitude (\u03B8) in radians
     * @return Returns the celestial spherical coordinates (\u03B1, \u03B4) in
     * degrees
     */
    protected double[] computeCelestialSpherical(double phi, double theta) {
        double ra, dec;
        LOG.log(Level.FINER, "INPUTS[deg]: (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});        
        
        double alphap = getCoordNativePole()[0];
        double deltap = getCoordNativePole()[1];
        LOG.log(Level.FINEST, "CoordinateNativePole[deg]: (alphap,deltap)=({0},{1})", new Object[]{Math.toDegrees(alphap),Math.toDegrees(deltap)});        
        
        if (NumericalUtils.equal(deltap, HALF_PI)) {
            ra = alphap + phi - getPhip() - Math.PI;
            dec = theta;
        } else if (NumericalUtils.equal(deltap, -HALF_PI)) {
            ra = alphap - phi + getPhip();
            dec = -theta;
        } else {            
            ra = alphap + NumericalUtils.aatan2(-Math.cos(theta) * Math.sin(phi - phip),
                    Math.sin(theta) * Math.cos(deltap)
                    - Math.cos(theta) * Math.sin(deltap)
                    * Math.cos(phi - getPhip()),0);            

            dec = NumericalUtils.aasin(Math.sin(theta) * Math.sin(deltap)
                    + Math.cos(theta) * Math.cos(deltap)
                    * Math.cos(phi - getPhip()));
        }
        // convert ra, dec to degrees
        ra = Math.toDegrees(ra);
        dec = Math.toDegrees(dec);
        if (ra < 0) {
            ra += 360;
        }
        
        double[] pos = {ra, dec};
        LOG.log(Level.FINER, "OUTPUTS[deg] pos=({0},{1})", new Object[]{Math.toDegrees(pos[0]),Math.toDegrees(pos[1])});
        return pos;
    }

    /**
     * Computes the native spherical coordinates (\u03D5, \u03B8) from 
     * the celestial spherical coordinates (\u03B1, \u03B4)  .
     * 
     * The computation is performed by applying the inverse of the spherical 
     * coordinate rotation.<br>
     * \u03D5 = \u03D5<sub>p</sub> + arg(sin\u03B4cos\u03B4<sub>p</sub>-cos\u03B4sin\u03B4<sub>p</sub>cos(\u03B1-\u03B1<sub>p</sub>),-cos\u03B4sin(\u03B1-\u03B1<sub>p</sub>))<br>
     * \u03B8 = asin(sin\u03B4sin\u03B4<sub>p</sub>+cos\u03B4cos\u03B4<sub>p</sub>cos(\u03B1-\u03B1<sub>p</sub>))
     *
     * @param ra Celestial longitude in radians
     * @param dec Celestial latitude in radians
     * @return Returns native longitude and latitude in radians
     */
    protected double[] computeNativeSpherical(double ra, double dec) {
        LOG.log(Level.FINER, "INPUTS[deg]: (ra,dec)=({0},{1})", new Object[]{Math.toDegrees(ra),Math.toDegrees(dec)});        

        double ra_p = getCoordNativePole()[0];
        double dec_p = getCoordNativePole()[1];
        LOG.log(Level.FINEST, "CoordinateNativePole[deg]: (alphap,deltap)=({0},{1})", new Object[]{Math.toDegrees(ra_p),Math.toDegrees(dec_p)});                
        
        double phi, theta;
        if (NumericalUtils.equal(dec_p, HALF_PI)) {
            phi = Math.PI + getPhip() + ra - ra_p;
            theta = dec;
        } else if (NumericalUtils.equal(dec_p, -HALF_PI)) {
            phi = getPhip() - ra + ra_p;
            theta = -dec;            
        } else {
            phi = getPhip() + NumericalUtils.aatan2(-Math.cos(dec) * Math.sin(ra - ra_p),
                    Math.sin(dec) * Math.cos(dec_p)
                    - Math.cos(dec) * Math.sin(dec_p)
                    * Math.cos(ra - ra_p), 0);
            theta = NumericalUtils.aasin(Math.sin(dec) * Math.sin(dec_p)
                     + Math.cos(dec) * Math.cos(dec_p)
                     * Math.cos(ra - ra_p));      
        }                   
        double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});
        return pos;
    }

    /**
     * Computes the celestial coordinates (\u03B1<sub>p</sub>,
     * \u03B4<sub>p</sub>) of the native pole
     * (\u03B4<sub>p</sub>=\u03B8<sub>p</sub>).
     *
     * Projections such as the cylindricals and conics for which
     * (\u03D5<sub>0</sub>, \u03B8<sub>0</sub>) = (0, HALF_PI) are handled by
     * providing formulae to compute (\u03B1<sub>p</sub>, \u03B4<sub>p</sub>)
     * from (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     *
     * @param phi_p Native longitude (\u03D5<sub>p</sub>) in radians of the
     * celestial pole
     * @return Celestial (\u03B1<sub>p</sub>, \u03B4<sub>p</sub>) longitude and
     * latitude in radians of the native pole
     */
    protected double[] computeCoordNativePole(double phi_p) {

        if (NumericalUtils.equal(getPhi0(), 0)
                && NumericalUtils.equal(getTheta0(), HALF_PI)) {
            LOG.log(Level.FINEST,"No need to compute the coordinates of the native pole");
            return new double[]{getCrval1(), getCrval2()};
        }

        // native longitude of the celestial pole in radians
        double alphap, deltap;
        if (NumericalUtils.equal(getTheta0(), 0.0d) && NumericalUtils.equal(getCrval2(), 0) && NumericalUtils.equal(Math.abs(phi_p - getPhi0()), HALF_PI)) {
            deltap = getThetap();
        } else {
            double deltap_arg = NumericalUtils.aatan2(Math.sin(getTheta0()), Math.cos(getTheta0()) * Math.cos(phi_p - getPhi0()));
            double deltap_acos = NumericalUtils.aacos(Math.sin(getCrval2()) / Math.sqrt(1 - Math.pow(Math.cos(getTheta0()), 2) * Math.pow(Math.sin(phi_p - getPhi0()), 2)));
            double deltap1 = deltap_arg + deltap_acos;
            double deltap2 = deltap_arg - deltap_acos;

            if (NumericalUtils.equal(getTheta0(), 0)
                    && NumericalUtils.equal(getCrval2(), 0)
                    && NumericalUtils.equal(Math.abs(getPhip()-getPhi0()), HALF_PI)) {
                deltap = getThetap();
            } else {

                boolean isDeltap1InInterval = NumericalUtils.isInInterval(deltap1, -HALF_PI, HALF_PI);
                boolean isDeltap2InInterval = NumericalUtils.isInInterval(deltap2, -HALF_PI, HALF_PI);

                if (isDeltap1InInterval && isDeltap2InInterval) {
                    double diff1 = Math.abs(deltap1 - getThetap());
                    double diff2 = Math.abs(deltap2 - getThetap());
                    deltap = (diff1 < diff2) ? deltap1 : deltap2;
                } else if (isDeltap1InInterval) {
                    deltap = deltap1;
                } else if (isDeltap2InInterval) {
                    deltap = deltap2;
                } else {
                    throw new JWcsError("No valid solution for thetap");
                }
            }
        }

        if (NumericalUtils.equal(Math.abs(getCrval2()), HALF_PI)) {
            alphap = getCrval1();
        } else if (NumericalUtils.equal(deltap, HALF_PI)) {
            alphap = getCrval1() + phi_p - getPhi0() - Math.PI;
        } else if (NumericalUtils.equal(deltap, -HALF_PI)) {
            alphap = getCrval1() - phi_p + getPhi0();      
        } else {
            double das = Math.sin(phi_p - getPhi0()) * Math.cos(getTheta0()) / Math.cos(getCrval2());
            double dac = (Math.sin(getTheta0()) - Math.sin(deltap) * Math.sin(getCrval2())) / (Math.cos(deltap) * Math.cos(getCrval2()));
            alphap = getCrval1() - NumericalUtils.aatan2(das, dac);
        }
        double[] pos = {alphap, deltap};        
        return pos;
    }

    /**
     * Sets the native longitude in radians of the celestial pole (\u03D5<sub>p</sub>).
     *
     * @param phip The native longitude in radians of the celestial pole (\u03D5<sub>p</sub>)
     */
    public final void setPhip(double phip) {
        this.phip = phip;
        this.coordNativePole = computeCoordNativePole(phip);
    }

    /**
     * Returns the native longitude in radians of the celestial pole (\u03D5<sub>p</sub>).
     *
     * @return the native longitude in radians of the celestial pole (\u03D5<sub>p</sub>)
     */
    public double getPhip() {
        return this.phip;
    }

    /**
     * Sets the native latitude in radians of the celestial pole (\u03B8<sub>p</sub>).
     *
     * @param thetap the native latitude in radians of the celestial pole (\u03B8<sub>p</sub>)
     */
    public final void setThetap(double thetap) {
        this.thetap = thetap;
    }

    /**
     * Returns the native latitude in radians of the celestial pole (\u03B8<sub>p</sub>).
     *
     * @return the native latitude in radians of the celestial pole (\u03B8<sub>p</sub>)
     */
    public double getThetap() {
        return this.thetap;
    }

    /**
     * Returns phi between [-PI, PI].
     *
     * @param phi phi in radians
     * @return phi between [-PI, PI]
     */
    protected final double phiRange(double phi) {
        phi = phi % (TWO_PI);
        if (phi > Math.PI) {
            phi -= TWO_PI;
        } else if (phi < -Math.PI) {
            phi += TWO_PI;
        }
        return phi;
    }

    /**
     * Computes the celestial spherical coordinates (\u03B1, \u03B4) from the projection plane
     * coordinates (\u03B4, \u03B8).
     *
     * @param x projection plane longitude (\u03D5) in radians
     * @param y projection plane latitude (\u03B8) in radians
     * @return the celestial spherical coordinates in degrees (\u03B1, \u03B4)
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException when
     * an error happens while the projection
     */
    public double[] projectionPlane2wcs(double x, double y) throws ProjectionException {
        double[] pos = project(x, y);
        return computeCelestialSpherical(pos[0], pos[1]);
    }

    /**
     * Computes the projection plane coordinates (\u03B4, \u03B8) from the celestial spherical
     * coordinates (\u03B1, \u03B4).
     *
     * @param ra right ascension in radians (\u03B1)
     * @param dec declination in radians (\u03B4)
     * @return the projection plane coordinates (\u03D5, \u03B8)
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException when
     * an error happens while the projection
     */
    public double[] wcs2projectionPlane(double ra, double dec) throws ProjectionException {
        ra = NumericalUtils.normalizeLongitude(ra);
        double[] nativeSpherical = computeNativeSpherical(ra, dec);
        return projectInverse(nativeSpherical[0], nativeSpherical[1]);
    }

    /**
     * Returns the celestial longitude in radians of the ﬁducial point (\u03B1<sub>0</sub>)
     *
     * @return \u03B1<sub>0</sub> in radians
     */
    public double getCrval1() {
        return this.crval1;
    }

    /**
     * Returns the celestial latitude in radians of the ﬁducial point (\u03B4<sub>0</sub>) 
     *
     * @return  \u03B4<sub>0</sub> in radians
     */
    public double getCrval2() {
        return this.crval2;
    }

    /**
     * Returns true if the given lat/lon point is visible in this projection.
     *
     * @param lon longitude in radians (\u03B1).
     * @param lat latitude in radians (\u03B4).
     * @return True when the point is visible otherwise False.
     */
    public abstract boolean inside(double lon, double lat);
    
    /**
     * Checks if the line is visible.
     * @param pos1 First point of the line
     * @param pos2 Last point of the line
     * @return Returns True when the line is visible otherwise False
     */
    public abstract boolean isLineToDraw(double[] pos1, double[] pos2); 

    /**
     * Returns the projection's name.
     *
     * @return the projection's name
     */
    public abstract String getName();

    /**
     * Returns the projection's family.
     *
     * @return the projection's family
     */
    public abstract String getNameFamily();

    /**
     * Returns the projection's description.
     *
     * @return the projection's description
     */
    public abstract String getDescription();

    /**
     * Returns the celestial longitude and latitude of the native pole (\u03B1<sub>p</sub>, \u03B4<sub>p</sub>).
     * @return the coordNativePole (\u03B1<sub>p</sub>, \u03B4<sub>p</sub>)
     */
    protected double[] getCoordNativePole() {
        return coordNativePole;
    }
    
    /**
     * Returns the logger of the projection family.
     * @return the logger
     */
    public abstract Logger getLogger();

    /**
     * Returns the projection parameters for a specific projection.
     * @return the projection parameters.
     */
    public abstract ProjectionParameter[] getProjectionParameters();
    
    /**
     * The ProjectionParameter object deFINERs few metadata about a projection parameter.
     * 
     * This object is used in the GUI to display the projection parameter. It deFINERs :
     * <ul>
     * <li>The name of the parameter.
     * <li>The PV keyword related to the name.
     * <li>The valid interval of the parameter
     * <li>The default value of the parameter to display in the GUI.    
     * </ul>
     * To deFINER an undeFINERd value, the value is set to Double.POSITIVE_INFINITY 
     * for positive number and Double.NEGATIVE_INFINITY for negative number.
     */
    public class ProjectionParameter {
        
        /**
         * Name of the parameter.
         */
        private final String name ;
        /**
         * PV keyword related to the name of the parameter.
         */
        private final String PVName;
        /**
         * valid interval of the parameter.
         */
        private final double[] validInterval;
        /**
         * Default value of the parameter.
         */
        private final double defaultValue;
        
        /**
         * Creates a new parameter.
         * @param name its name
         * @param PVName its related PV keyword in FITS
         * @param validInterval its valid interval
         * @param defaultValue  its default value.
         */
        public ProjectionParameter(String name, String PVName, double[] validInterval, double defaultValue) {
            this.name = name;
            this.PVName = PVName;
            this.validInterval = validInterval;
            this.defaultValue = defaultValue;
        }
        
        /**
         * Returns the name of the parameter.
         * @return the name of the parameter
         */
        public String getName() {
            return this.name;
        }
        
        /**
         * Returns the related keyword to PV.
         * @return the related keyword to PV
         */
        public String getPVName() {
            return this.PVName;
        }
        
        /**
         * Returns the valid interval of the parameter.
         * @return the valid interval of the parameter
         */
        public double[] getValidInterval() {
            return this.validInterval;
        }
        
        /**
         * Returns the default value of the parameter.
         * @return the default value
         */
        public double getDefaultValue() {
            return this.defaultValue;
        } 
        
    }    
}
