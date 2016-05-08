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

import io.github.malapert.jwcs.proj.exception.JWcsError;
import io.github.malapert.jwcs.utility.NumericalUtils;
import static io.github.malapert.jwcs.utility.TimeUtils.epochBessel2JD;
import static io.github.malapert.jwcs.utility.TimeUtils.epochJulian2JD;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * TimeUtils class for handling the rotation matrix.
 * 
 * The methods in this class have been traduced from Python to JAVA.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 * @see <a href="http://www.astro.rug.nl/software/kapteyn/">The original code in Python</a>
 */
public class Utility {

//In header:                           In wcs structure:
//
//EPOCH       EQUINOX     RADECSYS     epoch     equinox    radecsys
//1950        none        none         date-obs  1950       FK4
//2000        none        none         date-obs  2000       FK5
//date<1980   none        none         date-obs  date       FK4
//date>1980   none        none         date-obs  date       FK5
//none        none        none         date-obs  2000       FK5
//none        1950        none         date-obs  1950       FK4
//none        2000        none         date-obs  2000       FK5
//0           none        none         date-obs  1950       FK4
//0           1950        none         date-obs  1950       FK4
//0           2000        none         date-obs  2000       FK5    
    
    /**
     * Calculates the matrix that represents a 3d rotation around the X axis.
     * 
     * Reference:  
     * ----------
     * Diebel, J. 2006, Stanford University, Representing Attitude:
     * Euler angles, Unit Quaternions and Rotation Vectors. 
     * http://ai.stanford.edu/~diebel/attitude.html
     * 
     * Notes:
     * ------
     * Return the rotation matrix for a rotation around the X axis.
     * This is a rotation in the YZ plane. Note that we construct
     * a new vector with: xnew = R1.x
     * In the literature, this rotation is usually called R1
     *
     * @param angle Rotation angle in degrees
     * @return A 3x3 matrix representing the rotation about angle around X axis.
     */
    public final static RealMatrix rotX(final double angle) {
        double angleRadians = Math.toRadians(angle);
        double[][] array = {
            {1.d, 0.d, 0.d},
            {0.d, Math.cos(angleRadians), Math.sin(angleRadians)},
            {0.d, -1 * Math.sin(angleRadians), Math.cos(angleRadians)}
        };
        return MatrixUtils.createRealMatrix(array);
    }

    /**
     * Calculates the matrix that represents a 3d rotation around the Y axis.
     *
     * Reference:  
     * ----------
     * Diebel, J. 2006, Stanford University, Representing Attitude:
     * Euler angles, Unit Quaternions and Rotation Vectors. 
     * http://ai.stanford.edu/~diebel/attitude.html
     * 
     * Notes:
     * ------
     * Return the rotation matrix for a rotation around the X axis.
     * This is a rotation in the YZ plane. Note that we construct
     * a new vector with: xnew = R1.x
     * In the literature, this rotation is usually called R1
     * 
     * @param angle Rotation angle in degrees
     * @return A 3x3 matrix representing the rotation about angle around Y axis.
     */
    public final static RealMatrix rotY(double angle) {
        double angleRadians = Math.toRadians(angle);
        double[][] array = {
            {Math.cos(angleRadians), 0.d, -1 * Math.sin(angleRadians)},
            {0.d, 1.d, 0.d},
            {Math.sin(angleRadians), 0.d, Math.cos(angleRadians)}
        };
        return MatrixUtils.createRealMatrix(array);
    }

    /**
     * Calculates the matrix that represents a 3d rotation around the Z axis.
     *     
     * @param angle Rotation angle in degrees
     * @return A 3x3 matrix representing the rotation about angle around Z axis.
     */
    public final static RealMatrix rotZ(double angle) {
        double angleRadians = Math.toRadians(angle);
        double[][] array = {
            {Math.cos(angleRadians), Math.sin(angleRadians), 0.d},
            {-1 * Math.sin(angleRadians), Math.cos(angleRadians), 0.d},
            {0.d, 0.d, 1.d}
        };
        return MatrixUtils.createRealMatrix(array);
    }

    /**
     * Precession from one epoch to another in the fk5 system.
     *
     * Reference: 
     * ----------
     * Seidelman, P.K.,  1992.  Explanatory Supplement to the Astronomical
     * Almanac.  University Science Books, Mill Valley. 3.214 p 106
     * 
     * Notes:     
     * ------
     * The precession matrix is: M = rotZ(-z).rotY(+theta).rotZ(-zeta)
     * 
     * @param jEpoch1 Julian start epoch
     * @param jEpoch2 Julian epoch to process to
     * @return 3x3 rotation matrix M as in XYZepoch2 = M * XYZepoch1
     */
    public final static RealMatrix julianMatrixEpoch12Epoch2(double jEpoch1, double jEpoch2) {
        double jd1 = epochJulian2JD(jEpoch1);
        double jd2 = epochJulian2JD(jEpoch2);
        double[] precessionAngles = lieskeprecangles(jd1, jd2);
        return precessionMatrix(precessionAngles[0], precessionAngles[1], precessionAngles[2]);
    }

    /**
     * Calculates IAU 1976 precession angles for a precession of epoch
     * corresponding to Julian date jd1 to epoch corresponds to Julian date jd2.
     *
     * Reference:
     * ----------
     * Lieske,J.H., 1979. Astron.Astrophys.,73,282. equations (6) and (7), p283.
     * 
     * Notes:
     * ------
     * The ES (Explanatory Supplement to the Astronomical Almanac)
     * lists for a IAU1976 precession from 1984, January 1d0h to J2000
     * the angles in **arcsec**:  ``xi_a=368.9985, ze_a=369.0188 and 
     * th_a=320.7279``
     *      
     * @param jd1 Julian date for start epoch
     * @param jd2 Julian date for end epoch
     * @return Angles \u03B6 (zeta), z, \u03B8 (theta) degrees
     */
    public final static double[] lieskeprecangles(double jd1, double jd2) {    
        // T = (Current epoch - 1 jan, 2000, 12h noon)
        double T = (jd1 - 2451545.0d) / 36525.0d;
        double t = (jd2 - jd1) / 36525.0d;
        double d1 = 2306.2181d;
        double d2 = 1.39656d;
        double d3 = -0.000139d;
        double d4 = 0.30188d;
        double d5 = 0.000344d;
        double d6 = 0.017998d;
        double D1 = d1 + T * (d2 + T * d3);
        double zeta_a = t * (D1 + t * ((d4 + d5 * T) + t * d6));
        d4 = 1.09468d;
        d5 = -0.000066d;
        d6 = 0.018203d;
        double z_a = t * (D1 + t * ((d4 + d5 * T) + t * d6));
        d1 = 2004.3109d;
        d2 = -0.85330d;
        d3 = -0.000217d;
        d4 = -0.42665d;
        d5 = -0.000217d;
        d6 = -0.041833d;
        D1 = d1 + T * (d2 + T * d3);
        double theta_a = t * (D1 + t * ((d4 + d5 * T) + t * d6));
        //Return values in degrees
        double[] precessionAngles = {zeta_a / 3600.0d, z_a / 3600.0d, theta_a / 3600.0d};
        return precessionAngles;
    }

