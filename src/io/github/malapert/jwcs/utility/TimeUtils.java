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
package io.github.malapert.jwcs.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * TimeUtils class for handling time.
 *
 * Parts of this class have been traduced from Python to JAVA.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 1.0
 * @see <a href="http://www.astro.rug.nl/software/kapteyn/">Original code in
 * python</a>
 */
public abstract class TimeUtils {

    /**
     * Convert a Julian epoch to a Julian date
     *
     * @param jEpoch Julian epoch (in format nnnn.nn)
     * @return Julian date
     */
    public final static double epochJulian2JD(float jEpoch) {
        return (jEpoch - 2000.0d) * 365.25d + 2451545.0d;
    }

    /**
     * Convert a Besselian epoch to a Julian date
     *
     * @param bEpoch Besselian epoch in format nnnn.nn
     * @return Julian date
     */
    public final static double epochBessel2JD(float bEpoch) {
        return (bEpoch - 1900.0) * 365.242198781 + 2415020.31352;
    }

    /**
     * Converts a JD to ISO date.
     *
     * @param julianDate julian date
     * @return ISO date
     */
    public static String julianDateToISO(final double julianDate) {

        // Calcul date calendrier Grégorien à partir du jour Julien éphéméride
        // Tous les calculs sont issus du livre de Jean MEEUS "Calcul astronomique"
        // Chapitre 3 de la société astronomique de France 3 rue Beethoven 75016 Paris
        // Tel 01 42 24 13 74
        // Valable pour les années négatives et positives mais pas pour les jours Juliens négatifs
        double jd = julianDate;
        double z, f, a, b, c, d, e, m, aux;
        Date date = new Date();
        jd += 0.5;
        z = Math.floor(jd);
        f = jd - z;

        if (z >= 2299161.0) {
            a = Math.floor((z - 1867216.25) / 36524.25);
            a = z + 1 + a - Math.floor(a / 4);
        } else {
            a = z;
        }

        b = a + 1524;
        c = Math.floor((b - 122.1) / 365.25);
        d = Math.floor(365.25 * c);
        e = Math.floor((b - d) / 30.6001);
        aux = b - d - Math.floor(30.6001 * e) + f;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, (int) aux);

        double hhd = aux - calendar.get(Calendar.DAY_OF_MONTH);
        aux = ((aux - calendar.get(Calendar.DAY_OF_MONTH)) * 24);

        calendar.set(Calendar.HOUR_OF_DAY, (int) aux);
        calendar.set(Calendar.MINUTE, (int) ((aux - calendar.get(Calendar.HOUR_OF_DAY)) * 60));

        // Calcul secondes
        double mnd = (24 * hhd) - calendar.get(Calendar.HOUR_OF_DAY);
        double ssd = (60 * mnd) - calendar.get(Calendar.MINUTE);
        int ss = (int) (60 * ssd);
        calendar.set(Calendar.SECOND, ss);

        if (e < 13.5) {
            m = e - 1;
        } else {
            m = e - 13;
        }
        // Se le resta uno al mes por el manejo de JAVA, donde los meses empiezan en 0.
        calendar.set(Calendar.MONTH, (int) m - 1);
        if (m > 2.5) {
            calendar.set(Calendar.YEAR, (int) (c - 4716));
        } else {
            calendar.set(Calendar.YEAR, (int) (c - 4715));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    /**
     * Converts a MJD to ISO date.
     *
     * @param modifiedJulianDate modified julian date
     * @return ISO date
     */
    public static String modifiedJulianDateToISO(final double modifiedJulianDate) {
        final double julianDate = modifiedJulianDate + 2400000.5;
        return julianDateToISO(julianDate);
    }


    /**
     * Transforms an ISO date to a julian date.
     *
     * @param dateObs observation date as ISO format
     * @return a Julian date
     * @throws ParseException When the dateObs format is wrong
     */
    public static double ISOToJulianDate(String dateObs) throws ParseException {
        SimpleDateFormat sdf;
        if (dateObs.contains("T") && dateObs.contains(".")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
        } else if (dateObs.contains("T")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        } else if (dateObs.contains("-")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            sdf = new SimpleDateFormat("dd/MM/yy");
        }

        Date date = sdf.parse(dateObs);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        double extra = (100.0 * year) + month - 190002.5;
        return (367.0 * year)
                - (Math.floor(7.0 * (year + Math.floor((month + 9.0) / 12.0)) / 4.0))
                + Math.floor((275.0 * month) / 9.0)
                + day + ((hour + ((minute + (second / 60.0)) / 60.0)) / 24.0)
                + 1721013.5 - ((0.5 * extra) / Math.abs(extra)) + 0.5;
    }
    
    /**
     * Transforms an ISO date to a modified julian date.
     *
     * @param dateObs observation date as ISO
     * @return a Julian date
     * @throws ParseException When the dateObs format is wrong
     */
    public static double ISOToModifiedJulianDate(String dateObs) throws ParseException {
        double jd = ISOToJulianDate(dateObs);
        double modifiedJulianDate = jd - 2400000.5;
        return modifiedJulianDate;
    }

}
