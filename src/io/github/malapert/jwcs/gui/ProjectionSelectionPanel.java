/*
 * ProjectionSelectionPanel.java
 *
 * Created on September 16, 2006, 2:54 PM
 */
package io.github.malapert.jwcs.gui;

import io.github.malapert.jwcs.*;
import io.github.malapert.jwcs.coordsystem.Utility;
import io.github.malapert.jwcs.proj.CylindricalProjection;
import io.github.malapert.jwcs.proj.PolyConicProjection;
import io.github.malapert.jwcs.proj.Projection;
import io.github.malapert.jwcs.proj.exception.JWcsException;
import io.github.malapert.jwcs.proj.exception.ProjectionException;
import io.github.malapert.jwcs.utility.NumericalUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

/**
 * ProjectionSelectionPanel lets the user select a projection, applies the
 * selected projection to a group of lines, and displays basic information about
 * the projection.
 *
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class ProjectionSelectionPanel extends javax.swing.JPanel {

    /**
     * The lines that are displayed. Must be in geographic coordinates
     * (degrees).
     */
    private List<MapLine> lines = new ArrayList();
    private final List<MapLine> linesFromClient = new ArrayList();

    /**
     * Creates new form ProjectionSelectionPanel
     */
    public ProjectionSelectionPanel() {
        initComponents();
        Object[] projNames = new Object[]{"ARC", "AZP", "SIN", "STG", "SZP", "TAN", "ZEA", "ZPN",
            "AIT", "CAR", "CEA", "CYP", "MER", "MOL", "PAR", "SFL",
            "COD", "COE", "COO", "COP",
            "BON", "PCO"};
        //Object[] projNames = new Object[]{"AIT"};

        projectionComboBox.setModel(new DefaultComboBoxModel(projNames));
    }

    protected final List<MapLine> drawBorder(JWcs wcs) {
        List<MapLine> borderLines = new ArrayList<>();
        JWcsMap wcsMap = (JWcsMap) wcs;
        double crval1 = wcsMap.getValueAsDouble(JWcs.CRVAL1);
        double crval2 = wcsMap.getValueAsDouble(JWcs.CRVAL2);
        double[] center = new double[]{
            crval1, crval2
        };

        double[] pos1 = new double[2];
        pos1[0] = Double.NaN;
        double[] xyzCenter = NumericalUtils.radec2xyz(center);
        for (int lon = 0; lon <= 360; lon += 10) {
            double[] pos2;
            try {
                double latRad = Utility.getBorderLatitude(xyzCenter, Math.toRadians(lon));
                if (latRad > Projection.HALF_PI) {
                    latRad = Projection.HALF_PI - latRad;
                }
                if (latRad < -Projection.HALF_PI) {
                    latRad = -Projection.HALF_PI - latRad;
                }

//                if (latRad>Projection.HALF_PI) {
//                    latRad = Math.PI - latRad;
//                }                
//                if (latRad<-Projection.HALF_PI) {
//                    latRad = -Math.PI + Math.abs(latRad);
//                }                  
                pos2 = wcs.wcs2pix((double) lon, Math.toDegrees(latRad));
                if (Double.isFinite(pos1[0])) {
                    MapLine line = new MapLine();
                    line.addPoint(pos1[0], pos1[1]);
                    line.addPoint(pos2[0], pos2[1]);
                    borderLines.add(line);
                }
                System.arraycopy(pos2, 0, pos1, 0, pos2.length);
            } catch (ProjectionException ex) {
                //Logger.getLogger(ProjectionFrame.class.getName()).log(Level.SEVERE, null, ex);
                pos1[0] = Double.NaN;
            }
        }
        return borderLines;
    }

    protected final List<MapLine> drawLatitudeLines(JWcs wcs) {
        List<MapLine> latitudes = new ArrayList<>();
        double[] pos1 = new double[2];
        for (int lat = -90; lat <= 90; lat += 5) {
            pos1[0] = Double.NaN;
            for (int lon = 0; lon <= 360; lon += 10) {
                double[] pos2;
                try {
                    if (wcs.inside(lon, lat)) {
                        pos2 = wcs.wcs2pix(lon, lat);
                        if (Double.isFinite(pos1[0])) {
                            if ((wcs.getNameFamily().equals(CylindricalProjection.NAME) || wcs.getNameFamily().equals(PolyConicProjection.NAME)) && Math.abs(pos1[0] - pos2[0]) < 50) {
                                MapLine line = new MapLine();
                                line.addPoint(pos1[0], pos1[1]);
                                line.addPoint(pos2[0], pos2[1]);
                                latitudes.add(line);
                                //g2d.drawLine((int) pos1[0], (int) pos1[1], (int) pos2[0], (int) pos2[1]);
                            } else if (!wcs.getNameFamily().equals(CylindricalProjection.NAME) && !wcs.getNameFamily().equals(PolyConicProjection.NAME)) {
                                MapLine line = new MapLine();
                                line.addPoint(pos1[0], pos1[1]);
                                line.addPoint(pos2[0], pos2[1]);
                                latitudes.add(line);
                            }
                        }
                        System.arraycopy(pos2, 0, pos1, 0, pos2.length);
                    } else {
                        pos1[0] = Double.NaN;
                    }
                } catch (ProjectionException ex) {
                    //LOG.log(Level.WARNING, ex.getMessage());
                    pos1[0] = Double.NaN;
                }
            }
        }
        return latitudes;
    }

    protected final List<MapLine> drawLongitudeLines(JWcs wcs) {
        List<MapLine> longitudes = new ArrayList<>();
        double[] pos1 = new double[2];
        for (int lon = 0; lon < 360; lon += 10) {
            pos1[0] = Double.NaN;
            for (int lat = -90; lat <= 90; lat += 5) {
                double[] pos2;
                try {
                    if (wcs.inside(lon, lat)) {
                        pos2 = wcs.wcs2pix(lon, lat);
                        if (Double.isFinite(pos1[0])) {
                            if ((wcs.getNameFamily().equals(CylindricalProjection.NAME) || wcs.getNameFamily().equals(PolyConicProjection.NAME)) && Math.abs(pos1[0] - pos2[0]) < 50) {
                                MapLine line = new MapLine();
                                line.addPoint(pos1[0], pos1[1]);
                                line.addPoint(pos2[0], pos2[1]);
                                longitudes.add(line);
                                //g2d.drawLine((int) pos1[0], (int) pos1[1], (int) pos2[0], (int) pos2[1]);
                            } else if (!wcs.getNameFamily().equals(CylindricalProjection.NAME) && !wcs.getNameFamily().equals(PolyConicProjection.NAME)) {
                                MapLine line = new MapLine();
                                line.addPoint(pos1[0], pos1[1]);
                                line.addPoint(pos2[0], pos2[1]);
                                longitudes.add(line);
                            }
                        }
                        System.arraycopy(pos2, 0, pos1, 0, pos2.length);
                    } else {
                        pos1[0] = Double.NaN;
                    }
                } catch (ProjectionException ex) {
                    //LOG.log(Level.WARNING, ex.getMessage());
                    pos1[0] = Double.NaN;
                }
            }
        }
        return longitudes;
    }

    private List<MapLine> drawLines(JWcs wcs) {
        if (!this.linesFromClient.isEmpty()) {
            return new ArrayList<>();
        }
        List<MapLine> projectedLines = new ArrayList<>();
        linesFromClient.stream().map((line) -> line.getPoints()).map((pts) -> {
            MapLine projectedLine = new MapLine();
            pts.stream().filter((pt) -> (wcs.inside(pt.x, pt.y))).forEach((pt) -> {
                try {
                    double[] pos = wcs.wcs2pix(pt.x, pt.y);
                    projectedLine.addPoint(new MapPoint(pos[0], pos[1]));
                } catch (ProjectionException ex) {
                    //Logger.getLogger(ProjectionSelectionPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            return projectedLine;
        }).forEach((projectedLine) -> {
            projectedLines.add(projectedLine);
        });

        return projectedLines;
    }

    private JWcs init() throws JWcsException {
        String projName = (String) projectionComboBox.getSelectedItem();
        JWcs wcs = JWcsMap.getProjection(projName);
        this.lines.clear();
        return wcs;
    }

    private void computeGrid(JWcs wcs) {
        this.lines.addAll(drawLatitudeLines(wcs));
        this.lines.addAll(drawLongitudeLines(wcs));
        //this.lines.addAll(drawLines(wcs));
    }

    private void project() {

        try {
            JWcsMap jwcsMap = (JWcsMap) init();
            if (!NumericalUtils.equal(jwcsMap.getValueAsDouble(JWcs.CRVAL1), lon0Slider.getValue(), 1e-13)) {
                jwcsMap.getKeywords().put(JWcs.CRVAL1, String.valueOf(lon0Slider.getValue()));
            }
            if (!NumericalUtils.equal(jwcsMap.getValueAsDouble(JWcs.CRVAL2), lat0Slider.getValue(), 1e-13)) {
                jwcsMap.getKeywords().put(JWcs.CRVAL2, String.valueOf(lat0Slider.getValue()));
            }
            jwcsMap.doInit();
            computeGrid(jwcsMap);
            map.setLines(lines);
            updateProjectionInfo(jwcsMap);
//        try {
//            // find the selected name, create the corresponding projection.
//            String projName = (String) projectionComboBox.getSelectedItem();
//            Projection projection = ProjectionFactory.getNamedProjection(projName);
//
//            // use the selected projection to project the lines,
//            // and pass the projected lines to the map to display.
//            if (projection != null) {
//                projection.setProjectionLongitudeDegrees(lon0Slider.getValue());
//                projection.setEllipsoid(Ellipsoid.SPHERE);
//                projection.initialize();
//
//                LineProjector projector = new LineProjector();
//                ArrayList<MapLine> projectedLines = new ArrayList<MapLine>();
//                projector.constructGraticule(projectedLines, projection);
//                projector.projectLines(lines, projectedLines, projection);
//                if (inverse && projection.hasInverse()) {
//                    projectedLines = projector.inverse(projectedLines, projection);
//                }
//
//                map.setLines(projectedLines);
//            } else {
//                map.setLines(null);
//            }
//
//            // write some descriptive information about the selected projection.
//            updateProjectionInfo(projection);
//
//        }
        } catch (JWcsException ex) {
            String msg = ex.getMessage();
            String title = "Error";
            JOptionPane.showMessageDialog(selectionPanel, msg, title, JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(ProjectionSelectionPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set the lines that are projected and displayed.
     *
     * @param lines The lines to project. Must be in geographic coordinates
     * (degrees).
     */
    public void setLines(List<MapLine> lines) {
        // store the passed lines
        this.lines = lines;

        // pass the new lines to the map that displays the lines.
        map.setLines(lines);

        // reset the graphical user interface to the Geographical projection.
        projectionComboBox.setSelectedIndex(0);
        project();
    }

    public void addLines(List<MapLine> lines) {
        // store the passed lines
        this.linesFromClient.addAll(lines);
    }

    public void draw() {
        projectionComboBox.setSelectedIndex(0);
        project();
    }

    /**
     * Write basic infromation about the projection to the graphical user
     * interface.
     *
     * @projection The Projection that provides the information.
     */
    private void updateProjectionInfo(JWcs wcs) {
        if (wcs == null) {
            descriptionLabel.setText("-");
        } else {
            descriptionLabel.setText(wcs.getDescription());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectionPanel = new javax.swing.JPanel();
        projectionComboBox = new javax.swing.JComboBox();
        previousProjectionButton = new javax.swing.JButton();
        nextProjectionButton = new javax.swing.JButton();
        map = new io.github.malapert.jwcs.gui.MapComponent();
        infoPanel = new javax.swing.JPanel();
        javax.swing.JLabel descriptionLeadLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        javax.swing.JLabel longitudeLeadLabel = new javax.swing.JLabel();
        lon0Slider = new javax.swing.JSlider();
        lon0Label = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lat0Slider = new javax.swing.JSlider();
        lat0Label = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout(10, 10));

        selectionPanel.setPreferredSize(new java.awt.Dimension(100, 40));
        selectionPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        projectionComboBox.setMaximumRowCount(40);
        projectionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Plate Carrée (Geographic)", "Cylindrical Equal-Area", "Cylindrical Conformal (Mercator)", "Conical Equidistant", "Conical Equal-Area (Albers)", "Conical Conformal (Lambert)", "Azimuthal Equidistant", "Azimuthal Equal-Area (Lambert)", "Azimuthal Conformal (Stereographic)", "Azimuthal Orthographic", "Sinusoidal", "Pseudoconical Equal-Area (Bonne)" }));
        projectionComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                projectionComboBoxItemStateChanged(evt);
            }
        });
        selectionPanel.add(projectionComboBox);

        previousProjectionButton.setText("<");
        previousProjectionButton.setPreferredSize(new java.awt.Dimension(50, 29));
        previousProjectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousProjectionButtonActionPerformed(evt);
            }
        });
        selectionPanel.add(previousProjectionButton);

        nextProjectionButton.setText(">");
        nextProjectionButton.setPreferredSize(new java.awt.Dimension(50, 29));
        nextProjectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextProjectionButtonActionPerformed(evt);
            }
        });
        selectionPanel.add(nextProjectionButton);

        add(selectionPanel, java.awt.BorderLayout.NORTH);

        map.setPreferredSize(new java.awt.Dimension(400, 300));
        add(map, java.awt.BorderLayout.CENTER);

        infoPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(""), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        infoPanel.setMinimumSize(new java.awt.Dimension(400, 96));
        infoPanel.setPreferredSize(new java.awt.Dimension(500, 200));

        descriptionLeadLabel.setText("Description");

        descriptionLabel.setText("-");
        descriptionLabel.setMaximumSize(new java.awt.Dimension(300, 16));
        descriptionLabel.setMinimumSize(new java.awt.Dimension(300, 16));
        descriptionLabel.setPreferredSize(new java.awt.Dimension(300, 16));

        longitudeLeadLabel.setText("Longitude of Origin");

        lon0Slider.setMaximum(360);
        lon0Slider.setValue(0);
        lon0Slider.setMinimumSize(new java.awt.Dimension(200, 29));
        lon0Slider.setPreferredSize(new java.awt.Dimension(200, 29));
        lon0Slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lon0SliderStateChanged(evt);
            }
        });

        lon0Label.setText("0");
        lon0Label.setMaximumSize(new java.awt.Dimension(50, 16));
        lon0Label.setMinimumSize(new java.awt.Dimension(50, 16));
        lon0Label.setPreferredSize(new java.awt.Dimension(50, 16));

        jLabel1.setText("Latitude of Origin");

        lat0Slider.setMaximum(90);
        lat0Slider.setMinimum(-90);
        lat0Slider.setValue(-90);
        lat0Slider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lat0SliderStateChanged(evt);
            }
        });

        lat0Label.setText("0");

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(descriptionLeadLabel)
                        .addGap(10, 10, 10)
                        .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(infoPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lat0Slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(infoPanelLayout.createSequentialGroup()
                                .addComponent(longitudeLeadLabel)
                                .addGap(10, 10, 10)
                                .addComponent(lon0Slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(32, 32, 32)
                        .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lon0Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lat0Label, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(93, 93, 93))
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lat0Label)
                    .addGroup(infoPanelLayout.createSequentialGroup()
                        .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descriptionLeadLabel)
                            .addGroup(infoPanelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(infoPanelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(longitudeLeadLabel)
                                    .addComponent(lon0Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lon0Slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lat0Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        add(infoPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void lon0SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lon0SliderStateChanged
        JSlider slider = (JSlider) evt.getSource();
        lon0Label.setText(Integer.toString(slider.getValue()));
        //if (!slider.getValueIsAdjusting()) {
        project();
        //}
    }//GEN-LAST:event_lon0SliderStateChanged

    private void projectionComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_projectionComboBoxItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            project();
        }
    }//GEN-LAST:event_projectionComboBoxItemStateChanged

    private void previousProjectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousProjectionButtonActionPerformed
        int id = projectionComboBox.getSelectedIndex() - 1;
        if (id >= 0) {
            projectionComboBox.setSelectedIndex(id);
            project();
        }
    }//GEN-LAST:event_previousProjectionButtonActionPerformed

    private void nextProjectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextProjectionButtonActionPerformed
        int id = projectionComboBox.getSelectedIndex() + 1;
        if (id < projectionComboBox.getItemCount()) {
            projectionComboBox.setSelectedIndex(id);
            project();
        }
    }//GEN-LAST:event_nextProjectionButtonActionPerformed

    private void lat0SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lat0SliderStateChanged
        JSlider slider = (JSlider) evt.getSource();
        lat0Label.setText(Integer.toString(slider.getValue()));
        //if (!slider.getValueIsAdjusting()) {
        project();
        //}
    }//GEN-LAST:event_lat0SliderStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lat0Label;
    private javax.swing.JSlider lat0Slider;
    private javax.swing.JLabel lon0Label;
    private javax.swing.JSlider lon0Slider;
    private io.github.malapert.jwcs.gui.MapComponent map;
    private javax.swing.JButton nextProjectionButton;
    private javax.swing.JButton previousProjectionButton;
    private javax.swing.JComboBox projectionComboBox;
    private javax.swing.JPanel selectionPanel;
    // End of variables declaration//GEN-END:variables

}
