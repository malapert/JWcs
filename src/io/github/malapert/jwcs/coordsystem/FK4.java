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
package io.github.malapert.jwcs.coordsystem;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;


/**
 * Mean place pre-IAU 1976 system.
 * 
 * FK4 is the old barycentric (i.e. w.r.t. the common center of mass) equatorial
 * coordinate system, which should be qualified by an Equinox value.
 * For accurate work FK4 coordinate systems should also be qualified
 * by an Epoch value. This is the *epoch of observation*.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class FK4 implements ReferenceSystemInterface {
    /**
     * The name of this reference frame.
     */     
    private final static ReferenceSystemInterface.Type REF_SYSTEM = ReferenceSystemInterface.Type.FK4;
    
    /**
     * The default value of the equinox.
     */      
    private final static double DEFAULT_EQUINOX = 1950d;
    
    /**
     * The default value of the epoch of observation.
     */      
    private final static double DEFAULT_EPOCH = 1950d;
    
    /**
     * The epoch of the equinox.
     */    
    private double equinox;
    
    /**
     * The epoch of observation.
     */    
    private double epochObs;
    
    /**
     * Creates a FK4 reference frame with default values of both equinox
     * and epoch of observation.
     */
    public FK4() {
        init(DEFAULT_EQUINOX, DEFAULT_EPOCH);
    }
    
    /**
     * Creates a FK4 reference frame with a equinox.
     * @param equinox the equinox
     */
    public FK4(double equinox) {       
        init(equinox, DEFAULT_EPOCH);
    }
    
    /**
     * Creates a FK4 reference frame with both equinox and epoch of observation.
     * @param equinox the equinox
     * @param epoch the epoch of observation
     */
    public FK4(double equinox, double epoch) {       
        init(equinox, epoch);
    }
    
    /**
     * initialization.
     * @param equinox the equinox
     * @param epochObs the epoch of observation
     */
    private void init(double equinox, double epochObs) {
        this.setEpochObs(epochObs);
        this.setEquinox(equinox);
    }
    
    /**
     * Compute the E-terms (elliptic terms of aberration) for a given epoch.
     * 
     * Reference:
     * ----------
     * Seidelman, P.K.,  1992.  Explanatory Supplement to the Astronomical
     * Almanac.  University Science Books, Mill Valley
     *
     * Notes:     
     * -------
     * The method is described on page 170/171 of the ES.
     * One needs to process the e-terms for the appropriate
     * epoch This routine returns the e-term vector for arbitrary epoch.
     * 
     * @param epoch A Besselian epoch
     * @return A tuple containing the e-terms vector (DeltaD,DeltaC,DeltaC.tan(e0))
     */
    public final static RealMatrix getEterms(double epoch) {
        //Julian centuries since B1950
        double T = (epoch-1950.0d)*1.00002135903d/100.0d;
        //Eccentricity of the Earth's orbit
        double ec = 0.01673011d-(0.00004193d+0.000000126d*T)*T;
        //Mean obliquity of the ecliptic. Method is different compared to 
        //functions for the obliquity deFINERd earlier. This function depends
        //on time wrt. epoch 1950 not epoch 2000.
        double ob = (84404.836d-(46.8495d+(0.00319d+0.00181d*T)*T)*T);
        ob = Math.toRadians(ob/3600.0d);
        //Mean longitude of perihelion of the solar orbit
        double p = (1015489.951d+(6190.67d+(1.65d+0.012d*T)*T)*T);
        p = Math.toRadians(p/3600.0d);
        //Calculate the E-terms vector
        double ek = ec*Math.toRadians(20.49522d/3600.0d); // 20.49552 is constant of aberration at J2000
        double cp = Math.cos(p);
        //       -DeltaD        DeltaC            DeltaC.tan(e0)
        double[][] array = {
            {ek*Math.sin(p), -ek*cp*Math.cos(ob), -ek*cp*Math.sin(ob)}
        };
        return MatrixUtils.createRealMatrix(array);       
    } 

    @Override
    public Double getEpochObs() {
        return this.epochObs;
    }

    @Override
    public double getEquinox() {
        return this.equinox;
    }   

    @Override
    public Type getReferenceSystemType() {
        return REF_SYSTEM;
    }

    /**
     * Sets the equinox.
     * @param equinox the equinox to set
     */
    public void setEquinox(double equinox) {
        this.equinox = equinox;
    }

    /**
     * Sets the epoch.
     * @param epochObs the epochObs to set
     */
    public void setEpochObs(double epochObs) {
        this.epochObs = epochObs;
    }

    @Override
    public String toString() {
        return "FK4("+this.equinox+","+this.epochObs+")";
    }
        
}
