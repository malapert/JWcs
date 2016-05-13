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
package io.github.malapert.jwcs;

import io.github.malapert.jwcs.coordsystem.Equatorial;
import io.github.malapert.jwcs.coordsystem.Crs;
import io.github.malapert.jwcs.proj.exception.JWcsException;
import java.io.IOException;
import java.net.URL;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
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
public class JWcsTest {
    
    JWcs wcs;
    JWcs wcs1;
    
    public JWcsTest() throws FitsException, IOException, JWcsException {
        wcs = new JWcsFits(new Fits(new URL("http://fits.gsfc.nasa.gov/samples/WFPC2ASSNu5780205bx.fits")));
        wcs.doInit();
        wcs1 = new JWcsFits(new Fits(new URL("http://fits.gsfc.nasa.gov/samples/FOCx38i0101t_c0f.fits")), 0);
        wcs1.doInit();
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
     * Test of getCrs method, of class JWcs.
     */
    @Test
    public void testGetCrs() {
        System.out.println("getSkySystem");
        Crs result = wcs.getCrs();
        double equinox = ((Equatorial)result).getEquinox();
        Double epoch = ((Equatorial)result).getEpochObs();
        String refSystem = ((Equatorial)result).getRefSystem().getReferenceSystemType().name();       
        assertEquals(2000.0, equinox, 1e-12);
        if (Double.isNaN(epoch)) {
            epoch = null;
        }
        assertEquals(null, epoch);
        assertEquals("FK5", refSystem);
        
        result = wcs1.getCrs();
        equinox = ((Equatorial)result).getEquinox();
        epoch = ((Equatorial)result).getEpochObs();
        refSystem = ((Equatorial)result).getRefSystem().getReferenceSystemType().name();
        assertEquals(2000.0, equinox, 1e-12);
        if (Double.isNaN(epoch)) {
            epoch = null;
        }        
        assertEquals(null, epoch);
        assertEquals("FK5", refSystem);        
    }

    /**
     * Test of pix2wcs method, of class JWcs.
     * @throws java.lang.Exception
     */
    @Test
    public void testPix2wcs_doubleArr() throws Exception {
        System.out.println("pix2wcs");
        double[] pixels = new double[]{1,1,1,100,100,100,100,1};
        double[] expResult = new double[]{182.632805,39.406235,182.632538,39.403504,182.636069,39.403298,182.636336,39.406029};
        double[] result = wcs.pix2wcs(pixels);
        assertArrayEquals(expResult, result, 1e-6);
        
        pixels = new double[]{1,1,1,1024,1024,1024,1024,1};
        expResult = new double[]{182.64307,39.406175,182.63451,39.411264,182.62843,39.405157,182.63699,39.40007};
        result = wcs1.pix2wcs(pixels);
        assertArrayEquals(expResult, result, 1e-5);        
    }

    /**
     * Test of getCenter method, of class JWcs.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCenter() throws Exception {
        System.out.println("getCenter");
        double[] expResult = new double[]{182.63442,39.404782};
        double[] result = wcs.getCenter();
        assertArrayEquals(expResult, result, 1e-5);
    }

    /**
     * Test of getFov method, of class JWcs.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetFov() throws Exception {
        System.out.println("getFov");
        double[] pixels = new double[]{1,1,100,1,100,100,1,100};
        double[] expResult = wcs.pix2wcs(pixels);
        double[] result = wcs.getFov();
        assertArrayEquals(expResult, result, 1e-5);
    }


    
}
