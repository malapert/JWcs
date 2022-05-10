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
package io.github.malapert.jwcs.crs;

import io.github.malapert.jwcs.crs.Equatorial;
import io.github.malapert.jwcs.datum.FK4;
import io.github.malapert.jwcs.datum.CoordinateReferenceFrame;
import io.github.malapert.jwcs.datum.FK4NoEterms;
import io.github.malapert.jwcs.proj.exception.JWcsException;

/**
 *
 * @author malapert
 */
public class EquatorialFK4ToEquatorialFK4_NO_ETest extends ConverterTest {
    
    /**
     *
     * @throws JWcsException
     */
    public EquatorialFK4ToEquatorialFK4_NO_ETest() throws JWcsException {
        super();
        final CoordinateReferenceFrame fk4 = new FK4();
        final CoordinateReferenceFrame fk4NoEterms = new FK4NoEterms();
        this.source = new Equatorial(fk4);
        this.target = new Equatorial(fk4NoEterms);        
        
    }
}
