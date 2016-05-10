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

import io.github.malapert.jwcs.utility.TimeUtils;
import static io.github.malapert.jwcs.utility.TimeUtils.jd;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
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
public class UtilityTest {
    
    public UtilityTest() {
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
     * Test of julianMatrixEpoch12Epoch2 method, of class Utility.
     */
    @Test
    public void testJulianMatrixEpoch12Epoch2() {
        System.out.println("julianMatrixEpoch12Epoch2");
        double jEpoch1 = 1950.0d;
        double jEpoch2 = 2000.0d;
        RealMatrix result1 = Utility.julianMatrixEpoch12Epoch2(jEpoch1, jEpoch2);
        RealMatrix result2 = Utility.julianMatrixEpoch12Epoch2(jEpoch2, jEpoch1);
        RealMatrix result = result1.multiply(result2);
        RealMatrix expResult = MatrixUtils.createRealIdentityMatrix(3);
        assertArrayEquals(expResult.getRow(0), result.getRow(0), 1e-12);
        assertArrayEquals(expResult.getRow(1), result.getRow(1), 1e-12);
        assertArrayEquals(expResult.getRow(2), result.getRow(2), 1e-12);
    }

    /**
     * Test of lieskeprecangles method, of class Utility.
     */
    @Test
    public void testLieskeprecangles() {
        System.out.println("lieskeprecangles");
        double jd1 = jd(1984,1,1);
        double jd2 = jd(2000,1,1.5);
        double[] expResult = new double[]{0.10249958598931658, 0.10250522534285664, 0.089091092843880629};
        double[] result = Utility.lieskeprecangles(jd1, jd2);
        assertArrayEquals(expResult, result, 1e-9);
    }

//    /**
//     * Test of precessionMatrix method, of class Utility.
//     */
//    @Test
//    public void testPrecessionMatrix() {
//        System.out.println("precessionMatrix");
//        double zeta = 0.0;
//        double z = 0.0;
//        double theta = 0.0;
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.precessionMatrix(zeta, z, theta);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of besselianMatrixEpoch12Epoch2 method, of class Utility.
//     */
//    @Test
//    public void testBesselianMatrixEpoch12Epoch2() {
//        System.out.println("besselianMatrixEpoch12Epoch2");
//        double bEpoch1 = 0.0;
//        double bEpoch2 = 0.0;
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.besselianMatrixEpoch12Epoch2(bEpoch1, bEpoch2);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
    /**
     * Test of newcombPrecAngles method, of class Utility.
     */
    @Test
    public void testNewcombPrecAngles() {
        System.out.println("newcombPrecAngles");
        double epoch1 = 1950.0;
        double epoch2 = TimeUtils.epochs("F1984-01-01")[0];
        double[] expResult = new double[]{783.70924627097793, 783.80093464073127, 681.38829828393466};
        double[] result = Utility.newcombPrecAngles(epoch1, epoch2);
        result[0]*=3600;
        result[1]*=3600;
        result[2]*=3600;
        assertArrayEquals(expResult, result, 1e-12);
    }
//
//    /**
//     * Test of FK42FK5Matrix method, of class Utility.
//     */
//    @Test
//    public void testFK42FK5Matrix_float() {
//        System.out.println("FK42FK5Matrix");
//        float t = 0.0F;
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.FK42FK5Matrix(t);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of FK42FK5Matrix method, of class Utility.
//     */
//    @Test
//    public void testFK42FK5Matrix_0args() {
//        System.out.println("FK42FK5Matrix");
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.FK42FK5Matrix();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
    /**
     * Test of FK52FK4Matrix method, of class Utility.
     */
    @Test
    public void testFK52FK4Matrix() {
        System.out.println("FK52FK4Matrix");        
        RealMatrix resultTest1 = Utility.FK42FK5Matrix(null);
        RealMatrix resultTest2 = Utility.FK52FK4Matrix(null);
        RealMatrix result = resultTest1.multiply(resultTest2);
        RealMatrix expResult = MatrixUtils.createRealIdentityMatrix(3);
        assertArrayEquals(expResult.getRow(0), result.getRow(0), 1e-12);
        assertArrayEquals(expResult.getRow(1), result.getRow(1), 1e-12);
        assertArrayEquals(expResult.getRow(2), result.getRow(2), 1e-12);
    }

//
//    /**
//     * Test of ICRS2FK5Matrix method, of class Utility.
//     */
//    @Test
//    public void testICRS2FK5Matrix() {
//        System.out.println("ICRS2FK5Matrix");
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.ICRS2FK5Matrix();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of ICRS2J2000Matrix method, of class Utility.
//     */
//    @Test
//    public void testICRS2J2000Matrix() {
//        System.out.println("ICRS2J2000Matrix");
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.ICRS2J2000Matrix();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of IAU2006MatrixEpoch12Epoch2 method, of class Utility.
//     */
//    @Test
//    public void testIAU2006MatrixEpoch12Epoch2() {
//        System.out.println("IAU2006MatrixEpoch12Epoch2");
//        float epoch1 = 0.0F;
//        float epoch2 = 0.0F;
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.IAU2006MatrixEpoch12Epoch2(epoch1, epoch2);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of IAU2006PrecAngles method, of class Utility.
//     */
//    @Test
//    public void testIAU2006PrecAngles() {
//        System.out.println("IAU2006PrecAngles");
//        float epoch = 0.0F;
//        double[] expResult = null;
//        double[] result = Utility.IAU2006PrecAngles(epoch);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

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
        assertArrayEquals(new double[]{longitude, latitude}, resultLongLat, 1e-12);
    }

//    /**
//     * Test of longlatRad2xyz method, of class Utility.
//     */
//    @Test
//    public void testLonglatRad2xyz() {
//        System.out.println("longlatRad2xyz");
//        double longitudeRad = 0.0;
//        double latitudeRad = 0.0;
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.longlatRad2xyz(longitudeRad, latitudeRad);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

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
        RealMatrix xyz = MatrixUtils.createRealMatrix(array);
        double[] expResult = xyz.getColumn(0);
        double[] result = Utility.xyz2longlat(xyz);
        RealMatrix lonlat = Utility.longlat2xyz(result[0], result[1]);
        assertArrayEquals(expResult, lonlat.getColumn(0), 1e-6);
    }

