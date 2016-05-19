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

import io.github.malapert.jwcs.JWcsFits;
import io.github.malapert.jwcs.proj.exception.JWcsException;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Projection test.
 *
 * @author Jean-Christophe Malapert
 */
@Ignore
public class AbstractProjectionTest {

    /**
     *
     */
    protected JWcsFits wcs;

    /**
     *
     */
    protected double minLat;

    /**
     *
     */
    protected double maxLat;

    /**
     *
     */
    protected double tolerance;

    private static final double TOLERANCE = 1.0e-10;

    /**
     *
     * @param wcs
     * @throws JWcsException
     */
    public AbstractProjectionTest(final JWcsFits wcs) throws JWcsException {
        this(wcs, TOLERANCE);
    }

    /**
     *
     * @param wcs
     * @param tolerance
     * @throws JWcsException
     */
    public AbstractProjectionTest(final JWcsFits wcs, final double tolerance) throws JWcsException {
        this.wcs = wcs;
        this.tolerance = tolerance;
        this.wcs.doInit();
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println();
        System.out.println("--------------------------");
    }

    /**
     *
     */
    @Test
    public void testProjUnproj() {
        System.out.println("project & inverse project on the whole sphere");
        double deltaLongitudeMax = 0.0;
        double deltaLatitudeMax = 0.0;

        for (int latitude = -90; latitude <= 90; latitude++) {
            for (int longitude = 0; longitude < 360; longitude++) {
                try {
                    if (wcs.inside(longitude, latitude)) {
                        final double[] pixels = wcs.wcs2pix(longitude, latitude);
                        final double[] skyPos = wcs.pix2wcs(pixels);
                        double deltaLongitude = Math.abs(skyPos[0] - longitude);
                        if (deltaLongitude > 180) {
                            deltaLongitude = 360 - deltaLongitude;
                        }
                        if (Math.abs(latitude) != 90 && deltaLongitude > deltaLongitudeMax) {
                            deltaLongitudeMax = deltaLongitude;
                        }
                        final double deltaLatitude = Math.abs(skyPos[1] - latitude);
                        if (deltaLatitude > deltaLatitudeMax) {
                            deltaLatitudeMax = deltaLatitude;
                        }

                        if (deltaLatitude > tolerance) {
                            System.out.printf("longitude = %d lat = %d\n", longitude, latitude);
                            System.out.printf("poject: x = %20.15f y = %20.15f\n", pixels[0], pixels[1]);
                            System.out.printf("Unproject : longitude = %20.15f latitude = %20.15f\n", skyPos[0], skyPos[1]);
                            System.out.println();
                        } else if (Math.abs(latitude) != 90 && deltaLongitude > tolerance) {
                            System.out.printf("longitue = %d lat = %d\n", longitude, latitude);
                            System.out.printf("poject: x = %20.15f y = %20.15f\n", pixels[0], pixels[1]);
                            System.out.printf("Unproject : longitude = %20.15f latitude = %20.15f\n", skyPos[0], skyPos[1]);
                            System.out.println();
                        }
                    }
                } catch (ProjectionException err) {
                    System.out.printf("Error: lng = %d  lat = %d\n",
                            longitude, latitude);
                    System.out.println(err.getMessage());
                }

            }
        }
        System.out.printf("  Maximum residual (sky): lng: %12.6e  lat: %12.6e\n",
                deltaLongitudeMax, deltaLatitudeMax);
        assertEquals(1e-12, deltaLongitudeMax, tolerance);
        assertEquals(1e-12, deltaLatitudeMax, tolerance);
    }

}
