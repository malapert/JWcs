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
 * is defined by the intersection of this plane with the galactic plane.
 *         
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class SuperGalactic extends SkySystem {
    /**
     * Name of this coordinate system.
     */
    private static final SkySystems SKY_NAME = SkySystems.SUPER_GALACTIC;
    /**
     * Value of the epoch of the equinox.
     */
    private static final float EQUINOX = 2000.0f;
    
    @Override
    protected RealMatrix getRotationMatrix(final SkySystem refFrame) {
        RealMatrix m;
        if (refFrame instanceof Equatorial) {
            RealMatrix m1 = Utility.MatrixGal2Sgal().transpose(); 
            RealMatrix m2 = Utility.MatrixEqB19502Gal().transpose();
            RealMatrix m3 = Utility.MatrixEpoch12Epoch2(1950.0f, (float)((Equatorial) refFrame).getEquinox(), ReferenceSystemInterface.Type.FK4, ((Equatorial) refFrame).getReferenceSystemType(), null);
            m = m3.multiply(m2).multiply(m1);
        } else if (refFrame instanceof Galactic) {
            m = Utility.MatrixGal2Sgal().transpose();       
        } else if (refFrame instanceof SuperGalactic) {
            m = MatrixUtils.createRealIdentityMatrix(3);
        } else if (refFrame instanceof Ecliptic) {
            RealMatrix m1 = Utility.MatrixGal2Sgal().transpose();
            RealMatrix m2 = Utility.MatrixEqB19502Gal().transpose();
            //TODO : Revérifier après implémentation
            RealMatrix m3 = Utility.MatrixEpoch12Epoch2(1950.0f, refFrame.getEquinox(), ReferenceSystemInterface.Type.FK4, ReferenceSystemInterface.Type.FK5, null);
            RealMatrix m4 = Utility.MatrixEq2Ecl(refFrame.getEquinox(), ReferenceSystemInterface.Type.FK5);
            ///
            m = m4.multiply(m3).multiply(m2).multiply(m1);
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
        return EQUINOX;
    }
}
