/*
 * Copyright (C) 2016 Jean-Christophe Malapert
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
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Jean-Christophe Malapert
 */
public class ProjectionFrame extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JWcs wcs;

    public ProjectionFrame(String projectionCode) throws JWcsException {
        Map wcsKeywords = new HashMap();
        wcsKeywords.put(JWcs.NAXIS, "2");
        wcsKeywords.put(JWcs.NAXIS1, "600");
        wcsKeywords.put(JWcs.NAXIS2, "300");
        wcsKeywords.put(JWcs.RADESYS, "ICRS");
        wcsKeywords.put(JWcs.CRPIX1, "300");
        wcsKeywords.put(JWcs.CRPIX2, "150");
        wcsKeywords.put(JWcs.CRVAL1, "0");
        wcsKeywords.put(JWcs.CRVAL2, "0");
        wcsKeywords.put(JWcs.CD11, String.valueOf(180d / 300d));
        wcsKeywords.put(JWcs.CD12, "0");
        wcsKeywords.put(JWcs.CD21, "0");
        wcsKeywords.put(JWcs.CD22, String.valueOf(90d / 150d));
        wcsKeywords.put(JWcs.CTYPE1, "RA---" + projectionCode);
        wcsKeywords.put(JWcs.CTYPE2, "DEC--" + projectionCode);
        wcs = new WcsMap(wcsKeywords);
        wcs.doInit();

    }

    public void run() {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.red);

        Dimension size = getSize();
        int w = size.width;
        int h = size.height;
        for (int latitude = -90; latitude <= 90; latitude = latitude + 5) {
            for (int longitude = 0; longitude < 360; longitude = longitude + 5) {
                double[] xy;
                try {
                    xy = wcs.wcs2pix(longitude, latitude);
                    int x = (int) xy[0];
                    int y = (int) xy[1];
                    g2d.drawLine(x, y, x, y);
                } catch (ProjectionException ex) {
                    //Logger.getLogger(io.github.malapert.jwcs.proj.AITFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void main(String[] args) throws JWcsException {
        String[] projections = new String[]{"AIT", "ARC", "AZP", "BON", "CAR", "CEA", "COD", "COE", "COO", "COP", "CYP", "MER", "MOL", "PAR", "PCO", "SFL", "SIN", "STG", "SZP", "TAN", "ZEA", "ZPN"};

        for (String projectionCode : projections) {
            ProjectionFrame points = new ProjectionFrame(projectionCode);
            JFrame frame = new JFrame(projectionCode);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(points);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

}
