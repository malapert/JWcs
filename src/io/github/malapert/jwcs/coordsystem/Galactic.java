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
public class Galactic extends SkySystem {
    /**
     * Name of this coordinate system.
     */
    private final static SkySystems SKY_NAME = SkySystems.GALACTIC;
    
    /**
     * Default value for equinox.
     */
    private final static float DEFAULT_EQUINOX = 2000.0f;
    
    /**
     * The equinox.
     */
    private final float equinox;
    
    /**
     * Creates a Galactic coordinate system.
     */
    public Galactic() {
        this.equinox = DEFAULT_EQUINOX;
    }        
    
    @Override
    protected RealMatrix getRotationMatrix(final SkySystem refFrame) {
        RealMatrix m;
        if (refFrame instanceof Equatorial) {
            RealMatrix m1 = Utility.MatrixEqB19502Gal().transpose(); 
            RealMatrix m2 = Utility.MatrixEpoch12Epoch2(1950.0f, refFrame.getEquinox(), ReferenceSystemInterface.Type.FK4, ((Equatorial)refFrame).getReferenceSystemType(), null);
            m = m2.multiply(m1);
        } else if (refFrame instanceof Galactic) {
            m = MatrixUtils.createRealIdentityMatrix(3);
        } else if (refFrame instanceof SuperGalactic) {
            m = Utility.MatrixGal2Sgal();
        } else if (refFrame instanceof Ecliptic) {
            RealMatrix m1 = Utility.MatrixEqB19502Gal().transpose();
            RealMatrix m2 = Utility.MatrixEpoch12Epoch2(1950.0f, refFrame.getEquinox(), ReferenceSystemInterface.Type.FK4, ReferenceSystemInterface.Type.FK5, null);
            RealMatrix m3 = Utility.MatrixEq2Ecl(refFrame.getEquinox(), ReferenceSystemInterface.Type.FK5);
            m = m3.multiply(m2).multiply(m1);
        } else {
            throw new JWcsError(String.format("Unknown output sky system: %s", refFrame.getSkySystemName()));
        }
        return m;
    }

    @Override
    public SkySystems getSkySystemName() {
        return SKY_NAME;
    }

    @Override
    protected float getEquinox() {
        return this.equinox;
    }
}
