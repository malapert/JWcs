/* 
 * Copyright (C) 2014-2022 Jean-Christophe Malapert
 *
 * This file is part of JWcs.
 * 
 * JWcs is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.github.malapert.jwcs.crs;

import io.github.malapert.jwcs.datum.FK4NoEterms;
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
public class FK4NoEtermsTest {
    
    public FK4NoEtermsTest() {
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
     * Test of getEpochObs method, of class FK4NoEterms.
     */
    @Test
    public void testGetEpochObs() {
        System.out.println("getEpochObs");
        FK4NoEterms instance = new FK4NoEterms();
        double expResult = 1950.0;
        double result = instance.getEpochObs();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getEquinox method, of class FK4NoEterms.
     */
    @Test
    public void testGetEquinox() {
        System.out.println("getEquinox");
        FK4NoEterms instance = new FK4NoEterms();
        double expResult = 1950.0;
        double result = instance.getEquinox();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setEquinox method, of class FK4NoEterms.
     */
    @Test
    public void testSetEquinox_String() {
        System.out.println("setEquinox");
        String equinox = "B1950";
        FK4NoEterms instance = new FK4NoEterms();
        instance.setEquinox(equinox);
        double expResult = 1950.0;
        double result = instance.getEquinox();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setEpochObs method, of class FK4NoEterms.
     */
    @Test
    public void testSetEpochObs_String() {
        System.out.println("setEpochObs");
        String epochObs = "B1950";
        FK4NoEterms instance = new FK4NoEterms();
        instance.setEpochObs(epochObs);
        double expResult = 1950.0;
        double result = instance.getEpochObs();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setEquinox method, of class FK4NoEterms.
     */
    @Test
    public void testSetEquinox_double() {
        System.out.println("setEquinox");
        double equinox = 2000.0;
        FK4NoEterms instance = new FK4NoEterms();
        instance.setEquinox(equinox);
        double expResult = 2000.0;
        double result = instance.getEquinox();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setEpochObs method, of class FK4NoEterms.
     */
    @Test
    public void testSetEpochObs_double() {
        System.out.println("setEpochObs");
        double epochObs = 2000.0;
        FK4NoEterms instance = new FK4NoEterms();
        instance.setEpochObs(epochObs);
        double expResult = 2000.0;
        double result = instance.getEpochObs();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of toString method, of class FK4NoEterms.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        FK4NoEterms instance = new FK4NoEterms("B1950", "B2000");
        instance.setEquinox(1978);
        String expResult = "FK4_NO_E(B1978.0,B2000.0)";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class FK4NoEterms.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        FK4NoEterms obj = new FK4NoEterms();
        obj.setEquinox(2000);
        FK4NoEterms instance = new FK4NoEterms();
        instance.setEquinox("B2000");
        boolean expResult = true;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
    }
    
}
