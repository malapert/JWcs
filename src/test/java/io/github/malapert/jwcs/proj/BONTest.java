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
package io.github.malapert.jwcs.proj;

import io.github.malapert.jwcs.proj.exception.JWcsException;
import io.github.malapert.jwcs.JWcsFits;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
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
 * BON unit test.
 * @author Jean-Christophe Malapert
 */
public class BONTest extends AbstractProjectionTest{

    /**
     *
     * @throws FitsException
     * @throws IOException
     * @throws JWcsException
     */
    public BONTest() throws FitsException, IOException, JWcsException {
        super(new JWcsFits(new Fits(new URL("http://tdc-www.harvard.edu/wcstools/samples/1904-66_BON.fits"))));
    }

    @BeforeClass
    public static void setUpClass() {
        //do nothing
    }

    @AfterClass
    public static void tearDownClass() {
        //do nothing
    }
   
    @Before
    public void setUp() {
        //do nothing
    }

    
    @After
    public void tearDown() {
        //do nothing
    }

    /**
     * Test of project method, of class BON.
     *
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException
     */
    @Test
    public void testProjectBON() throws ProjectionException {
        System.out.println("project BON");
        final double expectedResults[][] = {
            { 273.393413003838305,  -73.624666333473925},
            {266.237211427240538,  -60.534492822654933},
            {289.996444429144617,  -58.437400650158317},
            {309.662280407939761,  -68.545004618933845}
        };
        double[] result = wcs.pix2wcs(1, 1);
        assertArrayEquals(expectedResults[0], result, 1e-13);

        result = wcs.pix2wcs(192, 1);
        assertArrayEquals(expectedResults[1], result, 1e-13);

        result = wcs.pix2wcs(192, 192);
        assertArrayEquals(expectedResults[2], result, 1e-13);

        result = wcs.pix2wcs(1, 192);
        assertArrayEquals(expectedResults[3], result, 1e-13);    
    }

    /**
     * Test of projectInverse method, of class BON.
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException
     */
    @Test
    public void testProjectInverseBON() throws ProjectionException {
        System.out.println("projectInverse BON");
        final double expectedResults[][] = {
            {1.0d, 1.0d},
            {192.d, 1.0d},
            {192.d, 192d},
            {1.0d, 192d}
        };   
        double[] result;
        for (final double[] expectedResult : expectedResults) {
            result = wcs.pix2wcs(expectedResult);
            result = wcs.wcs2pix(result);
             assertArrayEquals(expectedResult, result, 1e-12);
        }  
    }
    
}
