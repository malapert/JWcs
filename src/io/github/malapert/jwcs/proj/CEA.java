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
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import java.util.logging.Level;

/**
 * The cylindrical equal area projection.
 * 
 * <p>
 * Reference: "Representations of celestial coordinates in FITS", 
 * M. R. Calabretta and E. W. Greisen
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class CEA extends CylindricalProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Cylindrical equal area";

    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "\u03BB=%s";

    /**
     * \u03BB Scaling parameter.
     */
    private double lambda;

    /**
     * Default value for \u03BB.
     */
    private static final int DEFAULT_VALUE = 1;

    /**
     * Constructs a CEA based on the celestial longitude and latitude of the
     * fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>).
     *
     * \u03BB is set to {@link CEA#DEFAULT_VALUE}.
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @throws BadProjectionParameterException When projection parameters are wrong
     */
    public CEA(final double crval1, final double crval2) throws BadProjectionParameterException {
        this(crval1, crval2, DEFAULT_VALUE);
    }

    /**
     * Constructs a CEA based on the celestial longitude and latitude of the
     * fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>) and \u03BB.
     * 
     * \u03BB is set by the FITS keyword PV<code>nbAxis</code>_1.
     *
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the
     * fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the
     * fiducial point
     * @param lambda \u03BB dimensionless.
     * @throws BadProjectionParameterException lambda not in ]0,1]
     */
    public CEA(final double crval1, final double crval2, final double lambda) throws BadProjectionParameterException {
        super(crval1, crval2);
        LOG.log(Level.FINER, "INPUTS[Deg] (crval1,crval2,lambda)=({0},{1},{2})", new Object[]{crval1,crval2,lambda});                        
        if (NumericalUtils.equal(lambda, 0) || lambda < 0 || lambda > 1.0) {
            throw new BadProjectionParameterException(this,"lambda =" + lambda + " - lambda outside of range (0,1]");
        }
        setLambda(lambda);
    }

    @Override
    protected double[] project(final double x, final double y) throws ProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                        
        final double xr = Math.toRadians(x);
        final double yr = Math.toRadians(y);
        final double phi = xr;
        final double arg = getLambda() * yr;
        final double theta = NumericalUtils.aasin(arg);
        if(Double.isNaN(theta)) {
            throw new PixelBeyondProjectionException(this, "(x,y)=("+x+","+y+")");
        }
        final double[] pos = {phi, theta};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                
        return pos;
    }

    @Override
    protected double[] projectInverse(final double phi, final double theta) throws ProjectionException {
        LOG.log(Level.FINER, "INPUTS[Deg] (phi,theta)=({0},{1})", new Object[]{Math.toDegrees(phi),Math.toDegrees(theta)});                                                        
        final double phiCorrect = phiRange(phi);
        final double x = Math.toDegrees(phiCorrect);
        final double y = Math.toDegrees(Math.sin(theta) / getLambda());
        final double[] coord = {x, y};
        LOG.log(Level.FINER, "OUTPUTS[Deg] (x,y)=({0},{1})", new Object[]{x,y});                                                
        return coord;
    }

    /**
     * Returns \u03BB, the scaling parameter.
     *
     * @return the lambda
     */
    private double getLambda() {
        return lambda;
    }

    /**
     * Sets \u03BB, the scaling parameter.
     *
     * @param lambda the lambda to set
     */
    private void setLambda(final double lambda) {
        this.lambda = lambda;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtils.round(this.lambda));
    }
    
    @Override
    public ProjectionParameter[] getProjectionParameters() {
        final ProjectionParameter p1 = new ProjectionParameter("lambda", JWcs.PV21, new double[]{0, 1}, 1);
        return new ProjectionParameter[]{p1};        
    }    

}
