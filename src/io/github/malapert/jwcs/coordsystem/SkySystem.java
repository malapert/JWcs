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

import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * A sky definition can consist of a <b>sky system</b>,
 * a <b>reference system</b>, an <b>equinox</b> and an <b>epoch of observation</b>. 
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public abstract class SkySystem {

    /**
     * List of supported sky systems
     */
    public enum SkySystems {
        /**
         * Galactic coordinates (lII, bII)
         */
        GALACTIC, 
        /**
         * Equatorial coordinates (\u03B1, \u03B4),
         */
        EQUATORIAL,
        /**
         * De Vaucouleurs Supergalactic coordinates (sgl, sgb)
         */
        SUPER_GALACTIC, 
        /**
         * Ecliptic coordinates (\u03BB, \u03B2) referred to the ecliptic
         * and mean equinox
         */
        ECLIPTIC
    };

    /**
     * Calculates the rotation matrix to from a reference frame to another one.
     * 
     * The methods in this class have been traduced from Python to JAVA 
     * 
     * @param refFrame the output reference frame
     * @return the rotation matrix in the output reference frame
     * @see <a href="http://www.astro.rug.nl/software/kapteyn/">The original code in Python</a>
     */
    protected abstract RealMatrix getRotationMatrix(final SkySystem refFrame);

    /**
     * Returns the coordinate system name.
     * @return the coordinate system name
     */
    public abstract SkySystems getSkySystemName();

    /**
     * Returns the equinox.
     * @return the equinox
     */
    protected abstract float getEquinox();        

    /**
     * Returns Eterms matrix for the input reference system.
     * @return Eterms matrix
     */
    private RealMatrix getEtermsIn() {
        RealMatrix eterms = null;
        ReferenceSystemInterface.Type refSystem;
        switch(getSkySystemName()) {
            case EQUATORIAL:
                refSystem = ((Equatorial) this).getReferenceSystemType();
                if (ReferenceSystemInterface.Type.FK4.equals(refSystem)) {
                    eterms = FK4.getEterms(getEquinox());
                }
                break;
            case ECLIPTIC:
                refSystem = ((Ecliptic) this).getReferenceSystemType();
                if (ReferenceSystemInterface.Type.FK4.equals(refSystem)) {
                    eterms = FK4.getEterms(getEquinox());
                }                
                break;
        }
        return eterms;
    }
    
    /**
     * Returns Eterms matrix for the output reference system.
     * @param refFrame the output reference system
     * @return Eterms matrix
     */
    private RealMatrix getEtermsOut(final SkySystem refFrame) {
        RealMatrix eterms = null;
        ReferenceSystemInterface.Type refSystem;
        switch(refFrame.getSkySystemName()) {
            case EQUATORIAL:
                refSystem = ((Equatorial) refFrame).getReferenceSystemType();
                if (ReferenceSystemInterface.Type.FK4.equals(refSystem)) {
                    eterms = FK4.getEterms(getEquinox());
                }
                break;
            case ECLIPTIC:
                refSystem = ((Ecliptic) refFrame).getReferenceSystemType();
                if (ReferenceSystemInterface.Type.FK4.equals(refSystem)) {
                    eterms = FK4.getEterms(getEquinox());
                }                
                break;
        }
        return eterms;               
    }    

    /**
     * Converts the (longitude, latitude) coordinates into the output 
     * reference system.
     * 
     * The method has been traduced from Python to JAVA.
     * 
     * @param refFrame the output reference system
     * @param longitude longitude in degrees
     * @param latitude latitude in degrees
     * @return the position in the sky in the output reference system
     * @see <a href="http://www.astro.rug.nl/software/kapteyn/">The original code in Python</a>
     */
    public SkyPosition convertTo(final SkySystem refFrame, double longitude, double latitude) {
        RealMatrix xyz = Utility.longlat2xyz(longitude, latitude);        
        RealMatrix rotation = getRotationMatrix(refFrame);
        RealMatrix etermsIn = getEtermsIn();
        RealMatrix etermsOut = getEtermsOut(refFrame);       
        if (etermsIn != null) {
            xyz = Utility.removeEterms(xyz, etermsIn);
        }
        xyz = rotation.multiply(xyz);
        if (etermsOut != null) {
            xyz = Utility.addEterms(xyz, etermsOut);
        }
        double[] position = Utility.xyz2longlat(xyz);
        return new SkyPosition(position[0], position[1], refFrame);
    }

}
