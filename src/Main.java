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

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.github.malapert.jwcs.*;
import io.github.malapert.jwcs.coordsystem.Ecliptic;
import io.github.malapert.jwcs.coordsystem.Equatorial;
import io.github.malapert.jwcs.coordsystem.FK4;
import io.github.malapert.jwcs.coordsystem.FK4_NO_E;
import io.github.malapert.jwcs.coordsystem.FK5;
import io.github.malapert.jwcs.coordsystem.ICRS;
import io.github.malapert.jwcs.coordsystem.J2000;
import static io.github.malapert.jwcs.coordsystem.CoordinateReferenceFrame.ReferenceFrame.FK5;
import static io.github.malapert.jwcs.coordsystem.CoordinateReferenceFrame.ReferenceFrame.J2000;
import io.github.malapert.jwcs.coordsystem.SkyPosition;
import io.github.malapert.jwcs.coordsystem.Crs;
import io.github.malapert.jwcs.coordsystem.gui.ConvertSelectionPanel;
import io.github.malapert.jwcs.proj.exception.JWcsException;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import io.github.malapert.jwcs.proj.gui.ProjectionSelectionPanel;
import io.github.malapert.jwcs.utility.HeaderFitsReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.util.Cursor;
import io.github.malapert.jwcs.coordsystem.CoordinateReferenceFrame;

