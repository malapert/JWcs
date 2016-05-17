/*
 * Copyright (C) 2014-2016 malapert
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

import static io.github.malapert.jwcs.utility.TimeUtils.epochs;

/**
 * This is an equatorial coordinate system based on 
 * the mean dynamical equator and equinox at epoch J2000.
 * The dynamical equator and equinox differ slightly 
 * compared to the equator and equinox of FK5 at J2000 and 
 * the ICRS system. This system need not be qualified 
 * by an Equinox value.
 *         
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class J2000 implements CoordinateReferenceFrame {

    /**
     * The name of this reference frame.
     */
    private final static CoordinateReferenceFrame.ReferenceFrame REF_SYSTEM = CoordinateReferenceFrame.ReferenceFrame.J2000;

    /**
     * The default value of the equinox sets to J2000.
     */ 
    private final static String DEFAULT_EPOCH = "J2000";

    /**
     * The epoch of the equinox.
     */
    private double equinox;

    /**
     * Creates J2000 frame based on {@link J2000#DEFAULT_EPOCH}.
     */
    public J2000() {
        init(DEFAULT_EPOCH);
    }

    /**
     * Initialization.
     *
     * @param epoch the epoch of equinox
     */
    private void init(final String epoch) {
        this.equinox = epochs(epoch)[1];
    }

    @Override
    public ReferenceFrame getReferenceFrame() {
        return REF_SYSTEM;
    }

    /**
     * Returns Double.NaN.
     * 
     * No need to specify an epoch of observation in J2000 reference frame
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
     * No need to specify the epoch of observation
     */    
    @Override
    public void setEpochObs(final String epochObs) { 
        //Do nothing
    }

    /**
     * Do nothing.
     * No need to specify the epoch of observation
     */     
    @Override   
    public void setEpochObs(final double epochObs) {
        //DO nothing
    }     

    @Override
    public String toString() {
        return "J2000";
    }        

}
