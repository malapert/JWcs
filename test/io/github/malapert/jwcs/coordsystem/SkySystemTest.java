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
        double expectedSeparation = 1.4045335865;      
        assertEquals(expectedSeparation, separation, 0.0000000001);      
    }     
    
    @Test
    public void testConvertFK4B1950ToFK5J2000() {
        System.out.println("convert FK4 B1950 to FK5 J2000");
        ReferenceSystemInterface fk4 = new FK4(1950.0f);
        SkySystem sysEqFK4 = new Equatorial(fk4);
        ReferenceSystemInterface fk5 = new FK5(2000.0f);
        SkySystem sysEqFK5 = new Equatorial(fk5);
        SkyPosition pos = sysEqFK4.convertTo(sysEqFK5, 0.0d, 0.0d);
        double expectedLongitude = 0.640691;
        double expectedLatitude = 0.27840944;        
        assertEquals(expectedLongitude, pos.getLongitude(), 0.000001);
        assertEquals(expectedLatitude, pos.getLatitude(), 0.000001);        
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
        double expectedLongitude = 121.174241811;
        double expectedLatitude = -21.5728855724;        
        assertEquals(expectedLongitude, posInGal.getLongitude(), 0.000000001);
        assertEquals(expectedLatitude, posInGal.getLatitude(), 0.000000001);        
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
        double expectedLongitude = 10.6845915393;
        double expectedLatitude = 41.2691714591;        
        assertEquals(expectedLongitude, posInFk5.getLongitude(), 0.000000001);
        assertEquals(expectedLatitude, posInFk5.getLatitude(), 0.000000001);        
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
        SkyPosition posInFk5J2000 = sysEqIcrs.convertTo(EqFk5J2000, 10.68458, 41.26917);        
        ReferenceSystemInterface fk5_1975 = new FK5(1975.000f);
        SkySystem EqFk5J1975 = new Equatorial(fk5_1975);
        SkyPosition posInFk5J1975 = EqFk5J2000.convertTo(EqFk5J1975, posInFk5J2000.getLongitude(), posInFk5J2000.getLatitude());
        double expectedLongitude = 10.3420913461;
        double expectedLatitude = 41.1323211229;        
        assertEquals(expectedLongitude, posInFk5J1975.getLongitude(), 0.00002);
        assertEquals(expectedLatitude, posInFk5J1975.getLatitude(), 0.00002);        
    }    
    
    /**
     * Test of convertTo method, of class SkySystem.
     */
    @Test
    public void testConvertTo() {       
        System.out.println("convert ICRS To FK5");
        ReferenceSystemInterface ref = new ICRS();
        SkySystem sys1 = new Equatorial(ref);
        SkySystem sys2 = new Equatorial(new FK5());
        SkyPosition position = sys1.convertTo(sys2, 182.63867, 39.401167);
        double expectedLongitude = 182.63867;
        double expectedLatitude = 39.401165;
        assertEquals(expectedLongitude, position.getLongitude(), 0.00001);
        assertEquals(expectedLatitude, position.getLatitude(), 0.00001);
        
        System.out.println("convert ICRS To FK4");
        sys2 = new Equatorial(new FK4());
        position = sys1.convertTo(sys2, 182.63867, 39.401167);
        expectedLongitude = 182.0073;
        expectedLatitude = 39.679217;
        assertEquals(expectedLongitude, position.getLongitude(), 0.0001);
        assertEquals(expectedLatitude, position.getLatitude(), 0.0001); 
        
        System.out.println("convert ICRS To Galactic");
        sys2 = new Galactic();
        position = sys1.convertTo(sys2, 182.63867, 39.401167);
        expectedLongitude = 155.08125;
        expectedLatitude = 75.068157;
        assertEquals(expectedLongitude, position.getLongitude(), 0.00001);
        assertEquals(expectedLatitude, position.getLatitude(), 0.00001);   
        
        System.out.println("convert to hms/dms");
        SkyPosition pos = new SkyPosition(182.63867, 39.401167, new Equatorial());
        assertEquals("12:10:33.281", pos.getLongitudeAsSexagesimal());
        assertEquals("+39:24:04.20", pos.getLatitudeAsSexagesimal());
    }

    
}
