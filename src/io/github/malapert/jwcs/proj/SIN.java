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
package io.github.malapert.jwcs.proj;

import io.github.malapert.jwcs.proj.exception.BadProjectionParameterException;
import io.github.malapert.jwcs.utility.NumericalUtils;

/**
 * Slant orthographic.
 *
 * <p>
 * It represents the visual appearance of a sphere, e.g. a planet, when seen
 * from a great distance.
 * </p>
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 */
public class SIN extends ZenithalProjection {

    /**
     * Projection's name.
     */
    private static final String NAME_PROJECTION = "Slant orthographic";

    /**
     * Projection's description.
     */
    private static final String DESCRIPTION = "\u046F=%s \u03B7=%s";

    /**
     * DEfault value.
     */
    public static final double DEFAULT_VALUE = 0;

    private final double ksi;
    private final double eta;

    /**
     * Creates a new instance.
     *
     * @param crval1 Celestial longitude in degrees of the ﬁducial point
     * @param crval2 Celestial latitude in degrees of the ﬁducial point
     */
    public SIN(double crval1, double crval2) {
        this(crval1, crval2, DEFAULT_VALUE, DEFAULT_VALUE);
    }

    public SIN(double crval1, double crval2, double ksi, double eta) {
        super(crval1, crval2);
        this.ksi = ksi;
        this.eta = eta;
    }

    @Override
    public double[] project(double x, double y) throws BadProjectionParameterException {
        double xr = Math.toRadians(x);
        double yr = Math.toRadians(y);
        double phi, theta;
        if (NumericalUtils.equal(ksi, DEFAULT_VALUE, DOUBLE_TOLERANCE) && NumericalUtils.equal(eta, DEFAULT_VALUE, DOUBLE_TOLERANCE)) {
            double r_theta = Math.hypot(xr, yr);
            phi = NumericalUtils.aatan2(xr, -yr);
            theta = NumericalUtils.aacos(r_theta);
        } else {
            double a = Math.pow(ksi, 2) + Math.pow(eta, 2) + 1;
            double b = ksi * (xr - ksi) + eta * (yr - eta);
            double c = Math.pow((xr - ksi),2) + Math.pow((yr - eta),2) - 1;
            double theta1 = NumericalUtils.aasin((-b + Math.sqrt(b * b - a * c)) / a);
            double theta2 = NumericalUtils.aasin((-b - Math.sqrt(b * b - a * c)) / a);
            boolean isTheta1Valid = NumericalUtils.isInInterval(theta1, -HALF_PI, HALF_PI, DOUBLE_TOLERANCE);
            boolean isTheta2Valid = NumericalUtils.isInInterval(theta2, -HALF_PI, HALF_PI, DOUBLE_TOLERANCE);
            if (isTheta1Valid && isTheta2Valid) {
                double diffTheta1Pole = Math.abs(theta1 - HALF_PI);
                double diffTheta2Pole = Math.abs(theta2 - HALF_PI);
                theta = (diffTheta1Pole < diffTheta2Pole) ? theta1 : theta2;
            } else if (isTheta1Valid) {
                theta = theta1;
            } else if (isTheta2Valid) {
                theta = theta2;
            } else {
                throw new BadProjectionParameterException(("ksi = " + ksi + " , eta = " + eta));
            }

            phi = NumericalUtils.aatan2(xr - ksi * (1 - Math.sin(theta)), -(yr - eta * (1 - Math.sin(theta))));
        }

        double[] pos = {phi, theta};
        return pos;
    }

    @Override
    public double[] projectInverse(double phi, double theta) {
        phi = phiRange(phi);
        double x = Math.toDegrees(Math.cos(theta) * Math.sin(phi) + ksi * (1 - Math.sin(theta)));
        double y = -Math.toDegrees(Math.cos(theta) * Math.cos(phi) - eta * (1 - Math.sin(theta)));
        double[] coord = {x, y};
        return coord;
    }

    @Override
    public String getName() {
        return NAME_PROJECTION;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, NumericalUtils.round(this.ksi), NumericalUtils.round(this.eta));
    }
}