    /**
     * Given three precession angles, creates the corresponding rotation matrix.
     * 
     * Return the precession matrix for the three precession angles 
     * zeta, z and theta. Rotation matrix: R = rotZ(-z).rotY(th).rotZ(-zeta) 
     * (ES 3.21-7, p 103). Also allowed is the expression: 
     * rotZ(-90-z)*rotX(th)*rotZ(90-zeta)
     * 
     * @param zeta zeta in decimal degree
     * @param z z in decimal degree
     * @param theta theta in decimal degree
     * @return Rotation matrix M as in XYZepoch1 = M * XYZepoch2
     */
    public final static RealMatrix precessionMatrix(double zeta, double z, double theta) {
        return rotZ(-z).multiply(rotY(theta)).multiply(rotZ(-zeta));
    }

    /**
     * Precession from one epoch to another in the fk4 system.
     *
     * Reference: 
     * ----------
     * Seidelman, P.K.,  1992.  Explanatory Supplement to the Astronomical
     * Almanac.  University Science Books, Mill Valley. 3.214 p 106
     * 
     * Notes:     
     * ------
     * The precession matrix is: M = rotZ(-z).rotY(+theta).rotZ(-zeta)
     *     
     * @param bEpoch1 Besselian start epoch
     * @param bEpoch2 Besselian epoch to precess to.
     * @return 3x3 rotation matrix M as in XYZepoch2 = M * XYZepoch1
     */
    public final static RealMatrix besselianMatrixEpoch12Epoch2(double bEpoch1, double bEpoch2) {
        double[] precessionAngles = newcombPrecAngles(bEpoch1, bEpoch2);
        return precessionMatrix(precessionAngles[0], precessionAngles[1], precessionAngles[2]);
    }

    /**
     * Calculates precession angles for a precession in FK4, using Newcomb's
     * method (Woolard and Clemence angles).
     *
     * Notes:
     * ------
     * Newcomb's precession angles for old catalogs (FK4), see ES 3.214 p.106.
     * Input are **Besselian epochs**!
     * Adopted accumulated precession angles from equator and equinox at 
     * B1950 to 1984 January 1d 0h according to ES (table 3.214.1, p 107) are:
     * ``zeta=783.7092, z=783.8009 and theta=681.3883``. The Woolard and 
     * Clemence angles (derived in this routine) are:
     * ``zeta=783.70925, z=783.80093 and theta=681.38830``
     * (see same ES table as above).
     * 
     * @param epoch1 Besselian start epoch
     * @param epoch2 Besselian end epoch
     * @return Angles \u03B6 (zeta), z, \u03B8 (theta) degrees
     */
    public final static double[] newcombPrecAngles(double epoch1, double epoch2) {
        double t1 = (epoch1 - 1850.0d) / 1000.0d;    //1000 tropical years
        double t2 = (epoch2 - 1850.0d) / 1000.0d;
        double tau = t2 - t1;

        double d0 = 23035.545d;
        double d1 = 139.720d;
        double d2 = 0.060d;
        double d3 = 30.240d;
        double d4 = -0.27d;
        double d5 = 17.995d;
        double a0 = d0 + t1 * (d1 + d2 * t1);
        double a1 = d3 + d4 * t1;
        double a2 = d5;
        double zeta_a = tau * (a0 + tau * (a1 + tau * a2));

        d0 = 23035.545d;
        d1 = 139.720d;
        d2 = 0.060d;
        d3 = 109.480d;
        d4 = 0.39d;
        d5 = 18.325d;
        a0 = d0 + t1 * (d1 + d2 * t1);
        a1 = d3 + d4 * t1;
        a2 = d5;
        double z_a = tau * (a0 + tau * (a1 + tau * a2));

        d0 = 20051.12d;
        d1 = -85.29d;
        d2 = -0.37d;
        d3 = -42.65d;
        d4 = -0.37d;
        d5 = -41.80d;
        a0 = d0 + t1 * (d1 + d2 * t1);
        a1 = d3 + d4 * t1;
        a2 = d5;
        double theta_a = tau * (a0 + tau * (a1 + tau * a2));
        // Return values in degrees
        double[] precessionAngles = {zeta_a / 3600.0d, z_a / 3600.0d, theta_a / 3600.0d};
        return precessionAngles;
    }

