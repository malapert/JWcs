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

import io.github.malapert.jwcs.coordsystem.SkySystem.SkySystems;
import io.github.malapert.jwcs.utility.DMS;
import io.github.malapert.jwcs.utility.HMS;

/**
 * Represents a position in the sky.
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class SkyPosition {
    
    public static final double TO_HOURS = 24d/360d;
    
    /**
     * Longitude in decimal degrees of the position
     * @see #getLongitude() 
     * @see #getLongitudeAsSexagesimal() 
     */
    private double longitude;
    /**
     * Latitude in decimal degrees of the position
     * @see #getLatitude() 
     * @see #getLatitudeAsSexagesimal()  
     */    
    private double latitude;
    /**
     * SkySystem of the position.
     * @see #getRefFrame() 
     */
    private SkySystem refFrame;

    /**
     * Creates a position in the sky.
     * <p>
     * The position can be created in different kind of sky system.
     * </p>
     * @param longitude longitude in decimal degrees of the position
     * @param latitude latitude in decimal degrees of the position
     * @param refFrame sky system of the position
     */
    public SkyPosition(double longitude, double latitude, final SkySystem refFrame) {
        this.longitude = longitude%360;
        this.latitude = latitude;
        this.refFrame = refFrame;
    } 

    /**
     * Returns the longitude in decimal degrees.
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }
    
    /**
     * Returns the longitude in sexagesimal HH:MM:SS.SS
     * @return the longitude in sexagesimal HH:MM:SS.SS
     */
    public String getLongitudeAsSexagesimal() {
        HMS hms = new HMS(getLongitude()*TO_HOURS);
        return hms.toString(true);
    }

    /**
     * Sets the longitude in decimal degrees.
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the latitude in decimal degrees.
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude in decimal degrees.
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    /**
     * Returns the latitude in sexagesimal DD:MM:SS.SS
     * @return the latitude
     */
    public String getLatitudeAsSexagesimal() {
        DMS dms = new DMS(getLatitude());
        return dms.toString(true);
    }   
    
    /**
     * Returns the skyposition as a double array.
     * @return the sky position.
     */
    public double[] getDoubleArray() {
        return new double[]{getLongitude(), getLatitude()};
    }

    /**
     * Returns the sky system.
     * @return the refFrame
     */
    public SkySystem getRefFrame() {
        return refFrame;
    }

    /**
     * Sets the sky system.
     * @param refFrame the refFrame to set
     */
    public void setRefFrame(final SkySystem refFrame) {
        this.refFrame = refFrame;
    }

    /**
     * Returns the cartesian coordinates in the current reference frame.
     * @return the cartesian coordinates as an array
     */
    public double[] getCartesian() {
        return Utility.longlat2xyz(this.longitude, this.latitude).getColumn(0);
    }
      
    @Override
    public String toString() {
        String referenceSystem;
        SkySystems skySystemName = getRefFrame().getSkySystemName();
        switch (skySystemName) {
            case EQUATORIAL:
                referenceSystem = ((Equatorial) getRefFrame()).getReferenceSystemType().name();
                break;
            case ECLIPTIC:
                referenceSystem = ((Ecliptic) getRefFrame()).getReferenceSystemType().name();
                break;
            default:
                referenceSystem = "No reference";
        }

        return "SkyPosition : (" + getLongitude() + "," + getLatitude() + ") or (" + getLongitudeAsSexagesimal() + "," + getLatitudeAsSexagesimal() + ") in " + skySystemName.name() + " system with " + referenceSystem + " system";
    }  
}
