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

import io.github.malapert.jwcs.JWcsFits;
import io.github.malapert.jwcs.proj.exception.JWcsException;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import java.io.IOException;
import java.net.URL;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

/**
 * CEA unit test.
 * @author Jean-Christophe Malapert
 */
public class CEATest extends AbstractProjectionTest {

    /**
     *
     * @throws JWcsException
     * @throws FitsException
     * @throws IOException
     */
    public CEATest() throws JWcsException, FitsException, IOException {
        super(new JWcsFits(new Fits(new URL("http://tdc-www.harvard.edu/wcstools/samples/1904-66_CEA.fits"))));
    }  
    
    /**
     *
     * @throws ProjectionException
     */
    @Test
    public void testProjectCEA() throws ProjectionException {
        System.out.println("project CEA");
        double expectedResults[][] = {
            { 268.440852654621153,  -73.379693805485672},
            { 269.090243859417001,  -60.649088748117173},
            { 294.131910549115673,  -58.362095662786679},
            { 307.520448192392109,  -69.383029011108476}
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
     *
     * @throws ProjectionException
     */
    @Test
    public void testProjectInverseCEA() throws ProjectionException {
        System.out.println("projectInverse CEA");
        double expectedResults[][] = {
            {1.0d, 1.0d},
            {192.d, 1.0d},
            {192.d, 192d},
            {1.0d, 192d}
        };   
        double[] result;
        for (double[] expectedResult : expectedResults) {
            result = wcs.pix2wcs(expectedResult);
            result = wcs.wcs2pix(result);
             assertArrayEquals(expectedResult, result, 1e-12);
        }  
    }    
    
}
