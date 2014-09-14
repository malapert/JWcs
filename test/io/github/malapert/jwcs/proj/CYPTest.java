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
package io.github.malapert.jwcs.proj;

import io.github.malapert.jwcs.proj.exception.JWcsException;
import io.github.malapert.jwcs.WcsFits;
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
 * CYP unit test.
 * @author Jean-Christophe Malapert
 */
public class CYPTest extends ProjectionTest{

    public CYPTest() throws FitsException, IOException, JWcsException {
        super(new WcsFits(new Fits(new URL("http://tdc-www.harvard.edu/wcstools/samples/1904-66_CYP.fits"))), -90, 90);
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
     * Test of project method, of class CYP.
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException
     */
    @Test
    public void testProject() throws ProjectionException {
        System.out.println("project CYP");
        double[] expResult = new double[]{263.69301, -75.954803};
        double[] result = wcs.pix2wcs(1, 1);
        assertArrayEquals(expResult, result, 0.00001);

        expResult = new double[]{267.1165, -57.996208};
        result = wcs.pix2wcs(192, 1);
        assertArrayEquals(expResult, result, 0.00001);

        expResult = new double[]{294.10768, -55.635187};
        result = wcs.pix2wcs(192, 192);
        assertArrayEquals(expResult, result, 0.0001);

        expResult = new double[]{314.47701, -70.787547};
        result = wcs.pix2wcs(1, 192);
        assertArrayEquals(expResult, result, 0.00001);
    }

    /**
     * Test of projectInverse method, of class CYP.
     * @throws io.github.malapert.jwcs.proj.exception.ProjectionException
     */
    @Test
    public void testProjectInverse() throws ProjectionException {
        System.out.println("projectInverse CYP");
        double[] expResult = new double[]{1, 1};
        double[] result = wcs.wcs2pix(263.69301, -75.954803);
        assertArrayEquals(expResult, result, 0.1);

        expResult = new double[]{192, 1};
        result = wcs.wcs2pix(267.1165, -57.996208);
        assertArrayEquals(expResult, result, 0.1);

        expResult = new double[]{192, 192};
        result = wcs.wcs2pix(294.10768, -55.635187);
        assertArrayEquals(expResult, result, 0.1);

        expResult = new double[]{1, 192};
        result = wcs.wcs2pix(314.47701, -70.787547);
        assertArrayEquals(expResult, result, 0.1);
    }
}
