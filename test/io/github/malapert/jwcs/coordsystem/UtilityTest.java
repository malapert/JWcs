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
        assertArrayEquals(expResult.getRow(0), result.getRow(0), NumericalUtils.DOUBLE_TOLERANCE);
        assertArrayEquals(expResult.getRow(1), result.getRow(1), NumericalUtils.DOUBLE_TOLERANCE);
        assertArrayEquals(expResult.getRow(2), result.getRow(2), NumericalUtils.DOUBLE_TOLERANCE);
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
        assertArrayEquals(expResult, result, NumericalUtils.DOUBLE_TOLERANCE);
    }

    /**
     * Test of FK52FK4Matrix method, of class Utility.
     */
    @Test
    public void testFK52FK4Matrix() {
        System.out.println("FK52FK4Matrix");        
        RealMatrix resultTest1 = Utility.FK42FK5Matrix(Double.NaN);
        RealMatrix resultTest2 = Utility.FK52FK4Matrix(Double.NaN);
        RealMatrix result = resultTest1.multiply(resultTest2);
        RealMatrix expResult = MatrixUtils.createRealIdentityMatrix(3);
        assertArrayEquals(expResult.getRow(0), result.getRow(0), NumericalUtils.DOUBLE_TOLERANCE);
        assertArrayEquals(expResult.getRow(1), result.getRow(1), NumericalUtils.DOUBLE_TOLERANCE);
        assertArrayEquals(expResult.getRow(2), result.getRow(2), NumericalUtils.DOUBLE_TOLERANCE);
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
        RealMatrix xyz = MatrixUtils.createRealMatrix(array);
        double[] expResult = xyz.getColumn(0);
        double[] result = Utility.xyz2longlat(xyz);
        RealMatrix lonlat = Utility.longlat2xyz(result[0], result[1]);
        assertArrayEquals(expResult, lonlat.getColumn(0), 1e-6);
    }

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
        assertArrayEquals(expResult.getRow(0), result.getRow(0), NumericalUtils.DOUBLE_TOLERANCE);
        assertArrayEquals(expResult.getRow(1), result.getRow(1), NumericalUtils.DOUBLE_TOLERANCE);
        assertArrayEquals(expResult.getRow(2), result.getRow(2), NumericalUtils.DOUBLE_TOLERANCE);
    }

    /**
     * Test of MatrixEpoch12Epoch2 method, of class Utility.
     */
    @Test
    public void testMatrixEpoch12Epoch2() {
        System.out.println("MatrixEpoch12Epoch2");
        double epoch1 = 1940d;
        double epoch2 = 1960d;
        CoordinateReferenceFrame.ReferenceFrame s1 = CoordinateReferenceFrame.ReferenceFrame.FK4;
        CoordinateReferenceFrame.ReferenceFrame s2 = CoordinateReferenceFrame.ReferenceFrame.FK5;
        Double epobs = 1950d;
        RealMatrix result = Utility.MatrixEpoch12Epoch2(epoch1, epoch2, s1, s2, epobs);
        assertArrayEquals(new double[]{9.99988107e-01,  -4.47301372e-03,  -1.94362889e-03}, result.getRow(0), 1e-9);
        assertArrayEquals(new double[]{4.47301372e-03,   9.99989996e-01,  -4.34712255e-06}, result.getRow(1), 1e-9);
        assertArrayEquals(new double[]{1.94362889e-03,  -4.34680782e-06,   9.99998111e-01}, result.getRow(2), 1e-9);
    }
    
}
