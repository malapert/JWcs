/*
 * Copyright (C) 2016 malapert
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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author malapert
 */
public class SkyPositionTest {
    
    public SkyPositionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getLongitude method, of class SkyPosition.
     */
    @Test
    public void testGetLongitude() {
        System.out.println("getLongitude");
        SkyPosition instance = new SkyPosition(10, 0, new Equatorial());
        double expResult = 10.0;
        double result = instance.getLongitude();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getLongitudeAsSexagesimal method, of class SkyPosition.
     */
    @Test
    public void testGetLongitudeAsSexagesimal() {
        System.out.println("getLongitudeAsSexagesimal");
        SkyPosition instance = new SkyPosition(10.5, 0, new Equatorial());
        String expResult = "00:42:00.000";
        String result = instance.getLongitudeAsSexagesimal();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLatitude method, of class SkyPosition.
     */
    @Test
    public void testGetLatitude() {
        System.out.println("getLatitude");
        SkyPosition instance = new SkyPosition(10.5, 0, new Equatorial());
        double expResult = 0.0;
        double result = instance.getLatitude();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getLatitudeAsSexagesimal method, of class SkyPosition.
     */
    @Test
    public void testGetLatitudeAsSexagesimal() {
        System.out.println("getLatitudeAsSexagesimal");
        SkyPosition instance = new SkyPosition(10.5, 0, new Equatorial());
        String expResult = "+00:00:00.00";
        String result = instance.getLatitudeAsSexagesimal();
        assertEquals(expResult, result);
    }
}
