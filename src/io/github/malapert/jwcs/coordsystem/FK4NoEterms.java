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
 * The old FK4 (barycentric) equatorial system
 * but without the *E-terms of aberration*.
 * 
 * <p>This coordinate system should also be 
 * qualified by both an Equinox and an Epoch value.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class FK4NoEterms implements CoordinateReferenceFrame {

    /**
     * The name of this reference frame.
     */    
    private final static CoordinateReferenceFrame.ReferenceFrame REF_SYSTEM = CoordinateReferenceFrame.ReferenceFrame.FK4_NO_E;
    
    /**
     * The default value of the epoch sets to B1950.
     */      
    private final static String DEFAULT_EPOCH = "B1950";    
    
    /**
     * The epoch of the equinox.
     */
    private double equinox;
    
    /**
     * The epoch of observation.
     */
    private double epochObs;
    
    /**
     * Creates a FK4_NO_E reference frame with default value of the epoch of equinox {@link FK4_NO_E#DEFAULT_EPOCH}.
     */
    public FK4NoEterms() {
        init(DEFAULT_EPOCH, null);
    }
    
    /**
     * Creates a FK4_NO_E reference frame with a equinox.
     * @param epoch the epoch of equinox
     */    
    public FK4NoEterms(final String epoch) {       
        init(epoch, null);
    }
    
    /**
     * Creates a FK4_NO_E reference frame with both equinox and epoch of observation.
     * @param epoch the epoch
     * @param epochObs the epoch of observation
     */    
    public FK4NoEterms(final String epoch, final String epochObs) {       
        init(epoch, epochObs);
    }
    
    /**
     * Initialization.
     * @param epoch the epoch
     * @param epochObs the epoch of observation
     */
    private void init(final String epoch, final String epochObs) {        
        this.setEpochObs(epochObs);        
        this.setEquinox(epoch);
    }
    
    @Override
    public ReferenceFrame getReferenceFrame() {
        return REF_SYSTEM;
    }

    @Override
    /**
     * Returns the Besselian value of the epoch of observation.
     */    
    public double getEpochObs() {
        return this.epochObs;
    }

    @Override
    /**
     * Returns the Besselian value of the equinox.
     */    
    public double getEquinox() {
        return this.equinox;
    }

    /**
     * Sets the equinox.
     * @param equinox the equinox to set
     */
    @Override
    public final void setEquinox(final String equinox) {
        this.equinox = epochs(equinox)[0];
    }        

    /**
     * Sets the epoch of observation.
     * @param epochObs the epochObs to set
     */
    @Override
    public final void setEpochObs(final String epochObs) {
        this.epochObs = (epochObs == null) ? Double.NaN : epochs(epochObs)[0];
    }
    
    /**
     * Sets the Besselian epoch of the equinox.
     * @param equinox the equinox to set
     */
    @Override
    public void setEquinox(final double equinox) {
        this.equinox = equinox;
    }        

    /**
     * Sets the Besselian epoch of the epoch of observation.
     * @param epochObs the epochObs to set
     */
    @Override
    public void setEpochObs(final double epochObs) {
        this.epochObs = epochObs;
    }    
    
    @Override
    public String toString() {
        final String result;
        if (Double.isNaN(this.epochObs)) {
            result = "FK4_NO_E(B"+this.equinox+")";
        } else {
            result = "FK4_NO_E(B"+this.equinox+",B"+this.epochObs+")";
        }
        return result;
    }        
}
