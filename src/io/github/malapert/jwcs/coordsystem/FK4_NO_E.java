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
     * The default value of the equinox.
     */      
    private final static float DEFAULT_EQUINOX = 1950f;
    
    /**
     * The default value of the epoch of observation.
     */     
    private final static float DEFAULT_EPOCH = 1950f;
    
    /**
     * The epoch of the equinox.
     */
    private float equinox;
    
    /**
     * The epoch of observation.
     */
    private float epochObs;
    
    /**
     * Creates a FK4 reference frame with default values of both equinox
     * and epoch of observation.
     */
    public FK4_NO_E() {
        init(DEFAULT_EQUINOX, DEFAULT_EPOCH);
    }
    
    /**
     * Creates a FK4_NO_E reference frame with a equinox.
     * @param equinox the equinox
     */    
    public FK4_NO_E(float equinox) {       
        init(equinox, DEFAULT_EPOCH);
    }
    
    /**
     * Creates a FK4_NO_E reference frame with both equinox and epoch of observation.
     * @param equinox the equinox
     * @param epoch the epoch of observation
     */    
    public FK4_NO_E(float equinox, float epoch) {       
        init(equinox, epoch);
    }
    
    /**
     * initialization.
     * @param equinox the equinox
     * @param epochObs the epoch of observation
     */
    private void init(float equinox, float epochObs) {
        this.setEpochObs(epochObs);
        this.setEquinox(equinox);
    }
    
    @Override
    public Type getReferenceSystemType() {
        return REF_SYSTEM;
    }

    @Override
    public Float getEpochObs() {
        return this.epochObs;
    }

    @Override
    public float getEquinox() {
        return this.equinox;
    }

    /**
     * Sets the equinox.
     * @param equinox the equinox to set
     */
    public void setEquinox(float equinox) {
        this.equinox = equinox;
    }

    /**
     * Sets the epoch.
     * @param epochObs the epochObs to set
     */
    public void setEpochObs(float epochObs) {
        this.epochObs = epochObs;
    }
}
