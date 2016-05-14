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
    
    /**
     *
     */
    public TimeUtilsTest() {
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
    
    /**
     *
     * @throws Exception
     */
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

    /**
     * Test of epochJulian2JD method, of class TimeUtils.
     */
    @Test
    public void testEpochJulian2JD() {
        System.out.println("epochJulian2JD");
        double jEpoch = 1983.99863107d;
        double expResult = 2445700.5d;
        double result = TimeUtils.epochJulian2JD(jEpoch);
        assertEquals(expResult, result, 1e-1);
    }

    /**
     * Test of JD2epochJulian method, of class TimeUtils.
     */
    @Test
    public void testJD2epochJulian() {
        System.out.println("JD2epochJulian");
        double jd = 2445700.5d;
        double expResult = 1983.99863107d;
        double result = TimeUtils.JD2epochJulian(jd);
        assertEquals(expResult, result, 1e-7);
    }

    /**
     * Test of epochBessel2JD method, of class TimeUtils.
     */
    @Test
    public void testEpochBessel2JD() {
        System.out.println("epochBessel2JD");
        double bEpoch = 1983.99956681d;
        double expResult = 1983.99956681d;
        double jd = TimeUtils.epochBessel2JD(bEpoch);
        double result = TimeUtils.JD2epochBessel(jd);
        assertEquals(expResult, result, 1e-7);
    }

    /**
     * Test of epochs method, of class TimeUtils.
     */
    @Test
    public void testEpochs() {
        System.out.println("epochs");
        String epoch = "F2008-03-31T8:09";
        double[] expResult = new double[]{2008.2474210134737, 2008.2459673739454, 2454556.8395833336};
        double[] result = TimeUtils.epochs(epoch);
        assertArrayEquals(expResult, result, 1e-12);
    }

    /**
     * Test of epochs method, of class TimeUtils.
     */
    @Test
    public void testEpochs1() {
        System.out.println("epochs");
        String epoch = "F2007-01-14T13:18:59.9";
        double[] expResult = new double[]{2007.0378545262108, 2007.0364267212976, 2454115.0548599539};
        double[] result = TimeUtils.epochs(epoch);
        assertArrayEquals(expResult, result, 1e-12);
    }

    /**
     * Test of epochs method, of class TimeUtils.
     */
    @Test
    public void testEpochs2() {
        System.out.println("epochs");
        String epoch = "j2007.0364267212976";
        double[] expResult = new double[]{2007.0378545262108, 2007.0364267212976, 2454115.0548599539};
        double[] result = TimeUtils.epochs(epoch);
        assertArrayEquals(expResult, result, 1e-12);
    }

    /**
     * Test of epochs method, of class TimeUtils.
     */
    @Test
    public void testEpochs3() {
        System.out.println("epochs");
        String epoch = "b2007.0378545262108";
        double[] expResult = new double[]{2007.0378545262108, 2007.0364267212976, 2454115.0548599539};
        double[] result = TimeUtils.epochs(epoch);
        assertArrayEquals(expResult, result, 1e-12);
    }
    

    /**
     * Test of jd method, of class TimeUtils.
     */
    @Test
    public void testJd() {
        System.out.println("jd");
        int year = -4712;
        int month = 1;
        double dayNumber = 1.5d;
        double expResult = 0.0d;
        double result = TimeUtils.jd(year, month, dayNumber);
        assertEquals(expResult, result, 0.0);
    }
    
    /**
     * Test of jd method, of class TimeUtils.
     */
    @Test
    public void testJd1() {
        System.out.println("jd");
        int year = 0;
        int month = 1;
        double dayNumber = 1.0d;
        double expResult = 1721057.5d;
        double result = TimeUtils.jd(year, month, dayNumber);
        assertEquals(expResult, result, 0.0);
    }    

    /**
     * Test of jd method, of class TimeUtils.
     */
    @Test
    public void testJd2() {
        System.out.println("jd");
        int year = 1582;
        int month = 10;
        double dayNumber = 4.0d;
        double expResult = 2299159.5d;
        double result = TimeUtils.jd(year, month, dayNumber);
        assertEquals(expResult, result, 0.0);
    }      
    
    /**
     * Test of jd method, of class TimeUtils.
     */
    @Test
    public void testJd3() {
        System.out.println("jd");
        int year = 1582;
        int month = 10;
        double dayNumber = 15.0d;
        double expResult = 2299170.5d;
        double result = TimeUtils.jd(year, month, dayNumber);
        assertEquals(expResult, result, 0.0);
    }       
    
    /**
     * Test of jd method, of class TimeUtils.
     */
    @Test
    public void testJd4() {
        System.out.println("jd");
        int year = 1582;
        int month = 10;
        double dayNumber = 15.5d;
        double expResult = 2299161.0d;
        double result = TimeUtils.jd(year, month, dayNumber);
        assertEquals(expResult, result, 0.0);
    }     
    
    /**
     * Test of jd method, of class TimeUtils.
     */
    @Test
    public void testJd5() {
        System.out.println("jd");
        int year = 1970;
        int month = 1;
        double dayNumber = 1d;
        double expResult = 2440587.5d;
        double result = TimeUtils.jd(year, month, dayNumber);
        assertEquals(expResult, result, 0.0);
    }      
}
