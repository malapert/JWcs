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
package io.github.malapert.jwcs.proj;

/**
 * The plate carrée projection.
 * 
 * <p>
 * The equator and all meridians are correctly scaled in the plate
 * carrée projection.
 * 
 * Reference: "Representations of celestial coordinates in FITS", 
 * M. R. Calabretta and E. W. Greisen - page 16
 * </p>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class CAR extends CylindricalProjection{
    
    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Plate carrée";
    
    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "no limits";     

    /**
     * Constructs a CAR based on the celestial longitude and 
     * latitude of the fiducial point (\u03B1<sub>0</sub>, \u03B4<sub>0</sub>)
     * @param crval1 Celestial longitude \u03B1<sub>0</sub> in degrees of the fiducial point
     * @param crval2 Celestial longitude \u03B4<sub>0</sub> in degrees of the fiducial point
     */
    public CAR(double crval1, double crval2) {
        super(crval1, crval2);
    }

    @Override
    protected double[] project(double x, double y) {
        double phi = Math.toRadians(x);
        double theta = Math.toRadians(y);
        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    protected double[] projectInverse(double phi, double theta) {
        phi = phiRange(phi);
        double x = Math.toDegrees(phi);
        double y = Math.toDegrees(theta);
        double[] coord = {x, y};
        return coord;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

}
