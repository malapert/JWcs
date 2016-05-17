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
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import static io.github.malapert.jwcs.utility.NumericalUtils.HALF_PI;
import java.util.logging.Level;

/**
 * Slant orthographic.
 *
 * <p>
 * It represents the visual appearance of a sphere, e.g. a planet, when seen
 * from a great distance.
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class SIN extends ZenithalProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Slant orthographic";

    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "\u046F=%s \u03B7=%s";

    /**
     * Default value.
     */
    public static final double DEFAULT_VALUE = 0;

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
     * \u03BE,\u03B7 parameters are set to {@link SIN#DEFAULT_VALUE}.
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
     * of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and \u03BE,\u03B7      
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
        final double xr = Math.toRadians(x);
        final double yr = Math.toRadians(y);
        final double phi, theta;
        if (NumericalUtils.equal(ksi, DEFAULT_VALUE) && NumericalUtils.equal(eta, DEFAULT_VALUE)) {
            final double r_theta = computeRadius(xr, yr);
            if(NumericalUtils.equal(r_theta, 1)) {
                throw new PixelBeyondProjectionException(this,"(x,y)=("+x+","+y+") : r_theta must be < 1");
            }
            phi = computePhi(xr, yr, r_theta);
            theta = NumericalUtils.aacos(r_theta);
        } else {
            final double a = Math.pow(ksi, 2) + Math.pow(eta, 2) + 1;
            final double b = ksi * (xr - ksi) + eta * (yr - eta);
            final double c = Math.pow((xr - ksi),2) + Math.pow((yr - eta),2) - 1;
            final double theta1 = NumericalUtils.aasin((-b + Math.sqrt(b * b - a * c)) / a);
            final double theta2 = NumericalUtils.aasin((-b - Math.sqrt(b * b - a * c)) / a);
            final boolean isTheta1Valid = NumericalUtils.isInInterval(theta1, -HALF_PI, HALF_PI);
            final boolean isTheta2Valid = NumericalUtils.isInInterval(theta2, -HALF_PI, HALF_PI);
            if (isTheta1Valid && isTheta2Valid) {
                final double diffTheta1Pole = Math.abs(theta1 - HALF_PI);
                final double diffTheta2Pole = Math.abs(theta2 - HALF_PI);
                theta = diffTheta1Pole < diffTheta2Pole ? theta1 : theta2;
            } else if (isTheta1Valid) {
                theta = theta1;
            } else if (isTheta2Valid) {
                theta = theta2;
            } else {
                throw new BadProjectionParameterException(this," (ksi,eta) = (" + ksi + " , " + eta+")");
            }

            phi = NumericalUtils.aatan2(xr - ksi * (1 - Math.sin(theta)), -(yr - eta * (1 - Math.sin(theta))));
        }

        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                                                                                
        return pos;
    }

    @Override
    public double[] projectInverse(final double phi, final double theta) throws PixelBeyondProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                                                                                                        
        final double phiCorrect = phiRange(phi);
        final double thetax = - Math.atan(ksi*Math.sin(phiCorrect)-eta*Math.cos(phiCorrect));
        if (theta < thetax) {
            throw new PixelBeyondProjectionException(this,"(phi,theta)=("+Math.toDegrees(phi)+","+Math.toDegrees(theta)+")");
        }
        final double x = Math.toDegrees(Math.cos(theta) * Math.sin(phiCorrect) + ksi * (1 - Math.sin(theta)));
        final double y = Math.toDegrees(-Math.cos(theta) * Math.cos(phiCorrect) + eta * (1 - Math.sin(theta)));
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
        return String.format(DESCRIPTION, NumericalUtils.round(this.ksi), NumericalUtils.round(this.eta));
    }
    
    @Override
    public ProjectionParameter[] getProjectionParameters() {
        final ProjectionParameter p1 = new ProjectionParameter("ksi", JWcs.PV21, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}, 0);
        final ProjectionParameter p2 = new ProjectionParameter("eta", JWcs.PV22, new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY}, 0);
        return new ProjectionParameter[]{p1,p2};    
    }    
}