    /**
     * Creates a matrix to precess from B1950 in FK4 to J2000 in FK5 
     * following to Murray's (1989) procedure.
     * 
     * Reference: 
     * ----------
     * * Murray, C.A. The Transformation of coordinates between the 
     * systems B1950.0 and J2000.0, and the principal galactic axis 
     * referred to J2000.0, Astronomy and Astrophysics (ISSN 0004-6361), 
     * vol. 218, no. 1-2, July 1989, p. 325-329.
     * * Poppe P.C.R.,, Martin, V.A.F., Sobre as Bases de Referencia Celeste 
     * SitientibusSerie Ciencias Fisicas
     * 
     * Notes:     
     * ------
     * Murray precesses from B1950 to J2000 using a precession matrix
     * by Lieske. Then applies the equinox correction and ends up with a
     * transformation matrix *X(0)* as given in this function.
     * In Murray's article it is proven that using the procedure as
     * described in the article,  ``r_fk5 = X(0).r_fk4`` for extra galactic
     * sources where we assumed that the proper motion in FK5 is zero.
     * This procedure is independent of the epoch of observation.
     * Note that the matrix is not a rotation matrix.
     * FK4 is not an inertial coordinate frame (because of the error
     * in precession and the motion of the equinox. This has 
     * consequences for the proper motions. e.g. a source with zero
     * proper motion in FK5 has a fictitious proper motion in FK4.
     * This affects the actual positions in a way that the correction
     * is bigger if the epoch of observation is further away from 1950.0
     * The focus of this library is on data of which we do not have
     * information about the proper motions. So for positions of which
     * we allow non zero proper motion in FK5 one needs to supply the
     * epoch of observation.
     * 
     * @param t Besselian epoch as epoch of observation
     * @return 3x3 matrix M as in XYZfk5 = M * XYZfk4
     */
    public static final RealMatrix FK42FK5Matrix(double t) {
        RealMatrix mat = FK42FK5Matrix();
        double jd = epochBessel2JD(t);
        double T = (jd - 2433282.423d) / 36525.0d; //t-1950 in Julian centuries = F^-1.t1 from Murray (1989)
        double r00 = mat.getEntry(0, 0) - 0.0026455262d * T / 1000000.0d;
        double r01 = mat.getEntry(0, 1) - 1.1539918689d * T / 1000000.0d;
        double r02 = mat.getEntry(0, 2) + 2.1111346190d * T / 1000000.0d;
        double r10 = mat.getEntry(1, 0) + 1.1540628161d * T / 1000000.0d;
        double r11 = mat.getEntry(1, 1) - 0.0129042997d * T / 1000000.0d;
        double r12 = mat.getEntry(1, 2) + 0.0236021478d * T / 1000000.0d;
        double r20 = mat.getEntry(2, 0) - 2.1112979048d * T / 1000000.0d;
        double r21 = mat.getEntry(2, 1) - 0.0056024448d * T / 1000000.0d;
        double r22 = mat.getEntry(2, 2) + 0.0102587734d * T / 1000000.0d;
        mat.setEntry(0, 0, r00);
        mat.setEntry(0, 1, r01);
        mat.setEntry(0, 2, r02);
        mat.setEntry(1, 0, r10);
        mat.setEntry(1, 1, r11);
        mat.setEntry(1, 2, r12);
        mat.setEntry(2, 0, r20);
        mat.setEntry(2, 1, r21);
        mat.setEntry(2, 2, r22);
        return mat;
    }

    /**
     * See FK42FK5Matrix
     * @return 3x3 matrix M as in XYZfk5 = M * XYZfk4
     */
    public static final RealMatrix FK42FK5Matrix() {
        double[][] array = {
            {0.9999256794956877d, -0.0111814832204662d, -0.0048590038153592d},
            {0.0111814832391717d, 0.9999374848933135d, -0.0000271625947142d},
            {0.0048590037723143d, -0.0000271702937440d, 0.9999881946023742d}
        };
        return MatrixUtils.createRealMatrix(array);
    }

    /**
     * Returns the matrix FK5 to FK4.
     * @return a matrix
     */
    public final static RealMatrix FK52FK4Matrix() {
        return MatrixUtils.inverse(FK42FK5Matrix());
    }
    
    /**
     * Creates a matrix to convert a position in fk5 to fk4 using 
     * the inverse matrix FK42FK5Matrix.
     * 
     * @param t Epoch of observation for those situations where we allow 
     * no-zero proper motion in fk4
     * @return Rotation matrix M as in XYZfk5 = M * XYZfk4
     */
    public final static RealMatrix FK52FK4Matrix(double t) {
        return MatrixUtils.inverse(FK42FK5Matrix(t));
    }

    /**
     * Creates a rotation matrix to convert a position from ICRS to fk5, J2000.
     * 
     * Reference:  
 ----------
 Kaplan G.H., The IAU Resolutions on Astronomical Reference systems, 
 TimeUtils scales, and Earth Rotation Models, US Naval Observatory, 
 Circular No. 179
 
 Notes:      
 ------
 Return a matrix that converts a position vector in ICRS to FK5, J2000.
 We do not use the first or second order approximations given in the
 reference, but use the three rotation matrices from the same paper to
 obtain the exact result
 M =  rotX(-eta0)*rotY(xi0)*rotZ(da0)
 eta0 = -19.9 mas, xi0 = 9.1 mas and da0 = -22.9 mas
     * 
     * @return 3x3 rotation matrix M as in XYZfk5 = M * XYZicrs
     */
    public final static RealMatrix ICRS2FK5Matrix() {
        double eta0 = -19.9d / (3600d * 1000d);  //Convert mas to degree
        double xi0 = 9.1d / (3600d * 1000d);
        double da0 = -22.9d / (3600d * 1000d);
        return rotX(-eta0).multiply(rotY(xi0)).multiply(rotZ(da0));
    }

    /**
     * Returns a rotation matrix for conversion of a position in the 
     * ICRS to the dynamical reference system based on the dynamical 
     * mean equator and equinox of J2000.0 (called the dynamical J2000 system).
     * 
     * Reference: 
 ----------
 Hilton and Hohenkerk (2004), Astronomy and Astrophysics 413, 765-770
 Kaplan G.H., The IAU Resolutions on Astronomical Reference systems,
 TimeUtils scales, and Earth Rotation Models, US Naval Observatory,
 Circular No. 179
 
 Notes:     
 ------
 Return a matrix that converts a position vector in ICRS to Dyn. J2000. 
 We do not use the first or second order approximations given in the 
 reference, but use the three rotation matrices to obtain the exact. 
 
 Reference:
 ----------
 Capitaine N. et al.: IAU 2000 precession A and A 412, 567-586 (2003)
 
 Notes:     
 ------
 Note that we apply this precession only to equatorial coordinates in the
 system of dynamical J2000 coordinates.
 When converting from ICRS coordinates this means applying a frame bias. 
 Therefore the angles differ from the precession Fukushima-Williams angles
 (IAU 2006).
 The precession matrix is:
 M = rotZ(-z).rotY(+theta).rotZ(-zeta)
     * 
     * @return Rotation matrix to transform positions from ICRS to dyn J2000
     */
    public final static RealMatrix ICRS2J2000Matrix() {
        double eta0 = -6.8192d / (3600d * 1000d); //Convert mas to degree
        double xi0 = -16.617d / (3600d * 1000d);
        double da0 = -14.6d / (3600d * 1000d);
        return rotX(-eta0).multiply(rotY(xi0)).multiply(rotZ(da0));
    }

