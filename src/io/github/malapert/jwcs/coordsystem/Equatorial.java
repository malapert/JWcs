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

import org.apache.commons.math3.linear.RealMatrix;

/**
 * The equatorial coordinate system is a widely used celestial coordinate system
 * used to specify the positions of celestial objects. 
 * 
 * It may be implemented in spherical or rectangular coordinates, both deFINERd
 * by an origin at the center of the Earth, a fundamental plane consisting of
 * the projection of the Earth's equator onto the celestial sphere
 * (forming the celestial equator), a primary direction towards the vernal
 * equinox, and a right-handed convention.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class Equatorial extends SkySystem implements ReferenceSystemInterface {

    /**
     * This coordinate system name.
     */
    private final static SkySystems SKY_NAME = SkySystems.EQUATORIAL;
    
    /**
     * The reference frame of the Equatorial coordinate system.
     */
    private ReferenceSystemInterface refSystem;

    /**
     * Creates an Equatorial coordinate system based on the reference frame.
     * @param refSystem the reference frame
     */    
    public Equatorial(final ReferenceSystemInterface refSystem) {
        this.refSystem = refSystem;
    }

    /**
     * Creates an Equatorial coordinate system based on the ICRS reference frame.
     */
    public Equatorial() {
        this(new ICRS());
    }

    @Override
    protected RealMatrix getRotationMatrix(final SkySystem refFrame) {
        RealMatrix m;
        if (refFrame instanceof Equatorial) {
            m = Utility.MatrixEpoch12Epoch2(getEquinox(), ((Equatorial) refFrame).getEquinox(), getReferenceSystemType(), ((Equatorial) refFrame).getReferenceSystemType(), getEpochObs());
        } else if (refFrame instanceof Galactic) {
            RealMatrix m1 = Utility.MatrixEpoch12Epoch2(getEquinox(), 1950.0f, getReferenceSystemType(), ReferenceSystemInterface.Type.FK4, null);
            RealMatrix m2 = Utility.MatrixEqB19502Gal();
            m = m2.multiply(m1);
        } else if (refFrame instanceof SuperGalactic) {
            RealMatrix m1 = Utility.MatrixEpoch12Epoch2(getEquinox(), 1950.0f, getReferenceSystemType(), ReferenceSystemInterface.Type.FK4, null);
            RealMatrix m2 = Utility.MatrixEqB19502Gal();
            RealMatrix m3 = Utility.MatrixGal2Sgal();
            m = m3.multiply(m2).multiply(m1);
        } else if (refFrame instanceof Ecliptic) {
            RealMatrix m1 = Utility.MatrixEpoch12Epoch2(getEquinox(), 2000f, getReferenceSystemType(), ReferenceSystemInterface.Type.FK5, null);
            RealMatrix m2 = Utility.MatrixEq2Ecl(2000f, ReferenceSystemInterface.Type.FK5);
            m = m2.multiply(m1);
        } else {
            throw new IllegalArgumentException(String.format("Unknown output sky system: %s", refFrame.getSkySystemName()));
        }
        return m;
    }

    @Override
    public SkySystems getSkySystemName() {
        return SKY_NAME;
    }

    @Override
    public float getEquinox() {
        return this.getRefSystem().getEquinox();
    }

    @Override
    public Float getEpochObs() {
        return this.getRefSystem().getEpochObs();
    }

    @Override
    public ReferenceSystemInterface.Type getReferenceSystemType() {
        return this.getRefSystem().getReferenceSystemType();
    }

    /**
     * Returns the reference system.
     * @return the refSystem
     */
    public ReferenceSystemInterface getRefSystem() {
        return refSystem;
    }

    /**
     * Sets the reference system.
     * @param refSystem the refSystem to set
     */
    public void setRefSystem(final ReferenceSystemInterface refSystem) {
        this.refSystem = refSystem;
    }

    @Override
    public String toString() {
        return SKY_NAME+"("+refSystem+")";
    }
    
    
}
