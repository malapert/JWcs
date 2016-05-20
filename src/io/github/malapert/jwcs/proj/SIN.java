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
import io.github.malapert.jwcs.proj.exception.JWcsException;
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtility;
import java.util.logging.Level;
import org.apache.commons.math3.util.FastMath;

/**
 * Slant orthographic.
 *
 * <p>It represents the visual appearance of a sphere, e.g. a planet, when seen
 * from a great distance. 
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class SIN extends AbstractZenithalProjection {

    /**
     * Projection's name.
     */
    private final static String NAME_PROJECTION = "Slant orthographic";

    /**
     * Projection's description.
     */
    private final static String DESCRIPTION = "\u046F=%s \u03B7=%s";

    /**
     * Default value.
     */
    public final static double DEFAULT_VALUE = 0;

    /**
     * \u03BE is deFINERd as \u03BE = cot\u03B8<sub>c</sub>sin\u03D5<sub>c</sub>.    
     */
    private final double ksi;
    /**
     * \u03B7 is deFINERd as \u03B7 = -cot\u03B8<sub>c</sub>cos\u03D5<sub>c</sub>.
     */
    private final double eta;

   /**
     * Constructs a SIN projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) with default \u03BE,\u03B7 
     * 
     * <p>\u03BE,\u03B7 parameters are set to {@link SIN#DEFAULT_VALUE}.
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     */
    public SIN(final double crval1, final double crval2) {
        this(crval1, crval2, DEFAULT_VALUE, DEFAULT_VALUE);
    }

   /**
     * Constructs a SIN projection based on the celestial longitude and latitude
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and \u03BE,\u03B7.      
     * 
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @param ksi \u03BE dimensionless
     * @param eta \u03B7 dimensionless
     */    
    public SIN(final double crval1, final double crval2, final double ksi, final double eta) {
        super(crval1, crval2);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2,ksi,eta)=({0},{1},{2},{3})", new Object[]{crval1,crval2,ksi,eta});                                                
        this.ksi = ksi;
        this.eta = eta;
    }

    @Override
    public double[] project(final double x, final double y) throws BadProjectionParameterException, PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                                                                                        
        final double xr = FastMath.toRadians(x);
        final double yr = FastMath.toRadians(y);
        final double phi;
        final double theta;
        if (NumericalUtility.equal(ksi, DEFAULT_VALUE) && NumericalUtility.equal(eta, DEFAULT_VALUE)) {
            final double r_theta = computeRadius(xr, yr);
            if(NumericalUtility.equal(r_theta, 1)) {
                throw new PixelBeyondProjectionException(this,"(x,y)=("+x+","+y+") : r_theta must be < 1");
            }
            phi = computePhi(xr, yr, r_theta);
            theta = NumericalUtility.aacos(r_theta);
        } else {
            final double a = FastMath.pow(ksi, 2) + FastMath.pow(eta, 2) + 1;
            final double b = (ksi * (xr - ksi) + eta * (yr - eta)) * 2;
            final double c = FastMath.pow(xr - ksi,2) + FastMath.pow(yr - eta,2) - 1;
            try {
                theta = NumericalUtility.computeQuatraticSolution(new double[]{c,b,a});
            } catch (JWcsException ex) {
                throw new BadProjectionParameterException(this," (ksi,eta) = (" + ksi + " , " + eta+")");
            }

            phi = NumericalUtility.aatan2(xr - ksi * (1 - FastMath.sin(theta)), -(yr - eta * (1 - FastMath.sin(theta))));
        }

        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                                                                                                                                
        return pos;
    }

    @Override
    public double[] projectInverse(final double phi, final double theta) throws PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{FastMath.toDegrees(phi),FastMath.toDegrees(theta)});                                                                                                                                        
        final double thetax = -FastMath.atan(ksi*FastMath.sin(phi)-eta*FastMath.cos(phi));
        if (theta < thetax) {
            throw new PixelBeyondProjectionException(this,"(phi,theta)=("+FastMath.toDegrees(phi)+","+FastMath.toDegrees(theta)+")");
        }
        final double x = FastMath.toDegrees(FastMath.cos(theta) * FastMath.sin(phi) + ksi * (1 - FastMath.sin(theta)));
        final double y = FastMath.toDegrees(-FastMath.cos(theta) * FastMath.cos(phi) + eta * (1 - FastMath.sin(theta)));
        final double[] coord = {x, y};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                                                                                        
        return coord;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtility.round(this.ksi), NumericalUtility.round(this.eta));
    }
    
    @Override
    public ProjectionParameter[] getProjectionParameters() {
        final ProjectionParameter p1 = new ProjectionParameter("ksi", AbstractJWcs.PV21, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}, 0);
        final ProjectionParameter p2 = new ProjectionParameter("eta", AbstractJWcs.PV22, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}, 0);
        return new ProjectionParameter[]{p1,p2};    
    }    
}
