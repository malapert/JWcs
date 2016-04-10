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
package io.github.malapert.jwcs;

import io.github.malapert.jwcs.proj.exception.JWcsException;
import java.io.IOException;
import java.util.Iterator;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;

/**
 * A class allowing to compute the WCS by initializing the WCS structure by 
 * reading the FITS file.
 * 
 * For example :
 * <pre>
   JWcs wcs = new WcsFits(new Fits("/tmp/WFPC2ASSNu5780205bx.fits"));
   double[] pos = wcs.pix2wcs(1, 1);
 </pre>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class JWcsFits extends JWcs {

    /**
     * The Header FITS that contains the WCS keyword.
     */
    private Header hdr;
    
    /**
     * Clone
     * @param wcs wcsFits
     */
    public JWcsFits(JWcsFits wcs) {
        setHdr(wcs.getHdr());
    }

    /**
     * Creates an instance and reads the WCS keyword in the first FITS extension.
     * @param simpleFits the FITS file
     * @throws FitsException FitsException
     * @throws IOException IOException
     */
    public JWcsFits(final Fits simpleFits) throws FitsException, IOException {      
        this(simpleFits.readHDU().getHeader());
    }
    
    public JWcsFits(final Fits fits, int extension) throws FitsException, IOException {
        this(fits.getHDU(extension).getHeader());
    }
    
    /**
     * Creates an instance and reads the WCS keyword in the FITS header.
     * @param hdr FITS header
     */
    public JWcsFits(final Header hdr) {     
        this.hdr = hdr;     
    }        
    
    @Override
    public void doInit() throws JWcsException {
        super.init();
    }

    @Override
    public boolean hasKeyword(final String keyword) {
        return this.getHdr().containsKey(keyword);
    }

    @Override
    public int getValueAsInt(final String keyword) {
        return this.getHdr().getIntValue(keyword);
    }

    @Override
    public double getValueAsDouble(final String keyword) {
        return this.getHdr().getDoubleValue(keyword);
    }
    
    @Override
    public float getValueAsFloat(final String keyword) {
        return this.getHdr().getFloatValue(keyword);
    }    

    @Override
    public Iterator iterator() {
        return this.getHdr().iterator();
    }

    @Override
    public String getValueAsString(final String keyword) {
        return this.getHdr().getStringValue(keyword);
    }

    /**
     * Returns the header FITS.
     * @return the header FITS.
     */
    private Header getHdr() {
        return hdr;
    }

    /**
     * Sets the header FITS.
     * @param hdr the header FITS to set
     */
    private void setHdr(final Header hdr) {
        this.hdr = hdr;
    }

}
