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

import io.github.malapert.jwcs.coordsystem.Galactic;
import io.github.malapert.jwcs.coordsystem.FK5;
import io.github.malapert.jwcs.coordsystem.FK4;
import io.github.malapert.jwcs.coordsystem.SkySystem;
import io.github.malapert.jwcs.coordsystem.ICRS;
import io.github.malapert.jwcs.coordsystem.Equatorial;
import io.github.malapert.jwcs.coordsystem.ReferenceSystemInterface;
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
