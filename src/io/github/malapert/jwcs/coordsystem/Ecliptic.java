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
import org.apache.commons.math3.linear.RealMatrix;


/**
 * The ecliptic is the apparent path of the Sun on the celestial sphere, 
 * and is the basis for the ecliptic coordinate system. 
 * 
 * It also refers to the plane of this path, which is coplanar with both
 * the orbit of the Earth around the Sun and the apparent orbit of the Sun
 * around the Earth. The path of the Sun is not normally noticeable
 * from the Earth's surface because the Earth rotates, carrying the observer
 * through the cycle of sunrise and sunset, obscuring the apparent motion
 * of the Sun with respect to the stars.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class Ecliptic extends Crs implements CoordinateReferenceFrame {
 
    /**
     * This coordinate system name.
     */
    private final static CoordinateSystem SKY_NAME = CoordinateSystem.ECLIPTIC;
    
    /**
     * The reference system of the ecliptic coordinate system.
     */
    private CoordinateReferenceFrame refSystem;    
    
    /**
     * Creates the Ecliptic coordinate system based on the reference system.
     * @param refSystem the reference system
     */
    public Ecliptic(final CoordinateReferenceFrame refSystem) {
        this.refSystem = refSystem;
    }     
    
    /**
     * Creates the Ecliptic coordinate system based on FK5 reference system.
     */
    public Ecliptic() {
        this(new FK5());
    }
    
    /**
     * Transforms the coordinate reference frame if necessary.
     * In 'Representations of celestial coordinates in FITS' (Calabretta and Greisen) 
     * we read that all reference systems are allowed for both equatorial- and 
     * ecliptic coordinates, except FK4-NO-E, which is only allowed for equatorial 
     * coordinates. If FK4-NO-E is given in combination with an ecliptic 
     * sky system then silently FK4 is assumed.     
     * @param refSystem the coordinate reference system
     * @return the coordinate reference system
     */
    protected CoordinateReferenceFrame init(CoordinateReferenceFrame refSystem) {
        CoordinateReferenceFrame result;
        if (ReferenceFrame.FK4_NO_E.equals(refSystem.getReferenceSystemType())) {
            result = new FK4(); 
            result.setEpochObs(refSystem.getEpochObs());
            result.setEquinox(refSystem.getEquinox());
        } else {
            result = refSystem;
        }
        return result;
    }

    @Override
    protected RealMatrix getRotationMatrix(final Crs refFrame) {
        RealMatrix m;
       if (refFrame instanceof Equatorial) {         
            RealMatrix m1 = Utility.MatrixEq2Ecl(this.getEquinox(), getReferenceSystemType()).transpose();
            RealMatrix m2 = Utility.MatrixEpoch12Epoch2(this.getEquinox(), refFrame.getEquinox(), getReferenceSystemType(), ((Equatorial)refFrame).getReferenceSystemType(), Double.NaN);
            m = m2.multiply(m1);
        } else if (refFrame instanceof Galactic) {
            RealMatrix m1 = Utility.MatrixEq2Ecl(this.getEquinox(), getReferenceSystemType()).transpose();
            RealMatrix m2 = Utility.MatrixEpoch12Epoch2(this.getEquinox(), 1950.0d, getReferenceSystemType(), CoordinateReferenceFrame.ReferenceFrame.FK4, Double.NaN);
            RealMatrix m3 = Utility.MatrixEqB19502Gal();
            m = m3.multiply(m2).multiply(m1);
        } else if (refFrame instanceof SuperGalactic) {
            RealMatrix m1 = Utility.MatrixEq2Ecl(this.getEquinox(),getReferenceSystemType()).transpose();
            RealMatrix m2 = Utility.MatrixEpoch12Epoch2(this.getEquinox(), 1950.0d, getReferenceSystemType(), CoordinateReferenceFrame.ReferenceFrame.FK4, Double.NaN);
            RealMatrix m3 = Utility.MatrixEqB19502Gal();
            RealMatrix m4 = Utility.MatrixGal2Sgal();
            m = m4.multiply(m3).multiply(m2).multiply(m1);
        } else if (refFrame instanceof Ecliptic) {
            RealMatrix m1 = Utility.MatrixEq2Ecl(this.getEquinox(), getReferenceSystemType()).transpose();
            RealMatrix m2 = Utility.MatrixEpoch12Epoch2(this.getEquinox(), refFrame.getEquinox(), getReferenceSystemType(), ((Ecliptic)refFrame).getReferenceSystemType(), Double.NaN);
            RealMatrix m3 = Utility.MatrixEq2Ecl(this.getEquinox(), ((Ecliptic)refFrame).getReferenceSystemType());
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
    public double getEquinox() {
        return this.getRefSystem().getEquinox();
    }
    
    @Override
    public double getEpochObs() {
        return this.getRefSystem().getEpochObs();
    }
    
    @Override
    public void setEquinox(final String equinox) {
        this.getRefSystem().setEquinox(equinox);
    }
    
    @Override
    public void setEpochObs(final String epoch) {
        this.getRefSystem().setEpochObs(epoch);
    }  
    
    @Override
    public void setEquinox(final double equinox) {
        this.getRefSystem().setEquinox(equinox);
    }
    
    @Override
    public void setEpochObs(final double epoch) {
        this.getRefSystem().setEpochObs(epoch);
    }     
    
    @Override
    public CoordinateReferenceFrame.ReferenceFrame getReferenceSystemType() {
        return this.getRefSystem().getReferenceSystemType();
    }    

    /**
     * Returns the reference system.
     * @return the refSystem
     */
    public CoordinateReferenceFrame getRefSystem() {
        return refSystem;
    }

    /**
     * Sets the reference system.
     * @param refSystem the refSystem to set
     */
    public void setRefSystem(final CoordinateReferenceFrame refSystem) {
        this.refSystem = refSystem;
    }

    @Override
    public String toString() {
        return SKY_NAME.name();
    }
        
}
