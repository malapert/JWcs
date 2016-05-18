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
 * The equatorial coordinate system is a widely used celestial coordinate system
 * used to specify the positions of celestial objects. 
 * 
 * It may be implemented in spherical or rectangular coordinates, both defined
 * by an origin at the center of the Earth, a fundamental plane consisting of
 * the projection of the Earth's equator onto the celestial sphere
 * (forming the celestial equator), a primary direction towards the vernal
 * equinox, and a right-handed convention.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class Equatorial extends AbstractCrs implements CoordinateReferenceFrame {

    /**
     * This coordinate system name.
     */
    private static final CoordinateSystem SKY_NAME = CoordinateSystem.EQUATORIAL;
    
    /**
     * The reference frame of the Equatorial coordinate system.
     */
    private CoordinateReferenceFrame coordinateReferenceFrame;

    /**
     * Creates an Equatorial coordinate system based on the coordinate 
     * reference frame.
     * @param coordinateReferenceFrame the coordinate reference frame
     */    
    public Equatorial(final CoordinateReferenceFrame coordinateReferenceFrame) {
        this.coordinateReferenceFrame = coordinateReferenceFrame;
    }

    /**
     * Creates an Equatorial coordinate system based on the ICRS reference frame.
     */
    public Equatorial() {
        this(new ICRS());
    }
   
    @Override
    protected RealMatrix getRotationMatrix(final AbstractCrs crs) throws JWcsError {
        final RealMatrix m;
        final CoordinateReferenceFrame targetCrs = crs.getCoordinateReferenceFrame();        
        if (crs instanceof Equatorial) {
            m = convertMatrixEpoch12Epoch2(getEquinox(), targetCrs.getEquinox(), getReferenceFrame(), targetCrs.getReferenceFrame(), getEpochObs());
        } else if (crs instanceof Galactic) {
            final RealMatrix m1 = convertMatrixEpoch12Epoch2(getEquinox(), 1950.0d, getReferenceFrame(), CoordinateReferenceFrame.ReferenceFrame.FK4, Double.NaN);
            final RealMatrix m2 = convertMatrixEqB19502Gal();
            m = m2.multiply(m1);
        } else if (crs instanceof SuperGalactic) {
            final RealMatrix m1 = convertMatrixEpoch12Epoch2(getEquinox(), 1950.0d, getReferenceFrame(), CoordinateReferenceFrame.ReferenceFrame.FK4, Double.NaN);
            final RealMatrix m2 = convertMatrixEqB19502Gal();
            final RealMatrix m3 = convertMatrixGal2Sgal();
            m = m3.multiply(m2).multiply(m1);
        } else if (crs instanceof Ecliptic) {
            final RealMatrix m1 = convertMatrixEpoch12Epoch2(getEquinox(), targetCrs.getEquinox(), getReferenceFrame(), targetCrs.getReferenceFrame(), Double.NaN);
            final RealMatrix m2 = convertMatrixEq2Ecl(targetCrs.getEquinox(), targetCrs.getReferenceFrame());
            m = m2.multiply(m1);
        } else {
            throw new JWcsError(String.format("Unknown output crs: %s", crs.getCoordinateSystem()));
        }
        return m;
    }

    @Override
    public CoordinateSystem getCoordinateSystem() {
        return SKY_NAME;
    }

    @Override
    public double getEquinox() {
        return this.getCoordinateReferenceFrame().getEquinox();
    }

    @Override
    public double getEpochObs() {
        return this.getCoordinateReferenceFrame().getEpochObs();
    }
    
    @Override
    public void setEquinox(final String equinox) {
        this.getCoordinateReferenceFrame().setEquinox(equinox);
    }
    
    @Override
    public void setEpochObs(final String epoch) {
        this.getCoordinateReferenceFrame().setEpochObs(epoch);
    }
    
    @Override
    public void setEquinox(final double equinox) {
        this.getCoordinateReferenceFrame().setEquinox(equinox);
    }
    
    @Override
    public void setEpochObs(final double epoch) {
        this.getCoordinateReferenceFrame().setEpochObs(epoch);
    }    

    @Override
    public CoordinateReferenceFrame.ReferenceFrame getReferenceFrame() {
        return this.getCoordinateReferenceFrame().getReferenceFrame();
    }

    /**
     * Returns the coordinate reference frame.
     * @return the coordinate reference frame
     */
    @Override
    public CoordinateReferenceFrame getCoordinateReferenceFrame() {
        return coordinateReferenceFrame;
    }

    /**
     * Sets the reference system.
     * @param coordinateReferenceFrame the coordinateReferenceFrame to set
     */
    @Override
    public void setCoordinateReferenceFrame(final CoordinateReferenceFrame coordinateReferenceFrame) {
        this.coordinateReferenceFrame = coordinateReferenceFrame;
    }

    @Override
    public String toString() {
        return SKY_NAME+"("+coordinateReferenceFrame+")";
    }        
}
