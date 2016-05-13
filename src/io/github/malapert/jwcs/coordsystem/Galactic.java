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
import org.apache.commons.math3.linear.MatrixUtils;
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
 * @version 1.0
 */
public class Galactic extends Crs {
    /**
     * Name of this coordinate system.
     */
    private final static CoordinateSystem SKY_NAME = CoordinateSystem.GALACTIC;
    
    /**
     * Default value for equinox.
     */
    private final static double DEFAULT_EQUINOX = 2000.0d;
    
    /**
     * The equinox.
     */
    private final double equinox;
    
    /**
     * Creates a Galactic coordinate system.
     */
    public Galactic() {
        this.equinox = DEFAULT_EQUINOX;
    }        
    
    @Override
    protected RealMatrix getRotationMatrix(final Crs refFrame) {
        RealMatrix m;
        if (refFrame instanceof Equatorial) {
            RealMatrix m1 = Utility.MatrixEqB19502Gal().transpose(); 
            RealMatrix m2 = Utility.MatrixEpoch12Epoch2(1950.0d, refFrame.getEquinox(), CoordinateReferenceFrame.ReferenceFrame.FK4, ((Equatorial)refFrame).getReferenceSystemType(), Double.NaN);
            m = m2.multiply(m1);
        } else if (refFrame instanceof Galactic) {
            m = MatrixUtils.createRealIdentityMatrix(3);
        } else if (refFrame instanceof SuperGalactic) {
            m = Utility.MatrixGal2Sgal();
        } else if (refFrame instanceof Ecliptic) {
            RealMatrix m1 = Utility.MatrixEqB19502Gal().transpose();
            RealMatrix m2 = Utility.MatrixEpoch12Epoch2(1950.0d, refFrame.getEquinox(), CoordinateReferenceFrame.ReferenceFrame.FK4, ((Ecliptic)refFrame).getReferenceSystemType(), Double.NaN);
            RealMatrix m3 = Utility.MatrixEq2Ecl(refFrame.getEquinox(), ((Ecliptic)refFrame).getReferenceSystemType());
            m = m3.multiply(m2).multiply(m1);
        } else {
            throw new JWcsError(String.format("Unknown output sky system: %s", refFrame.getCoordinateSystem()));
        }
        return m;
    }

    @Override
    public CoordinateSystem getCoordinateSystem() {
        return SKY_NAME;
    }

    @Override
    protected double getEquinox() {
        return this.equinox;
    }

    @Override
    public String toString() {
        return SKY_NAME.name();
    }
        
}
