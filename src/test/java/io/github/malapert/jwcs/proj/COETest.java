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
 * COE unit test.
 * @author Jean-Christophe Malapert
 */
public class COETest extends AbstractProjectionTest {
    
    /**
     *
     * @throws FitsException
     * @throws IOException
     * @throws JWcsException
     */
    public COETest() throws FitsException, IOException, JWcsException {
        super(new JWcsFits(new Fits(new URL("http://tdc-www.harvard.edu/wcstools/samples/1904-66_COE.fits"))));
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
     * Test of project method, of class COE.
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException
     */
    @Test
    public void testProjectCOE() throws ProjectionException {
        System.out.println("project COE");
        final double expectedResults[][] = {
            { 271.441283018566764,  -73.707516970513367},
            { 268.765747911368521,  -60.131677869285078},
            { 292.588283774027445,  -58.330669761448092},
            { 308.212254209497758,  -69.763922466547697}
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
     * Test of projectInverse method, of class AIT.
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException
     */
    @Test
    public void testProjectInverseCOE() throws ProjectionException {
        System.out.println("projectInverse COE");
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
