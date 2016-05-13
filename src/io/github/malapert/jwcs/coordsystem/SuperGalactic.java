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
 * Supergalactic coordinates are coordinates in a spherical coordinate system
 * which was designed to have its equator aligned with the supergalactic plane,
 * a major structure in the local universe formed by the preferential
 * distribution of nearby galaxy clusters (such as the Virgo cluster, 
 * the Great Attractor and the Pisces-Perseus supercluster) towards a 
 * (two-dimensional) plane. 
 * 
 * The supergalactic plane was recognized by Gérard de Vaucouleurs in 1953 
 * from the Shapley-Ames Catalog, although a flattened distribution of nebulae
 * had been noted by William Herschel over 200 years earlier.
 * By convention, supergalactic latitude and supergalactic longitude are usually
 * denoted by SGB and SGL, respectively, by analogy to b and l conventionally
 * used for galactic coordinates. The zero point for supergalactic longitude
 * is deFINERd by the intersection of this plane with the galactic plane.
 *         
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class SuperGalactic extends Crs {
    /**
     * Name of this coordinate system.
     */
    private static final CoordinateSystem SKY_NAME = CoordinateSystem.SUPER_GALACTIC;
    /**
     * Value of the epoch of the equinox.
     */
    private static final double EQUINOX = 2000.0d;
    
    @Override
    protected RealMatrix getRotationMatrix(final Crs refFrame) {
        RealMatrix m;
        if (refFrame instanceof Equatorial) {
            RealMatrix m1 = Utility.MatrixGal2Sgal().transpose(); 
            RealMatrix m2 = Utility.MatrixEqB19502Gal().transpose();
            RealMatrix m3 = Utility.MatrixEpoch12Epoch2(1950.0d, refFrame.getEquinox(), CoordinateReferenceFrame.ReferenceFrame.FK4, ((Equatorial) refFrame).getReferenceSystemType(), Double.NaN);
            m = m3.multiply(m2).multiply(m1);
        } else if (refFrame instanceof Galactic) {
            m = Utility.MatrixGal2Sgal().transpose();       
        } else if (refFrame instanceof SuperGalactic) {
            m = MatrixUtils.createRealIdentityMatrix(3);
        } else if (refFrame instanceof Ecliptic) {
            RealMatrix m1 = Utility.MatrixGal2Sgal().transpose();
            RealMatrix m2 = Utility.MatrixEqB19502Gal().transpose();
            RealMatrix m3 = Utility.MatrixEpoch12Epoch2(1950.0d, refFrame.getEquinox(), CoordinateReferenceFrame.ReferenceFrame.FK4, ((Ecliptic) refFrame).getReferenceSystemType(), Double.NaN);
            RealMatrix m4 = Utility.MatrixEq2Ecl(refFrame.getEquinox(), ((Ecliptic) refFrame).getReferenceSystemType());
            m = m4.multiply(m3).multiply(m2).multiply(m1);
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
        return EQUINOX;
    }

    @Override
    public String toString() {
        return SKY_NAME.name();
    }
        
}
