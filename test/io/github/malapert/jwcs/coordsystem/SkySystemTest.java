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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jean-Christophe Malapert
 */
public class SkySystemTest {
    
    private final static double EPSILON_SINGLE = 1e-12;
    
    public SkySystemTest() {
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
     
    
    @Test
    public void separation() {
        System.out.println("separation");
        ReferenceSystemInterface icrs = new ICRS();
        SkySystem sysEqIcrs = new Equatorial(icrs);
        ReferenceSystemInterface fk5 = new FK5();
        SkySystem sysEqFK5 = new Equatorial(fk5);
        SkyPosition pos1 = new SkyPosition(10, 9, sysEqIcrs);
        SkyPosition pos2 = new SkyPosition(11, 10, sysEqFK5);
        double separation = SkySystem.separation(pos1, pos2);
        double expectedSeparation = 1.4045335865d;      
        assertEquals(expectedSeparation, separation, 1e-8);      
    }     
    
    @Test
    public void testConvertFK4B1950ToFK5J2000() {
        System.out.println("convert FK4 B1950 to FK5 J2000");
        ReferenceSystemInterface fk4 = new FK4(1950.0f);
        SkySystem sysEqFK4 = new Equatorial(fk4);
        ReferenceSystemInterface fk5 = new FK5(2000.0f);
        SkySystem sysEqFK5 = new Equatorial(fk5);
        SkyPosition pos = sysEqFK4.convertTo(sysEqFK5, 0.0d, 0.0d);
        double expectedLongitude = 0.640691d;
        double expectedLatitude = 0.27840944d;        
        assertEquals(expectedLongitude, pos.getLongitude(), 0.000001);
        assertEquals(expectedLatitude, pos.getLatitude(), 0.000001);        
    }

    @Test
    public void testConvertInverseFK4B1950ToFK5J2000() {
        System.out.println("convert FK4 B1950 to FK5 J2000 and inverse");
        ReferenceSystemInterface fk4 = new FK4(1950.0f);
        SkySystem sysEqFK4 = new Equatorial(fk4);
        ReferenceSystemInterface fk5 = new FK5(2000.0f);
        SkySystem sysEqFK5 = new Equatorial(fk5);
        SkyPosition pos = sysEqFK4.convertTo(sysEqFK5, 30.031d, 10.031d);
        pos = sysEqFK5.convertTo(sysEqFK4, pos.getLongitude(), pos.getLatitude());
        double expectedLongitude = 30.031d;
        double expectedLatitude = 10.031d;        
        assertEquals(expectedLongitude, pos.getLongitude(), 0.0001);
        assertEquals(expectedLatitude, pos.getLatitude(), 0.0001);        
    }      

    @Test
    /**
     * Test based on http://docs.astropy.org/en/stable/coordinates/
     */
    public void testConvertIcrsToGal() {
        System.out.println("convert ICRS to Gal");
        ReferenceSystemInterface icrs = new ICRS();
        SkySystem sysEqIcrs = new Equatorial(icrs);
        SkySystem galactic = new Galactic();
        SkyPosition posInGal = sysEqIcrs.convertTo(galactic, 10.68458d, 41.26917d);
        double expectedLongitude = 121.174241811d;
        double expectedLatitude = -21.5728855724d;        
        assertEquals(expectedLongitude, posInGal.getLongitude(), 1e-8);
        assertEquals(expectedLatitude, posInGal.getLatitude(), 1e-8); 
    }
 
    @Test
    /**
     * Test based on http://docs.astropy.org/en/stable/coordinates/
     */
    public void testConvertInverseIcrsToGal() {
        System.out.println("convert ICRS to Gal and Inverse");
        ReferenceSystemInterface icrs = new ICRS();
        SkySystem sysEqIcrs = new Equatorial(icrs);
        SkySystem galactic = new Galactic();
        SkyPosition posInGal = sysEqIcrs.convertTo(galactic, 10.68458d, 41.26917d);
        posInGal = galactic.convertTo(sysEqIcrs, posInGal.getLongitude(), posInGal.getLatitude());
        double expectedLongitude = 10.68458d;
        double expectedLatitude = 41.26917d;        
        assertEquals(expectedLongitude, posInGal.getLongitude(), EPSILON_SINGLE);
        assertEquals(expectedLatitude, posInGal.getLatitude(), EPSILON_SINGLE);        
    }
    
    @Test
    /**
     * Test based on http://docs.astropy.org/en/stable/coordinates/
     */
    public void testConvertArrayIcrsToGal() {
        System.out.println("convert Array ICRS to Gal");
        ReferenceSystemInterface icrs = new ICRS();
        SkySystem sysEqIcrs = new Equatorial(icrs);
        SkySystem galactic = new Galactic();
        SkyPosition posInGal = sysEqIcrs.convertTo(galactic, 10.68458d, 41.26917d);
        double expectedLongitude1 = posInGal.getLongitude();
        double expectedLatitude1 = posInGal.getLatitude();
        
        posInGal = sysEqIcrs.convertTo(galactic, 0.68458d, 1.26917d);
        double expectedLongitude2 = posInGal.getLongitude();
        double expectedLatitude2 = posInGal.getLatitude();
        
        SkyPosition[] posInGalArray = sysEqIcrs.convertTo(galactic, new double[]{10.68458d, 41.26917d, 0.68458d, 1.26917d});        
        assertEquals(expectedLongitude1, posInGalArray[0].getLongitude(), EPSILON_SINGLE);
        assertEquals(expectedLatitude1, posInGalArray[0].getLatitude(), EPSILON_SINGLE);  
        assertEquals(expectedLongitude2, posInGalArray[1].getLongitude(), EPSILON_SINGLE);
        assertEquals(expectedLatitude2, posInGalArray[1].getLatitude(), EPSILON_SINGLE);         
    }    
    
    @Test
    /**
     * Test based on http://docs.astropy.org/en/stable/coordinates/
     */    
    public void testConvertIcrsToFK5J2000() {
        System.out.println("convert ICRS to FK5 J2000");
        ReferenceSystemInterface icrs = new ICRS();
        SkySystem sysEqIcrs = new Equatorial(icrs);
        ReferenceSystemInterface fk5 = new FK5(2000.000f);
        SkySystem EqFk5 = new Equatorial(fk5);
        SkyPosition posInFk5 = sysEqIcrs.convertTo(EqFk5, 10.68458d, 41.26917d);
        double expectedLongitude = 10.6845915393d;
        double expectedLatitude = 41.2691714591d;        
        assertEquals(expectedLongitude, posInFk5.getLongitude(), 1e-10);
        assertEquals(expectedLatitude, posInFk5.getLatitude(), 1e-10);        
    }   
    
    @Test
    /**
     * Test based on http://docs.astropy.org/en/stable/coordinates/
     */    
    public void testConvertInverseIcrsToFK5J2000() {
        System.out.println("convert ICRS to FK5 J2000 and inverse");
        ReferenceSystemInterface icrs = new ICRS();
        SkySystem sysEqIcrs = new Equatorial(icrs);
        ReferenceSystemInterface fk5 = new FK5(2000.000f);
        SkySystem EqFk5 = new Equatorial(fk5);
        SkyPosition posInFk5 = sysEqIcrs.convertTo(EqFk5, 10.68458d, 41.26917d);
        posInFk5 = EqFk5.convertTo(sysEqIcrs, posInFk5.getLongitude(), posInFk5.getLatitude());
        double expectedLongitude = 10.68458d;
        double expectedLatitude = 41.26917d;        
        assertEquals(expectedLongitude, posInFk5.getLongitude(), EPSILON_SINGLE);
        assertEquals(expectedLatitude, posInFk5.getLatitude(), EPSILON_SINGLE);        
    }     

    @Test
    /**
     * Test based on http://docs.astropy.org/en/stable/coordinates/
     */    
    public void testConvertToFK5J1975() {
        System.out.println("convert FK5J2000 to FK5 J1975");
        ReferenceSystemInterface icrs = new ICRS();
        SkySystem sysEqIcrs = new Equatorial(icrs);
        ReferenceSystemInterface fk5 = new FK5(2000.000f);
        SkySystem EqFk5J2000 = new Equatorial(fk5);    
        SkyPosition posInFk5J2000 = sysEqIcrs.convertTo(EqFk5J2000, 10.68458d, 41.26917d);        
        ReferenceSystemInterface fk5_1975 = new FK5(1975.000f);
        SkySystem EqFk5J1975 = new Equatorial(fk5_1975);
        SkyPosition posInFk5J1975 = EqFk5J2000.convertTo(EqFk5J1975, posInFk5J2000.getLongitude(), posInFk5J2000.getLatitude());
        double expectedLongitude = 10.3420913461d;
        double expectedLatitude = 41.1323211229d;        
        assertEquals(expectedLongitude, posInFk5J1975.getLongitude(), 0.00002);
        assertEquals(expectedLatitude, posInFk5J1975.getLatitude(), 0.00002);        
    }    
    
    @Test
    /**
     * Test based on http://docs.astropy.org/en/stable/coordinates/
     */    
    public void testConvertInverseToFK5J1975() {
        System.out.println("convert FK5J2000 to FK5 J1975 and inverse");

        ReferenceSystemInterface fk5 = new FK5(2000.000f);
        SkySystem EqFk5J2000 = new Equatorial(fk5);    
      
        ReferenceSystemInterface fk5_1975 = new FK5(1975.000f);
        SkySystem EqFk5J1975 = new Equatorial(fk5_1975);
        
        SkyPosition posInFk5J1975 = EqFk5J2000.convertTo(EqFk5J1975, 10.3420913461d, 41.1323211229d);
        posInFk5J1975  = EqFk5J1975.convertTo(EqFk5J2000, posInFk5J1975.getLongitude(), posInFk5J1975.getLatitude());
        double expectedLongitude = 10.3420913461d;
        double expectedLatitude = 41.1323211229d;        
        assertEquals(expectedLongitude, posInFk5J1975.getLongitude(), 1e-8);
        assertEquals(expectedLatitude, posInFk5J1975.getLatitude(), 1e-8);        
    }
    
    /**
     * Test of convertTo method, of class SkySystem.
     */
    @Test
    public void testIcrsToFK5() {       
        System.out.println("convert ICRS To FK5");
        ReferenceSystemInterface ref = new ICRS();
        SkySystem sys1 = new Equatorial(ref);
        SkySystem sys2 = new Equatorial(new FK5());
        SkyPosition position = sys1.convertTo(sys2, 182.63867d, 39.401167d);
        double expectedLongitude = 182.63867d;
        double expectedLatitude = 39.401165d;
        assertEquals(expectedLongitude, position.getLongitude(), 0.00001);
        assertEquals(expectedLatitude, position.getLatitude(), 0.000001);
    }
    
    @Test
    public void testIcrsToFK5Inverse() {       
        System.out.println("convert ICRS To FK5 and Inverse");
        ReferenceSystemInterface ref = new ICRS();
        SkySystem sys1 = new Equatorial(ref);
        SkySystem sys2 = new Equatorial(new FK5());
        SkyPosition position = sys1.convertTo(sys2, 182.63867d, 39.401167d);
        double expectedLongitude = 182.63867d;
        double expectedLatitude = 39.401167d;
        position = sys2.convertTo(sys1, position.getLongitude(), position.getLatitude());
        assertEquals(expectedLongitude, position.getLongitude(), EPSILON_SINGLE);
        assertEquals(expectedLatitude, position.getLatitude(), EPSILON_SINGLE);
    }    

    @Test
    public void testIcrsToFK4() { 
        System.out.println("convert ICRS To FK4");
        ReferenceSystemInterface ref = new ICRS();
        SkySystem sys1 = new Equatorial(ref);        
        SkySystem sys2 = new Equatorial(new FK4());
        SkyPosition position = sys1.convertTo(sys2, 182.63867, 39.401167);
        double expectedLongitude = 182.0073;
        double expectedLatitude = 39.679217;
        assertEquals(expectedLongitude, position.getLongitude(), 0.0001);
        assertEquals(expectedLatitude, position.getLatitude(), 0.0001); 
    }
    
    @Test
    public void testIcrsToFK4Inverse() { 
        System.out.println("convert ICRS To FK4 and inverse");
        ReferenceSystemInterface ref = new ICRS();
        SkySystem sys1 = new Equatorial(ref);        
        SkySystem sys2 = new Equatorial(new FK4());
        SkyPosition position = sys1.convertTo(sys2, 182.63867d, 39.401167d);
        position = sys2.convertTo(sys1, position.getLongitude(), position.getLatitude());
        double expectedLongitude = 182.63867d;
        double expectedLatitude = 39.401167d;
        assertEquals(expectedLongitude, position.getLongitude(), 0.0001);
        assertEquals(expectedLatitude, position.getLatitude(), 0.0001); 
    }
    
    @Test
    public void testIcrsToGalactic() {     
        System.out.println("convert ICRS To Galactic");
        ReferenceSystemInterface ref = new ICRS();
        SkySystem sys1 = new Equatorial(ref);         
        SkySystem sys2 = new Galactic();
        SkyPosition position = sys1.convertTo(sys2, 182.63867, 39.401167);
        double expectedLongitude = 155.08125;
        double expectedLatitude = 75.068157;
        assertEquals(expectedLongitude, position.getLongitude(), 0.00001);
        assertEquals(expectedLatitude, position.getLatitude(), 0.00001);   
    }
    
        @Test
    public void testIcrsToGalacticInverse() {     
        System.out.println("convert ICRS To Galactic and inverse");
        ReferenceSystemInterface ref = new ICRS();
        SkySystem sys1 = new Equatorial(ref);         
        SkySystem sys2 = new Galactic();
        SkyPosition position = sys1.convertTo(sys2, 182.63867d, 39.401167d);
        position = sys2.convertTo(sys1, position.getLongitude(), position.getLatitude());
        double expectedLongitude = 182.63867d;
        double expectedLatitude = 39.401167d;        
        assertEquals(expectedLongitude, position.getLongitude(), EPSILON_SINGLE);
        assertEquals(expectedLatitude, position.getLatitude(), EPSILON_SINGLE);   
    }

    @Test
    public void testConvertDegreeToHexa() { 
        System.out.println("convert Degrees to hms/dms");
        SkyPosition pos = new SkyPosition(182.63867, 39.401167, new Equatorial());
        assertEquals("12:10:33.281", pos.getLongitudeAsSexagesimal());
        assertEquals("+39:24:04.20", pos.getLatitudeAsSexagesimal());
    }
    
    @Test
    public void testICRS2GAL() {
        System.out.println("ICRS <--> GAL");
        double coordinates[][] = new double[][]{
            {0,0},
            {180,60},
            {359,60},
            {86,-35}
        };
        SkySystem sk1 = new Equatorial();
        SkySystem sk2 = new Galactic();
        for (double[] coordinate:coordinates) {
            SkyPosition[] result = sk1.convertTo(sk2, coordinate);
            result = sk2.convertTo(sk1, result[0].getDoubleArray());
            assertArrayEquals(coordinate, result[0].getDoubleArray(), EPSILON_SINGLE);
        }
    }

    @Test
    public void testICRS2SUPERGAL() {
        System.out.println("ICRS <--> SUPERGAL");
        double coordinates[][] = new double[][]{
            {0,0},
            {180,60},
            {359,60},
            {86,-35}
        };
        SkySystem sk1 = new Equatorial();
        SkySystem sk2 = new SuperGalactic();
        for (double[] coordinate:coordinates) {
            SkyPosition[] result = sk1.convertTo(sk2, coordinate);
            result = sk2.convertTo(sk1, result[0].getDoubleArray());
            assertArrayEquals(coordinate, result[0].getDoubleArray(), EPSILON_SINGLE);
        }
    }    

    @Test
    public void testGAL2SUPERGAL() {
        System.out.println("GAL <--> SUPERGAL");
        double coordinates[][] = new double[][]{
            {0,0},
            {180,60},
            {359,60},
            {86,-35}
        };
        SkySystem sk1 = new Galactic();
        SkySystem sk2 = new SuperGalactic();
        for (double[] coordinate:coordinates) {
            SkyPosition[] result = sk1.convertTo(sk2, coordinate);
            result = sk2.convertTo(sk1, result[0].getDoubleArray());
            assertArrayEquals(coordinate, result[0].getDoubleArray(), EPSILON_SINGLE);
        }
    }
    

    @Test    
    public void testICRS2FK5() {
        System.out.println("ICRS <--> FK5");
        double coordinates[][] = new double[][]{
            {0,0},
            {180,60},
            {359,60},
            {86,-35}
        };
        SkySystem sk1 = new Equatorial();        
        SkySystem sk2 = new Equatorial(new FK5());
        for (double[] coordinate:coordinates) {
            SkyPosition[] result = sk1.convertTo(sk2, coordinate);
            result = sk2.convertTo(sk1, result[0].getDoubleArray());
            assertArrayEquals(coordinate, result[0].getDoubleArray(), EPSILON_SINGLE);
        }
    }       
    
    @Test    
    public void testFK5J20002FK5J1950() {
        System.out.println("FK5(J2000) <--> FK5(1950)");
        double coordinates[][] = new double[][]{
            {1,0},
            {180,60},
            {359,60},
            {86,-35}
        };
        SkySystem sk1 = new Equatorial(new FK5(2000));        
        SkySystem sk2 = new Equatorial(new FK5(1950));
        for (double[] coordinate:coordinates) {
            SkyPosition[] result = sk1.convertTo(sk2, coordinate);
            result = sk2.convertTo(sk1, result[0].getDoubleArray());           
            assertArrayEquals(coordinate, result[0].getDoubleArray(), 1e-7);
        }
    }  
    
    @Test    
    public void testFK5J20002FK4() {
        System.out.println("FK5(J2000) <--> FK4");
        double coordinates[][] = new double[][]{
            {1,0},
            {180,60},
            {359,60},
            {86,-35}
        };
        SkySystem sk1 = new Equatorial(new FK5(2000));        
        SkySystem sk2 = new Equatorial(new FK4());
        for (double[] coordinate:coordinates) {
            SkyPosition[] result = sk1.convertTo(sk2, coordinate);
            result = sk2.convertTo(sk1, result[0].getDoubleArray());           
            assertArrayEquals(coordinate, result[0].getDoubleArray(), 1e-4);
        }        
    }
       
}
