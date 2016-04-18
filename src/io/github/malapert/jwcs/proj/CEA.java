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

import io.github.malapert.jwcs.proj.exception.BadProjectionParameterException;
import io.github.malapert.jwcs.proj.exception.PixelBeyondProjectionException;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;

/**
 *
 * @author Jean-Christophe Malapert
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
    
    private double lambda;

    public CEA(double crval1, double crval2, double lambda) throws BadProjectionParameterException {
        super(crval1, crval2);
	if (NumericalUtils.equal(lambda, 0, DOUBLE_TOLERANCE) || lambda < 0 || lambda > 1.0) 
	  throw new BadProjectionParameterException(
            "lambda outside of range (0,1]: " + lambda);        
        setLambda(lambda);
    }

    @Override
    protected double[] project(double x, double y) throws ProjectionException {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double phi = xr;
        double arg = getLambda() * yr;
        if (Math.abs(arg) > 1) {
            throw new PixelBeyondProjectionException("CEA: y=" + y + " < "
                    + 1 / lambda);
        }
        double theta = NumericalUtils.aasin(arg);
        double[] pos = {phi, theta};
        return pos;        
    }

    @Override
    protected double[] projectInverse(double phi, double theta) throws ProjectionException {
        phi = phiRange(phi);
        double x = Math.toDegrees(phi);
        double y = Math.toDegrees(Math.sin(theta) / getLambda());
        double[] coord = {x, y};
        return coord;
    }

    /**
     * @return the lambda
     */
    private double getLambda() {
        return lambda;
    }

    /**
     * @param lambda the lambda to set
     */
    private void setLambda(double lambda) {
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

}
