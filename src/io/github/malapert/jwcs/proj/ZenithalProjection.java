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
     * Default Native longitude (\u03D5<SUB>0</SUB>) value in radians for zenithal projection.
     */
    public static final double DEFAULT_PHI0 = 0;
    /**
     * Default Native latitude (\u03B8<SUB>0</SUB>) value in radians for zenithal projection.
     */
    public static final double DEFAULT_THETA0 = HALF_PI;

    /**
     * Native longitude (\u03D5<SUB>0</SUB>) in radians of the ﬁducial point for the Zenithal
     * Projection.
     */
    private double phi0;
    /**
     * Native latitude (\u03B8<SUB>0</SUB>) in radians of the ﬁducial point for the Zenithal
     * Projection.
     */
    private double theta0;

    /**
     * Creates a zenithal projection based on the celestial longitude and
     * latitude of the ﬁducial point.
     * Creates a zenithal projection by setting :
     * <ul>
     *  <li>(\u03D5<SUB>0</SUB>, \u03B8<SUB>0</SUB>) = (0, HALF_PI)</li>
     *  <li>by computing \u03D5<SUB>p</SUB></li>
     * </ul>
     * @see ZenithalProjection#computeDefaultValueForPhip() 
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point (\u03B1<sub>0</sub>)
     * @param crval2 Celestial latitude in degrees of the ﬁducial point (\u03B4<sub>0</sub>)
     */
    protected ZenithalProjection(double crval1, double crval2) {
        super(crval1, crval2);
        setPhi0(DEFAULT_PHI0);
        setTheta0(DEFAULT_THETA0);
        setPhip(computeDefaultValueForPhip());
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
    public final void setPhi0(double phi0) {
        this.phi0 = phi0;
    }

    @Override
    public final void setTheta0(double theta0) {
        this.theta0 = theta0;
    }
        
    @Override
    protected double[] computeCoordNativePole(double phi_p) {
       return new double[]{getCrval1(), getCrval2()};
    }

    protected double computeRadius(double x, double y) {
        return Math.hypot(x, y);
    }
    
    protected double computeX(double radius, double phi) {
        return radius*Math.sin(phi);
    }
    
    protected double computeY(double radius, double phi) {
        return -radius*Math.cos(phi);
    }    
    
    protected double computePhi(double x, double y, double radius) {
        return NumericalUtils.equal(radius, 0, DOUBLE_TOLERANCE) ? 0 : NumericalUtils.aatan2(x, -y);
    }       
    
    @Override
    public String getNameFamily() {
        return NAME;
    } 

    @Override
    public boolean inside(double lon, double lat) {    
       double angle = NumericalUtils.distAngle(new double[]{getCrval1(), getCrval2()}, new double[]{lon, lat});
       if(NumericalUtils.equal(angle, Projection.HALF_PI, DOUBLE_TOLERANCE)) {
           angle = Projection.HALF_PI;
       }
       return (angle <= Projection.HALF_PI );
    } 
    
    @Override
    public boolean isLineToDraw(double[] pos1, double[] pos2) {
        return true;
    }        
}
