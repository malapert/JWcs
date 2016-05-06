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

/**
 * A frame of reference (or reference frame) refers 
 * to a coordinate system used to represent and 
 * measure properties of objects, such as their 
 * position and orientation, at different moments of time.
 * 
 * In 'Representations of celestial coordinates in FITS' (Calabretta and Greisen) 
 * we read that all reference systems are allowed for both equatorial- and 
 * ecliptic coordinates, except FK4-NO-E, which is only allowed for equatorial 
 * coordinates. If FK4-NO-E is given in combination with an ecliptic 
 * sky system then silently FK4 is assumed.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public interface ReferenceSystemInterface {

    /**
     * A representation of the different reference systems.
     */
    public enum Type {
        /**
         * The International Celestial Reference System, for optical data 
         * realized through the Hipparcos catalog. 
         * By definition, ICRS is not an equatorial system, but it is 
         * very close to the FK5 (J2000) system. No Equinox value is required.
         */
        ICRS("ICRS", false, false),
        /**
         * Mean place post IAU 1976 system. 
         * Also a barycentric equatorial coordinate system. 
         * This should be qualified by an Equinox value (only).
         */
        FK5("FK5", true, false), 
        /**
         * Mean place pre-IAU 1976 system. 
         * FK4 is the old barycentric (i.e. w.r.t. the common 
         * center of mass) equatorial coordinate 
         * system, which should be qualified by an Equinox value.
         * For accurate work FK4 coordinate systems should also be qualified
         * by an Epoch value. This is the *epoch of observation*.
         */
        FK4("FK4", true, true),
        /**
         * The old FK4 (barycentric) equatorial system
         * but without the *E-terms of aberration*.
         * This coordinate system should also be 
         * qualified by both an Equinox and an Epoch value.
         */
        FK4_NO_E("FK4 NO E-terms", true, true), 
        /**
         * This is an equatorial coordinate system based on 
         * the mean dynamical equator and equinox at epoch J2000.
         * The dynamical equator and equinox differ slightly 
         * compared to the equator and equinox of FK5 at J2000 and 
         * the ICRS system. This system need not be qualified 
         * by an Equinox value.
         */
        J2000("J2000", false, false);
        
        /**
         * Name of the reference frame.
         */
        private final String name;
        
        /**
         * Needs an equinox value as parameter.
         */
        private final boolean hasEquinox;
        
        /**
         * Needs an epoch value as parameter.
         */
        private final boolean hasEpoch;
        
        /**
         * Constructor.
         * @param name Name of the reference frame
         * @param hasEquinox Can have equinox as parameter
         * @param hasEpoch Can have epoch as parameter
         */
        Type(final String name, boolean hasEquinox, boolean hasEpoch) {
            this.name = name;
            this.hasEquinox = hasEquinox;
            this.hasEpoch = hasEpoch;
        }
        
        public String getName() {
            return this.name;
        }
        
        /**
         * Returns True when the reference frame needs the equinox as parameter.
         * @return True when the reference frame needs the equinox as parameter otherwise False
         */
        public boolean hasEquinox() {
            return this.hasEquinox;
        }

        /**
         * Returns True when the reference frame needs the epoch as parameter.
         * @return True when the reference frame needs the epoch as parameter otherwise False
         */        
        public boolean hasEpoch() {
            return this.hasEpoch;
        }
        
        /**
         * Returns the ReferenceFrame based on its name.
         * @param name name of the reference frame
         * @return the ReferenceFrame type
         */
        public static Type valueOfByName(final String name) {
            Type result = null;
            Type[] values = Type.values();
            for (Type value : values) {
                if(value.getName().equals(name)) {
                    result = value;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("Referenc frame not found by searching by its name "+name);
            } else {
                return result;
            }
        }
        
        /**
         * Returns the names of ReferenceFrame.
         * @return the names of ReferenceFrame
         */
        public static String[] ReferenceFramesName() {            
            Type[] values = Type.values();
            String[] result = new String[values.length];
            int index = 0;
            for (Type value : values) {
                result[index] = value.getName();
                index++;
            }
            return result;
        }
                
    };    
   
    /**
     * Returns the reference system that is used.
     * @return the reference system that is used
     */
    ReferenceSystemInterface.Type getReferenceSystemType();
    
    /**
     * Returns the epoch of observation
     * @return null when epoch of observation is not required other the epoch
     * of observation
     */
    Float getEpochObs();
    
    /**
     * Returns the equinox.
     * @return the equinox
     */
    float getEquinox();
       
}
