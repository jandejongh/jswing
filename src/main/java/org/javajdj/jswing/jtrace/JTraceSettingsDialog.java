/* 
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.javajdj.jswing.jtrace;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jenumsquare.JEnumSquare;

/** Dialog for the settings of a {@link JTrace} (and {@link JTraceDisplay}).
 * 
 * <p>
 * The dialog supports cancellation of changes.
 * 
 * <p>
 * The dialog actually controls many aspects of the {@link JTraceDisplay} embedded in {@link JTrace} as well. 
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JTraceSettingsDialog
  extends JDialog
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTraceSettingsDialog.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the dialog for given {@link JTrace} instance.
   * 
   * <p>
   * The dialog is centered mid-screen.
   * 
   * @param jTrace The {@link JTrace} panel, non-{@code null}.
   * 
   * @throws IllegalArgumentException If {@code jTrace == null}.
   * 
   */
  public JTraceSettingsDialog (
    final JTrace jTrace)
  {
    
    super ((JFrame) null, true);
    
    if (jTrace == null)
      throw new IllegalArgumentException ();
    
    setTitle ("Trace Display Settings");
    
    setPreferredSize (new Dimension (1024, 768));
    
    setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
    
    // TOP-LEVEL PANELS
    
    final JPanel contentPane = new JPanel ();
    
    contentPane.setLayout (new GridLayout (3, 3));
    
    final JPanel geometryPanel = new JPanel ();
    geometryPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.green, 2),
        "Geometry"));
    contentPane.add (geometryPanel);
    
    final JPanel paintPanel = new JPanel ();
    paintPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.green, 2),
        "Paint"));
    contentPane.add (paintPanel);
    
    final JPanel displayPanel = new JPanel ();
    displayPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.green, 2),
        "Display"));
    contentPane.add (displayPanel);
    
    final JPanel featuresPanel = new JPanel ();
    featuresPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.green, 2),
        "Features"));
    contentPane.add (featuresPanel);
    
    final JPanel zModulationPanel = new JPanel ();
    zModulationPanel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.green, 2),
        "Z Modulation"));
    contentPane.add (zModulationPanel);
    
    final JPanel spare1Panel = new JPanel ();
    spare1Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.green, 2),
        "Spare"));
    contentPane.add (spare1Panel);
    
    final JPanel spare2Panel = new JPanel ();
    spare2Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.green, 2),
        "Spare"));
    contentPane.add (spare2Panel);
    
    final JPanel spare3Panel = new JPanel ();
    spare3Panel.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.green, 2),
        "Spare"));
    contentPane.add (spare3Panel);
    
    final JPanel cancelOKPane = new JPanel ();
    cancelOKPane.setBorder (
      BorderFactory.createTitledBorder (
        BorderFactory.createLineBorder (Color.red, 2),
        "Exit"));
    contentPane.add (cancelOKPane);
    
    // GEOMETRY PANEL
    
    geometryPanel.setLayout (new GridLayout (6, 2));
    
    geometryPanel.add (new JLabel ("Side Panel Width"));
    final JComboBox<JTrace.SidePanelWidth> jSidePanelWidth = new JComboBox<> (JTrace.SidePanelWidth.values ());
    jSidePanelWidth.setEditable (false);
    jSidePanelWidth.setSelectedItem (jTrace.getSidePanelWidth ());
    jSidePanelWidth.addItemListener ((final ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        if (JTraceSettingsDialog.this.savedSidePanelWidth == null)
          JTraceSettingsDialog.this.savedSidePanelWidth = jTrace.getSidePanelWidth ();
        final JTrace.SidePanelWidth newValue = (JTrace.SidePanelWidth) ie.getItem ();
        jTrace.setSidePanelWidth (newValue);
      }
    });
    geometryPanel.add (JCenter.Y (jSidePanelWidth));
    
    geometryPanel.add (new JLabel ("Display X Margin"));
    final JSpinner jMarginX = new JSpinner (new SpinnerNumberModel (
      jTrace.getXMargin (),
      JTrace.MINIMUM_MARGIN,
      JTrace.MAXIMUM_MARGIN,
      1));
    ((JSpinner.DefaultEditor) jMarginX.getEditor ()).getTextField ().setEditable (false);
    jMarginX.addChangeListener ((final ChangeEvent e) ->
    {
      final int newValue = (int) jMarginX.getValue ();
      if (JTraceSettingsDialog.this.savedMarginX == null)
        JTraceSettingsDialog.this.savedMarginX = jTrace.getXMargin ();
      jTrace.setXMargin (newValue);
    });
    geometryPanel.add (JCenter.Y (jMarginX));
    
    geometryPanel.add (new JLabel ("Display Y Margin"));
    final JSpinner jMarginY = new JSpinner (new SpinnerNumberModel (
      jTrace.getYMargin (),
      JTrace.MINIMUM_MARGIN,
      JTrace.MAXIMUM_MARGIN,
      1));
    ((JSpinner.DefaultEditor) jMarginY.getEditor ()).getTextField ().setEditable (false);
    jMarginY.addChangeListener ((final ChangeEvent e) ->
    {
      final int newValue = (int) jMarginY.getValue ();
      if (JTraceSettingsDialog.this.savedMarginY == null)
        JTraceSettingsDialog.this.savedMarginY = jTrace.getYMargin ();
      jTrace.setYMargin (newValue);
    });
    geometryPanel.add (JCenter.Y (jMarginY));
    
    geometryPanel.add (new JLabel ());
    geometryPanel.add (new JLabel ());
    
    geometryPanel.add (new JLabel ());
    geometryPanel.add (new JLabel ());
    
    geometryPanel.add (new JLabel ());
    geometryPanel.add (new JLabel ());
    
    // PAINT PANEL
    
    paintPanel.setLayout (new GridLayout (10, 2));
    
    final Dimension buttonDimension = new Dimension (15, 15);
    
    paintPanel.add (new JLabel ("Background Color"));
    final JButton backgroundColorButton = new JButton ();
    backgroundColorButton.setPreferredSize (buttonDimension);
    backgroundColorButton.setMaximumSize (buttonDimension);
    backgroundColorButton.setMinimumSize (buttonDimension);
    backgroundColorButton.setBackground (jTrace.getJTraceDisplay ().getBackground ());
    backgroundColorButton.addActionListener ((final ActionEvent ae) ->
    {
      final Color newColor = JColorChooser.showDialog (
        JTraceSettingsDialog.this,
        "Choose Background Color",
        jTrace.getJTraceDisplay ().getBackground ());
      if (newColor != null)
      {
        if (JTraceSettingsDialog.this.savedBackgroundColor == null)
          JTraceSettingsDialog.this.savedBackgroundColor = jTrace.getJTraceDisplay ().getBackground ();
        jTrace.getJTraceDisplay ().setBackground (newColor);
        backgroundColorButton.setBackground (newColor);
      }
    });
    paintPanel.add (JCenter.Y (backgroundColorButton));
    
    paintPanel.add (new JLabel ("Graticule Color"));
    final JButton graticuleColorButton = new JButton ();
    graticuleColorButton.setPreferredSize (buttonDimension);
    graticuleColorButton.setMaximumSize (buttonDimension);
    graticuleColorButton.setMinimumSize (buttonDimension);
    graticuleColorButton.setBackground (jTrace.getJTraceDisplay ().getGraticuleColor ());
    graticuleColorButton.addActionListener ((final ActionEvent ae) ->
    {
      final Color newColor = JColorChooser.showDialog (
        JTraceSettingsDialog.this,
        "Choose Graticule Color",
        jTrace.getJTraceDisplay ().getGraticuleColor ());
      if (newColor != null)
      {
        if (JTraceSettingsDialog.this.savedGraticuleColor == null)
          JTraceSettingsDialog.this.savedGraticuleColor = jTrace.getJTraceDisplay ().getGraticuleColor ();
        jTrace.getJTraceDisplay ().setGraticuleColor (newColor);
        graticuleColorButton.setBackground (newColor);
      }
    });
    paintPanel.add (JCenter.Y (graticuleColorButton));
    
    paintPanel.add (new JLabel ("Grat. Highlight Color"));
    final JButton graticuleHighlightColorButton = new JButton ();
    graticuleHighlightColorButton.setPreferredSize (buttonDimension);
    graticuleHighlightColorButton.setMaximumSize (buttonDimension);
    graticuleHighlightColorButton.setMinimumSize (buttonDimension);
    graticuleHighlightColorButton.setBackground (jTrace.getJTraceDisplay ().getGraticuleHighlightColor ());
    graticuleHighlightColorButton.addActionListener ((final ActionEvent ae) ->
    {
      final Color newColor = JColorChooser.showDialog (
        JTraceSettingsDialog.this,
        "Choose Graticule Highlight Color",
        jTrace.getJTraceDisplay ().getGraticuleHighlightColor ());
      if (newColor != null)
      {
        if (JTraceSettingsDialog.this.savedGraticuleHighlightColor == null)
          JTraceSettingsDialog.this.savedGraticuleHighlightColor = jTrace.getJTraceDisplay ().getGraticuleHighlightColor ();
        jTrace.getJTraceDisplay ().setGraticuleHighlightColor (newColor);
        graticuleHighlightColorButton.setBackground (newColor);
      }
    });
    paintPanel.add (JCenter.Y (graticuleHighlightColorButton));

    paintPanel.add (new JLabel ("Crosshair Color"));
    final JButton crosshairColorButton = new JButton ();
    crosshairColorButton.setPreferredSize (buttonDimension);
    crosshairColorButton.setMaximumSize (buttonDimension);
    crosshairColorButton.setMinimumSize (buttonDimension);
    crosshairColorButton.setBackground (jTrace.getJTraceDisplay ().getCrosshairColor ());
    crosshairColorButton.addActionListener ((final ActionEvent ae) ->
    {
      final Color newColor = JColorChooser.showDialog (
        JTraceSettingsDialog.this,
        "Choose Crosshair Color",
        jTrace.getJTraceDisplay ().getCrosshairColor ());
      if (newColor != null)
      {
        if (JTraceSettingsDialog.this.savedCrosshairColor == null)
          JTraceSettingsDialog.this.savedCrosshairColor = jTrace.getJTraceDisplay ().getCrosshairColor ();
        jTrace.getJTraceDisplay ().setCrosshairColor (newColor);
        crosshairColorButton.setBackground (newColor);
      }
    });
    paintPanel.add (JCenter.Y (crosshairColorButton));

    paintPanel.add (new JLabel ("Side Panel Color"));
    final JButton sidePanelColorButton = new JButton ();
    sidePanelColorButton.setPreferredSize (buttonDimension);
    sidePanelColorButton.setMaximumSize (buttonDimension);
    sidePanelColorButton.setMinimumSize (buttonDimension);
    sidePanelColorButton.setBackground (jTrace.getSidePanelColor ());
    sidePanelColorButton.addActionListener ((final ActionEvent ae) ->
    {
      final Color newColor = JColorChooser.showDialog (
        JTraceSettingsDialog.this,
        "Choose Side Panel Color",
        jTrace.getSidePanelColor ());
      if (newColor != null)
      {
        if (JTraceSettingsDialog.this.savedSidePanelColor == null)
          JTraceSettingsDialog.this.savedSidePanelColor = jTrace.getSidePanelColor ();
        jTrace.setSidePanelColor (newColor);
        sidePanelColorButton.setBackground (newColor);
      }
    });
    paintPanel.add (JCenter.Y (sidePanelColorButton));

    paintPanel.add (new JLabel ("Corner Button Color"));
    final JButton cornerButtonColorButton = new JButton ();
    cornerButtonColorButton.setPreferredSize (buttonDimension);
    cornerButtonColorButton.setMaximumSize (buttonDimension);
    cornerButtonColorButton.setMinimumSize (buttonDimension);
    cornerButtonColorButton.setBackground (jTrace.getCornerButtonColor ());
    cornerButtonColorButton.addActionListener ((final ActionEvent ae) ->
    {
      final Color newColor = JColorChooser.showDialog (
        JTraceSettingsDialog.this,
        "Choose Corner Button Color",
        jTrace.getCornerButtonColor ());
      if (newColor != null)
      {
        if (JTraceSettingsDialog.this.savedCornerButtonColor == null)
          JTraceSettingsDialog.this.savedCornerButtonColor = jTrace.getCornerButtonColor ();
        jTrace.setCornerButtonColor (newColor);
        cornerButtonColorButton.setBackground (newColor);
      }
    });
    paintPanel.add (JCenter.Y (cornerButtonColorButton));
    
    paintPanel.add (new JLabel ("Margin Stub Color"));
    final JButton marginStubColorButton = new JButton ();
    marginStubColorButton.setPreferredSize (buttonDimension);
    marginStubColorButton.setMaximumSize (buttonDimension);
    marginStubColorButton.setMinimumSize (buttonDimension);
    marginStubColorButton.setBackground (jTrace.getMarginStubColor ());
    marginStubColorButton.addActionListener ((final ActionEvent ae) ->
    {
      final Color newColor = JColorChooser.showDialog (
        JTraceSettingsDialog.this,
        "Choose Margin Stub Color",
        jTrace.getMarginStubColor ());
      if (newColor != null)
      {
        if (JTraceSettingsDialog.this.savedMarginStubColor == null)
          JTraceSettingsDialog.this.savedMarginStubColor = jTrace.getMarginStubColor ();
        jTrace.setMarginStubColor (newColor);
        marginStubColorButton.setBackground (newColor);
      }
    });
    paintPanel.add (JCenter.Y (marginStubColorButton));
    
    paintPanel.add (new JLabel ("Clip Color"));
    final JButton clipColorButton = new JButton ();
    clipColorButton.setPreferredSize (buttonDimension);
    clipColorButton.setMaximumSize (buttonDimension);
    clipColorButton.setMinimumSize (buttonDimension);
    clipColorButton.setBackground (jTrace.getJTraceDisplay ().getClipColor ());
    clipColorButton.addActionListener ((final ActionEvent ae) ->
    {
      final Color newColor = JColorChooser.showDialog (
        JTraceSettingsDialog.this,
        "Choose Clip Color",
        jTrace.getJTraceDisplay ().getClipColor ());
      if (newColor != null)
      {
        if (JTraceSettingsDialog.this.savedClipColor == null)
          JTraceSettingsDialog.this.savedClipColor = jTrace.getJTraceDisplay ().getClipColor ();
        jTrace.getJTraceDisplay ().setClipColor (newColor);
        clipColorButton.setBackground (newColor);
      }
    });
    paintPanel.add (JCenter.Y (clipColorButton));
    
    paintPanel.add (new JLabel ("Def Trace Line Width"));
    final JSpinner jDefaultTraceLineWidth = new JSpinner (new SpinnerNumberModel (
      (Float) jTrace.getJTraceDisplay ().getDefaultTraceLineWidth (),
      (Float) JTraceDisplay.MINIMUM_DEFAULT_TRACE_LINE_WIDTH,
      (Float) JTraceDisplay.MAXIMUM_DEFAULT_TRACE_LINE_WIDTH,
      (Float) 0.1f));
    ((JSpinner.DefaultEditor) jDefaultTraceLineWidth.getEditor ()).getTextField ().setEditable (false);
    jDefaultTraceLineWidth.addChangeListener ((final ChangeEvent e) ->
    {
      final float newValue = (float) jDefaultTraceLineWidth.getValue ();
      if (JTraceSettingsDialog.this.savedDefaultTraceLineWidth == null)
        JTraceSettingsDialog.this.savedDefaultTraceLineWidth = jTrace.getJTraceDisplay ().getDefaultTraceLineWidth ();
      jTrace.getJTraceDisplay ().setDefaultTraceLineWidth (newValue);
    });
    paintPanel.add (JCenter.Y (jDefaultTraceLineWidth));
    
    paintPanel.add (new JLabel ());
    paintPanel.add (new JLabel ());
    
    // DISPLAY PANEL
    
    displayPanel.setLayout (new GridLayout (10, 2));
    
    displayPanel.add (new JLabel ("Traces"));
    final JCheckBox jTracesEnable = new JCheckBox ();
    jTracesEnable.setSelected (jTrace.getJTraceDisplay ().isEnableTraces ());
    jTracesEnable.addActionListener ((final ActionEvent ae) ->
    {
      if (JTraceSettingsDialog.this.savedEnableTraces == null)
        JTraceSettingsDialog.this.savedEnableTraces = jTrace.getJTraceDisplay ().isEnableTraces ();
      jTrace.getJTraceDisplay ().setEnableTraces (((JCheckBox) ae.getSource ()).isSelected ());
    });
    displayPanel.add (JCenter.Y (jTracesEnable));
    
    displayPanel.add (new JLabel ("Graticule"));
    final JCheckBox jGraticuleEnable = new JCheckBox ();
    jGraticuleEnable.setSelected (jTrace.getJTraceDisplay ().isEnableGraticule ());
    jGraticuleEnable.addActionListener ((final ActionEvent ae) ->
    {
      if (JTraceSettingsDialog.this.savedEnableGraticule == null)
        JTraceSettingsDialog.this.savedEnableGraticule = jTrace.getJTraceDisplay ().isEnableGraticule ();
      jTrace.getJTraceDisplay ().setEnableGraticule (((JCheckBox) ae.getSource ()).isSelected ());
    });
    displayPanel.add (JCenter.Y (jGraticuleEnable));
    
    displayPanel.add (new JLabel ("X Axis"));
    final JCheckBox jGraticuleXAxisEnable = new JCheckBox ();
    jGraticuleXAxisEnable.setSelected (jTrace.getJTraceDisplay ().isEnableGraticuleXAxis ());
    jGraticuleXAxisEnable.addActionListener ((final ActionEvent ae) ->
    {
      if (JTraceSettingsDialog.this.savedEnableGraticuleXAxis == null)
        JTraceSettingsDialog.this.savedEnableGraticuleXAxis = jTrace.getJTraceDisplay ().isEnableGraticuleXAxis ();
      jTrace.getJTraceDisplay ().setEnableGraticuleXAxis (((JCheckBox) ae.getSource ()).isSelected ());
    });
    displayPanel.add (JCenter.Y (jGraticuleXAxisEnable));
    
    displayPanel.add (new JLabel ("X Axis Ticks"));
    final JCheckBox jGraticuleXAxisTicksEnable = new JCheckBox ();
    jGraticuleXAxisTicksEnable.setSelected (jTrace.getJTraceDisplay ().isEnableGraticuleXAxisTicks ());
    jGraticuleXAxisTicksEnable.addActionListener ((final ActionEvent ae) ->
    {
      if (JTraceSettingsDialog.this.savedEnableGraticuleXAxisTicks == null)
        JTraceSettingsDialog.this.savedEnableGraticuleXAxisTicks = jTrace.getJTraceDisplay ().isEnableGraticuleXAxisTicks ();
      jTrace.getJTraceDisplay ().setEnableGraticuleXAxisTicks (((JCheckBox) ae.getSource ()).isSelected ());
    });
    displayPanel.add (JCenter.Y (jGraticuleXAxisTicksEnable));
    
    displayPanel.add (new JLabel ("Y Axis"));
    final JCheckBox jGraticuleYAxisEnable = new JCheckBox ();
    jGraticuleYAxisEnable.setSelected (jTrace.getJTraceDisplay ().isEnableGraticuleYAxis ());
    jGraticuleYAxisEnable.addActionListener ((final ActionEvent ae) ->
    {
      if (JTraceSettingsDialog.this.savedEnableGraticuleYAxis == null)
        JTraceSettingsDialog.this.savedEnableGraticuleYAxis = jTrace.getJTraceDisplay ().isEnableGraticuleYAxis ();
      jTrace.getJTraceDisplay ().setEnableGraticuleYAxis (((JCheckBox) ae.getSource ()).isSelected ());
    });
    displayPanel.add (JCenter.Y (jGraticuleYAxisEnable));
    
    displayPanel.add (new JLabel ("Y Axis Ticks"));
    final JCheckBox jGraticuleYAxisTicksEnable = new JCheckBox ();
    jGraticuleYAxisTicksEnable.setSelected (jTrace.getJTraceDisplay ().isEnableGraticuleYAxisTicks ());
    jGraticuleYAxisTicksEnable.addActionListener ((final ActionEvent ae) ->
    {
      if (JTraceSettingsDialog.this.savedEnableGraticuleYAxisTicks == null)
        JTraceSettingsDialog.this.savedEnableGraticuleYAxisTicks = jTrace.getJTraceDisplay ().isEnableGraticuleYAxisTicks ();
      jTrace.getJTraceDisplay ().setEnableGraticuleYAxisTicks (((JCheckBox) ae.getSource ()).isSelected ());
    });
    displayPanel.add (JCenter.Y (jGraticuleYAxisTicksEnable));
    
    displayPanel.add (new JLabel ("Graticule Origin"));
    displayPanel.add (JCenter.Y (new JEnumSquare<> (
      JTraceDisplay.GraticuleOrigin.class,
      jTrace.getJTraceDisplay ().getGraticuleOrigin (),
      (final JTraceDisplay.GraticuleOrigin go) ->
      {
        if (JTraceSettingsDialog.this.savedGraticuleOrigin == null)
          JTraceSettingsDialog.this.savedGraticuleOrigin = jTrace.getJTraceDisplay ().getGraticuleOrigin ();
        jTrace.getJTraceDisplay ().setGraticuleOrigin (go);
      },
      Color.red,
      new Dimension (10, 5))));

    displayPanel.add (new JLabel ("Crosshair"));
    final JCheckBox jCrosshairEnable = new JCheckBox ();
    jCrosshairEnable.setSelected (jTrace.getJTraceDisplay ().isEnableCrosshair ());
    jCrosshairEnable.addActionListener ((final ActionEvent ae) ->
    {
      if (JTraceSettingsDialog.this.savedEnableCrosshair == null)
        JTraceSettingsDialog.this.savedEnableCrosshair = jTrace.getJTraceDisplay ().isEnableCrosshair ();
      jTrace.getJTraceDisplay ().setEnableCrosshair (((JCheckBox) ae.getSource ()).isSelected ());
    });
    displayPanel.add (JCenter.Y (jCrosshairEnable));
    
    displayPanel.add (new JLabel ());
    displayPanel.add (new JLabel ());
    
    displayPanel.add (new JLabel ());
    displayPanel.add (new JLabel ());
    
    // FEATURES PANEL
    
    featuresPanel.setLayout (new GridLayout (6, 2));
    
    featuresPanel.add (new JLabel ("X Divisions"));
    final JSpinner jXDivisions = new JSpinner (new SpinnerNumberModel (
      jTrace.getJTraceDisplay ().getXDivisions (),
      JTraceDisplay.MINIMUM_X_DIVISIONS,
      JTraceDisplay.MAXIMUM_X_DIVISIONS,
      1));
    ((JSpinner.DefaultEditor) jXDivisions.getEditor ()).getTextField ().setEditable (false);
    jXDivisions.addChangeListener ((final ChangeEvent e) ->
    {
      final int newValue = (int) jXDivisions.getValue ();
      if (JTraceSettingsDialog.this.savedXDivisions == null)
        JTraceSettingsDialog.this.savedXDivisions = jTrace.getJTraceDisplay ().getXDivisions ();
      jTrace.getJTraceDisplay ().setXDivisions (newValue);
    });
    featuresPanel.add (JCenter.Y (jXDivisions));
    
    featuresPanel.add (new JLabel ("Y Divisions"));
    final JSpinner jYDivisions = new JSpinner (new SpinnerNumberModel (
      jTrace.getJTraceDisplay ().getYDivisions (),
      JTraceDisplay.MINIMUM_Y_DIVISIONS,
      JTraceDisplay.MAXIMUM_Y_DIVISIONS,
      1));
    ((JSpinner.DefaultEditor) jYDivisions.getEditor ()).getTextField ().setEditable (false);
    jYDivisions.addChangeListener ((final ChangeEvent e) ->
    {
      final int newValue = (int) jYDivisions.getValue ();
      if (JTraceSettingsDialog.this.savedYDivisions == null)
        JTraceSettingsDialog.this.savedYDivisions = jTrace.getJTraceDisplay ().getYDivisions ();
      jTrace.getJTraceDisplay ().setYDivisions (newValue);
    });
    featuresPanel.add (JCenter.Y (jYDivisions));
    
    featuresPanel.add (new JLabel ());
    featuresPanel.add (new JLabel ());
    
    featuresPanel.add (new JLabel ());
    featuresPanel.add (new JLabel ());
    
    featuresPanel.add (new JLabel ());
    featuresPanel.add (new JLabel ());
    
    // Z MODULATION PANEL
    
    zModulationPanel.setLayout (new GridLayout (6, 2));
    
    zModulationPanel.add (new JLabel ("Display Policy"));
    final JComboBox<JTraceDisplay.ZModulationDisplayPolicy> jZModulationDisplayPolicy =
      new JComboBox<> (JTraceDisplay.ZModulationDisplayPolicy.values ());
    jZModulationDisplayPolicy.setEditable (false);
    jZModulationDisplayPolicy.setSelectedItem (jTrace.getJTraceDisplay ().getZModulationDisplayPolicy ());
    jZModulationDisplayPolicy.addItemListener ((final ItemEvent ie) ->
    {
      if (ie.getStateChange () == ItemEvent.SELECTED)
      {
        if (JTraceSettingsDialog.this.savedZModulationDisplayPolicy == null)
          JTraceSettingsDialog.this.savedZModulationDisplayPolicy = jTrace.getJTraceDisplay ().getZModulationDisplayPolicy ();
        final JTraceDisplay.ZModulationDisplayPolicy newValue = (JTraceDisplay.ZModulationDisplayPolicy) ie.getItem ();
        jTrace.getJTraceDisplay ().setZModulationDisplayPolicy (newValue);
      }
    });
    zModulationPanel.add (JCenter.Y (jZModulationDisplayPolicy));
    
    zModulationPanel.add (new JLabel ("Number of Levels"));
    final JSpinner jZModulationLevels = new JSpinner (new SpinnerNumberModel (
      jTrace.getJTraceDisplay ().getZModulationLevels (),
      JTraceDisplay.MINIMUM_Z_MODULATION_LEVELS,
      JTraceDisplay.MAXIMUM_Z_MODULATION_LEVELS,
      1));
    ((JSpinner.DefaultEditor) jZModulationLevels.getEditor ()).getTextField ().setColumns (3); // Align with other spinners.
    ((JSpinner.DefaultEditor) jZModulationLevels.getEditor ()).getTextField ().setEditable (false);
    jZModulationLevels.addChangeListener ((final ChangeEvent e) ->
    {
      final int newValue = (int) jZModulationLevels.getValue ();
      if (JTraceSettingsDialog.this.savedZModulationLevels == null)
        JTraceSettingsDialog.this.savedZModulationLevels = jTrace.getJTraceDisplay ().getZModulationLevels ();
      jTrace.getJTraceDisplay ().setZModulationLevels (newValue);
    });
    zModulationPanel.add (JCenter.Y (jZModulationLevels));
    
    zModulationPanel.add (new JLabel ("Minimum Brightness"));
    final JSpinner jZModulationMinimumBrightness = new JSpinner (new SpinnerNumberModel (
      (Float) jTrace.getJTraceDisplay ().getZModulationMinimumBrightness (),
      (Float) JTraceDisplay.MINIMUM_Z_MODULATION_MINIMUM_BRIGHTNESS,
      (Float) JTraceDisplay.MAXIMUM_Z_MODULATION_MINIMUM_BRIGHTNESS,
      (Float) 0.01f));
    ((JSpinner.DefaultEditor) jZModulationMinimumBrightness.getEditor ()).getTextField ().setColumns (3);
    ((JSpinner.DefaultEditor) jZModulationMinimumBrightness.getEditor ()).getTextField ().setEditable (false);
    jZModulationMinimumBrightness.addChangeListener ((final ChangeEvent e) ->
    {
      final float newValue = (float) jZModulationMinimumBrightness.getValue ();
      if (JTraceSettingsDialog.this.savedZModulationMinimumBrightness == null)
        JTraceSettingsDialog.this.savedZModulationMinimumBrightness =
          jTrace.getJTraceDisplay ().getZModulationMinimumBrightness ();
      jTrace.getJTraceDisplay ().setZModulationMinimumBrightness (newValue);
    });
    zModulationPanel.add (JCenter.Y (jZModulationMinimumBrightness));
    
    zModulationPanel.add (new JLabel ("Minimum Line Width"));
    final JSpinner jZModulationMinimumLineWidth = new JSpinner (new SpinnerNumberModel (
      (Float) jTrace.getJTraceDisplay ().getZModulationMinimumLineWidth (),
      (Float) JTraceDisplay.MINIMUM_Z_MODULATION_MINIMUM_LINE_WIDTH,
      (Float) JTraceDisplay.MAXIMUM_Z_MODULATION_MINIMUM_LINE_WIDTH,
      (Float) 0.1f));
    final JSpinner jZModulationMaximumLineWidth = new JSpinner (new SpinnerNumberModel (
      (Float) jTrace.getJTraceDisplay ().getZModulationMaximumLineWidth (),
      (Float) JTraceDisplay.MINIMUM_Z_MODULATION_MAXIMUM_LINE_WIDTH,
      (Float) JTraceDisplay.MAXIMUM_Z_MODULATION_MAXIMUM_LINE_WIDTH,
      (Float) 1f));
    ((JSpinner.DefaultEditor) jZModulationMinimumLineWidth.getEditor ()).getTextField ().setColumns (3);
    ((JSpinner.DefaultEditor) jZModulationMinimumLineWidth.getEditor ()).getTextField ().setEditable (false);
    jZModulationMinimumLineWidth.addChangeListener ((final ChangeEvent e) ->
    {
      final float newValue = (float) jZModulationMinimumLineWidth.getValue ();
      if (JTraceSettingsDialog.this.savedZModulationMinimumLineWidth == null)
        JTraceSettingsDialog.this.savedZModulationMinimumLineWidth =
          jTrace.getJTraceDisplay ().getZModulationMinimumLineWidth ();
      if (JTraceSettingsDialog.this.savedZModulationMaximumLineWidth == null)
        JTraceSettingsDialog.this.savedZModulationMaximumLineWidth =
          jTrace.getJTraceDisplay ().getZModulationMaximumLineWidth ();
      jTrace.getJTraceDisplay ().setZModulationMinimumLineWidth (newValue);
      jZModulationMaximumLineWidth.setValue (jTrace.getJTraceDisplay ().getZModulationMaximumLineWidth ());
    });
    zModulationPanel.add (JCenter.Y (jZModulationMinimumLineWidth));
    
    zModulationPanel.add (new JLabel ("Maximum Line Width"));
    ((JSpinner.DefaultEditor) jZModulationMaximumLineWidth.getEditor ()).getTextField ().setColumns (3);
    ((JSpinner.DefaultEditor) jZModulationMaximumLineWidth.getEditor ()).getTextField ().setEditable (false);
    jZModulationMaximumLineWidth.addChangeListener ((final ChangeEvent e) ->
    {
      final float newValue = (float) jZModulationMaximumLineWidth.getValue ();
      if (JTraceSettingsDialog.this.savedZModulationMaximumLineWidth == null)
        JTraceSettingsDialog.this.savedZModulationMaximumLineWidth =
          jTrace.getJTraceDisplay ().getZModulationMaximumLineWidth ();
      if (JTraceSettingsDialog.this.savedZModulationMinimumLineWidth == null)
        JTraceSettingsDialog.this.savedZModulationMinimumLineWidth =
          jTrace.getJTraceDisplay ().getZModulationMinimumLineWidth ();
      jTrace.getJTraceDisplay ().setZModulationMaximumLineWidth (newValue);
      jZModulationMinimumLineWidth.setValue (jTrace.getJTraceDisplay ().getZModulationMinimumLineWidth ());
    });
    zModulationPanel.add (JCenter.Y (jZModulationMaximumLineWidth));
    
    zModulationPanel.add (new JLabel ());
    zModulationPanel.add (new JLabel ());
    
    // CANCEL/OK BUTTONS
    
    cancelOKPane.setLayout (new GridLayout (3, 3));
    
    for (int i = 1; i <= 7; i++)
      cancelOKPane.add (new JLabel ());
    
    final JButton jCancel = new JButton ("Cancel");
    jCancel.addActionListener ((ae) ->
    {
      // Undo all changes...
      if (JTraceSettingsDialog.this.savedSidePanelWidth != null)
        jTrace.setSidePanelWidth (JTraceSettingsDialog.this.savedSidePanelWidth);
      if (JTraceSettingsDialog.this.savedMarginX != null)
        jTrace.setXMargin (JTraceSettingsDialog.this.savedMarginX);
      if (JTraceSettingsDialog.this.savedMarginY != null)
        jTrace.setYMargin (JTraceSettingsDialog.this.savedMarginY);
      if (JTraceSettingsDialog.this.savedBackgroundColor != null)
        jTrace.getJTraceDisplay ().setBackground (JTraceSettingsDialog.this.savedBackgroundColor);
      if (JTraceSettingsDialog.this.savedGraticuleColor != null)
        jTrace.getJTraceDisplay ().setGraticuleColor (JTraceSettingsDialog.this.savedGraticuleColor);
      if (JTraceSettingsDialog.this.savedGraticuleHighlightColor != null)
        jTrace.getJTraceDisplay ().setGraticuleHighlightColor (JTraceSettingsDialog.this.savedGraticuleHighlightColor);
      if (JTraceSettingsDialog.this.savedCrosshairColor != null)
        jTrace.getJTraceDisplay ().setCrosshairColor (JTraceSettingsDialog.this.savedCrosshairColor);
      if (JTraceSettingsDialog.this.savedSidePanelColor != null)
        jTrace.setSidePanelColor (JTraceSettingsDialog.this.savedSidePanelColor);
      if (JTraceSettingsDialog.this.savedCornerButtonColor != null)
        jTrace.setCornerButtonColor (JTraceSettingsDialog.this.savedCornerButtonColor);
      if (JTraceSettingsDialog.this.savedMarginStubColor != null)
        jTrace.setMarginStubColor (JTraceSettingsDialog.this.savedMarginStubColor);
      if (JTraceSettingsDialog.this.savedClipColor != null)
        jTrace.getJTraceDisplay ().setClipColor (JTraceSettingsDialog.this.savedClipColor);
      if (JTraceSettingsDialog.this.savedDefaultTraceLineWidth != null)
        jTrace.getJTraceDisplay ().setDefaultTraceLineWidth (JTraceSettingsDialog.this.savedDefaultTraceLineWidth);
      if (JTraceSettingsDialog.this.savedEnableTraces != null)
        jTrace.getJTraceDisplay ().setEnableTraces (JTraceSettingsDialog.this.savedEnableTraces);
      if (JTraceSettingsDialog.this.savedEnableGraticule != null)
        jTrace.getJTraceDisplay ().setEnableGraticule (JTraceSettingsDialog.this.savedEnableGraticule);
      if (JTraceSettingsDialog.this.savedEnableGraticuleXAxis != null)
        jTrace.getJTraceDisplay ().setEnableGraticuleXAxis (JTraceSettingsDialog.this.savedEnableGraticuleXAxis);
      if (JTraceSettingsDialog.this.savedEnableGraticuleXAxisTicks != null)
        jTrace.getJTraceDisplay ().setEnableGraticuleXAxisTicks (JTraceSettingsDialog.this.savedEnableGraticuleXAxisTicks);
      if (JTraceSettingsDialog.this.savedEnableGraticuleYAxis != null)
        jTrace.getJTraceDisplay ().setEnableGraticuleYAxis (JTraceSettingsDialog.this.savedEnableGraticuleYAxis);
      if (JTraceSettingsDialog.this.savedEnableGraticuleYAxisTicks != null)
        jTrace.getJTraceDisplay ().setEnableGraticuleYAxisTicks (JTraceSettingsDialog.this.savedEnableGraticuleYAxisTicks);
      if (JTraceSettingsDialog.this.savedGraticuleOrigin != null)
        jTrace.getJTraceDisplay ().setGraticuleOrigin (JTraceSettingsDialog.this.savedGraticuleOrigin);
      if (JTraceSettingsDialog.this.savedEnableCrosshair != null)
        jTrace.getJTraceDisplay ().setEnableCrosshair (JTraceSettingsDialog.this.savedEnableCrosshair);
      if (JTraceSettingsDialog.this.savedXDivisions != null)
        jTrace.getJTraceDisplay ().setXDivisions (JTraceSettingsDialog.this.savedXDivisions);
      if (JTraceSettingsDialog.this.savedYDivisions != null)
        jTrace.getJTraceDisplay ().setYDivisions (JTraceSettingsDialog.this.savedYDivisions);
      if (JTraceSettingsDialog.this.savedZModulationDisplayPolicy != null)
        jTrace.getJTraceDisplay ().setZModulationDisplayPolicy (JTraceSettingsDialog.this.savedZModulationDisplayPolicy);
      if (JTraceSettingsDialog.this.savedZModulationLevels != null)
        jTrace.getJTraceDisplay ().setZModulationLevels (JTraceSettingsDialog.this.savedZModulationLevels);
      if (JTraceSettingsDialog.this.savedZModulationMinimumBrightness != null)
        jTrace.getJTraceDisplay ().setZModulationMinimumBrightness (JTraceSettingsDialog.this.savedZModulationMinimumBrightness);
      if (JTraceSettingsDialog.this.savedZModulationMinimumLineWidth != null)
        jTrace.getJTraceDisplay ().setZModulationMinimumLineWidth (JTraceSettingsDialog.this.savedZModulationMinimumLineWidth);
      if (JTraceSettingsDialog.this.savedZModulationMaximumLineWidth != null)
        jTrace.getJTraceDisplay ().setZModulationMaximumLineWidth (JTraceSettingsDialog.this.savedZModulationMaximumLineWidth);
      JTraceSettingsDialog.this.dispose ();
    });
    cancelOKPane.add (JCenter.XY (jCancel));
    
    final JButton jOK = new JButton ("  OK  ");
    jOK.addActionListener ((ae) ->
    {
      JTraceSettingsDialog.this.dispose ();
    });
    cancelOKPane.add (JCenter.XY (jOK));
    
    setContentPane (contentPane);
    pack ();
    setLocationRelativeTo (null);
        
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SAVED VALUES [CANCEL]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private JTrace.SidePanelWidth savedSidePanelWidth = null;
  
  private Integer savedMarginX = null;
  
  private Integer savedMarginY = null;
  
  private Color savedBackgroundColor = null;
  
  private Color savedGraticuleColor = null;
  
  private Color savedGraticuleHighlightColor = null;
  
  private Color savedCrosshairColor = null;
  
  private Color savedSidePanelColor = null;
  
  private Color savedCornerButtonColor = null;
  
  private Color savedMarginStubColor = null;
  
  private Color savedClipColor = null;
  
  private Float savedDefaultTraceLineWidth = null;
  
  private Boolean savedEnableTraces = null;
  
  private Boolean savedEnableGraticule = null;
  
  private Boolean savedEnableGraticuleXAxis = null;
  
  private Boolean savedEnableGraticuleXAxisTicks = null;
  
  private Boolean savedEnableGraticuleYAxis = null;
  
  private Boolean savedEnableGraticuleYAxisTicks = null;
  
  private JTraceDisplay.GraticuleOrigin savedGraticuleOrigin = null;
  
  private Boolean savedEnableCrosshair = null;
  
  private Integer savedXDivisions = null;
  
  private Integer savedYDivisions = null;
  
  private JTraceDisplay.ZModulationDisplayPolicy savedZModulationDisplayPolicy = null;
  
  private Integer savedZModulationLevels = null;
  
  private Float savedZModulationMinimumBrightness = null;
  
  private Float savedZModulationMinimumLineWidth = null;
  
  private Float savedZModulationMaximumLineWidth = null;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
