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

import io.github.malapert.jwcs.JWcs;
import io.github.malapert.jwcs.proj.exception.BadProjectionParameterException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import java.util.logging.Level;

/**
 * Conic perspective.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class COP extends ConicProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Conic perspective";

    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "\u03B8a=%s \u03B7=%s";

    /**
     * Constant of the cone in radians.
     *
     * This constant is defined as c=sin\u03B8<sub>a</sub>
     */
    private final double c;

    /**
     * Constructs a COP projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and
     * 03B8<sub>a</sub> and \u03B7.
     *
     * \u03B8<sub>a</sub> is set by the FITS keyword PV<code>nbAxis</code>_1 in
     * degrees. \u03B7 is set by the FITS keyword PV<code>nbAxis</code>_2 in
     * degrees.
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @param theta_a \u03B8<sub>a</sub> in degrees and defined as
     * \u03B8<sub>a</sub>=(\u03B8<sub>1</sub>+\u03B8<sub>2</sub>)/2
     * @param eta \u03B7 in degrees and deFINERd as
     * \u03B7=|\u03B8<sub>1</sub>-\u03B8<sub>2</sub>|/2
     * @throws
     * io.github.malapert.jwcs.proj.exception.BadProjectionParameterException
     * When projection parameters are wrong
     */
    public COP(double crval1, double crval2, double theta_a, double eta) throws BadProjectionParameterException {
        super(crval1, crval2, theta_a, eta);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2,theta_a,eta)=({0},{1},{2},{3})", new Object[]{crval1, crval2, theta_a, eta});
        this.c = Math.sin(getTheta_a());
        if (NumericalUtils.equal(this.c, 0)) {
            throw new BadProjectionParameterException(this, "theta_a: " + getTheta_a() + ". It must be !=0");
        }
    }

    @Override
    protected double[] project(double x, double y) throws BadProjectionParameterException {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                                
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double d = Math.cos(getEta());
        if (NumericalUtils.equal(d, 0)) {
            throw new BadProjectionParameterException(this, "Bad value for eta = " + getEta() + ". eta must be > 0");
        }
        double y0 = d / Math.tan(getTheta_a());
        double r_theta = Math.signum(getTheta_a()) * Math.sqrt(Math.pow(xr, 2) + Math.pow(y0 - yr, 2));
        double phi = computePhi(xr, yr, r_theta, y0, c);
        double theta = getTheta_a() + Math.atan(1.0 / Math.tan(getTheta_a()) - r_theta / Math.cos(getEta()));
        double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                        
        return pos;
    }

    @Override
    protected double[] projectInverse(double phi, double theta) throws BadProjectionParameterException {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                                
        phi = phiRange(phi);
        double y0 = Math.cos(getEta()) / Math.tan(getTheta_a());
        double r_theta = y0 - Math.cos(getEta()) * Math.tan(theta - getTheta_a());
        double x = computeX(phi, r_theta, c);
        double y = computeY(phi, r_theta, c, y0);
        double[] coord = {Math.toDegrees(x), Math.toDegrees(y)};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{coord[0],coord[1]});                                                                        
        return coord;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtils.round(Math.toDegrees(this.getTheta_a())), NumericalUtils.round(Math.toDegrees(this.getEta())));
    }

    @Override
    public boolean inside(double lon, double lat) {
        LOG.log(Level.FINER, "(lon,lat)=({0},{1}) {2}",new Object[]{Math.toDegrees(lon),Math.toDegrees(lat),super.inside(lon, lat)});        
        return super.inside(lon, lat);
    }

    @Override
    public ProjectionParameter[] getProjectionParameters() {
        ProjectionParameter p1 = new ProjectionParameter("theta_a", JWcs.PV21, new double[]{-90, 90}, -45);
        ProjectionParameter p2 = new ProjectionParameter("eta", JWcs.PV22, new double[]{0, 90}, 0);
        return new ProjectionParameter[]{p1, p2};
    }

}
