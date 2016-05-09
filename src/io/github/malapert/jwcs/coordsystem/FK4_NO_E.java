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
 * The old FK4 (barycentric) equatorial system
 * but without the *E-terms of aberration*.
 * 
 * This coordinate system should also be 
 * qualified by both an Equinox and an Epoch value.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class FK4_NO_E implements ReferenceSystemInterface {

    /**
     * The name of this reference frame.
     */    
    private final static ReferenceSystemInterface.Type REF_SYSTEM = ReferenceSystemInterface.Type.FK4_NO_E;
    
    /**
     * The default value of the epoch.
     */      
    private final static String DEFAULT_EPOCH = "B1950";    
    
    /**
     * The epoch of the equinox.
     */
    private double equinox;
    
    /**
     * The epoch of observation.
     */
    private Double epochObs;
    
    /**
     * Creates a FK4 reference frame with default values of both epoch
     * and epoch of observation.
     */
    public FK4_NO_E() {
        init(DEFAULT_EPOCH, null);
    }
    
    /**
     * Creates a FK4_NO_E reference frame with a equinox.
     * @param epoch the epoch
     */    
    public FK4_NO_E(final String epoch) {       
        init(epoch, null);
    }
    
    /**
     * Creates a FK4_NO_E reference frame with both equinox and epoch of observation.
     * @param epoch the epoch
     * @param epochObs the epoch of observation
     */    
    public FK4_NO_E(final String epoch, final String epochObs) {       
        init(epoch, epochObs);
    }
    
    /**
     * initialization.
     * @param epoch the epoch
     * @param epochObs the epoch of observation
     */
    private void init(final String epoch, final String epochObs) {        
        this.setEpochObs(epochObs);        
        this.setEquinox(epoch);
    }
    
    @Override
    public Type getReferenceSystemType() {
        return REF_SYSTEM;
    }

    @Override
    public Double getEpochObs() {
        return this.epochObs;
    }

    @Override
    public double getEquinox() {
        return this.equinox;
    }

    /**
     * Sets the equinox.
     * @param equinox the equinox to set
     */
    public void setEquinox(final String equinox) {
        this.equinox = epochs(equinox)[0];
    }

    /**
     * Sets the epoch.
     * @param epochObs the epochObs to set
     */
    public void setEpochObs(final String epochObs) {
        this.epochObs = (epochObs == null) ? null : epochs(epochObs)[0];
    }

    @Override
    public String toString() {
        return "FK4_NO_E("+this.equinox+","+this.epochObs+")";
    }
        
}
