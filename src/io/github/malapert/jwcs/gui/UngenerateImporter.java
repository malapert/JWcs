/*
 * UngenerateImporter.java
 *
 * Created on April 1, 2005, 12:02 PM
 * Apache License 2
 */

package io.github.malapert.jwcs.gui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/**
 * An importer for the ESRI Ungenerate file format.
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class UngenerateImporter {

    private UngenerateImporter() {
    }
    
    /**
     * Reads an Ungenerate file and returns the found lines in a Vector.
     * @param filePath The file to import.
     * @return A Vector containing all read MapLines.
     * @throws java.io.IOException Exception
     */
    public static List<MapLine> importData(String filePath) throws IOException {
        return importData (new FileInputStream(filePath));
    }
    
    public static List<MapLine> importData(InputStream inputStream) throws IOException {
        // store the read lines in this vector.
        List<MapLine> lines = new ArrayList<>();
        
        // read lines until we reach the end of the file
        try ( // open the file
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String idStr;
            while ((idStr = in.readLine()) != null) {
                MapLine line = readLine(in);
                if (line != null) {
                    lines.add(line);
                }
            }
        }
        
        // return the read lines
        return lines;
    }
    
    /**
     * Reads a MapLine from the current file position.
     * @param in The reader that provides the data to read.
     * @return MapLine The read line.
     * @exception java.io.IOException Exception
     */
    private static MapLine readLine(BufferedReader in) throws java.io.IOException {
        String str;
        MapLine line = new MapLine();
        
        while (true) {
            str = in.readLine();
            if (str == null || str.length() == 0) {
                break;
            }
            str = str.trim().toLowerCase();
            if (str.startsWith("end") ) {
                break;
            }
            try {
                StringTokenizer tokenizer = new StringTokenizer(str, " \t,");
                double x = Double.parseDouble((String)tokenizer.nextToken());
                double y = Double.parseDouble((String)tokenizer.nextToken());
                if (x < 0) {
                    x = 360+x;
                }
                line.addPoint(x, y);
            } catch (NoSuchElementException e) {
                // found a line without any readable data. Just read the next line
            }
        }
        return line;
    }

}
