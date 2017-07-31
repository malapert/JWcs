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
package io.github.malapert.jwcs.proj;

import io.github.malapert.jwcs.JWcsFits;
import io.github.malapert.jwcs.proj.exception.JWcsException;
import java.io.IOException;
import java.net.URL;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;

/**
 *
 * @author malapert
 */
public class AIRTest extends AbstractProjectionTest {
    
    public AIRTest() throws JWcsException, FitsException, IOException {
        super(new JWcsFits(new Fits(new URL("http://tdc-www.harvard.edu/wcstools/samples/1904-66_AIR.fits"))));
    }
    
}
