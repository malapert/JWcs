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
package io.github.malapert.jwcs.crs;

import io.github.malapert.jwcs.crs.Equatorial;
import io.github.malapert.jwcs.datum.FK5;
import io.github.malapert.jwcs.datum.J2000;

/**
 *
 * @author malapert
 */
public class EquatorialJ2000ToEquatorialFK5Test extends ConverterTest {
    
    public EquatorialJ2000ToEquatorialFK5Test() {
        this.source = new Equatorial(new J2000());
        this.target = new Equatorial(new FK5());
    }
    
}