/**
 * The main class.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public class Main {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    private static final int DEFAULT_EXTENSION = 0;

    /**
     * List of programs
     */
    private enum PROG {

        SKY_CONVERTER(""),
        PROJECT(""),
        UNPROJECT(""),
        GUI(null);

        private String commandLine;

        PROG(String commandLine) {
            this.commandLine = commandLine;
        }

        public String getCommandLine() {
            return this.commandLine;
        }

        public void setCommandLine(String commandLine) {
            this.commandLine = commandLine;
        }

    }

    /**
     * Exit code.
     */
    private enum EXIT {

        OK(0),
        USER_INPUT_ERROR(1),
        NO_EXIT(100);

        private final int code;

        EXIT(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    /**
     * Usage.
     */
    private static void usage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage: java -jar JWcs.jar -g PROG [OPTIONS]\n");
        sb.append("    or java -jar JWcs.jar --file HDR_FILE --project X,Y [OPTIONS]\n");
        sb.append("    or java -jar JWcs.jar --file HDR_FILE --unproject RA,DEC [OPTIONS]\n");
        sb.append("    or java -jar JWcs.jar --file HDR_FILE --convert RA,DEC --to SYS_TARGET [OPTIONS]\n");
        sb.append("    or java -jar JWcs.jar --convert RA,DEC --from SYS_ORGIN --to SYS_TARGET [OPTIONS]\n");
        sb.append("           where:\n");
        sb.append("               - PROG: either projection or converter\n");
        sb.append("               - HDR_FILE: Header FITS or FITS file\n");
        sb.append("               - X: pixel coordinate along X axis on the camera (starts to 1) \n");
        sb.append("               - Y: pixel coordinate along Y axis on the camera (starts to 1) \n");
        sb.append("               - RA: sky coordinate\n");
        sb.append("               - DEC: sky coordinate\n");
        sb.append("               - SYS_ORIGIN: sky system of the sky coordinates\n");
        sb.append("               - SYS_TARGET: convert sky coordinates to the SYS_TARGET\n\n");

        sb.append("           SYS_ORIGIN or SYS_TARGET can be:\n");
        sb.append("               - GALACTIC\n");
        sb.append("               - SUPER_GALACTIC\n");
        sb.append("               - EQUATORIAL\n");
        sb.append("               - EQUATORIAL(ICRS())\n");
        sb.append("               - EQUATORIAL(J2000())\n");
        sb.append("               - EQUATORIAL(FK5())\n");
        sb.append("               - EQUATORIAL(FK5(<equinox>))\n");
        sb.append("               - EQUATORIAL(FK4())\n");
        sb.append("               - EQUATORIAL(FK4(<equinox>))\n");
        sb.append("               - EQUATORIAL(FK4(<equinox>,<epoch>))\n");
        sb.append("               - EQUATORIAL(FK4_NO_E())\n");
        sb.append("               - EQUATORIAL(FK4_NO_E(<equinox>))\n");
        sb.append("               - EQUATORIAL(FK4_NO_E(<equinox>,<epoch>))\n");
        sb.append("               - ECLIPTIC\n");
        sb.append("               - ECLIPTIC(ICRS())\n");
        sb.append("               - ECLIPTIC(J2000())\n");
        sb.append("               - ECLIPTIC(FK5())\n");
        sb.append("               - ECLIPTIC(FK5(<equinox>))\n");
        sb.append("               - ECLIPTIC(FK4())\n");
        sb.append("               - ECLIPTIC(FK4(<equinox>))\n");
        sb.append("               - ECLIPTIC(FK4(<equinox>,<epoch>))\n");
        sb.append("               - ECLIPTIC(FK4_NO_E())\n");
        sb.append("               - ECLIPTIC(FK4_NO_E(<equinox>))\n");
        sb.append("               - ECLIPTIC(FK4_NO_E(<equinox>,<epoch>))\n");

        sb.append("\nProjection and sky conversion library\n");
        sb.append("\n");
        sb.append("Mandatory arguments to long options are mandatory for short options too.\n");
        sb.append("  -p, --project            Project a pixel to the sky\n");
        sb.append("  -u, --unproject          Unproject a point on the sky to 2D \n");
        sb.append("  -f, --file               Header file or Fits file starting by a scheme (ex: file://, http://)\n");
        sb.append("  -s, --from               Origin sky system\n");
        sb.append("  -t, --to                 Target sky system\n");
        sb.append("  -c, --convert            Convert a sky coordinate from a sky system to antoher one\n");
        sb.append("  -g, --gui                Display projection or converter with a GUI\n");
        sb.append("  -h, --help               Display this help and exit\n");
        sb.append("\n");
        sb.append("OPTIONS are the following:\n");
        sb.append("  -d, --debug              Sets the DEBUG level : ALL,CONFIG,FINER,FINEST,INFO,OFF,SEVERE,WARNING\n");
        sb.append("  -e, --extension          HDU number starting at 0 when --file argument is used. If not set, 0 is default\n");
        sb.append("  -r, --precision          Precision such as %.6f. By default, precision is set to %.15f\n");

        System.out.println(sb.toString());
        System.exit(EXIT.OK.getCode());
    }

    /**
     * Project camera to sky from command line.
     *
     * @param pos position in the camera frame
     * @param file file
     * @param extension HDU number when file is a FITS file
     * @param precision precision such as %.15f
     * @throws ProjectionException an error during the projection
     * @throws JWcsException JWcs error
     * @throws IOException an error when loading the FITS file
     * @throws URISyntaxException an error when loading the FITS file 
     */
    private static void projectToSkyFromCommandLine(final String pos, final String file, final int extension, final String precision) throws ProjectionException, JWcsException, IOException, URISyntaxException {
        Map<String, String> keyMap = new HashMap();
        String[] argumentsPos = pos.split(",");
        if (argumentsPos.length != 2) {
            throw new IllegalArgumentException("The position " + pos + " is not correct");
        }
        if (file == null) {
            throw new IllegalArgumentException("--file argument is required");
        } else {
            URI uri = new URI(file);
            try {
                Fits fits = new Fits(uri.toURL());
                Header hdr = fits.getHDU(extension).getHeader();
                Cursor c = hdr.iterator();
                while (c.hasNext()) {
                    HeaderCard card = (HeaderCard) c.next();
                    keyMap.put(card.getKey(), card.getValue());
                }
            } catch (nom.tam.fits.FitsException | IOException ex) {
                HeaderFitsReader hdr = new HeaderFitsReader(uri.toURL());
                List<List<String>> listKeywords = hdr.readKeywords();
                listKeywords.stream().forEach((keywordLine) -> {
                    keyMap.put(keywordLine.get(0), keywordLine.get(1));
                });
            }
        }
        JWcsMap wcs = new JWcsMap(keyMap);
        wcs.doInit();
        LOG.log(Level.INFO, "Executing pix2wcs(%s,%s)", argumentsPos);
        double[] result = wcs.pix2wcs(Double.valueOf(argumentsPos[0]), Double.valueOf(argumentsPos[1]));

        System.out.printf("(ra,dec)=(" + precision + ", " + precision + ")\n", result[0], result[1]);
        LOG.log(Level.INFO, "(ra,dec) = (%s,%s)", result);

    }

    /**
     * Project sky to camera from command line.
     *
     * @param pos Sky position
     * @param file Header file
     * @param extension HDU number when file is a FITS file
     * @param precision precision such as %.15f
     */
    private static void projectToCameraFromCommandLine(final String pos, final String file, final int extension, final String precision) throws ProjectionException, JWcsException, IOException, URISyntaxException {
        Map<String, String> keyMap = new HashMap();
        String[] argumentsPos = pos.split(",");
        if (argumentsPos.length != 2) {
            throw new IllegalArgumentException("The position " + pos + " is not correct");
        }
        if (file == null) {
            throw new IllegalArgumentException("--file argument is required");
        } else {
            URI uri = new URI(file);
            try {
                Fits fits = new Fits(uri.toURL());
                Header hdr = fits.getHDU(extension).getHeader();
                Cursor c = hdr.iterator();
                while (c.hasNext()) {
                    HeaderCard card = (HeaderCard) c.next();
                    keyMap.put(card.getKey(), card.getValue());
                }
            } catch (nom.tam.fits.FitsException | IOException ex) {
                HeaderFitsReader hdr = new HeaderFitsReader(uri.toURL());
                List<List<String>> listKeywords = hdr.readKeywords();
                listKeywords.stream().forEach((keywordLine) -> {
                    keyMap.put(keywordLine.get(0), keywordLine.get(1));
                });
            }
        }
        JWcsMap wcs = new JWcsMap(keyMap);
        wcs.doInit();
        LOG.log(Level.INFO, "Executing wcs2pix(%s,%s)", argumentsPos);
        double[] result = wcs.wcs2pix(Double.valueOf(argumentsPos[0]), Double.valueOf(argumentsPos[1]));
        System.out.printf("(x,y)=(" + precision + ", " + precision + ")\n", result[0], result[1]);
        LOG.log(Level.INFO, "(x,y) = (%s,%s)", result);
    }

    /**
     * Checks if the sky system has a reference system.
     *
     * @param skySystem sky system
     * @return True when the sky system has a reference frame
     */
    private static boolean hasReferenceFrame(final String skySystem) {
        return skySystem.contains("(");
    }

    /**
     * Extract reference frame name from sky system
     *
     * @param skySystem sky system
     * @return Reference frame name
     */
    private static String extractReferenceFrame(final String skySystem) {
        return skySystem.substring(skySystem.indexOf('(') + 1, skySystem.lastIndexOf(')'));
    }

    /**
     * Extracts parameters from the reference frame.
     *
     * @param referenceFrame reference frame
     * @return parameters of the reference frame
     */
    private static String[] extractReferenceFrameParameter(final String referenceFrame) {
        String parameters = referenceFrame.substring(referenceFrame.indexOf('(') + 1, referenceFrame.lastIndexOf(')'));
        return (parameters.isEmpty()) ? null : parameters.split(",");
    }

    /**
     * Gets the reference frame with parameters
     *
     * @param refFrame reference frame type
     * @param parameters parameters of the reference frame
     * @return the reference frame
     */
    private static CoordinateReferenceFrame getReferenceFrame(final CoordinateReferenceFrame.ReferenceFrame refFrame, final String[] parameters) {
        CoordinateReferenceFrame ref;
        switch (refFrame) {
            case ICRS:
                ref = new ICRS();
                break;
            case FK5:
                if (parameters == null) {
                    ref = new FK5();
                } else {
                    ref = new FK5(parameters[0]);
                }
                break;
            case FK4:
                if (parameters != null) {
                    switch (parameters.length) {
                        case 1:
                            ref = new FK4(parameters[0]);
                            break;
                        case 2:
                            ref = new FK4(parameters[0], parameters[1]);
                            break;
                        default:
                            throw new IllegalArgumentException("");
                    }
                } else {
                    ref = new FK4();
                }
                break;
            case FK4_NO_E:
                if (parameters != null) {
                    switch (parameters.length) {
                        case 1:
                            ref = new FK4_NO_E(parameters[0]);
                            break;
                        case 2:
                            ref = new FK4_NO_E(parameters[0], parameters[1]);
                            break;
                        default:
                            throw new IllegalArgumentException("");
                    }
                } else {
                    ref = new FK4_NO_E();
                }
                break;
            case J2000:
                ref = new J2000();
                break;
            default:
                throw new IllegalArgumentException("Reference frame not supported");
        }
        return ref;
    }

    /**
     * Gets the sky system.
     *
     * @param skySystem the sky system name
     * @return the Crs object
     */
    private static Crs getSkySystem(final String skySystem) {
        Crs result;
        if (hasReferenceFrame(skySystem)) {
            String skySystemName = skySystem.substring(0, skySystem.indexOf('('));
            String refFrameStr = extractReferenceFrame(skySystem);
            String refFrameName = refFrameStr.substring(0, refFrameStr.indexOf('('));
            CoordinateReferenceFrame.ReferenceFrame refFrame = CoordinateReferenceFrame.ReferenceFrame.valueOf(refFrameName);
            String[] parameters = extractReferenceFrameParameter(refFrameStr);
            CoordinateReferenceFrame refSystem = getReferenceFrame(refFrame, parameters);
            result = Crs.createCrsFromCoordinateSystem(Crs.CoordinateSystem.valueOf(skySystemName));
            switch (result.getCoordinateSystem()) {
                case EQUATORIAL:
                    ((Equatorial) result).setCoordinateReferenceFrame(refSystem);
                    break;
                case ECLIPTIC:
                    ((Ecliptic) result).setCoordinateReferenceFrame(refSystem);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown sky system");
            }
        } else {
            result = Crs.createCrsFromCoordinateSystem(Crs.CoordinateSystem.valueOf(skySystem));
        }
        return result;
    }

    /**
     * Converts a position frome a sky system to another one.
     *
     * @param pos Sky position
     * @param file Fits or header file
     * @param from source sky system
     * @param to target sky system
     * @param precision precision such as %.15f
     * @throws URISyntaxException Cannot retrieve the Header file
     * @throws IOException Header file not found
     * @throws JWcsException JWS Error
     * @throws IllegalArgumentException Either --file argument or --from and --to arguments are required
     */
    private static void convertFromCommandLine(final String pos, final String file, final String from, final String to, final int extension, final String precision) throws URISyntaxException, IOException, JWcsException {
        Map<String, String> keyMap = new HashMap();
        Crs skySystemFrom;
        if (file == null && from == null && to == null) {
            throw new IllegalArgumentException("Either --file argument or --from and --to arguments are required");
        } else if (file != null) {
            URI uri = new URI(file);
            try {
                Fits fits = new Fits(uri.toURL());
                Header hdr = fits.getHDU(extension).getHeader();
                Cursor c = hdr.iterator();
                while (c.hasNext()) {
                    HeaderCard card = (HeaderCard) c.next();
                    keyMap.put(card.getKey(), card.getValue());
                }
            } catch (nom.tam.fits.FitsException | IOException ex) {
                HeaderFitsReader hdr = new HeaderFitsReader(uri.toURL());
                List<List<String>> listKeywords = hdr.readKeywords();
                listKeywords.stream().forEach((keywordLine) -> {
                    keyMap.put(keywordLine.get(0), keywordLine.get(1));
                });
            }
            JWcsMap wcs = new JWcsMap(keyMap);
            wcs.doInit();
            skySystemFrom = wcs.getCrs();

        } else {
            skySystemFrom = getSkySystem(from);
        }
        String skySystemTarget = to;
        double[] skyPos = Arrays.stream(pos.split(","))
                .mapToDouble(Double::parseDouble)
                .toArray();
        Crs skySystemTo = getSkySystem(skySystemTarget);
        LOG.log(Level.INFO, "Executing convertTo(%s, %s,%s)", new Object[]{skySystemTo, skyPos[0], skyPos[1]});
        SkyPosition skyPosition = skySystemFrom.convertTo(skySystemTo, skyPos[0], skyPos[1]);
        System.out.printf(precision + ", " + precision + "\n", skyPosition.getLongitude(), skyPosition.getLatitude());
        LOG.log(Level.INFO, "(longitude,latitude) = (%s,%s)", skyPosition);
    }

    /**
     * Sets debug level.
     *
     * @param rootLogger the root logger
     * @param levelArg the debug level
     */
    private static void setDebug(final Logger rootLogger, final Getopt levelArg) {
        Level levelInfo = Level.parse(levelArg.getOptarg());
        Handler[] handlers = rootLogger.getHandlers();
        rootLogger.setLevel(levelInfo);
        for (Handler h : handlers) {
            if (h instanceof FileHandler) {
                h.setLevel(levelInfo);
            } else if (h instanceof ConsoleHandler) {
                h.setLevel(levelInfo);
            }
        }
        rootLogger.setLevel(levelInfo);
    }

    /**
     * The program starts with this method.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EXIT returnedCode = EXIT.OK;
        boolean isGui = false;
        int c;
        int extension = DEFAULT_EXTENSION;
        String arg;
        String from = null;
        String to = null;
        String file = null;
        String precision = "%.15f";
        String progGui = null;
        List<PROG> progChoice = new ArrayList<>();
        LongOpt[] longopts = new LongOpt[11];
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF);
         
        longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[1] = new LongOpt("gui", LongOpt.REQUIRED_ARGUMENT, null, 'g');
        longopts[2] = new LongOpt("project", LongOpt.REQUIRED_ARGUMENT, null, 'p');
        longopts[3] = new LongOpt("unproject", LongOpt.REQUIRED_ARGUMENT, null, 'u');
        longopts[4] = new LongOpt("convert", LongOpt.REQUIRED_ARGUMENT, null, 'c');
        longopts[5] = new LongOpt("debug", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        longopts[6] = new LongOpt("file", LongOpt.REQUIRED_ARGUMENT, null, 'f');
        longopts[7] = new LongOpt("from", LongOpt.REQUIRED_ARGUMENT, null, 's');
        longopts[8] = new LongOpt("to", LongOpt.REQUIRED_ARGUMENT, null, 't');
        longopts[9] = new LongOpt("extension", LongOpt.REQUIRED_ARGUMENT, null, 'e');
        longopts[10] = new LongOpt("precision", LongOpt.REQUIRED_ARGUMENT, null, 'r');
        // 
        Getopt g = new Getopt("JWcs", args, "-::p:u:c:d:f:s:t:e:r:g:h;", longopts);
        g.setOpterr(true);
        //
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'r':
                    precision = g.getOptarg();
                    break;
                case 'e':
                    try {
                        extension = Integer.valueOf(g.getOptarg());
                    } catch (NumberFormatException ex) {
                        System.err.println(ex);
                        returnedCode = EXIT.USER_INPUT_ERROR;
                    }
                    break;
                case 'f':
                    file = g.getOptarg();
                    break;
                case 's':
                    from = g.getOptarg();
                    break;
                case 't':
                    to = g.getOptarg();
                    break;
                case 'g':
                    progChoice.add(PROG.GUI);
                    progGui = g.getOptarg();
                    break;
                case 'c':
                    PROG.SKY_CONVERTER.setCommandLine(g.getOptarg());
                    progChoice.add(PROG.SKY_CONVERTER);
                    break;
                case 'd':
                    setDebug(rootLogger, g);
                    break;
                case 'p':
                    PROG.PROJECT.setCommandLine(g.getOptarg());
                    progChoice.add(PROG.PROJECT);
                    break;
                case 'u':
                    PROG.UNPROJECT.setCommandLine(g.getOptarg());
                    progChoice.add(PROG.UNPROJECT);
                    break;
                case 'h':
                    usage();
                    break;
                case ':':
                    System.err.println("You need an argument for option "
                            + (char) g.getOptopt());
                    LOG.log(Level.SEVERE, "You need an argument for option %s", new Object[]{g.getOptopt()});
                    returnedCode = EXIT.USER_INPUT_ERROR;
                    break;
                //
                case '?':
                    System.err.println("The option '" + (char) g.getOptopt()
                            + "' is not valid");
                    LOG.log(Level.SEVERE, "The option %s is not valid", new Object[]{g.getOptopt()});
                    returnedCode = EXIT.USER_INPUT_ERROR;
                    break;
                //
                default:
                    System.err.println("getopt() returned " + c);
                    LOG.log(Level.SEVERE, "getopt() returned %s", new Object[]{c});
                    returnedCode = EXIT.USER_INPUT_ERROR;
                    break;
            }
        }
        for (int i = g.getOptind(); i < args.length; i++) {
            System.err.println("Non option argv element: " + args[i] + "\n");
            LOG.log(Level.SEVERE, "Non option argv element: %s", new Object[]{args[i]});
            returnedCode = EXIT.USER_INPUT_ERROR;
        }
        if (EXIT.OK != returnedCode) {
            System.exit(returnedCode.getCode());
        }

        if (progChoice.size() != 1) {
            System.err.println("You need to select only one of the available program : gui, project, unproject, converter");
            returnedCode = EXIT.USER_INPUT_ERROR;
            usage();
            System.exit(returnedCode.getCode());
        }

        try {

            PROG prog = progChoice.get(0);
            switch (prog) {
                case GUI:
                    if (null != progGui) {
                        switch (progGui) {
                            case "projection":
                                java.awt.EventQueue.invokeLater(() -> {
                                    try {
                                        ProjectionSelectionPanel.createWindow();
                                    } catch (IOException ex) {
                                        System.err.println("Error : "+ex.getMessage());
                                        LOG.log(Level.SEVERE, null, ex);                                        
                                    }
                                });
                                break;
                            case "converter":
                                java.awt.EventQueue.invokeLater(() -> {
                                    ConvertSelectionPanel.createWindow();
                                });
                                break;
                            default:
                                throw new IllegalArgumentException("The GUI program " + progGui + " is not supported");
                        }
                    }
                    isGui = true;
                    break;
                case PROJECT:
                    projectToSkyFromCommandLine(prog.getCommandLine(), file, extension, precision);
                    break;
                case UNPROJECT:
                    projectToCameraFromCommandLine(prog.getCommandLine(), file, extension, precision);
                    break;
                case SKY_CONVERTER:
                    convertFromCommandLine(prog.getCommandLine(), file, from, to, extension, precision);
                    break;
                default:
                    throw new IllegalArgumentException(prog.name() + " not supported");
            }
            returnedCode = EXIT.OK;
        } catch (JWcsException | IOException | RuntimeException | URISyntaxException ex) {
            LOG.log(Level.SEVERE, "Error : ", ex.getMessage());
            System.err.println("Error: " + ex.getMessage());
            returnedCode = EXIT.USER_INPUT_ERROR;
        } finally {
            if (!isGui) {
                System.exit(returnedCode.getCode());
            }
        }
    }
}
