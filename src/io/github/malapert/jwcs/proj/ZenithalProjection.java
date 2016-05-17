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
import java.util.logging.Logger;

/**
 * Zenithal or azimuthal projections all map the sphere directly onto a plane.
 *
 * <p>
 * The native coordinate system is chosen to have the polar axis orthogonal to
 * the plane of projection at the refer.
 * </p>
 * <p>
 * Ref : "Representations of celestial coordinates in FITS", Calabretta, M.R.,
 * and Greisen, E.W., (2002), Astronomy and Astrophysics, 395, 1077-1122. - p9
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public abstract class ZenithalProjection extends Projection {

    /**
     * Logger.
     */
    protected static final Logger LOG = Logger.getLogger(ZenithalProjection.class.getName());

    /**
     * Projection name.
     */
    public static final String NAME = "Zenithal (azimuthal) projections";
    /**
     * Default Native longitude (\u03D5<SUB>0</SUB>) value in radians for
     * zenithal projection.
     */
    public static final double DEFAULT_PHI0 = 0;
    /**
     * Default Native latitude (\u03B8<SUB>0</SUB>) value in radians for
     * zenithal projection.
     */
    public static final double DEFAULT_THETA0 = HALF_PI;

    /**
     * Native longitude (\u03D5<SUB>0</SUB>) in radians of the ﬁducial point for
     * the Zenithal Projection.
     */
    private double phi0;
    /**
     * Native latitude (\u03B8<SUB>0</SUB>) in radians of the ﬁducial point for
     * the Zenithal Projection.
     */
    private double theta0;

    /**
     * Creates a zenithal projection based on the celestial longitude and
     * latitude of the ﬁducial point. Creates a zenithal projection by setting :
     * <ul>
     * <li>(\u03D5<SUB>0</SUB>, \u03B8<SUB>0</SUB>) = (0, HALF_PI)</li>
     * <li>by computing \u03D5<SUB>p</SUB></li>
     * </ul>
     *
     * @see ZenithalProjection#computeDefaultValueForPhip()
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * (\u03B1<sub>0</sub>)
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     * (\u03B4<sub>0</sub>)
     */
    protected ZenithalProjection(final double crval1, final double crval2) {
        super(crval1, crval2);
        setPhi0(DEFAULT_PHI0);
        setTheta0(DEFAULT_THETA0);
        setPhip(computeDefaultValueForPhip());
        LOG.log(Level.FINER, "INPUTS[deg]: (crval1,crval2)=({0},{1})", new Object[]{crval1, crval2});
        LOG.log(Level.FINEST, "(phi0,theta0)[DEG]=({0},{1})", new Object[]{Math.toDegrees(DEFAULT_PHI0), Math.toDegrees(DEFAULT_THETA0)});
        LOG.log(Level.FINEST, "phip[deg]={0}", Math.toDegrees(computeDefaultValueForPhip()));

    }

    @Override
    public double getPhi0() {
        return phi0;
    }

    @Override
    public double getTheta0() {
        return theta0;
    }

    @Override
    public final void setPhi0(final double phi0) {
        this.phi0 = phi0;
    }

    @Override
    public final void setTheta0(final double theta0) {
        this.theta0 = theta0;
    }

    @Override
    protected double[] computeCoordNativePole(final double phi_p) {
        return new double[]{getCrval1(), getCrval2()};
    }

    /**
     * Computes the radius.
     * @param x the projection plane coordinate along X
     * @param y the projection plane coordinate along Y
     * @return the radius
     */
    protected double computeRadius(final double x, final double y) {
        return Math.hypot(x, y);
    }

    /**
     * Computes the projection plane coordinate along X.
     * @param radius the radius
     * @param phi the native spherical coordinate (\u03D5) in radians along longitude
     * @return the projection plane coordinate along X
     */
    protected double computeX(final double radius, final double phi) {
        return radius * Math.sin(phi);
    }

    /**
     * Computes the projection plane coordinate along Y.
     * @param radius the radius
     * @param phi the native spherical coordinate (\u03D5) in radians along longitude
     * @return the projection plane coordinate along Y
     */    
    protected double computeY(final double radius, final double phi) {
        return -radius * Math.cos(phi);
    }

    /**
     * Computes the  native spherical coordinate (\u03D5) in radians along longitude.
     * @param x the projection plane coordinate along X
     * @param y the projection plane coordinate along Y
     * @param radius the radius
     * @return the  native spherical coordinate (\u03D5) in radians along longitude
     */
    protected double computePhi(final double x, final double y, final double radius) {
        return NumericalUtils.equal(radius, 0) ? 0 : NumericalUtils.aatan2(x, -y);
    }

    @Override
    public String getNameFamily() {
        return NAME;
    }

    @Override
    public boolean inside(final double lon, final double lat) {
        double angle = NumericalUtils.distAngle(new double[]{getCrval1(), getCrval2()}, new double[]{lon, lat});
        LOG.log(Level.FINER, "(lont,lat,distAngle)[deg] = ({0},{1}) {2}", new Object[]{Math.toDegrees(lon), Math.toDegrees(lat), angle});
        return NumericalUtils.equal(angle, HALF_PI) || angle <= HALF_PI;
    }

    @Override
    public boolean isLineToDraw(final double[] pos1, final double[] pos2) {
        LOG.log(Level.FINER, "true");
        return true;
    }
    
    @Override
    public final Logger getLogger() {
        return LOG;
    }    
}
