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
 * The ecliptic is the apparent path of the Sun on the celestial sphere, and is
 * the basis for the ecliptic coordinate system.
 *
 * It also refers to the plane of this path, which is coplanar with both the
 * orbit of the Earth around the Sun and the apparent orbit of the Sun around
 * the Earth. The path of the Sun is not normally noticeable from the Earth's
 * surface because the Earth rotates, carrying the observer through the cycle of
 * sunrise and sunset, obscuring the apparent motion of the Sun with respect to
 * the stars.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class Ecliptic extends Crs implements CoordinateReferenceFrame {

    /**
     * This coordinate system name.
     */
    private final static CoordinateSystem SKY_NAME = CoordinateSystem.ECLIPTIC;

    /**
     * The coordinate reference system.
     */
    private CoordinateReferenceFrame coordinateReferenceFrame;

    /**
     * Creates the Ecliptic coordinate system based on the reference system.
     *
     * @param coordinateReferenceFrame the reference system
     */
    public Ecliptic(final CoordinateReferenceFrame coordinateReferenceFrame) {
        this.coordinateReferenceFrame = coordinateReferenceFrame;
    }

    /**
     * Creates the Ecliptic coordinate system based on FK5 reference system.
     */
    public Ecliptic() {
        this(new FK5());
    }

    /**
     * Transforms the coordinate reference frame if necessary. 
     * In 'Representations of celestial coordinates in FITS' (Calabretta and
     * Greisen) we read that all reference systems are allowed for both
     * equatorial- and ecliptic coordinates, except FK4-NO-E, which is only
     * allowed for equatorial coordinates. If FK4-NO-E is given in combination
     * with an ecliptic sky system then silently FK4 is assumed.
     *
     * @param refSystem the coordinate reference system
     * @return the coordinate reference system
     */
    protected CoordinateReferenceFrame init(CoordinateReferenceFrame refSystem) {
        CoordinateReferenceFrame result;
        if (ReferenceFrame.FK4_NO_E.equals(refSystem.getReferenceFrame())) {
            result = new FK4();
            result.setEpochObs(refSystem.getEpochObs());
            result.setEquinox(refSystem.getEquinox());
        } else {
            result = refSystem;
        }
        return result;
    }

    @Override
    protected RealMatrix getRotationMatrix(final Crs crs) throws JWcsError {
        RealMatrix m;
        CoordinateReferenceFrame targetCrs = crs.getCoordinateReferenceFrame();
        if (crs instanceof Equatorial) {
            RealMatrix m1 = MatrixEq2Ecl(this.getEquinox(), getReferenceFrame()).transpose();
            RealMatrix m2 = MatrixEpoch12Epoch2(this.getEquinox(), targetCrs.getEquinox(), getReferenceFrame(), targetCrs.getReferenceFrame(), Double.NaN);
            m = m2.multiply(m1);
        } else if (crs instanceof Galactic) {
            RealMatrix m1 = MatrixEq2Ecl(this.getEquinox(), getReferenceFrame()).transpose();
            RealMatrix m2 = MatrixEpoch12Epoch2(this.getEquinox(), 1950.0d, getReferenceFrame(), CoordinateReferenceFrame.ReferenceFrame.FK4, Double.NaN);
            RealMatrix m3 = MatrixEqB19502Gal();
            m = m3.multiply(m2).multiply(m1);
        } else if (crs instanceof SuperGalactic) {
            RealMatrix m1 = MatrixEq2Ecl(this.getEquinox(), getReferenceFrame()).transpose();
            RealMatrix m2 = MatrixEpoch12Epoch2(this.getEquinox(), 1950.0d, getReferenceFrame(), CoordinateReferenceFrame.ReferenceFrame.FK4, Double.NaN);
            RealMatrix m3 = MatrixEqB19502Gal();
            RealMatrix m4 = MatrixGal2Sgal();
            m = m4.multiply(m3).multiply(m2).multiply(m1);
        } else if (crs instanceof Ecliptic) {
            RealMatrix m1 = MatrixEq2Ecl(this.getEquinox(), getReferenceFrame()).transpose();
            RealMatrix m2 = MatrixEpoch12Epoch2(this.getEquinox(), targetCrs.getEquinox(), getReferenceFrame(), targetCrs.getReferenceFrame(), Double.NaN);
            RealMatrix m3 = MatrixEq2Ecl(this.getEquinox(), targetCrs.getReferenceFrame());
            m = m3.multiply(m2).multiply(m1);
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
     *
     * @return the coordinate reference frame
     */
    @Override
    public CoordinateReferenceFrame getCoordinateReferenceFrame() {
        return coordinateReferenceFrame;
    }

    /**
     * Sets the reference system.
     *
     * @param coordinateReferenceFrame the coordinateReferenceFrame to set
     */
    public void setCoordinateReferenceFrame(final CoordinateReferenceFrame coordinateReferenceFrame) {
        this.coordinateReferenceFrame = coordinateReferenceFrame;
    }

    @Override
    public String toString() {
        return SKY_NAME.name();
    }

}