    /**
     * Creates a rotation matrix for a precession based on IAU 2000/2006
     * expressions, see `IAU2006precangles`.
     * 
     * Reference:
     * ----------
     * Capitaine N. et al.: IAU 2000 precession A and A 412, 567-586 (2003)
     * 
     * Notes:     
     * ------
     * Note that we apply this precession only to equatorial
     * coordinates in the system of dynamical J2000 coordinates.
     * When converting from ICRS coordinates this means applying 
     * a frame bias. 
     * Therefore the angles differ from the precession 
     * Fukushima-Williams angles (IAU 2006)     
     * The precession matrix is: M = rotZ(-z).rotY(+theta).rotZ(-zeta)
     * 
     * @param epoch1 Julian start epoch
     * @param epoch2 Julian epoch to precess to
     * @return Matrix to transform equatorial coordinates from epoch1 to 
     * epoch2 as in XYZepoch2 = M * XYZepoch1
     */
    public final static RealMatrix IAU2006MatrixEpoch12Epoch2(double epoch1, double epoch2) {
        RealMatrix result;
        if (NumericalUtils.equal(epoch1, epoch2)) {
            result = MatrixUtils.createRealIdentityMatrix(3);
        } else if (epoch1 == 2000.0) {
            double[] precessionAngles = IAU2006PrecAngles(epoch2);
            result = precessionMatrix(precessionAngles[0], precessionAngles[1], precessionAngles[2]);
        } else { // If both epochs are not J2000.0
            double[] precessionAngles = IAU2006PrecAngles(epoch1);
            RealMatrix m1 = precessionMatrix(precessionAngles[0], precessionAngles[1], precessionAngles[2]);
            m1 = m1.transpose();
            precessionAngles = IAU2006PrecAngles(epoch2);
            RealMatrix m2 = precessionMatrix(precessionAngles[0], precessionAngles[1], precessionAngles[2]);
            result = m1.multiply(m2);
        }
        return result;
    }

    /**
     * Calculates IAU 2000 precession angles for precession from input epoch
     * to J2000.
     * 
     * Reference:
     * ----------
     * Capitaine N. et al., IAU 2000 precession A and A 412, 567-586 (2003)
     * 
     * Notes:
     * ------
     * Input are Julian epochs!
     * ``T = (jd-2451545.0)/36525.0``
     * Combined with ``jd = Jepoch-2000.0)*365.25 + 2451545.0`` gives:
     * (see *epochJulian2JD(epoch)*)
     * ``T = (epoch-2000.0)/100.0``
     * This function should be updated as soon as there are IAU2006 adopted
     * angles to replace the angles used in this function.
     * 
     * @param epoch Julian epoch of observation
     * @return Angles \u03B6 (zeta), z, \u03B8 (theta) in degrees to setup a 
     * rotation matrix to transform from J2000 to input epoch.
     */
    public final static double[] IAU2006PrecAngles(double epoch) {
        // T = (Current epoch - 1 jan, 2000, 12h noon)
        double T = (epoch - 2000.0d) / 100.0d;
        double d0 = 2.5976176d;
        double d1 = 2306.0809506d;
        double d2 = 0.3019015d;
        double d3 = 0.0179663d;
        double d4 = -0.0000327d;
        double d5 = -0.0000002d;
        double zeta_a = T * (d1 + T * (d2 + T * (d3 + T * (d4 + T * (d5))))) + d0;

        d0 = -2.5976176d;
        d1 = 2306.0803226d;
        d2 = 1.0947790d;
        d3 = 0.0182273d;
        d4 = 0.0000470d;
        d5 = -0.0000003d;
        double z_a = T * (d1 + T * (d2 + T * (d3 + T * (d4 + T * (d5))))) + d0;

        d0 = 0.0;
        d1 = 2004.1917476d;
        d2 = -0.4269353d;
        d3 = -0.0418251d;
        d4 = -0.0000601d;
        d5 = -0.0000001d;
        double theta_a = T * (d1 + T * (d2 + T * (d3 + T * (d4 + T * (d5))))) + d0;

        //Return values in degrees
        double[] precessionAngles = {zeta_a / 3600.0d, z_a / 3600.0d, theta_a / 3600.0d};
        return precessionAngles;
    }

    /**
     * Given two angles in longitude and latitude returns corresponding 
     * Cartesian coordinates x,y,z.
     * 
     * Notes:     
     * ------
     * The three coordinate axes x, y and z, the set of right-handed Cartesian
     * axes that correspond to the usual celestial spherical coordinate system. 
     * The xy-plane is the equator, the z-axis points toward the north celestial
     * pole, and the x-axis points toward the origin of right ascension.
     * 
     * @param longitude longitude in decimal degree
     * @param latitude latitude in decimal degree
     * @return Corresponding values of x,y,z in same order as input
     */
    public final static RealMatrix longlat2xyz(double longitude, double latitude) {
        double longitudeRad = Math.toRadians(longitude);
        double latitudeRad = Math.toRadians(latitude);
        return Utility.longlatRad2xyz(longitudeRad, latitudeRad);
    }   
    
    /**
     * Given two angles in longitude and latitude returns corresponding 
     * Cartesian coordinates x,y,z.
     * 
     * Notes:     
     * ------
     * The three coordinate axes x, y and z, the set of right-handed Cartesian
     * axes that correspond to the usual celestial spherical coordinate system. 
     * The xy-plane is the equator, the z-axis points toward the north celestial
     * pole, and the x-axis points toward the origin of right ascension.
     * 
     * @param longitudeRad longitude in radians
     * @param latitudeRad latitude in radians
     * @return Corresponding values of x,y,z in same order as input
     */    
    public final static RealMatrix longlatRad2xyz(double longitudeRad, double latitudeRad) {
        double x = Math.cos(longitudeRad) * Math.cos(latitudeRad);
        double y = Math.sin(longitudeRad) * Math.cos(latitudeRad);
        double z = Math.sin(latitudeRad);
        double[][] array = {
            {x},
            {y},
            {z}
        };
        return MatrixUtils.createRealMatrix(array);
    }

