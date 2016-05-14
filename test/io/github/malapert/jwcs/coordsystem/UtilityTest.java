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
import static io.github.malapert.jwcs.utility.NumericalUtils.createRealMatrix;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author malapert
 */
public class UtilityTest {
    
    /**
     *
     */
    public UtilityTest() {
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
    }
    
    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     *
     */
    @Before
    public void setUp() {
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }



    /**
     * Test of longlat2xyz method, of class Utility.
     */
    @Test
    public void testLonglat2xyz() {
        System.out.println("longlat2xyz");
        double longitude = 11.109013566461968;
        double latitude = -89.9698527254359;
        RealMatrix result = Utility.longlat2xyz(longitude, latitude);
        double[]resultLongLat = Utility.xyz2longlat(result);
        assertArrayEquals(new double[]{longitude, latitude}, resultLongLat, NumericalUtils.DOUBLE_TOLERANCE);
    }

    /**
     * Test of xyz2longlat method, of class Utility.
     */
    @Test
    public void testXyz2longlat() {
        System.out.println("xyz2longlat");
        double[][] array = {
            {1.6255503576607638E-6},
            {3.191858779557839E-7},
            {-0.9999998615729888}               
        };        
        RealMatrix xyz = createRealMatrix(array);
        double[] expResult = xyz.getColumn(0);
        double[] result = Utility.xyz2longlat(xyz);
        RealMatrix lonlat = Utility.longlat2xyz(result[0], result[1]);
        assertArrayEquals(expResult, lonlat.getColumn(0), 1e-6);
    }
    
}
