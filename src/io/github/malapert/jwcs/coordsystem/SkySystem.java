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

import io.github.malapert.jwcs.utility.NumericalUtils;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * A sky definition can consist of a <b>sky system</b>, a <b>reference
 * system</b>, an <b>equinox</b> and an <b>epoch of observation</b>.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public abstract class SkySystem {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(SkySystem.class.getName());

    /**
     * List of supported sky systems
     */
    public enum SkySystems {
        /**
         * Galactic coordinates (lII, bII)
         */
        GALACTIC("Galactic", false),
        /**
         * Equatorial coordinates (\u03B1, \u03B4),
         */
        EQUATORIAL("Equatorial", true),
        /**
         * De Vaucouleurs Supergalactic coordinates (sgl, sgb)
         */
        SUPER_GALACTIC("Super Galactic", false),
        /**
         * Ecliptic coordinates (\u03BB, \u03B2) referred to the ecliptic and
         * mean equinox
         */
        ECLIPTIC("Ecliptic", true);
        
        /**
         * Sky system name.
         */
        private final String name;
        /**
         * Indicates if the sky system has a reference frame.
         */
        private final boolean hasReferenceFrame;
        
        /**
         * Constructor.
         * @param name sky system name
         * @param hasReferenceFrame Indicates if the sky system has e reference frame.
         */
        SkySystems(final String name, final boolean hasReferenceFrame) {
            this.name = name;
            this.hasReferenceFrame = hasReferenceFrame;
        }
        
        /**
         * Returns the name of the sky system.
         * @return sky system name
         */
        public final String getName() {
            return this.name;
        }
        
        /**
         * Tests if the sky system has a reference frame
         * @return True when the sky system has a reference frame otherwise false
         */
        public final boolean hasReferenceFrame() {
            return this.hasReferenceFrame;
        }
      
        /**
         * Returns the SkySystems based on the sky system name.
         * @param name name of the sky system
         * @return the SkySystems
         */
        public static SkySystems valueOfByName(final String name) {
            SkySystems result = null;
            SkySystems[] values = SkySystems.values();
            for (SkySystems value : values) {
                if(value.getName().equals(name)) {
                    result = value;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("SkySystem not found by searching by its name "+name);
            } else {
                return result;
            }
        }
        
        /**
         * Returns the names of SkySystems.
         * @return the names of SkySystems
         */
        public static String[] getSkySystemsName() {            
            SkySystems[] values = SkySystems.values();
            String[] result = new String[values.length];
            int index = 0;
            for (SkySystems value : values) {
                result[index] = value.getName();
                index++;
            }
            return result;
        }
    };

    /**
     * Calculates the rotation matrix to from a reference frame to another one.
     *
     * The methods in this class have been traduced from Python to JAVA
     *
     * @param refFrame the output reference frame
     * @return the rotation matrix in the output reference frame
     * @see <a href="http://www.astro.rug.nl/software/kapteyn/">The original
     * code in Python</a>
     */
    protected abstract RealMatrix getRotationMatrix(final SkySystem refFrame);

    /**
     * Returns the coordinate system name.
     *
     * @return the coordinate system name
     */
    public abstract SkySystems getSkySystemName();

    /**
     * Returns the equinox.
     *
     * @return the equinox
     */
    protected abstract double getEquinox();

    /**
     * Returns Eterms matrix for the input reference system.
     *
     * @return Eterms matrix
     */
    protected final RealMatrix getEtermsIn() {
        RealMatrix eterms = null;
        ReferenceSystemInterface.Type refSystem;
        switch (getSkySystemName()) {
            case EQUATORIAL:
                refSystem = ((Equatorial) this).getReferenceSystemType();
                if (ReferenceSystemInterface.Type.FK4.equals(refSystem)) {
                    double equinox = ((Equatorial) this).getEquinox();
                    eterms = FK4.getEterms(equinox);
                    LOG.log(Level.FINER, "getEterms EQUATORIAL(FK4) from {0} : {1}", new Object[]{equinox,eterms});
                }
                break;
            case ECLIPTIC:
                refSystem = ((Ecliptic) this).getReferenceSystemType();
                if (ReferenceSystemInterface.Type.FK4.equals(refSystem)) {
                    double equinox = ((Ecliptic) this).getEquinox();
                    eterms = FK4.getEterms(equinox);
                    LOG.log(Level.FINER, "getEterms ECLIPTIC(FK4) from {0} : {1}", new Object[]{equinox,eterms});                    
                }
                break;
        }
        return eterms;
    }

    /**
     * Returns Eterms matrix for the output reference system.
     *
     * @param refFrame the output reference system
     * @return Eterms matrix
     */
    protected final RealMatrix getEtermsOut(final SkySystem refFrame) {
        RealMatrix eterms = null;
        ReferenceSystemInterface.Type refSystem;
        switch (refFrame.getSkySystemName()) {
            case EQUATORIAL:
                refSystem = ((Equatorial) refFrame).getReferenceSystemType();
                if (ReferenceSystemInterface.Type.FK4.equals(refSystem)) {
                    double equinox = ((Equatorial) refFrame).getEquinox();
                    eterms = FK4.getEterms(equinox);
                    LOG.log(Level.FINER, "getEterms EQUATORIAL(FK4) from {0} : {1}", new Object[]{equinox,eterms});                    
                }
                break;
            case ECLIPTIC:
                refSystem = ((Ecliptic) refFrame).getReferenceSystemType();
                if (ReferenceSystemInterface.Type.FK4.equals(refSystem)) {
                    double equinox = ((Ecliptic) refFrame).getEquinox();
                    eterms = FK4.getEterms(equinox);
                    LOG.log(Level.FINER, "getEterms ECLIPTIC(FK4) from {0} : {1}", new Object[]{equinox,eterms});                                        
                }
                break;
        }
        return eterms;
    }
  
    /**
     * Checks the coordinates.
     * 
     * @param longitude longitude in degrees
     * @param latitude  latitude in degrees
     * @exception IllegalArgumentException when the coordinates are out of range
     */
    private void checkCoordinates(double longitude, double latitude) {
        boolean isLongInterval = NumericalUtils.isInInterval(longitude, 0, 360);
        boolean isLatInterval = NumericalUtils.isInInterval(latitude, -90, 90);        
        if(!isLongInterval && !isLatInterval) {
            throw new IllegalArgumentException("longitude must be in [0,360] and latitude in [-90,90]");
        } else if (!isLongInterval) {
            throw new IllegalArgumentException("longitude must be in [0,360]");    
        } else if (!isLatInterval) {
            throw new IllegalArgumentException("latitude must be in [0,360]");    
        }
    }

    /**
     * Converts the (longitude, latitude) coordinates into the output reference
     * system.
     *
     * The method has been traduced from Python to JAVA.
     *
     * @param refFrame the output reference system
     * @param longitude longitude in degrees
     * @param latitude latitude in degrees
     * @return the position in the sky in the output reference system
     * @see <a href="http://www.astro.rug.nl/software/kapteyn/">The original
     * code in Python</a>
     */
    public final SkyPosition convertTo(final SkySystem refFrame, double longitude, double latitude) {
        checkCoordinates(longitude, latitude);
        RealMatrix xyz = Utility.longlat2xyz(longitude, latitude);
        LOG.log(Level.FINER, "convert sky ({0},{1}) to xyz : {2}", new Object[]{longitude, latitude, xyz});
        RealMatrix rotation = getRotationMatrix(refFrame);
        LOG.log(Level.FINER, "Rotation matrix from {0} to {1} : {2}", new Object[]{this.getSkySystemName(),refFrame.getSkySystemName(),rotation});        
        RealMatrix etermsIn = getEtermsIn();
        LOG.log(Level.FINER, "EtermsIn : {0}", new Object[]{etermsIn});        
        RealMatrix etermsOut = getEtermsOut(refFrame);
        LOG.log(Level.FINER, "EtermsOut from {0} : {1}", new Object[]{refFrame.getSkySystemName(), etermsOut});        
        if (etermsIn != null) {
            xyz = Utility.removeEterms(xyz, etermsIn);
            LOG.log(Level.FINER, "Remove EtermsIn from xyz : {0}", new Object[]{xyz});            
        }
        xyz = rotation.multiply(xyz);
        LOG.log(Level.FINER, "Rotate xyz : {0}", new Object[]{xyz});                    
        if (etermsOut != null) {
            xyz = Utility.addEterms(xyz, etermsOut);
            LOG.log(Level.FINER, "Add EtermsOut to xyz : {0}", new Object[]{xyz});            
        }
        double[] position = Utility.xyz2longlat(xyz);
        LOG.log(Level.FINER, "Transforms xyz -> ra,dec : {0},{1}", new Object[]{position[0],position[1]});        
        LOG.log(Level.INFO, "convert ({0},{1}) from {2} to {3} --> ({4},{5})", new Object[]{longitude, latitude, this, refFrame, position[0], position[1]});
        return new SkyPosition(position[0], position[1], refFrame);
    }

    /**
     * Converts an array of (longitude1, latitude2, longitude2, latitude2, ...)
     * coordinates into the output reference system.
     *
     * @param refFrame the output reference system
     * @param coordinates an array of (longitude1, latitude2, longitude2,
     * latitude2, ...) in degrees
     * @return an array of SkyPosition
     * @throws IllegalArgumentException Raises an exception when
     * numberEltsOfCoordinates % 2 != 0
     */
    public final SkyPosition[] convertTo(final SkySystem refFrame, double[] coordinates) throws IllegalArgumentException {
        
        final int numberElts = coordinates.length;
        final int numberOfCoordinatesPerPoint = 3;
        if (numberElts % 2 != 0) {
            throw new IllegalArgumentException("coordinates should be an array containing a set of [longitude, latitude]");
        }
        final SkyPosition[] skyPositionArray = new SkyPosition[(int) (numberElts * 0.5) * numberOfCoordinatesPerPoint];

        RealMatrix rotation = getRotationMatrix(refFrame);
        LOG.log(Level.FINER, "Rotation matrix from {0} to {1} : {2}", new Object[]{this.getSkySystemName(),refFrame.getSkySystemName(),rotation});
        RealMatrix etermsIn = getEtermsIn();
        LOG.log(Level.FINER, "EtermsIn : {0}", new Object[]{etermsIn});
        RealMatrix etermsOut = getEtermsOut(refFrame);
        LOG.log(Level.FINER, "EtermsOut from {0} : {1}", new Object[]{refFrame.getSkySystemName(), etermsOut});

        int indice = 0;
        for (int i = 0; i < numberElts; i = i + 2) {
            checkCoordinates(coordinates[i], coordinates[i + 1]);
            RealMatrix xyz = Utility.longlat2xyz(coordinates[i], coordinates[i + 1]);
            LOG.log(Level.FINER, "xyz : {0}", new Object[]{xyz});
            if (etermsIn != null) {
                xyz = Utility.removeEterms(xyz, etermsIn);
                LOG.log(Level.FINER, "Remove EtermsIn from xyz : {0}", new Object[]{xyz});
            }
            xyz = rotation.multiply(xyz);
            LOG.log(Level.FINER, "Rotate xyz : {0}", new Object[]{xyz});            
            if (etermsOut != null) {
                xyz = Utility.addEterms(xyz, etermsOut);
                LOG.log(Level.FINER, "Add EtermsOut to xyz : {0}", new Object[]{xyz});
            }
            double[] position = Utility.xyz2longlat(xyz);
            LOG.log(Level.FINER, "Transforms xyz -> ra,dec : {0}", new Object[]{position});
            skyPositionArray[indice] = new SkyPosition(position[0], position[1], refFrame);
            indice++;
        }
        LOG.log(Level.INFO, "convert {0} from {1} to {2} --> {3}", new Object[]{Arrays.toString(coordinates), this.getSkySystemName(), refFrame.getSkySystemName(), Arrays.toString(skyPositionArray)});
        return skyPositionArray;
    }

    /**
     * Computes the angular separation between two sky positions.
     *
     * @param pos1 sky position in a reference frame
     * @param pos2 sky position in a reference frame
     * @return angular separation in degrees.
     */
    public static final double separation(final SkyPosition pos1, final SkyPosition pos2) {
        SkySystem skySystem = pos1.getRefFrame();
        SkyPosition pos1InRefFramePos2 = skySystem.convertTo(pos2.getRefFrame(), pos1.getLongitude(), pos1.getLatitude());
        double[] pos1XYZ = pos1InRefFramePos2.getCartesian();
        double[] pos2XYZ = pos2.getCartesian();
        double normPos1 = Math.sqrt(pos1XYZ[0] * pos1XYZ[0] + pos1XYZ[1] * pos1XYZ[1] + pos1XYZ[2] * pos1XYZ[2]);
        double normPos2 = Math.sqrt(pos2XYZ[0] * pos2XYZ[0] + pos2XYZ[1] * pos2XYZ[1] + pos2XYZ[2] * pos2XYZ[2]);
        double separation = NumericalUtils.aacos((pos1XYZ[0] * pos2XYZ[0] + pos1XYZ[1] * pos2XYZ[1] + pos1XYZ[2] * pos2XYZ[2]) / (normPos1 * normPos2));
        LOG.log(Level.INFO, "seratation({0},{1}) =  {2}", new Object[]{pos1, pos2, Math.toDegrees(separation)});
        return Math.toDegrees(separation);
    }

    /**
     * Returns a SkySystem based on the sky system name.
     *
     * @param skySystemName sky system name
     * @return the SkySystem
     */
    public static final SkySystem getSkySystemFromName(final SkySystems skySystemName) {
        SkySystem skySystem;
        LOG.log(Level.INFO, "Get sky system {0}", new Object[]{skySystemName.name()});
        switch (skySystemName) {
            case ECLIPTIC:
                skySystem = new Ecliptic();
                break;
            case EQUATORIAL:
                skySystem = new Equatorial();
                break;
            case GALACTIC:
                skySystem = new Galactic();
                break;
            case SUPER_GALACTIC:
                skySystem = new SuperGalactic();
                break;
            default:
                throw new IllegalArgumentException(skySystemName + " not supported as sky system");
        }
        return skySystem;
    }

}