    /**
     * Given Cartesian x,y,z return corresponding longitude and latitude in 
     * degrees.
     * 
     * Notes:
     * ------
     * Note that one can expect strange behavior for the values of the 
     * longitudes very close to the pole. In fact, at the poles itself, 
     * the longitudes are meaningless.
     * 
     * @param xyz Vector with values for x,y,z
     * @return The same number of positions (longitude, latitude and in the 
     * same order as the input.
     */
    public final static double[] xyz2longlat(final RealMatrix xyz) {
        double x = xyz.getEntry(0, 0);
        double y = xyz.getEntry(1, 0);
        double z = xyz.getEntry(2, 0);
        double longitude = Math.toDegrees(NumericalUtils.aatan2(y, x, 0));
        longitude = (longitude < 0) ? longitude + 360.0d : longitude;
        double latitude = Math.toDegrees(NumericalUtils.aasin(z));
        double coord[] = {longitude, latitude};
        return coord;
    }

    /**
     * Remove the elliptic component of annual aberration when this is included
     * in a catalogue fk4 position.
     * 
     * Notes:
     * ------
     * Return a new position where the elliptic terms of aberration 
     * are removed i.e. convert a apparent position from a catalog to
     * a mean place.
     * The effects of ecliptic aberration were included in the 
     * catalog positions to facilitate telescope pointing.
     * See also notes at 'addEterms'.
     * 
     * @param xyz vector xyz
     * @param eterm E-terms vector (as returned by getEterms()). If input a is 
     * omitted (== *null*), the e-terms for 1950 will be substituted.
     * @return **Mean place**
     */
    public final static RealMatrix removeEterms(final RealMatrix xyz, RealMatrix eterm) {
        RealMatrix xyzeterm = xyz.copy();

        if (eterm == null) {
            eterm = FK4.getEterms(1950);
        }
        double x = xyz.getEntry(0, 0);
        double y = xyz.getEntry(1, 0);
        double z = xyz.getEntry(2, 0);
        x -= eterm.getEntry(0, 0);
        y -= eterm.getEntry(0, 1);
        z -= eterm.getEntry(0, 2);
        xyzeterm.setEntry(0, 0, x);
        xyzeterm.setEntry(1, 0, y);
        xyzeterm.setEntry(2, 0, z);

        return xyzeterm;
    }

    /**
     * Add the elliptic component of annual aberration when the rsult must be a
     * catalogue fk4 position.
     * 
     * Reference: 
     * ----------
     * * Seidelman, P.K.,  1992.  Explanatory Supplement to the Astronomical
     * Almanac.  University Science Books, Mill Valley.
     * * Yallop et al, Transformation of mean star places,
     * AJ, 1989, vol 97, page 274
     * * Stumpff, On the relation between Classical and Relativistic
     * Theory of Stellar Aberration, Astron, Astrophys, 84, 257-259 (1980)
     * 
     * Notes:     
     * ------
     * There is a so called ecliptic component in the stellar aberration.
     * This vector depends on the epoch at which we want to process
     * these terms. It corresponds to the component of the earth's velocity
     * perpendicular to the major axis of the ellipse in the ecliptic.
     * The E-term corrections are as follows. A catalog FK4 position
     * include corrections for elliptic terms of aberration. 
     * These positions are apparent places. For precession and/or 
     * rotations to other sky systems, one processes only mean places.
     * So to get a mean place, one has to remove the E-terms vector.
     * The ES suggests for the removal to use a decompositions of the
     * E-term vector along the unit circle to get the approximate 
     * new vector, which has almost the correct angle and has almost 
     * length 1. The advantage is that when we add the E-term vector 
     * to this new vector, we obtain a new vector with the original 
     * angle, but with a length unequal to 1, which makes it suitable
     * for closure tests.
     * However, the procedure can be made more rigorous: 
     * For the subtraction we subtract the E-term vector from the 
     * start vector and normalize it afterwards. Then we have an
     * exact new angle (opposed to the approximation in the ES).
     * The procedure to go from a vector in the mean place system to 
     * a vector in the system of apparent places is a bit more 
     * complicated:
     * Find a value for lambda so that the current vector is
     * adjusted in length so that adding the e-term vector gives a new
     * vector with length 1. This is by definition the new vector
     * with the right angle.
     * 
     * @param xyz Cartesian position(s) converted from lonlat
     * @param eterm E-terms vector (as returned by getEterms()). If input *a* 
     * is omitted (i.e. *a == null*), the e-terms for 1950 will be substituted.
     * @return **Apparent place**,
     */
    public final static RealMatrix addEterms(final RealMatrix xyz, RealMatrix eterm) {
        RealMatrix xyzeterm = xyz.copy();

        if (eterm == null) {
            eterm = FK4.getEterms(1950);
        }        
        double x = xyz.getEntry(0, 0);
        double y = xyz.getEntry(1, 0);
        double z = xyz.getEntry(2, 0);
        // Normalize to get a vector of length 1. Our algorithm is based on that fact.
        double d = Math.sqrt(x * x + y * y + z * z);
        x /= d;
        y /= d;
        z /= d;
        // Find the lambda to stretch the vector 
        double w = 2.0d * (eterm.getEntry(0, 0) * x + eterm.getEntry(0, 1) * y + eterm.getEntry(0, 2) * z);
        double p = eterm.getEntry(0, 0) * eterm.getEntry(0, 0) + eterm.getEntry(0, 1) * eterm.getEntry(0, 1) + eterm.getEntry(0, 2) * eterm.getEntry(0, 2) - 1.0d;
        double lambda1 = (-1 * w + Math.sqrt(w * w - 4.0d * p)) / 2.0d;  //Vector a is small. We want only the positive lambda             

        x = lambda1 * x + eterm.getEntry(0, 0);
        y = lambda1 * y + eterm.getEntry(0, 1);
        z = lambda1 * z + eterm.getEntry(0, 2);
        xyzeterm.setEntry(0, 0, x);
        xyzeterm.setEntry(1, 0, y);
        xyzeterm.setEntry(2, 0, z);

        return xyzeterm;
    }

