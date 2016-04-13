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
     * Double tolerance.
     */
    protected static final double DOUBLE_TOLERANCE = 1e-12;

    /**
     *
     */
    public static final double HALF_PI = Math.PI * 0.5d;

    /**
     *
     */
    public static final double TWO_PI = Math.PI * 2.0d;

    /**
     *
     */
    public static final double DEFAULT_THETAP = HALF_PI;

    /**
     *
     */
    public static final double DEFAULT_PHIP = 0;
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(Projection.class.getName());

    /**
     * Native longitude in radians of the celestial pole for delta0 &lt; THETA0.
     */
    protected static final double LONPOLE_PI = Math.PI;

    /**
     * Native longitude in radians of the celestial pole for delta0 &gt; THETA0.
     */
    protected static final double LONPOLE_0 = 0;

    private final double crval1;
    private final double crval2;
    private double phip = DEFAULT_PHIP;
    private double thetap = HALF_PI;
    
    private double[] coordNativePole;

    /**
     * Creates an instance of projection by given sky position coordinates.
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     */
    protected Projection(double crval1, double crval2) {
        this.crval1 = Math.toRadians(crval1);
        this.crval2 = Math.toRadians(crval2);
        LOG.log(Level.FINER, "crval1[deg]", crval1);
        LOG.log(Level.FINER, "crval2[deg]", crval2);
        setThetap(DEFAULT_THETAP);
    }

    /**
     * Computes the native spherical coordinates from the projection plane
     * coordinates.
     *
     * @param x projection plane coordinate along X
     * @param y projection plane coordinate along Y
     * @return the native spherical coordinates in radians
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException when
     * there is an error while the projection
     */
    protected abstract double[] project(double x, double y) throws ProjectionException;

    /**
     * Computes the projection plane coordinates from the native spherical
     * coordinates.
     *
     * @param phi native spherical coordinate in radians along longitude
     * @param theta native spherical coordinate in radians along latitude
     * @return the projection plane coordinates
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException when
     * there is an error while the projection
     */
    protected abstract double[] projectInverse(double phi, double theta) throws ProjectionException;

    /**
     * Returns phi0 in radians.
     *
     * @return phi0 in radians.
     */
    public abstract double getPhi0();

    /**
     * Sets the native longitude in radians of the ﬁducial point.
     *
     * @param phio the native longitude in radians of the ﬁducial point
     */
    public abstract void setPhi0(double phio);

    /**
     * Returns the native latitude in radians of the ﬁducial point.
     *
     * @return theta0 in radians.
     */
    public abstract double getTheta0();

    /**
     * Sets the native latitude in radians of the ﬁducial point.
     *
     * @param theta0 the native latitude in radians of the ﬁducial point
     */
    public abstract void setTheta0(double theta0);

    /**
     * Computes phip
     * @return phip in radians.
     */
    protected abstract double computePhip();

    /**
     * Computes the celestial spherical coordinates from the native spherical
     * coordinates.
     *
     * @param phi Native longitude in radians
     * @param theta Native latitude in radians
     * @return Returns (right ascension, declination) in degrees
     */
    protected double[] computeCelestialSpherical(double phi, double theta) {
        double ra, dec;
        double alphap = getCoordNativePole()[0];
        double deltap = getCoordNativePole()[1];
        LOG.log(Level.FINER, "computeCelestialSpherical:phi[rad]", phi);
        LOG.log(Level.FINER, "computeCelestialSpherical:theta[rad]", theta);
        if (deltap >= HALF_PI) {
            ra = alphap + phi - getPhip() - Math.PI;
            dec = theta;
        } else if (deltap <= -HALF_PI) {
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
     * Computes the native spherical coordinates from the projection plane
     * coordinates.
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
     * Computes the coordinates of the native pole.
     *
     * @param phi_p Native longitude in radians of the celestial pole
     * @return Celestial longitude and latitude in radians of the native pole
     */
    protected double[] computeCoordNativePole(double phi_p) {

        // native longitude of the celestial pole in radians
        double alphap, deltap;
        LOG.log(Level.FINER, "computeCoordNativePole:phi_p[rad]", phi_p);
        if (NumericalUtils.equal(getTheta0(), 0.0d, DOUBLE_TOLERANCE) && NumericalUtils.equal(getCrval2(), 0, DOUBLE_TOLERANCE) && NumericalUtils.equal(Math.abs(phi_p - getPhi0()), HALF_PI, DOUBLE_TOLERANCE)) {
            deltap = getThetap();
        } else {
            double deltap_tmp = NumericalUtils.aatan2(Math.sin(getTheta0()), Math.cos(getTheta0()) * Math.cos(phi_p - getPhi0()));
            double deltap_cos = NumericalUtils.aacos(Math.sin(getCrval2()) / Math.sqrt(1 - Math.pow(Math.cos(getTheta0()), 2) * Math.pow(Math.sin(phi_p - getPhi0()), 2)));
            deltap = deltap_tmp + deltap_cos;
            double deltap2 = deltap_tmp - deltap_cos;
            if (Math.abs(deltap) > HALF_PI) {
                deltap = deltap2;
            }
            if (Math.abs(deltap2) > HALF_PI) {
                deltap2 = deltap;
            }
            if (Math.abs(deltap - getThetap()) > Math.abs(deltap2 - getThetap())) {
                deltap = deltap2;
            }
        }

        if (NumericalUtils.equal(deltap, HALF_PI, DOUBLE_TOLERANCE)) {
            alphap = getCrval1() + phi_p - getPhi0() - Math.PI;
        } else if (NumericalUtils.equal(deltap, -HALF_PI, DOUBLE_TOLERANCE)) {
            alphap = getCrval1() - phi_p + getPhi0();
        } else if (NumericalUtils.equal(Math.abs(getCrval2()), HALF_PI, DOUBLE_TOLERANCE)) {
            alphap = getCrval1();
        } else {
            double das = Math.sin(phi_p - getPhi0()) * Math.cos(getTheta0()) / Math.cos(getCrval2());
            double dac = (Math.sin(getTheta0()) - Math.sin(deltap) * Math.sin(getCrval2())) / (Math.cos(deltap) * Math.cos(getCrval2()));
            if (NumericalUtils.equal(das, 0, DOUBLE_TOLERANCE) && NumericalUtils.equal(dac, 0, DOUBLE_TOLERANCE)) {
                alphap = getCrval1() - Math.PI;
            } else {
                alphap = getCrval1() - NumericalUtils.aatan2(das, dac);
            }
        }
        double[] pos = {alphap, deltap};
        LOG.log(Level.FINER, "computeCoordNativePole:pos[rad]", pos);
        return pos;
    }

    /**
     * Sets the native longitude in radians of the celestial pole.
     *
     * @param phip The native longitude in radians of the celestial pole
     */
    public final void setPhip(double phip) {
        this.phip = phip;
        this.coordNativePole = computeCoordNativePole(phip);
    }

    /**
     * Returns the native longitude in radians of the celestial pole.
     *
     * @return the native longitude in radians of the celestial pole
     */
    public double getPhip() {
        return this.phip;
    }

    /**
     * Sets the native latitude in radians of the celestial pole.
     *
     * @param thetap the native latitude in radians of the celestial pole
     */
    public final void setThetap(double thetap) {
        this.thetap = thetap;
    }

    /**
     * Returns the native latitude in radians of the celestial pole.
     *
     * @return the native latitude in radians of the celestial pole
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
     * Computes the celestial spherical coordinates from the projection plane
     * coordinates.
     *
     * @param x projection plane coordinates along X axis
     * @param y projection plane coordinates along Y axis
     * @return the celestial spherical coordinates in degrees
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException when
     * there is an error while the projection
     */
    public double[] projectionPlane2wcs(double x, double y) throws ProjectionException {
        double[] pos = project(x, y);
        return computeCelestialSpherical(pos[0], pos[1]);
    }

    /**
     * Computes the projection plane coordinates from the celestial spherical
     * coordinates.
     *
     * @param ra right ascension in radians
     * @param dec declination in radians.
     * @return the projection plane coordinates.
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException when
     * there is an error while the projection
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
     * Returns the celestial longitude in radians of the ﬁducial point
     *
     * @return the celestial longitude in radians of the ﬁducial point
     */
    public double getCrval1() {
        return this.crval1;
    }

    /**
     * Returns the celestial latitude in radians of the ﬁducial point
     *
     * @return the celestial latitude in radians of the ﬁducial point
     */
    public double getCrval2() {
        return this.crval2;
    }

    /**
     * Returns true if the given lat/lon point is visible in this projection.
     *
     * @param lon longitude in radians.
     * @param lat latitude in radians.
     * @return
     */
    public abstract boolean inside(double lon, double lat);

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
     * @return the coordNativePole
     */
    protected double[] getCoordNativePole() {
        return coordNativePole;
    }
}
