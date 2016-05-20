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

import static io.github.malapert.jwcs.utility.NumericalUtility.createRealMatrix;
import static io.github.malapert.jwcs.utility.TimeUtility.epochs;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

/**
 * Mean place pre-IAU 1976 system.
 * 
 * <p>FK4 is the old barycentric (i.e. w.r.t. the common center of mass) equatorial
 * coordinate system, which should be qualified by an Equinox value.
 * For accurate work FK4 coordinate systems should also be qualified
 * by an Epoch value. This is the <b>epoch of observation</b>.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class FK4 implements CoordinateReferenceFrame {
    /**
     * The name of this reference frame.
     */     
    private final static CoordinateReferenceFrame.ReferenceFrame REF_SYSTEM = CoordinateReferenceFrame.ReferenceFrame.FK4;
    
    /**
     * The default value of the epoch set to B1950.
     */      
    private final static String DEFAULT_EPOCH = "B1950";   
    
    /**
     * The epoch of the equinox.
     */    
    private double equinox;
    
    /**
     * The epoch of observation.
     */    
    private double epochObs;       
    
    /**
     * Creates a FK4 reference frame with default value of epoch {@link FK4#DEFAULT_EPOCH}.
     */
    public FK4() {
        init(DEFAULT_EPOCH, null);
    }
    
    /**
     * Creates a FK4 reference frame with an epoch of equinox.
     * @param epoch the epoch of equinox
     */
    public FK4(final String epoch) {       
        init(epoch, null);
    }
    
    /**
     * Creates a FK4 reference frame with both equinox and epoch of observation.
     * @param epoch the epoch of equinox
     * @param epochObs the epoch of observation
     */
    public FK4(final String epoch, final String epochObs) {       
        init(epoch, epochObs);
    }
    
    /**
     * initialization.
     * @param epoch the epoch of equinox
     * @param epochObs the epoch of observation
     */
    private void init(final String epoch, final String epochObs) {        
        this.setEpochObs(epochObs);
        this.setEquinox(epoch);
    }
    
    /**
     * Compute the E-terms (elliptic terms of aberration) for a given epoch.
     * 
     * <p>Reference:<br>
     * ----------<br>
     * Seidelman, P.K.,  1992.  Explanatory Supplement to the Astronomical
     * Almanac.  University Science Books, Mill Valley
     *
     * <p>Notes:<br>     
     * -------<br>
     * The method is described on page 170/171 of the ES.
     * One needs to process the e-terms for the appropriate
     * epoch This routine returns the e-term vector for arbitrary epoch.
     * 
     * @param epoch A Besselian epoch
     * @return A tuple containing the e-terms vector (DeltaD,DeltaC,DeltaC.tan(e0))
     */
    public final static RealMatrix getEterms(final double epoch) {
        //Julian centuries since B1950
        final double T = (epoch-1950.0d)*1.00002135903d/100.0d;
        //Eccentricity of the Earth's orbit
        final double ec = 0.01673011d-(0.00004193d+0.000000126d*T)*T;
        //Mean obliquity of the ecliptic. Method is different compared to 
        //functions for the obliquity defined earlier. This function depends
        //on time wrt. epoch 1950 not epoch 2000.
        double ob = 84404.836d-(46.8495d+(0.00319d+0.00181d*T)*T)*T;
        ob = FastMath.toRadians(ob/3600.0d);
        //Mean longitude of perihelion of the solar orbit
        double p = 1015489.951d+(6190.67d+(1.65d+0.012d*T)*T)*T;
        p = FastMath.toRadians(p/3600.0d);
        //Calculate the E-terms vector
        final double ek = ec*FastMath.toRadians(20.49522d/3600.0d); // 20.49552 is constant of aberration at J2000
        final double cp = FastMath.cos(p);
        //       -DeltaD        DeltaC            DeltaC.tan(e0)
        final double[][] array = {
            {ek*FastMath.sin(p), -ek*cp*FastMath.cos(ob), -ek*cp*FastMath.sin(ob)}
        };
        return createRealMatrix(array);       
    } 

    /**
     * Returns the Besselian value of the epoch of observation.
     */    
    @Override
    public double getEpochObs() {
        return this.epochObs;
    }

    /**
     * Returns the Besselian epoch of the equinox.
     */    
    @Override
    public double getEquinox() {
        return this.equinox;
    }   

    @Override
    public ReferenceFrame getReferenceFrame() {
        return REF_SYSTEM;
    }

    /**
     * Sets the equinox.
     * @param equinox the equinox to set
     */
    @Override
    public final void setEquinox(final String equinox) {
        this.equinox = epochs(equinox)[0];
    }
    
    /**
     * Sets the Besselian epoch of the equinox.
     * @param equinox the equinox to set
     */
    @Override
    public final void setEquinox(final double equinox) {
        this.equinox = equinox;
    }    

    /**
     * Sets the epoch.
     * @param epochObs the epochObs to set
     */
    @Override
    public final void setEpochObs(final String epochObs) {
        this.epochObs = (epochObs == null)? Double.NaN : epochs(epochObs)[0];
    }
    
    /**
     * Sets the Besselian epoch of observation.
     *    
     * @param epochObs the epochObs to set
     */
    @Override
    public final void setEpochObs(final double epochObs) {
        this.epochObs = epochObs;
    }    

    @Override
    public String toString() {
        return "FK4("+this.equinox+","+this.epochObs+")";
    }        
}