    /**
     * Create matrix to convert equatorial fk4 coordinates (without e-terms)
     * to IAU 1958 lII,bII system of galactic coordinates.
     * 
     * Reference:
     * ----------
     * 1. Blaauw, A., Gum C.S., Pawsey, J.L., Westerhout, G.: 1958,
     * 2. Monthly Notices Roy. Astron. Soc. 121, 123,
     * 3. Blaauw, A., 2007. Private communications.
     * 
     * Notes:
     * ------
     * Original definitions from 1.:
     * *  The new north galactic pole lies in the direction
     * alpha = 12h49m (192.25 deg), delta=27.4 deg (equinox 1950.0).
     * *  The new zero of longitude is the great semicircle
     * originating at the new north galactic pole at the
     * position angle theta = 123 deg with respect
     * to the equatorial pole for 1950.0.
     * *  Longitude increases from 0 to 360 deg. The sense is
     * such that, on the galactic equator increasing galactic
     * longitude corresponds to increasing Right Ascension.
     * Latitude increases from -90 deg through 0 deg to 90 deg
     * at the new galactic pole.
     * 
     * Given the RA and Dec of the galactic pole, and using the Euler angles 
     * scheme: M = rotZ(a3).rotY(a2).rotZ(a1)
     * 
     * We first rotate the spin vector of the XY plane about an angle 
     * a1 = ra_pole and then rotate the spin vector in the XZ plane 
     * (i.e. around the Y axis) with an angle a2=90-dec_pole to point it in 
     * the right declination.
     * 
     * Now think of a circle with the galactic pole as its center.
     * The radius is equal to the distance between this center
     * and the equatorial pole. The zero point now is on the circle
     * and opposite to this pole.
     * 
     * We need to rotate along this circle (i.e. a rotation
     * around the new Z-axis) in a way that the angle between the
     * zero point and the equatorial pole is equal to 123 deg.
     * 
     * So first we need to compensate for the 180 deg of the
     * current zero longitude, opposite to the pole. Then we need
     * to rotate about an angle 123 deg but in a way that increasing
     * galactic longitude corresponds to increasing Right Ascension
     * which is opposite to the standard rotation of this circle
     * (note that we rotated the original X axis about 192.25 deg).
     * The last rotation angle therefore is a3=+180-123:
     * M = rotZ(180-123.0)*rotY(90-27.4)*rotZ(192.25)
     * 
     * The composed rotation matrix is the same as in Slalib's 'ge50.f' 
     * and the matrix in eq. (32) of Murray (1989).
     * 
     * @return 3x3 Matrix M as in XYZgal = M * XYZb1950
     */
    public final static RealMatrix MatrixEqB19502Gal() {
        return rotZ(180d - 123.0d).multiply(rotY(90d - 27.4d)).multiply(rotZ(192.25d));
    }

    /**
     * Transforms galactic to supergalactic coordinates.
     * 
     * Reference:  
     * ----------
     * Lahav, O., The supergalactic plane revisited with the Optical Redshift
     * Survey Mon. Not. R. Astron. Soc. 312, 166-176 (2000)
     * 
     * Notes:      
     * ------
     * The Supergalactic equator is conceptually deFINERd by the plane of the 
     * local (Virgo-Hydra-Centaurus) supercluster, and the origin of 
     * supergalactic longitude is at the intersection of the supergalactic and 
     * galactic planes. (de Vaucouleurs) 
     * 
     * North SG pole at l=47.37 deg, b=6.32 deg. 
     * Node at l=137.37, sgl=0 (inclination 83.68 deg).
     * 
     * Older references give for he position of the SG node 137.29
     * which differs from 137.37 deg in the official definition.
     * 
     * For the rotation matrix we chose the scheme *Rz.Ry.Rz*
     * Then first we rotate about 47.37 degrees along the Z-axis
     * ollowed by a rotation about 90-6.32 degrees is needed to
     * set the pole to the right declination.
     * The new plane intersects the old one at two positions.
     * One of them is l=137.37, b=0 (in galactic coordinates).
     * If we want this to be sgl=0 we have to rotate this plane along
     * the new Z-axis about an angle of 90 degrees. So the composed
     * rotation matrix is::
     * M = Rotz(90)*Roty(90-6.32)*Rotz(47.37)
     * 
     * @return Matrix M as in XYZsgal = M * XYZgal
     */
    public final static RealMatrix MatrixGal2Sgal() {
        return rotZ(90.0d).multiply(rotY(90d - 6.32d)).multiply(rotZ(47.37d));
    }

    /**
     * What is the obliquity of the ecliptic at this Julian date? 
     * (IAU model 2000).
     * 
     * Reference:
 ----------
 Fukushima, T. 2003, AJ, 126,1
 Kaplan, H., 2005, The IAU Resolutions
 on Astronomical Reference Systems, TimeUtils Scales, and Earth Rotation 
 Models, United States Naval Observatory circular no. 179,
 http://aa.usno.navy.mil/publications/docs/Circular_179.pdf (page 44)
 
 Notes:
 ------
 The epoch is entered in Julian date and the time is calculated
 w.r.t. J2000.
 The obliquity is the angle between the mean equator and
 ecliptic, or, between the ecliptic pole and mean celestial pole of date.
     * 
     * @param jd Julian date
     * @return Mean obliquity in degrees
     */
    public final static double obliquity2000(double jd) {
        // T = (Date - 1 jan, 2000, 12h noon)
        double T = (jd - 2451545.0d) / 36525.0d;
        double eps = (84381.406d
                + (-46.836769d
                + (-0.0001831d
                + (0.00200340d
                + (-0.000000576d
                + (-0.0000000434d) * T) * T) * T) * T) * T) / 3600.0d;
        return eps;
    }

    /**
     * What is the obliquity of the ecliptic at this Julian date? 
     * (IAU 1980 model). 
     * 
     * Reference:
     * ----------
     * Explanatory Supplement to the Astronomical Almanac,
     * P. Kenneth Seidelmann (ed), University Science Books (1992),
     * Expression 3.222-1 (p114).
     * 
     * Notes:
     * ------
     * The epoch is entered in Julian date and the time is calculated 
     * w.r.t. J2000.
     * The obliquity is the angle between the mean equator and
     * ecliptic, or, between the ecliptic pole and mean celestial pole of date
     * 
     * @param jd Julian date
     * @return Mean obliquity in degrees
     */
    public final static double obliquity1980(double jd) {
        // T = (Date - 1 jan, 2000, 12h noon)
        double T = (jd - 2451545.0d) / 36525.0d;
        double eps = (84381.448d + (-46.8150d + (-0.00059d + 0.001813d * T) * T) * T) / 3600.0d;
        return eps;
    }

