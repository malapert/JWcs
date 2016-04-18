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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A class allowing to compute the WCS by initializing the WCS structure by 
 * reading a map containing the WCS keywords.
 * @author Jean-Christophe Malapert
 */
public class JWcsMap extends JWcs {
    
    private Map keywords;
    
    public JWcsMap(JWcsMap wcs) {
        setKeywords(wcs.keywords);
    }
    
    public JWcsMap(Map keywords) {
        this.keywords = keywords;
    }

    @Override
    public void doInit() throws JWcsException {
        super.init();
    }

    @Override
    public int wcsaxes() {
        return this.getValueAsInt("NAXIS");    
    }

    @Override
    public boolean hasKeyword(String keyword) {
        return this.getKeywords().containsKey(keyword);
    }

    @Override
    public int getValueAsInt(String keyword) {
        String result = getValueAsString(keyword);
        return (result == null) ? 0 : Integer.valueOf(result);        
    }

    @Override
    public double getValueAsDouble(String keyword) {
        String result = getValueAsString(keyword);
        return (result == null) ? Double.NaN : Double.valueOf(result);
    }
    
    @Override
    public float getValueAsFloat(String keyword) {
        String result = getValueAsString(keyword);
        return (result == null) ? Float.NaN : Float.valueOf(result);
    }    

    @Override
    public String getValueAsString(String keyword) {
        return (String) this.getKeywords().getOrDefault(keyword, null);
    }

    @Override
    public Iterator iterator() {
        return this.getKeywords().keySet().iterator();
    }

    /**
     * Returns all the keywords.
     * @return the keywords
     */
    public Map getKeywords() {
        return keywords;
    }

    /**
     * Sets the whole map.
     * @param keywords the keywords to set
     */
    private void setKeywords(final Map keywords) {
        this.keywords = keywords;
    }  
    
    public static JWcs getProjection(String projectionCode) throws JWcsException {
    Map wcsKeywords = new HashMap();
        wcsKeywords.put(JWcs.NAXIS, "2");
        wcsKeywords.put(JWcs.NAXIS1, "600");
        wcsKeywords.put(JWcs.NAXIS2, "300");
        wcsKeywords.put(JWcs.RADESYS, "ICRS");
        wcsKeywords.put(JWcs.CRPIX1, "300");
        wcsKeywords.put(JWcs.CRPIX2, "150");
        wcsKeywords.put(JWcs.CRVAL1, "0");
        if (null != projectionCode) {
            switch (projectionCode) {
                case "ZEA":
                case "TAN":
                    wcsKeywords.put(JWcs.CRVAL2, "90");
                    break;
                case "BON":
                    wcsKeywords.put(JWcs.PV21, "45");
                    wcsKeywords.put(JWcs.CRVAL2, "0");
                    break;                    
                case "SZP":
                    wcsKeywords.put(JWcs.CRVAL2, "0");
                    wcsKeywords.put(JWcs.PV21, "2");
                    wcsKeywords.put(JWcs.PV22, "180");
                    wcsKeywords.put(JWcs.PV23, "60");
                    break;
                case "STG":
                    wcsKeywords.put(JWcs.CRVAL2, "-90");
                    break;
                case "CYP":
                    wcsKeywords.put(JWcs.CRVAL2, "0");
                    wcsKeywords.put(JWcs.PV21, "1");
                    wcsKeywords.put(JWcs.PV22, String.valueOf(Math.sqrt(2)*0.5));
                    break;
                case "COP":
                    wcsKeywords.put(JWcs.CRVAL2, "90");
                    wcsKeywords.put(JWcs.PV21, "45");
                    wcsKeywords.put(JWcs.PV22, "25");
                    break;
                case "COO":
                    wcsKeywords.put(JWcs.CRVAL2, "90");
                    wcsKeywords.put(JWcs.PV21, "45");
                    wcsKeywords.put(JWcs.PV22, "25");
                    break;
                case "COE":
                    wcsKeywords.put(JWcs.CRVAL2, "-90");
                    wcsKeywords.put(JWcs.PV21, "-45");
                    wcsKeywords.put(JWcs.PV22, "25");
                    break;
                case "COD":
                    wcsKeywords.put(JWcs.CRVAL2, "90");
                    wcsKeywords.put(JWcs.PV21, "45");
                    wcsKeywords.put(JWcs.PV22, "25");
                    break;
                case "AZP":
                    wcsKeywords.put(JWcs.CRVAL2, "60");
                    wcsKeywords.put(JWcs.PV21, "2");
                    wcsKeywords.put(JWcs.PV22, "30");
                    break;
                case "ARC":
                    wcsKeywords.put(JWcs.CRVAL2, "90");
                    break;
                case "ZPN":
                    wcsKeywords.put(JWcs.CRVAL2, "90");
                    wcsKeywords.put(JWcs.PV20, "0.050");
                    wcsKeywords.put(JWcs.PV21, "0.975");
                    wcsKeywords.put(JWcs.PV22, "-0.807");
                    wcsKeywords.put("PV2_3", "0.337");
                    wcsKeywords.put("PV2_4", "-0.065");
                    wcsKeywords.put("PV2_5", "0.010");
                    wcsKeywords.put("PV2_6", "0.003");
                    wcsKeywords.put("PV2_7", "-0.001");
                    break;
                default:
                    wcsKeywords.put(JWcs.CRVAL2, "0");
                    break;
            }
        }

        wcsKeywords.put(JWcs.CD11, String.valueOf(180d / 300d));
        wcsKeywords.put(JWcs.CD12, "0");
        wcsKeywords.put(JWcs.CD21, "0");
        wcsKeywords.put(JWcs.CD22, String.valueOf(90d / 150d));
        wcsKeywords.put(JWcs.CTYPE1, "RA---" + projectionCode);
        wcsKeywords.put(JWcs.CTYPE2, "DEC--" + projectionCode);
        JWcs wcs = new JWcsMap(wcsKeywords);
        wcs.doInit();
        return wcs;
    }
}
