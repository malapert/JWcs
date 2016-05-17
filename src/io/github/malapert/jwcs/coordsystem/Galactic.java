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
package io.github.malapert.jwcs.coordsystem;

import io.github.malapert.jwcs.proj.exception.JWcsError;
import static io.github.malapert.jwcs.utility.NumericalUtils.createRealIdentityMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * The galactic coordinate system is a celestial coordinate system
 * in spherical coordinates, with the Sun as its center, the primary direction
 * aligned with the approximate center of the Milky Way galaxy, 
 * and the fundamental plane approximately in the galactic plane. 
 * 
 * It uses the right-handed convention, meaning that coordinates are positive 
 * toward the north and toward the east in the fundamental plane.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class Galactic extends Crs {
    /**
     * Name of this coordinate system.
     */
    private final static CoordinateSystem SKY_NAME = CoordinateSystem.GALACTIC;       
    
    /**
     * Computes the rotation matrix from a reference frame to another one.
     *
     * @param crs the output coordinate Reference System
     * @return the rotation matrix in the output coordinate Reference System
     * @throws JWcsError Unknown output crs
     */    
    @Override
    protected RealMatrix getRotationMatrix(final Crs crs) throws JWcsError {
        RealMatrix m;
        CoordinateReferenceFrame targetCrs = crs.getCoordinateReferenceFrame();        
        if (crs instanceof Equatorial) {
            RealMatrix m1 = MatrixEqB19502Gal().transpose(); 
            RealMatrix m2 = MatrixEpoch12Epoch2(1950.0d, targetCrs.getEquinox(), CoordinateReferenceFrame.ReferenceFrame.FK4, targetCrs.getReferenceFrame(), Double.NaN);
            m = m2.multiply(m1);
        } else if (crs instanceof Galactic) {
            m = createRealIdentityMatrix(3);
        } else if (crs instanceof SuperGalactic) {
            m = convertMatrixGal2Sgal();
        } else if (crs instanceof Ecliptic) {
            RealMatrix m1 = MatrixEqB19502Gal().transpose();
            RealMatrix m2 = MatrixEpoch12Epoch2(1950.0d, targetCrs.getEquinox(), CoordinateReferenceFrame.ReferenceFrame.FK4, targetCrs.getReferenceFrame(), Double.NaN);
            RealMatrix m3 = convertMatrixEq2Ecl(targetCrs.getEquinox(), ((Ecliptic)crs).getReferenceFrame());
            m = m3.multiply(m2).multiply(m1);
        } else {
            throw new JWcsError(String.format("Unknown output coordinate reference system: %s", crs.getCoordinateSystem()));
        }
        return m;
    }

    @Override
    public CoordinateSystem getCoordinateSystem() {
        return SKY_NAME;
    }

    @Override
    public String toString() {
        return SKY_NAME.name();
    }

    @Override
    public CoordinateReferenceFrame getCoordinateReferenceFrame() {
        return null;
    }
       
}
