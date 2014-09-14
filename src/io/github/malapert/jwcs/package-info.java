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

/**
 * Provides the classes necessary to handle 
 * astronomical projections and conversions   
 * between pixels to world coordinates.
 * 
 * <h2>How to use it</h2>
 * First, you need to load a FITS file. This FITS file will be read to find 
 * necessary keywords for the computation. Then, use the object FitsWcs and the
 * methods :
 * <ul>
 * <li>pix2wcs : to compute the pixel position in the sky</li>
 * <li>wcs2pix : to compute the sky position in the camera</li>
 * </ul>
 * <pre>
 *    JWcs wcs = new WcsFits(new Fits("/tmp/1904-66_AIT.fits"));
 *    wcs.doInit();
 *    double[] skyPosition = wcs.pix2wcs(1, 1);
 * </pre>
 *
 * <h2>List of supported projections</h2>
 * The list of supported projections is described here :
 * @see <a href="../proj/package-summary.html">projections</a>
 * 
 */
package io.github.malapert.jwcs;
