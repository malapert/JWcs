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

import static io.github.malapert.jwcs.utility.TimeUtils.epochs;


/**
 * Mean place post IAU 1976 system. 
 * 
 * Also a barycentric equatorial coordinate system. 
 * This should be qualified by an Equinox value (only).
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class FK5 implements CoordinateReferenceFrame {    
    /**
     * The name of this reference frame.
     */    
    private final static CoordinateReferenceFrame.ReferenceFrame REF_SYSTEM = CoordinateReferenceFrame.ReferenceFrame.FK5;
    
    /**
     * The default value of the equinox sets to J2000.
     */    
    private final static String DEFAULT_EPOCH = "J2000";
    
    /**
     * The epoch of the equinox.
     */    
    private double equinox;
    
    /**
     * Creates a FK5 reference frame with the {@link FK5#DEFAULT_EPOCH} value.
     */
    public FK5(){
        init(DEFAULT_EPOCH);
    }
    
    /**
     * Creates a FK5 reference frame with a equinox value.
     * @param epoch the epoch of equinox
     */    
    public FK5(final String epoch) {
        init(epoch);
    } 

    /**
     * Initialization.
     * @param epoch the epoch of equinox
     */
    private void init(final String epoch) {          
        this.setEquinox(epoch);
    }

    /**
     * Returns the equinox as a Julian epoch.
     */    
    @Override
    public double getEquinox() {
        return this.equinox;
    }

    /**
     * Returns Double.NaN.
     * No need to specify an epoch of observation in FK5 reference frame
     */    
    @Override
    public double getEpochObs() {
        return Double.NaN;
    }    

    @Override
    public ReferenceFrame getReferenceFrame() {
        return REF_SYSTEM;
    }

    /**
     * Sets the equinox.
     * @param equinox the equinox to set
     */
    @Override
    public void setEquinox(final String equinox) {
        this.equinox = epochs(equinox)[1];
    }
    
    @Override
    /**
     * Sets the equinox as a Julian epoch.
     */
    public void setEquinox(final double equinox) {
        this.equinox = equinox;
    }      
    
    @Override
    /**
     * Do nothing.
     * No need to specify the epoch of observation in FK5 reference frame
     */
    public void setEpochObs(final String epochObs) {       
    }

    @Override
    /**
     * Do nothing.
     * No need to specify the epoch of observation in FK5 reference frame
     */    
    public void setEpochObs(double epochObs) {
    }  

    @Override
    public String toString() {
        return "FK5("+this.equinox+")";
    }
        
}
