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
import io.github.malapert.jwcs.coordsystem.ReferenceSystemInterface;
import static io.github.malapert.jwcs.coordsystem.ReferenceSystemInterface.Type.FK5;
import io.github.malapert.jwcs.coordsystem.SkyPosition;
import io.github.malapert.jwcs.coordsystem.SkySystem;
import io.github.malapert.jwcs.gui.MapLine;
import io.github.malapert.jwcs.gui.ProjectionSelectionPanel;
import io.github.malapert.jwcs.gui.UngenerateImporter;
import io.github.malapert.jwcs.proj.exception.JWcsException;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import io.github.malapert.jwcs.utility.HeaderFitsReader;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import javax.swing.JFrame;

/**
 * The main class.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public class Main {

    /**
     * Countries border.
     */
    private static final String CONTINENTS_PATH = "/io/github/malapert/jwcs/gui/continents.ung";

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

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
        sb.append("Usage: java -jar JWcs.jar -g [OPTIONS]\n");
        sb.append("    or java -jar JWcs.jar --project \"HDR_FILE X Y\" [OPTIONS]\n");
        sb.append("    or java -jar JWcs.jar --unproject \"HDR_FILE RA DEC\" [OPTIONS]\n");
        sb.append("    or java -jar JWcs.jar --convert \"RA,DEC SYS_ORGIN SYS_TARGET\" [OPTIONS]\n");
        sb.append("           where:\n");
        sb.append("               - HDR_FILE: Header FITS file\n");
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
        sb.append("  -c, --convert            Convert a sky coordinate from a sky system to antoher one\n");
        sb.append("  -g, --gui                Display projections with a GUI\n");
        sb.append("  -h, --help               Display this help and exit\n");
        sb.append("\n");
        sb.append("OPTIONS are the following:\n");
        sb.append("  -d, --debug              Sets the DEBUG level : ALL,CONFIG,FINER,FINEST,INFO,OFF,SEVERE,WARNING\n");

        System.out.println(sb.toString());
        System.exit(EXIT.OK.getCode());
    }

    /**
     * Project camera to sky from command line.
     *
     * @param commandLine options from command line
     */
    private static void projectToSkyFromCommandLine(final String commandLine) throws ProjectionException, JWcsException, IOException {
        String[] arguments = commandLine.split("\\s+");
        if (arguments.length != 3) {
            System.err.println("3 arguments are needed, please use -h option to have a look on the help");
            LOG.log(Level.SEVERE, "3 arguments are needed, please use -h option to have a look on the help");
            System.exit(EXIT.USER_INPUT_ERROR.getCode());
        }
        HeaderFitsReader hdr = new HeaderFitsReader(arguments[0]);
        List<List<String>> listKeywords = hdr.readKeywords();
        Map<String, String> keyMap = new HashMap();
        listKeywords.stream().forEach((keywordLine) -> {
            keyMap.put(keywordLine.get(0), keywordLine.get(1));
        });
        JWcsMap wcs = new JWcsMap(keyMap);
        wcs.doInit();
        LOG.log(Level.INFO, "Executing pix2wcs(%s,%s)", arguments);
        double[] result = wcs.pix2wcs(Double.valueOf(arguments[1]), Double.valueOf(arguments[2]));
        System.out.println("(ra,dec)=(" + result[0] + ", " + result[1] + ")");
        LOG.log(Level.INFO, "(ra,dec) = (%s,%s)", result);

    }

    /**
     * Project sky to camera from command line.
     *
     * @param String commandLine Options from command line
     */
    private static void projectToCameraFromCommandLine(final String commandLine) throws ProjectionException, JWcsException, IOException {

        String[] arguments = commandLine.split("\\s+");
        if (arguments.length != 3) {
            System.err.println("3 arguments are needed, please use -h option to have a look on the help");
            LOG.log(Level.SEVERE, "3 arguments are needed, please use -h option to have a look on the help");
            System.exit(EXIT.USER_INPUT_ERROR.getCode());
        }
        HeaderFitsReader hdr = new HeaderFitsReader(arguments[0]);
        List<List<String>> listKeywords = hdr.readKeywords();
        Map<String, String> keyMap = new HashMap();
        listKeywords.stream().forEach((keywordLine) -> {
            keyMap.put(keywordLine.get(0), keywordLine.get(1));
        });
        JWcsMap wcs = new JWcsMap(keyMap);
        wcs.doInit();
        LOG.log(Level.INFO, "Executing wcs2pix(%s,%s)", arguments);
        double[] result = wcs.wcs2pix(Double.valueOf(arguments[1]), Double.valueOf(arguments[2]));
        System.out.println("(x,y)=(" + result[0] + ", " + result[1] + ")");
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
        return skySystem.substring(skySystem.indexOf("(") + 1, skySystem.lastIndexOf(")"));
    }

    /**
     * Extracts parameters from the reference frame.
     *
     * @param referenceFrame reference frame
     * @return parameters of the reference frame
     */
    private static String[] extractReferenceFrameParameter(final String referenceFrame) {
        String parameters = referenceFrame.substring(referenceFrame.indexOf("(") + 1, referenceFrame.lastIndexOf(")"));
        return (parameters.isEmpty()) ? null : parameters.split(",");
    }

    /**
     * Gets the reference frame with parameters
     *
     * @param refFrame reference frame type
     * @param parameters parameters of the reference frame
     * @return the reference frame
     */
    private static ReferenceSystemInterface getReferenceFrame(final ReferenceSystemInterface.Type refFrame, final String[] parameters) {
        ReferenceSystemInterface ref;
        switch (refFrame) {
            case ICRS:
                ref = new ICRS();
                break;
            case FK5:
                if (parameters == null) {
                    ref = new FK5();
                } else {
                    ref = new FK5(Float.valueOf(parameters[0]));
                }
                break;
            case FK4:
                if (parameters != null) {
                    switch (parameters.length) {
                        case 1:
                            ref = new FK4(Float.valueOf(parameters[0]));
                            break;
                        case 2:
                            ref = new FK4(Float.valueOf(parameters[0]), Float.valueOf(parameters[1]));
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
                            ref = new FK4_NO_E(Float.valueOf(parameters[0]));
                            break;
                        case 2:
                            ref = new FK4_NO_E(Float.valueOf(parameters[0]), Float.valueOf(parameters[1]));
                            break;
                        default:
                            throw new IllegalArgumentException("");
                    }
                } else {
                    ref = new FK4_NO_E();
                }
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
     * @return the SkySystem object
     */
    private static SkySystem getSkySystem(final String skySystem) {
        SkySystem result;
        if (hasReferenceFrame(skySystem)) {
            String skySystemName = skySystem.substring(0, skySystem.indexOf("("));
            String refFrameStr = extractReferenceFrame(skySystem);
            String refFrameName = refFrameStr.substring(0, refFrameStr.indexOf("("));
            ReferenceSystemInterface.Type refFrame = ReferenceSystemInterface.Type.valueOf(refFrameName);
            String[] parameters = extractReferenceFrameParameter(refFrameStr);
            ReferenceSystemInterface refSystem = getReferenceFrame(refFrame, parameters);
            result = SkySystem.getSkySystemFromName(SkySystem.SkySystems.valueOf(skySystemName));
            switch (result.getSkySystemName()) {
                case EQUATORIAL:
                    ((Equatorial) result).setRefSystem(refSystem);
                    break;
                case ECLIPTIC:
                    ((Ecliptic) result).setRefSystem(refSystem);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown sky system");
            }
        } else {
            result = SkySystem.getSkySystemFromName(SkySystem.SkySystems.valueOf(skySystem));
        }
        return result;
    }

    /**
     * Converts a position frome a sky system to another one.
     *
     * @param commandLine Options from command line
     */
    private static void convertFromCommandLine(final String commandLine) {
        String[] arguments = commandLine.split("\\s+");
        if (arguments.length != 3) {
            System.err.println("3 arguments are needed, please use -h option to have a look on the help");
            LOG.log(Level.SEVERE, "3 arguments are needed, please use -h option to have a look on the help");
            System.exit(EXIT.USER_INPUT_ERROR.getCode());
        }
        String coordinates = arguments[0];
        String skySystemOrigin = arguments[1];
        String skySystemTarget = arguments[2];
        double[] skyPos = Arrays.stream(coordinates.split(","))
                .mapToDouble(Double::parseDouble)
                .toArray();
        SkySystem skySystemFrom = getSkySystem(skySystemOrigin);
        SkySystem skySystemTo = getSkySystem(skySystemTarget);
        LOG.log(Level.INFO, "Executing convertTo(%s, %s,%s)", new Object[]{skySystemTo, skyPos[0], skyPos[1]});
        SkyPosition skyPosition = skySystemFrom.convertTo(skySystemTo, skyPos[0], skyPos[1]);
        System.out.println(skyPosition.getLongitude() + " " + skyPosition.getLatitude());
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
        String arg;
        List<PROG> progChoice = new ArrayList<>();
        LongOpt[] longopts = new LongOpt[6];
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF);
        // 
        StringBuilder sb = new StringBuilder();
        longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[1] = new LongOpt("gui", LongOpt.NO_ARGUMENT, null, 'g');
        longopts[2] = new LongOpt("project", LongOpt.REQUIRED_ARGUMENT, null, 'p');
        longopts[3] = new LongOpt("unproject", LongOpt.REQUIRED_ARGUMENT, null, 'u');
        longopts[4] = new LongOpt("convert", LongOpt.REQUIRED_ARGUMENT, null, 'c');
        longopts[5] = new LongOpt("debug", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        // 
        Getopt g = new Getopt("JWcs", args, "-::p:u:c:d:gh;", longopts);
        g.setOpterr(true);
        //
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'g':
                    progChoice.add(PROG.GUI);
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
            System.exit(returnedCode.getCode());
        }

        try {

            PROG prog = progChoice.get(0);
            switch (prog) {
                case GUI:
                    java.awt.EventQueue.invokeLater(() -> {
                        createWindow();
                    });
                    isGui = true;
                    break;
                case PROJECT:
                    projectToSkyFromCommandLine(prog.getCommandLine());
                    break;
                case UNPROJECT:
                    projectToCameraFromCommandLine(prog.getCommandLine());
                    break;
                case SKY_CONVERTER:
                    convertFromCommandLine(prog.getCommandLine());
                    break;
                default:
                    throw new IllegalArgumentException(prog.name() + " not supported");
            }
            returnedCode = EXIT.OK;
        } catch (JWcsException | IOException | RuntimeException ex) {
            LOG.log(Level.SEVERE, "Error : ", ex);
            returnedCode = EXIT.USER_INPUT_ERROR;
        } finally {
            if (!isGui) {
                System.exit(returnedCode.getCode());
            }
        }
    }

    /**
     * Create a window, ask the user for lines to display, import the lines, and
     * display them.
     *
     */
    private static void createWindow() {

        try {

            // create a new window
            JFrame mapWindow = new JFrame("JWcs");
            mapWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ProjectionSelectionPanel panel = new ProjectionSelectionPanel();
            mapWindow.getContentPane().add(panel, BorderLayout.CENTER);
            mapWindow.pack();
            mapWindow.setLocationRelativeTo(null); // center on screen
            mapWindow.setVisible(true);
            URL url = Main.class.getResource(CONTINENTS_PATH);
            InputStream stream = url.openStream();
            List<MapLine> lines = UngenerateImporter.importData(stream);
            // pass the lines to the map component
            panel.addLines(lines);
            panel.draw();

        } catch (HeadlessException | IOException exc) {
            LOG.log(Level.SEVERE, "Error : %s", new Object[]{exc.getMessage()});
            System.exit(EXIT.USER_INPUT_ERROR.getCode());
        }

    }

}
