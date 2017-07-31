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
public class FK5Test {
    
    public FK5Test() {
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
     * Test of getEquinox method, of class FK5.
     */
    @Test
    public void testGetEquinox() {
        System.out.println("getEquinox");
        FK5 instance = new FK5();
        double expResult = 2000.0;
        double result = instance.getEquinox();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getEpochObs method, of class FK5.
     */
    @Test
    public void testGetEpochObs() {
        System.out.println("getEpochObs");
        FK5 instance = new FK5();
        double expResult = Double.NaN;
        double result = instance.getEpochObs();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setEquinox method, of class FK5.
     */
    @Test
    public void testSetEquinox_String() {
        System.out.println("setEquinox");
        String equinox = "J2001";
        FK5 instance = new FK5();
        instance.setEquinox(equinox);
        double expResult = 2001.0;
        double result = instance.getEquinox();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setEquinox method, of class FK5.
     */
    @Test
    public void testSetEquinox_double() {
        System.out.println("setEquinox");
        double equinox = 2001.0;
        FK5 instance = new FK5();
        instance.setEquinox(equinox);
        double expResult = 2001.0;
        double result = instance.getEquinox();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setEpochObs method, of class FK5.
     */
    @Test
    public void testSetEpochObs_String() {
        System.out.println("setEpochObs");
        String epochObs = "B1950";
        FK5 instance = new FK5();
        instance.setEpochObs(epochObs);
        double expResult = Double.NaN;
        double result = instance.getEpochObs();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setEpochObs method, of class FK5.
     */
    @Test
    public void testSetEpochObs_double() {
        System.out.println("setEpochObs");
        double epochObs = 1950.0;
        FK5 instance = new FK5();
        instance.setEpochObs(epochObs);
        double expResult = Double.NaN;
        double result = instance.getEpochObs();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of equals method, of class FK5.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = new FK5();
        Object obj2 =new FK5("B1950");
        FK5 instance = new FK5();       
        boolean expResult = true;
        boolean expResult2 = false;  
        boolean result = instance.equals(obj);
        boolean result2 = instance.equals(obj2);
        assertEquals(expResult, result);
        assertEquals(expResult2, result2);        
    }
    
}