//    /**
//     * Test of removeEterms method, of class Utility.
//     */
//    @Test
//    public void testRemoveEterms() {
//        System.out.println("removeEterms");
//        RealMatrix xyz = null;
//        RealMatrix eterm = null;
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.removeEterms(xyz, eterm);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addEterms method, of class Utility.
//     */
//    @Test
//    public void testAddEterms() {
//        System.out.println("addEterms");
//        RealMatrix xyz = null;
//        RealMatrix eterm = null;
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.addEterms(xyz, eterm);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of MatrixEqB19502Gal method, of class Utility.
     */
    @Test
    public void testMatrixEqB19502Gal() {
        System.out.println("MatrixEqB19502Gal");
        RealMatrix result1 = Utility.MatrixEqB19502Gal();
        RealMatrix result2 = Utility.MatrixEqB19502Gal().transpose();
        RealMatrix result = result1.multiply(result2);
        RealMatrix expResult = MatrixUtils.createRealIdentityMatrix(3);
        assertArrayEquals(expResult.getRow(0), result.getRow(0), 1e-12);
        assertArrayEquals(expResult.getRow(1), result.getRow(1), 1e-12);
        assertArrayEquals(expResult.getRow(2), result.getRow(2), 1e-12);
    }

//    /**
//     * Test of MatrixGal2Sgal method, of class Utility.
//     */
//    @Test
//    public void testMatrixGal2Sgal() {
//        System.out.println("MatrixGal2Sgal");
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.MatrixGal2Sgal();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of obliquity2000 method, of class Utility.
//     */
//    @Test
//    public void testObliquity2000() {
//        System.out.println("obliquity2000");
//        double jd = 0.0;
//        double expResult = 0.0;
//        double result = Utility.obliquity2000(jd);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of obliquity1980 method, of class Utility.
//     */
//    @Test
//    public void testObliquity1980() {
//        System.out.println("obliquity1980");
//        double jd = 0.0;
//        double expResult = 0.0;
//        double result = Utility.obliquity1980(jd);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of MatrixEq2Ecl method, of class Utility.
//     */
//    @Test
//    public void testMatrixEq2Ecl() {
//        System.out.println("MatrixEq2Ecl");
//        float epoch = 0.0F;
//        ReferenceSystemInterface.Type refSystem = null;
//        RealMatrix expResult = null;
//        RealMatrix result = Utility.MatrixEq2Ecl(epoch, refSystem);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of MatrixEpoch12Epoch2 method, of class Utility.
     */
    @Test
    public void testMatrixEpoch12Epoch2() {
        System.out.println("MatrixEpoch12Epoch2");
        double epoch1 = 1940d;
        double epoch2 = 1960d;
        ReferenceSystemInterface.Type s1 = ReferenceSystemInterface.Type.FK4;
        ReferenceSystemInterface.Type s2 = ReferenceSystemInterface.Type.FK5;
        Double epobs = 1950d;
        RealMatrix result = Utility.MatrixEpoch12Epoch2(epoch1, epoch2, s1, s2, epobs);
        assertArrayEquals(new double[]{9.99988107e-01,  -4.47301372e-03,  -1.94362889e-03}, result.getRow(0), 1e-9);
        assertArrayEquals(new double[]{4.47301372e-03,   9.99989996e-01,  -4.34712255e-06}, result.getRow(1), 1e-9);
        assertArrayEquals(new double[]{1.94362889e-03,  -4.34680782e-06,   9.99998111e-01}, result.getRow(2), 1e-9);
    }
    
}