    /**
     * Calculates a rotation matrix to convert equatorial coordinates to 
     * ecliptical coordinates.
     * 
     * Reference: 
     * ----------
     * Representations of celestial coordinates in FITS, 
     * Calabretta. M.R., and Greisen, E.W., (2002)
     * Astronomy and Astrophysics,  395,  1077-1122.
     * http://www.atnf.csiro.au/people/mcalabre/WCS/ccs.pdf
     * 
     * Notes:     
     * ------
     * 1. The origin for ecliptic longitude is the vernal equinox.
     * Therefore the coordinates of a fixed object is subject to 
     * shifts due to precession. The rotation matrix 
     * uses the obliquity to do the conversion to the wanted ecliptic 
     * coordinates.
     * So we always need to enter an epoch. Usually this is J2000, but it can 
     * also be the epoch of date. The additional reference system indicates 
     * whether we need a Besselian or a Julian epoch.
     * 
     * 2. In the FITS paper of Calabretta and Greisen (2002), one 
     * observes the following relations to FITS:
     * -Keyword RADESYSa sets the catalog system FK4, FK4-NO-E or FK5
     * This applies to equatorial and ecliptical coordinates with 
     * the exception of FK4-NO-E.
     * -FK4 coordinates are not strictly spherical since they include 
     * a contribution from the elliptic terms of aberration, the 
     * so-called e-terms which amount to max. 343 milliarcsec. 
     * FITS paper: *'Strictly speaking, therefore, a map obtained from, 
     * say, a radio synthesis telescope, should be regarded
     * as FK4-NO-E unless it has been appropriately re-sampled
     * or a distortion correction provided.
     * In common usage, however, CRVALia for such maps is usually 
     * given in FK4 coordinates. In doing so, the e-terms are effectively
     * corrected to first order only.'*. (See also ES, eq. 3.531-1 page 170.
     * -Keyword EQUINOX sets the epoch of the mean equator and equinox.
     * -Keyword EPOCH is often used in older FITS files. 
     * It is a deprecated keyword and should be replaced by EQUINOX. It does not
     * require keyword RADESYS. From its value we derive whether the reference 
     * system is FK4 or FK5 (the marker value is 1984.0)
     * -Ecliptic coordinates require the epoch of the equator and equinox
     * of date. This will be taken as the time of observation rather than
     * EQUINOX. 
     * FITS paper: *'The time of observation may also be required for
     * other astrometric purposes in addition to the usual astrophysical
     * uses, for example, to specify when the mean place was
     * correct in accounting for proper motion, including "fictitious"
     * proper motions in the conversion between the FK4 and FK5 systems.
     * The old *DATE-OBS* keyword may be used for this purpose.
     * However, to provide a more convenient specification we
     * here introduce the new keyword MJD-OBS'.*
     * So MJD-OBS is the modified Julian Date (JD - 2400000.5) of the
     * start of the observation.
     * 
     * 3. Equatorial to ecliptic transformations use the time dependent 
     * obliquity of the equator (also known as the obliquity of the ecliptic).
     * Again, start with:
     * M = rotZ(0).rotX(eps).rotZ(0) = E.rotX(eps).E = rotX(eps)
     * In fact this is only a rotation around the X axis
     * 
     * @param epoch Epoch of the equator and equinox of date 
     * @param refSystem equatorial system to determine if one entered epoch in 
     * B or J coordinates
     * @return 3x3 Matrix M as in XYZecl = M * XYZeq
     */
    public final static RealMatrix MatrixEq2Ecl(double epoch, final ReferenceSystemInterface.Type refSystem) {
        double jd;
        if (ReferenceSystemInterface.Type.FK4.equals(refSystem)) {
            jd = epochBessel2JD(epoch);
        } else {
            jd = epochJulian2JD(epoch);
        }
        double eps;
        if (ReferenceSystemInterface.Type.ICRS.equals(refSystem) || ReferenceSystemInterface.Type.J2000.equals(refSystem)) {
            eps = obliquity2000(jd);
        } else {
            eps = obliquity1980(jd);
        }
        return rotX(eps);
    }

