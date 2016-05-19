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

/**
 * Provides the classes necessary to handle 
 * astronomical coordinate reference system and conversion   
 * between the supported coordinate reference systems.
 *
 * <p>
 * These classes are capable of converting a coordinate reference system (crs)
 * to another one. This package contains three main groups of classes:
 * <ul>
 * <li>classes representing the supported coordinate systems : Ecliptic, Equatorial,
 * galactic and super-galactic.</li>
 * <li>classes representing the supported reference frame : FK4, FK4_NO_E, FK5, 
 * J2000 and ICRS</li>
 * <li>classes converting sky coordinates from a crs to another one</li>
 * </ul>
 * 
 * <h2>How to use it</h2>
 * Describe first your sky system. For example, let's say that we
 * want to convert sky coordinates from an equatorial system in ICRS to a 
 * super-galactic system. Then use the converTo method from a SkySystem object:
 * <pre>
 *    AbstractCrs sys1 = new Equatorial(new ICRS());
 *    AbstractCrs sys2 = new SuperGalactic();
 *    SkyPosition position = sys1.convertTo(sys2, 182.63867, 39.401167);
 * </pre>
 * 
 * <h2>Class diagram</h2>
 * 
 * <img alt="Class diagram of this package" src="doc-files/Architecture_coord.png">
 */
package io.github.malapert.jwcs.coordsystem;
