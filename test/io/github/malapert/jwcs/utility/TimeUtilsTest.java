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
package io.github.malapert.jwcs.utility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests of Utility package.
 * @author Jean-Christophe Malapert
 */
public class TimeUtilsTest {
    
    public TimeUtilsTest() {
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
     * Test of julianDateToISO method, of class TimeUtils.
     */
    @Test
    public void testJulianDateToISO() {
        System.out.println("julianDateToISO");
        double julianDate = 2456915;
        String expResult = "2014-09-14T12:00:00";
        String result = TimeUtils.julianDateToISO(julianDate);
        assertEquals(expResult, result);
    }

    /**
     * Test of modifiedJulianDateToISO method, of class TimeUtils.
     */
    @Test
    public void testModifiedJulianDateToISO() {
        System.out.println("modifiedJulianDateToISO");
        double modifiedJulianDate = 53005;
        String expResult = "2004-01-01T00:00:00";
        String result = TimeUtils.modifiedJulianDateToISO(modifiedJulianDate);
        assertEquals(expResult, result);
    }

    /**
     * Test of ISOToJulianDate method, of class TimeUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testISOToJulianDate() throws Exception {
        System.out.println("ISOToJulianDate");
        String dateObs = "2014-09-14T12:08:18";
        double expResult = 2456915.005763889;
        double result = TimeUtils.ISOToJulianDate(dateObs);
        assertEquals(expResult, result, 1e-9);             
    }
    
    
    /**
     * Test of ISOToModifiedJulianDate method, of class TimeUtils.
     * @throws java.lang.Exception
     */
    @Test
    public void testISOToModifiedJulianDate() throws Exception {
        System.out.println("ISOToModifiedJulianDate");
        
        String datObs = "2004-01-01T00:00:00.000";        
        double result = TimeUtils.ISOToModifiedJulianDate(datObs);
        double expResultMJD = 53005.0;
        assertEquals(expResultMJD, result, 1e-6);
    }
    
    @Test
    public void convertAndreverse() throws Exception {
        System.out.println("Convert and reverse");
        
        String dateObs = "10/03/78";        
        double result = TimeUtils.ISOToModifiedJulianDate(dateObs);
        String expResult = TimeUtils.modifiedJulianDateToISO(result);
        assertEquals(expResult, "1978-03-10T00:00:00");       
        
        dateObs = "1978-03-10";        
        result = TimeUtils.ISOToModifiedJulianDate(dateObs);
        expResult = TimeUtils.modifiedJulianDateToISO(result);
        assertEquals(expResult, "1978-03-10T00:00:00");        
        
        dateObs = "1978-03-10T10:20:02";        
        result = TimeUtils.ISOToModifiedJulianDate(dateObs);
        expResult = TimeUtils.modifiedJulianDateToISO(result);
        assertEquals(expResult, dateObs);           
    }
    
}
