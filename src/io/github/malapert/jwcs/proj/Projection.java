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

import io.github.malapert.jwcs.proj.exception.JWcsError;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Conversion of intermediate world coordinates (or projection plane
 * coordinates) to celestial coordinates.
 *
 *
 * Projection plane is given and must be computed from pixels coordinates by
 * applying a linear transformation.
 *
 * The conversion is organized by a pipeline doing the following steps:
 * <ul>
 * <li>computes the projection plane coordinates to native spherical coordinates
 * through a spherical projection</li>
 * <li>computes the native spherical coordinates to celestial spherical
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
 * @version 1.0
 */
public abstract class Projection {

    /**
     * Double tolerance for numerical precision operations sets to 1e-12.
     */
    protected static final double DOUBLE_TOLERANCE = 1e-12;

    /**
     * Half PI value.
     */
    public static final double HALF_PI = Math.PI * 0.5d;

    /**
     * Two Pi value.
     */
    public static final double TWO_PI = Math.PI * 2.0d;

    /**
     * Default native latitude of the celestial pole (\u03B8<sub>p</sub>) sets to {@link Projection#HALF_PI}.
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
     * Initializes the native latitude of the celestial pole (\u03B8<sub>p</sub>) to {@link Projection#HALF_PI}. 
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
        LOG.log(Level.FINER, "crval1[deg]", crval1);
        LOG.log(Level.FINER, "crval2[deg]", crval2);
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
     * @param phi native spherical coordinate (\u03D5) in radians along longitude
     * @param theta native spherical coordinate (\u03B8) in radians along latitude
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
        if (NumericalUtils.equal(getCrval2(), getTheta0(), DOUBLE_TOLERANCE)) {
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
        double alphap = getCoordNativePole()[0];
        double deltap = getCoordNativePole()[1];
        LOG.log(Level.FINER, "computeCelestialSpherical:phi[rad]", phi);
        LOG.log(Level.FINER, "computeCelestialSpherical:theta[rad]", theta);
        if (NumericalUtils.equal(deltap, HALF_PI, DOUBLE_TOLERANCE)) {
            ra = alphap + phi - getPhip() - Math.PI;
            dec = theta;
        } else if (NumericalUtils.equal(deltap, -HALF_PI, DOUBLE_TOLERANCE)) {
            ra = alphap - phi + getPhip();
            dec = -theta;
        } else {
            ra = alphap + NumericalUtils.aatan2(-Math.cos(theta) * Math.sin(phi - phip),
                    Math.sin(theta) * Math.cos(deltap)
                    - Math.cos(theta) * Math.sin(deltap)
                    * Math.cos(phi - getPhip()));

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
        LOG.log(Level.FINER, "computeCelestialSpherical:pos[deg]", pos);
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
        LOG.log(Level.FINER, "computeNativeSpherical:ra[rad]", ra);
        LOG.log(Level.FINER, "computeNativeSpherical:dec[rad]", dec);
        double ra_p = getCoordNativePole()[0];
        double dec_p = getCoordNativePole()[1];

        double phi = getPhip() + NumericalUtils.aatan2(-Math.cos(dec) * Math.sin(ra - ra_p),
                Math.sin(dec) * Math.cos(dec_p)
                - Math.cos(dec) * Math.sin(dec_p)
                * Math.cos(ra - ra_p));
        double theta = NumericalUtils.aasin(Math.sin(dec) * Math.sin(dec_p)
                + Math.cos(dec) * Math.cos(dec_p)
                * Math.cos(ra - ra_p));

        double[] pos = {phi, theta};
        LOG.log(Level.FINER, "computeNativeSpherical:pos[rad]", pos);
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

        if (NumericalUtils.equal(getPhi0(), 0, DOUBLE_TOLERANCE)
                && NumericalUtils.equal(getTheta0(), HALF_PI, DOUBLE_TOLERANCE)) {
            return new double[]{getCrval1(), getCrval2()};
        }

        // native longitude of the celestial pole in radians
        double alphap, deltap;
        LOG.log(Level.FINER, "computeCoordNativePole:phi_p[rad]", phi_p);
        if (NumericalUtils.equal(getTheta0(), 0.0d, DOUBLE_TOLERANCE) && NumericalUtils.equal(getCrval2(), 0, DOUBLE_TOLERANCE) && NumericalUtils.equal(Math.abs(phi_p - getPhi0()), HALF_PI, DOUBLE_TOLERANCE)) {
            deltap = getThetap();
        } else {
            double deltap_arg = NumericalUtils.aatan2(Math.sin(getTheta0()), Math.cos(getTheta0()) * Math.cos(phi_p - getPhi0()));
            double deltap_acos = NumericalUtils.aacos(Math.sin(getCrval2()) / Math.sqrt(1 - Math.pow(Math.cos(getTheta0()), 2) * Math.pow(Math.sin(phi_p - getPhi0()), 2)));
            double deltap1 = deltap_arg + deltap_acos;
            double deltap2 = deltap_arg - deltap_acos;

            if (NumericalUtils.equal(getTheta0(), 0, DOUBLE_TOLERANCE)
                    && NumericalUtils.equal(getCrval2(), 0, DOUBLE_TOLERANCE)
                    && NumericalUtils.equal(getPhip(), getPhi0(), HALF_PI)) {
                deltap = getThetap();
            } else {

                boolean isDeltap1InInterval = NumericalUtils.isInInterval(deltap1, -HALF_PI, HALF_PI, DOUBLE_TOLERANCE);
                boolean isDeltap2InInterval = NumericalUtils.isInInterval(deltap2, -HALF_PI, HALF_PI, DOUBLE_TOLERANCE);

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

        if (NumericalUtils.equal(Math.abs(getCrval2()), HALF_PI, DOUBLE_TOLERANCE)) {
            alphap = getCrval1();
        } else if (NumericalUtils.equal(deltap, HALF_PI, DOUBLE_TOLERANCE)) {
            alphap = getCrval1() + phi_p - getPhi0() - Math.PI;
        } else if (NumericalUtils.equal(deltap, -HALF_PI, DOUBLE_TOLERANCE)) {
            alphap = getCrval1() - phi_p + getPhi0();      
        } else {
            double das = Math.sin(phi_p - getPhi0()) * Math.cos(getTheta0()) / Math.cos(getCrval2());
            double dac = (Math.sin(getTheta0()) - Math.sin(deltap) * Math.sin(getCrval2())) / (Math.cos(deltap) * Math.cos(getCrval2()));
            alphap = getCrval1() - NumericalUtils.aatan2(das, dac);
        }
        double[] pos = {alphap, deltap};
        LOG.log(Level.FINER, "computeCoordNativePole:pos[rad]", pos);
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
        LOG.log(Level.FINER, "wcs2projectionPlane:ra[rad]", ra);
        LOG.log(Level.FINER, "wcs2projectionPlane:dec[rad]", dec);
        ra = NumericalUtils.normalizeLongitude(ra);
        double[] nativeSpherical = computeNativeSpherical(ra, dec);
        double[] projectionPlane = projectInverse(nativeSpherical[0], nativeSpherical[1]);
        LOG.log(Level.FINER, "wcs2projectionPlane:pos[rad]", projectionPlane);
        return projectionPlane;
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
}
