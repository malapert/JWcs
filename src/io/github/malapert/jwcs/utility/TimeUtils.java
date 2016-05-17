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

import io.github.malapert.jwcs.proj.exception.JWcsError;
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
    public final static double convertEpochJulian2JD(final double jEpoch) {
        return (jEpoch - 2000.0d) * 365.25d + 2451545.0d;
    }

    /**
     * Convert a Julian date to a Julian epoch
     *
     * @param jd Julian date
     * @return a Julian epoch
     */
    public final static double convertJD2epochJulian(final double jd) {
        return 2000.0d + (jd - 2451545.0d) / 365.25d;
    }

    /**
     * Convert a Besselian epoch to a Julian date
     *
     * @param bEpoch Besselian epoch in format nnnn.nn
     * @return Julian date
     */
    public final static double convertEpochBessel2JD(final double bEpoch) {
        return (bEpoch - 1900.0d) * 365.242198781d + 2415020.31352d;
    }
    
    /**
     * Convert a julian date to a Besselian epoch
     *
     * @param jd julian date
     * @return a Besselian epoch
     */
    public final static double convertJD2epochBessel(final double jd) {
        return 1900.0d + (jd - 2415020.31352d) / 365.242198781d;
    }

    /**
     * Converts a JD to ISO date.
     *
     * @param julianDate julian date
     * @return ISO date
     */
    public final static String convertJulianDateToISO(final double julianDate) {

        // Calcul date calendrier Grégorien à partir du jour Julien éphéméride
        // Tous les calculs sont issus du livre de Jean MEEUS "Calcul astronomique"
        // Chapitre 3 de la société astronomique de France 3 rue Beethoven 75016 Paris
        // Tel 01 42 24 13 74
        // Valable pour les années négatives et positives mais pas pour les jours Juliens négatifs
        double jd = julianDate;
        double z, f, a, b, c, d, e, m, aux;
        final Date date = new Date();
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
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, (int) aux);

        final double hhd = aux - calendar.get(Calendar.DAY_OF_MONTH);
        aux = ((aux - calendar.get(Calendar.DAY_OF_MONTH)) * 24);

        calendar.set(Calendar.HOUR_OF_DAY, (int) aux);
        calendar.set(Calendar.MINUTE, (int) ((aux - calendar.get(Calendar.HOUR_OF_DAY)) * 60));

        // Calcul secondes
        final double mnd = 24 * hhd - calendar.get(Calendar.HOUR_OF_DAY);
        final double ssd = 60 * mnd - calendar.get(Calendar.MINUTE);
        final int ss = (int) (60 * ssd);
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

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    /**
     * Converts a MJD to ISO date.
     *
     * @param modifiedJulianDate modified julian date
     * @return ISO date
     */
    public final static String convertModifiedJulianDateToISO(final double modifiedJulianDate) {
        final double julianDate = modifiedJulianDate + 2400000.5;
        return convertJulianDateToISO(julianDate);
    }

    /**
     * Transforms an ISO date to a julian date.
     *
     * @param dateObs observation date as ISO format
     * @return a Julian date
     * @throws ParseException When the dateObs format is wrong
     */
    public final static double convertISOToJulianDate(final String dateObs) throws ParseException {
        final SimpleDateFormat sdf;
        if (dateObs.contains("T") && dateObs.contains(".")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
        } else if (dateObs.contains("T")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        } else if (dateObs.contains("-")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            sdf = new SimpleDateFormat("dd/MM/yy");
        }

        final Date date = sdf.parse(dateObs);

        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);

        final double extra = 100.0 * year + month - 190002.5;
        return 367.0 * year
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
    public final static double convertISOToModifiedJulianDate(final String dateObs) throws ParseException {
        final double jd = convertISOToJulianDate(dateObs);
        final double modifiedJulianDate = jd - 2400000.5;
        return modifiedJulianDate;
    }

    /**
     * Flexible epoch parser. 
     * The functions in this module have different input
     * parameters (Julian epoch, Besselian epochs, Julian dates) because the
     * algorithms came from different sources. What we needed was a routine that
     * could convert a string which represents a date in various formats, to
     * values for a Julian epoch, Besselian epochs and a Julian date. This
     * function returns these value for any valid input date.
     *
     * @param epoch Julian epoch, Besselian epochs, Julian dates
     * @return Returns in order Besselian epoch, Julian epoch and
     * Julian date.
     * @throws JWcsError Epochs should start by J, B or date format    
     * @throws JWcsError No prefix or cannot convert epoch to a number
     * @throws JWcsError Unknown prefix for epoch
     */
    public final static double[] epochs(final String epoch) {
        String spec = epoch;
        final int i = spec.indexOf('_');
        if (i != -1) {
            spec = spec.substring(0, i);
        }
       
        final String epochPrefix = extractPrefixFromEpoch(spec);
        final String epochValue = extractValueFromEpoch(epoch, epochPrefix);
        final String prefix = epochPrefix.toUpperCase();
        return computeEpochs(prefix, epochValue);
    }
    
    /**
     * Computes Besselian epoch, Julian epoch and Julian date from an epoch
     * @param epochPrefix epochs should start by J, B or date format
     * @param epochValue epoch value
     * @return Returns in order Besselian epoch, Julian epoch and
     * Julian date.
     */
    private static double[] computeEpochs(final String epochPrefix, final String epochValue) {
        double b;
        double jd;
        double j;
        double mjd;
        double rjd;
        switch (epochPrefix) {
            case "B":
            case "-B":
                b = Double.valueOf(epochValue);
                if (epochPrefix.equals("-B")) {
                    b *= -1.0;
                }
                jd = convertEpochBessel2JD(b);
                j = convertJD2epochJulian(jd);
                break;
            case "J":
            case "-J":
                j = Double.valueOf(epochValue);
                if (epochPrefix.equals("-J")) {
                    j *= -1.0;
                }
                jd = convertEpochJulian2JD(j);
                b = convertJD2epochBessel(jd);
                break;
            case "JD":
                jd = Double.valueOf(epochValue);
                b = convertJD2epochBessel(jd);
                j = convertJD2epochJulian(jd);
                break;
            case "MJD":
                mjd = Double.valueOf(epochValue);
                jd = mjd + 2400000.5d;
                b = convertJD2epochBessel(jd);
                j = convertJD2epochJulian(jd);
                break;
            case "RJD":
                rjd = Double.valueOf(epochValue);
                jd = rjd + 2400000d;
                b = convertJD2epochBessel(jd);
                j = convertJD2epochJulian(jd);
                break;
            case "F":
                final Object[] fd = fitsdate(epochValue);
                jd = jd((int)fd[0], (int)fd[1], (double)fd[2]);
                b = convertJD2epochBessel(jd);
                j = convertJD2epochJulian(jd);                
                break;
            default:
                throw new JWcsError("Unknown prefix for epoch : " + epochPrefix);
        }        
        return new double[]{b, j, jd};
    }
    
    /**
     * Extracts prefix from epoch
     * @param epoch Julian epoch, Besselian epochs, Julian dates
     * @return J, B or date format
     * @throws JWcsError Epochs should start by J, B or date format
     */
    private static String extractPrefixFromEpoch(final String epoch) {
        final String[] val = epoch.split("(\\d.*)");  
        if (val.length == 0) {
            throw new JWcsError("Epochs should start by J, B or date format");
        }
        return val[0];
    }
    
   /**
     * Extracts Value from epoch
     * @param epoch Julian epoch, Besselian epochs, Julian dates
     * @param prefix prefix from epoch
     * @return the epoch value
     * @throws JWcsError Cannot convert epoch to a number
     */
    private static String extractValueFromEpoch(final String epoch, final String prefix) {
        final String val = epoch.replace(prefix,"");
        if(val == null || val.isEmpty()) {
            throw new JWcsError("Cannot convert epoch to a number");
        }
        return val;
    }    
    
    /**
     * Converts a fits date into a [integer year, integer month, fractional day].
     * Given a string from a FITS file, try to parse it and 
     * convert the string into three parts: an integer year, an
     * integer month and a fractional day.
     * It processes the following formats:
     * <ul>
     *  <li>DD/MM/YY or DD/MM/19YY
     *  <li>YYYY-MM-DD
     *  <li>YYYY-MM-DDTHH:MM:SS
     * </ul>
     * @param date A string, representing a date in FITS format
     * @return Integer year, integer month, fractional day.
     */
    public final static Object[] fitsdate(final String date) {
        String dateTmp = date;
        String[] parts = date.split("/");
        if (parts.length==3) {
            final int year = Integer.parseInt(parts[2])%1900+1900;
            final int month = Integer.parseInt(parts[1]);
            final double fractionalDay = Double.parseDouble(parts[0]);
            return new Object[]{year,month,fractionalDay};
        }

        parts = dateTmp.split("T");
        double time;
        if (parts.length == 2) {
           dateTmp = parts[0];
           parts = parts[1].split(":");
           final double[] facts = new double[]{3600.0d, 60.0d, 1.0d};
           time = 0.0d;
           for (int i = 0; i < parts.length; i++) {
               time += Double.parseDouble(parts[i])*facts[i];
           }
        } else {
            time = 0.0d;
        }
        parts = dateTmp.split("-");
        return new Object[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Double.parseDouble(parts[2])+time/86400.0d};
    }
    
    /**
     * Computes the Julian day based on a date
     * @param year year
     * @param month month
     * @param dayNumber day number
     * @return the Julian day
     */
    public final static double jd(final int year, final int month, final double dayNumber) {
        int y = 0;
        int m = 0;
        int A;
        int B;
        double jd;
        if (month > 2){
            y = year;
            m = month;
        } else if (month == 1 || month == 2) {
            y = year - 1;
            m = month + 12;
        }
        final double calday = year + month/100.0d + dayNumber / 10000.0d;
        if (calday > 1582.1015){
            A = (int)(y/100.0);
            B = 2 - A + (int)(A/4.0);
        } else {
            B = 0;
        }    
        if (calday > 0.0229) {  // Dates after 29 February year 0
            jd = (int)(365.25*y) + (int)(30.6001*(m+1)) + dayNumber + 1720994.50d + B;
        } else {
            jd = (int)(365.25*y-0.75) + (int)(30.6001*(m+1)) + dayNumber + 1720994.50d + B;
        }    
        return jd;        
    }

}