    /**
     * It handles precession and the transformation between **equatorial** 
     * systems. 
     * 
     * This function includes also conversions between reference systems.
     * 
     * Notes:     
     * ------
     * Return matrix to transform equatorial coordinates from
     * *epoch1* to *epoch2* in either reference system FK4 or FK5. 
     * Or transform from epoch, FK4 or FK5 to ICRS or J2000 vice versa.
     * Note that each transformation between FK4 and one of the
     * other reference systems involves a conversion to
     * FK5 and therefore the epoch of observation will be involved.
     * Note that if no systems are entered and the one
     * epoch is &gt; 1984 and the other &lt; 1984, then the
     * transformation involves both sky reference systems FK4
     * and FK5.
     * 
     * @param epoch1 Epoch belonging to system S1 depending on the reference 
     * system either Besselian or Julian.
     * @param epoch2 Epoch belonging to system S2 depending on the reference 
     * system either Besselian or Julian.
     * @param s1 Input reference system
     * @param s2 Output reference system
     * @param epobs Epoch of observation. Only valid for conversions between
     * FK4 and FK5.
     * @return Rotation matrix to transform a position in one of the 
     * reference systems *S1* with *epoch1* to an equatorial system 
     * with equator and equinox at *epoch2* in reference system *S2*.
     */
    public static RealMatrix MatrixEpoch12Epoch2(double epoch1, double epoch2, final ReferenceSystemInterface.Type s1, final ReferenceSystemInterface.Type s2, Double epobs) {
        if (s1.equals(ReferenceSystemInterface.Type.FK5) && s2.equals(ReferenceSystemInterface.Type.FK5)) {
            return julianMatrixEpoch12Epoch2(epoch1, epoch2);
        } else if ((s1.equals(ReferenceSystemInterface.Type.FK4) || s1.equals(ReferenceSystemInterface.Type.FK4_NO_E)) && (s2.equals(ReferenceSystemInterface.Type.FK4) || s2.equals(ReferenceSystemInterface.Type.FK4_NO_E))) {
            return besselianMatrixEpoch12Epoch2(epoch1, epoch2);
        } else if ((s1.equals(ReferenceSystemInterface.Type.FK4) || s1.equals(ReferenceSystemInterface.Type.FK4_NO_E)) && s2.equals(ReferenceSystemInterface.Type.FK5)) {
            RealMatrix m1 = besselianMatrixEpoch12Epoch2(epoch1, 1950.0d);
            RealMatrix m2 = (epobs == null) ? FK42FK5Matrix() : FK42FK5Matrix(epobs);
            RealMatrix m3 = julianMatrixEpoch12Epoch2(2000.0d, epoch2);
            return m3.multiply(m2).multiply(m1);
        } else if (s1.equals(ReferenceSystemInterface.Type.FK5) && (s2.equals(ReferenceSystemInterface.Type.FK4) || s2.equals(ReferenceSystemInterface.Type.FK4_NO_E))) {
            RealMatrix m1 = julianMatrixEpoch12Epoch2(epoch1, 2000d);
            RealMatrix m2 = (epobs == null) ? FK52FK4Matrix() : FK52FK4Matrix(epobs);
            RealMatrix m3 = besselianMatrixEpoch12Epoch2(1950.0d, epoch2);
            return m3.multiply(m2).multiply(m1);
        } else if (s1.equals(ReferenceSystemInterface.Type.ICRS) && s2.equals(ReferenceSystemInterface.Type.ICRS)) {
            return MatrixUtils.createRealIdentityMatrix(3);
        } else if (s1.equals(ReferenceSystemInterface.Type.ICRS) && (s2.equals(ReferenceSystemInterface.Type.FK4) || s2.equals(ReferenceSystemInterface.Type.FK4_NO_E))) {
            RealMatrix m1 = ICRS2FK5Matrix();
            RealMatrix m2 = (epobs == null) ? FK52FK4Matrix() : FK52FK4Matrix(epobs);
            RealMatrix m3 = besselianMatrixEpoch12Epoch2(1950.0d, epoch2);
            return m3.multiply(m2).multiply(m1);
        } else if (s1.equals(ReferenceSystemInterface.Type.ICRS) && s2.equals(ReferenceSystemInterface.Type.FK5)) {
            RealMatrix m1 = ICRS2FK5Matrix();
            RealMatrix m2 = julianMatrixEpoch12Epoch2(2000.0d, epoch2);
            return m2.multiply(m1);
        } else if (s1.equals(ReferenceSystemInterface.Type.FK5) && s2.equals(ReferenceSystemInterface.Type.ICRS)) {
            RealMatrix m1 = julianMatrixEpoch12Epoch2(epoch1, 2000.0d);
            RealMatrix m2 = ICRS2FK5Matrix().transpose();
            return m2.multiply(m1);
        } else if ((s1.equals(ReferenceSystemInterface.Type.FK4) || s1.equals(ReferenceSystemInterface.Type.FK4_NO_E)) && s2.equals(ReferenceSystemInterface.Type.ICRS)) {
            RealMatrix m1 = besselianMatrixEpoch12Epoch2(epoch1, 1950.0d);
            RealMatrix m2 = (epobs == null) ? FK42FK5Matrix() : FK42FK5Matrix(epobs);
            RealMatrix m3 = ICRS2FK5Matrix().transpose();
            return m3.multiply(m2).multiply(m1);
        } else if (s1.equals(ReferenceSystemInterface.Type.J2000) && s2.equals(ReferenceSystemInterface.Type.J2000)) {
            RealMatrix m1 = IAU2006MatrixEpoch12Epoch2(epoch1, epoch2);
            return m1;
        } else if (s1.equals(ReferenceSystemInterface.Type.J2000) && s2.equals(ReferenceSystemInterface.Type.ICRS)) {
            RealMatrix m1 = IAU2006MatrixEpoch12Epoch2(epoch1, 2000.0d);
            RealMatrix m2 = ICRS2J2000Matrix().transpose();
            return m2.multiply(m1);
        } else if (s1.equals(ReferenceSystemInterface.Type.J2000) && s2.equals(ReferenceSystemInterface.Type.FK5)) {
            RealMatrix m1 = IAU2006MatrixEpoch12Epoch2(epoch1, 2000.0d);
            RealMatrix m2 = ICRS2J2000Matrix().transpose();
            RealMatrix m3 = ICRS2FK5Matrix();
            RealMatrix m4 = julianMatrixEpoch12Epoch2(2000.0d, epoch2);
            return m4.multiply(m3).multiply(m2).multiply(m1);
        } else if (s1.equals(ReferenceSystemInterface.Type.J2000) && (s2.equals(ReferenceSystemInterface.Type.FK4) || s2.equals(ReferenceSystemInterface.Type.FK4_NO_E))) {
            RealMatrix m1 = IAU2006MatrixEpoch12Epoch2(epoch1, 2000.0d);
            RealMatrix m2 = ICRS2J2000Matrix().transpose();
            RealMatrix m3 = ICRS2FK5Matrix();
            RealMatrix m4 = (epobs == null) ? FK52FK4Matrix() : FK52FK4Matrix(epobs);
            RealMatrix m5 = besselianMatrixEpoch12Epoch2(1950d, epoch2);
            return m5.multiply(m4).multiply(m3).multiply(m2).multiply(m1);
        } else if (s1.equals(ReferenceSystemInterface.Type.ICRS) && s2.equals(ReferenceSystemInterface.Type.J2000)) {
            RealMatrix m1 = ICRS2J2000Matrix();
            RealMatrix m2 = IAU2006MatrixEpoch12Epoch2(2000.0d, epoch2);
            return m2.multiply(m1);
        } else if (s1.equals(ReferenceSystemInterface.Type.FK5) && s2.equals(ReferenceSystemInterface.Type.J2000)) {
            RealMatrix m1 = julianMatrixEpoch12Epoch2(epoch1, 2000.0d);
            RealMatrix m2 = ICRS2FK5Matrix().transpose();
            RealMatrix m3 = ICRS2J2000Matrix();
            RealMatrix m4 = IAU2006MatrixEpoch12Epoch2(2000.0d, epoch2);
            return m4.multiply(m3).multiply(m2).multiply(m1);
        } else if ((s1.equals(ReferenceSystemInterface.Type.FK4) || s1.equals(ReferenceSystemInterface.Type.FK4_NO_E)) && s2.equals(ReferenceSystemInterface.Type.J2000)) {
            RealMatrix m1 = besselianMatrixEpoch12Epoch2(epoch1, 1950.0d);
            RealMatrix m2 = (epobs == null) ? FK52FK4Matrix().transpose() : FK52FK4Matrix(epobs).transpose();
            RealMatrix m3 = ICRS2FK5Matrix().transpose();
            RealMatrix m4 = ICRS2J2000Matrix();
            RealMatrix m5 = IAU2006MatrixEpoch12Epoch2(2000.0d, epoch2);
            return m5.multiply(m4).multiply(m3).multiply(m2).multiply(m1);
        } else {
            throw new JWcsError();
        }
    }
}
