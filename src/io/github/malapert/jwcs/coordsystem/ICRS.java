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

import static io.github.malapert.jwcs.utility.TimeUtility.epochs;

/**
 * The International Celestial Reference System, for optical data 
 * realized through the Hipparcos catalog. 
 * 
 * <p>By definition, ICRS is not an equatorial system, but it is 
 * very close to the FK5 (J2000) system. No Equinox value is required.
 * 
 * <p>The International Celestial Reference System (ICRS) is the current standard
 * celestial reference system adopted by the International Astronomical Union
 * (IAU). Its origin is at the barycenter of the solar system, with axes that
 * are intended to be "fixed" with respect to space. ICRS coordinates are
 * approximately the same as equatorial coordinates: the mean pole at J2000.0
 * in the ICRS lies at 17.3±0.2 mas in the direction 12 h and 5.1±0.2 mas
 * in the direction 18 h. The mean equinox of J2000.0 is shifted from
 * the ICRS right ascension origin by 78±10 mas (direct rotation around
 * the polar axis).
 *         
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class ICRS implements CoordinateReferenceFrame {
  
    /**
     * The name of this reference frame.
     */
    private final static CoordinateReferenceFrame.ReferenceFrame REF_SYSTEM = CoordinateReferenceFrame.ReferenceFrame.ICRS;

    /**
     * The default value of the epoch sets to J2000.
     */
    private final static String DEFAULT_EPOCH = "J2000";
    
    /**
     * The epoch of the equinox.
     */
    private double equinox;

    /**
     * Creates a new ICRS reference frame based on {@link ICRS#DEFAULT_EPOCH}.
     */
    public ICRS() {
        init(DEFAULT_EPOCH);
    }

    /**
     * Initialization.
     * @param equinox the epoch of equinox
     */
    private void init(final String equinox) {
        this.equinox = epochs(equinox)[1];
    }

    @Override
    public CoordinateReferenceFrame.ReferenceFrame getReferenceFrame() {
        return REF_SYSTEM;
    }

    /**
     * Returns Double.NaN
     * No need to specify an epoch of observation in ICRS reference frame
     */    
    @Override
    public double getEpochObs() {
        return Double.NaN;
    }

    /**
     * Returns the equinox as a Julian epoch.
     */    
    @Override
    public double getEquinox() {
        return this.equinox;
    }
    
    /**
     * Do nothing.
     * No need to specify the epoch of equinox
     */ 
    @Override   
    public void setEquinox(final String equinox) {
        //Do nothing
    }    
    
    /**
     * Do nothing.
     * No need to specify the epoch of equinox
     */    
    @Override
    public void setEquinox(final double equinox) {
        //Do nothing
    }      
    
    /**
     * Do nothing.
     * No need to specify the epoch of observation in FK5 reference frame
     */    
    @Override
    public void setEpochObs(final String epochObs) { 
        //Do noting
    }

    /**
     * Do nothing.
     * No need to specify the epoch of observation in FK5 reference frame
     */    
    @Override    
    public void setEpochObs(final double epochObs) {
        //Do nothing
    }     

    @Override
    public String toString() {
        return "ICRS";
    }        
}
