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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** Panel showing one or more traces from for instance an instrument.
 *
 * <p>
 * Traces are each represented as double arrays, and distinguished with a
 * key of type {@code K}.
 * 
 * <p>
 * An extended version of the trace display is available through {@link JTrace}, which
 * includes various user controls and dialogs for fine-tuning the display.
 * 
 * @param <K> The key type used for distinguishing traces.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *  
 * @see JTrace
 * @see TraceDB
 * 
 */
public class JTraceDisplay<K>
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTraceDisplay.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a new trace display (main constructor).
   * 
   * @param traceDB The {@link TraceDB} to use; a new one is created if the argument is {@code null}.
   * 
   * @see #getTraceDB
   * 
   */
  public JTraceDisplay (final TraceDB<K> traceDB)
  {
    
    super ();
    
    this.traceDB = traceDB != null ? traceDB : new TraceDB<> ();
    
    setOpaque (true);
    setBackground (DEFAULT_BACKGROUND_COLOR);
    
    addMouseListener (this.mouseAdapter);
    addMouseMotionListener (this.mouseAdapter);
    
  }

  /** Creates a new trace display with a new {@link TraceDB} (auxiliary constructor).
   * 
   * @see #getTraceDB
   * 
   */
  public JTraceDisplay ()
  {
    this (null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE DB
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final TraceDB<K> traceDB;
  
  /** Returns the {@link TraceDB} providing trace (and other) data.
   * 
   * @return The trace database (fixed upon construction and always non-{@code null}).
   * 
   */
  public final TraceDB<K> getTraceDB ()
  {
    return this.traceDB;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT BACKGROUND COLOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The default background color of the panel.
   * 
   * @see #getBackground
   * @see #setBackground
   * 
   */
  public final static Color DEFAULT_BACKGROUND_COLOR = Color.black;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // X and Y DIVISIONS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The minimum number of X divisions supported.
   * 
   */
  public final static int MINIMUM_X_DIVISIONS = 1;
  
  /** The maximum number of X divisions supported.
   * 
   */
  public final static int MAXIMUM_X_DIVISIONS = 100;
  
  /** The default number of X divisions.
   * 
   */
  public final static int DEFAULT_X_DIVISIONS = 10;
  
  private volatile int xDivisions = DEFAULT_X_DIVISIONS;
  
  /** Returns the number of X divisions.
   * 
   * @return The number of X divisions.
   * 
   */
  public final int getXDivisions ()
  {
    return this.xDivisions;
  }

  /** Sets the number of X divisions.
   * 
   * @param xDivisions The new number of X divisions.
   * 
   * @throws IllegalArgumentException If the number is out or range.
   * 
   * @see #MINIMUM_X_DIVISIONS
   * @see #MAXIMUM_X_DIVISIONS
   * 
   */
  public final void setXDivisions (final int xDivisions)
  {
    if (xDivisions < MINIMUM_X_DIVISIONS || xDivisions > MAXIMUM_X_DIVISIONS)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (this.xDivisions != xDivisions)
      {
        this.xDivisions = xDivisions;
        SwingUtilities.invokeLater (() -> repaint ());
      }
    }
  }

  /** The minimum number of Y divisions supported.
   * 
   */
  public final static int MINIMUM_Y_DIVISIONS = 1;
  
  /** The maximum number of Y divisions supported.
   * 
   */
  public final static int MAXIMUM_Y_DIVISIONS = 100;
  
  /** The default number of Y divisions.
   * 
   */
  public final static int DEFAULT_Y_DIVISIONS = 10;
  
  private volatile int yDivisions = DEFAULT_Y_DIVISIONS;
  
  /** Returns the number of Y divisions.
   * 
   * @return The number of Y divisions.
   * 
   */
  public final int getYDivisions ()
  {
    return this.yDivisions;
  }

  /** Sets the number of Y divisions.
   * 
   * @param yDivisions The new number of Y divisions.
   * 
   * @throws IllegalArgumentException If the number is out or range.
   * 
   * @see #MINIMUM_Y_DIVISIONS
   * @see #MAXIMUM_Y_DIVISIONS
   * 
   */
  public final void setYDivisions (final int yDivisions)
  {
    if (yDivisions < MINIMUM_Y_DIVISIONS || yDivisions > MAXIMUM_Y_DIVISIONS)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (this.yDivisions != yDivisions)
      {
        this.yDivisions = yDivisions;
        SwingUtilities.invokeLater (() -> repaint ());
      }
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // X AND Y [INTERNAL] MARGINS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The minimum margin (X and Y) supported.
   * 
   * <p>
   * {@link JTraceDisplay} maintains user-settable X and Y margins
   * to allow for proper drawing (and breathing room) for border graticule lines.
   * 
   */
  public final static int MINIMUM_MARGIN = 0;
  
  /** The maximum margin (X and Y) supported.
   * 
   * <p>
   * {@link JTraceDisplay} maintains user-settable X and Y margins
   * to allow for proper drawing (and breathing room) for border graticule lines.
   * 
   */
  public final static int MAXIMUM_MARGIN = 255;
  
  /** The default X margin.
   * 
   * <p>
   * {@link JTraceDisplay} maintains a user-settable X margin
   * to allow for proper drawing (and breathing room) for border graticule lines.
   * 
   */
  public final static int DEFAULT_X_MARGIN = 6;
  
  /** The default Y margin.
   * 
   * <p>
   * {@link JTraceDisplay} maintains a user-settable Y margin
   * to allow for proper drawing (and breathing room) for border graticule lines.
   * 
   */
  public final static int DEFAULT_Y_MARGIN = 6;
  
  private volatile int xMargin = DEFAULT_X_MARGIN;
  
  private volatile int yMargin = DEFAULT_Y_MARGIN;
  
  /** Returns the X margin.
   * 
   * <p>
   * {@link JTraceDisplay} maintains a user-settable X margin
   * to allow for proper drawing (and breathing room) for border graticule lines.
   * 
   * @return The X margin.
   * 
   */
  public final int getXMargin ()
  {
    return this.xMargin;
  }
  
  /** Returns the Y margin.
   * 
   * <p>
   * {@link JTraceDisplay} maintains a user-settable Y margin
   * to allow for proper drawing (and breathing room) for border graticule lines.
   * 
   * @return The Y margin.
   * 
   */
  public final int getYMargin ()
  {
    return this.yMargin;
  }
  
  /** Sets X margin.
   * 
   * <p>
   * {@link JTraceDisplay} maintains a user-settable X margin
   * to allow for proper drawing (and breathing room) for border graticule lines.
   * 
   * @param xMargin The new X margin.
   * 
   * @throws IllegalArgumentException If the number is out or range.
   * 
   * @see #MINIMUM_MARGIN
   * @see #MAXIMUM_MARGIN
   * 
   */
  public final void setXMargin (final int xMargin)
  {
    if (xMargin < MINIMUM_MARGIN || xMargin > MAXIMUM_MARGIN)
      throw new IllegalArgumentException ();
    if (xMargin != this.xMargin)
    {
      this.xMargin = xMargin;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  /** Sets Y margin.
   * 
   * <p>
   * {@link JTraceDisplay} maintains a user-settable Y margin
   * to allow for proper drawing (and breathing room) for border graticule lines.
   * 
   * @param yMargin The new Y margin.
   * 
   * @throws IllegalArgumentException If the number is out or range.
   * 
   * @see #MINIMUM_MARGIN
   * @see #MAXIMUM_MARGIN
   * 
   */
  public final void setYMargin (final int yMargin)
  {
    if (yMargin < MINIMUM_MARGIN || yMargin > MAXIMUM_MARGIN)
      throw new IllegalArgumentException ();
    if (yMargin != this.yMargin)
    {
      this.yMargin = yMargin;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT TRACE PAINT [STROKE]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The minimum supported default trace line width.
   * 
   */
  public final static float MINIMUM_DEFAULT_TRACE_LINE_WIDTH = 0f;
  
  /** The maximum supported default trace line width.
   * 
   */
  public final static float MAXIMUM_DEFAULT_TRACE_LINE_WIDTH = 10f;
  
  /** The default trace line width (in absence of applicable Z modulation).
   * 
   */
  public final static float DEFAULT_TRACE_LINE_WIDTH = 1f;
  
  private volatile float defaultTraceLineWidth = DEFAULT_TRACE_LINE_WIDTH;

  /** Returns the default trace line width.
   * 
   * @return The default trace line width (in absence of applicable Z modulation).
   * 
   */  
  public final float getDefaultTraceLineWidth ()
  {
    // No need to synchronize here; float reads are atomic.
    return this.defaultTraceLineWidth;
  }

  /** Sets the default trace line width.
   * 
   * @param defaultTraceLineWidth The new default trace line width (in absence of applicable Z modulation).
   * 
   * @throws IllegalArgumentException If the number is out or range.
   * 
   * @see #MINIMUM_DEFAULT_TRACE_LINE_WIDTH
   * @see #MAXIMUM_DEFAULT_TRACE_LINE_WIDTH
   * 
   */
  public final void setDefaultTraceLineWidth (final float defaultTraceLineWidth)
  {
    if (defaultTraceLineWidth < MINIMUM_DEFAULT_TRACE_LINE_WIDTH
      || defaultTraceLineWidth > MAXIMUM_DEFAULT_TRACE_LINE_WIDTH)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (defaultTraceLineWidth != this.defaultTraceLineWidth)
      {
        this.defaultTraceLineWidth = defaultTraceLineWidth;
        SwingUtilities.invokeLater (() -> repaint ());
      }
    }
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Z MODULATION POLICY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The Z Modulation policy.
   * 
   */
  public enum ZModulationDisplayPolicy
  {
    
    /** Prohibit displaying Z Modulation.
     * 
     */
    Z_NONE ("None"),
    /** Z Modulation through trace brightness (this is the default).
     * 
     */
    Z_BRIGHTNESS ("Brightness"),
    /** Z Modulation through trace line width.
     * 
     */
    Z_LINE_WIDTH ("Line Width");
    
    final String string;

    private ZModulationDisplayPolicy (final String string)
    {
      this.string = string;
    }

    /** Returns a human-readable {@code String} representing this {@link ZModulationDisplayPolicy} value.
     * 
     * @return The {@code String} representation of this {@link ZModulationDisplayPolicy} value.
     * 
     */
    @Override
    public final String toString ()
    {
      return this.string;
    }
    
  }
  
  private volatile ZModulationDisplayPolicy zModulationDisplayPolicy = ZModulationDisplayPolicy.Z_BRIGHTNESS;
  
  /** Returns the Z Modulation policy.
   * 
   * @return The Z Modulation policy.
   * 
   */
  public final ZModulationDisplayPolicy getZModulationDisplayPolicy ()
  {
    return this.zModulationDisplayPolicy;
  }
  
  /** Sets the Z Modulation policy.
   * 
   * @param zModulationDisplayPolicy The new Z Modulation policy.
   * 
   * @throws IllegalArgumentException If the argument is {@code null}.
   * 
   */
  public final void setZModulationDisplayPolicy (final ZModulationDisplayPolicy zModulationDisplayPolicy)
  {
    if (zModulationDisplayPolicy == null)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (zModulationDisplayPolicy != this.zModulationDisplayPolicy)
      {
        this.zModulationDisplayPolicy = zModulationDisplayPolicy;
        SwingUtilities.invokeLater (() -> repaint ());
      }
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Z MODULATION LEVELS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The minimum number of Z Modulation levels supported.
   * 
   */
  public final static int MINIMUM_Z_MODULATION_LEVELS = 1;
  
  /** The maximum number of Z Modulation levels supported.
   * 
   */
  public final static int MAXIMUM_Z_MODULATION_LEVELS = 32;
  
  /** The default number of Z Modulation levels.
   * 
   */
  public final static int DEFAULT_Z_MODULATION_LEVELS = 8;
  
  private volatile int zModulationLevels = DEFAULT_Z_MODULATION_LEVELS;
  
  /** Returns the number of Z Modulation levels.
   * 
   * @return The number of Z Modulation levels.
   * 
   */
  public final int getZModulationLevels ()
  {
    return this.zModulationLevels;
  }

  /** Sets the number of Z Modulation levels.
   * 
   * @param zModulationLevels The new number of Z Modulation levels.
   * 
   * @throws IllegalArgumentException If the number is out or range.
   * 
   * @see #MINIMUM_Z_MODULATION_LEVELS
   * @see #MAXIMUM_Z_MODULATION_LEVELS
   * 
   */
  public final void setZModulationLevels (final int zModulationLevels)
  {
    if (zModulationLevels < MINIMUM_Z_MODULATION_LEVELS
      || zModulationLevels > MAXIMUM_Z_MODULATION_LEVELS)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (zModulationLevels != this.zModulationLevels)
      {
        this.zModulationLevels = zModulationLevels;
        SwingUtilities.invokeLater (() -> repaint ());
      }
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Z MODULATION [BRIGHTNESS]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The minimum supported minimum brightness for Z Brightness Modulation.
   * 
   */
  public final static float MINIMUM_Z_MODULATION_MINIMUM_BRIGHTNESS = 0f;
  
  /** The maximum supported minimum brightness for Z Brightness Modulation.
   * 
   */
  public final static float MAXIMUM_Z_MODULATION_MINIMUM_BRIGHTNESS = 1f;
  
  /** The default minimum brightness for Z Brightness Modulation.
   * 
   */
  public final static float DEFAULT_Z_MODULATION_MINIMUM_BRIGHTNESS = 0.25f;
  
  private volatile float zModulationMinimumBrightness = DEFAULT_Z_MODULATION_MINIMUM_BRIGHTNESS;
  
  /** Returns the minimum brightness for Z Brightness Modulation.
   * 
   * @return The minimum brightness for Z Brightness Modulation.
   * 
   */
  public final float getZModulationMinimumBrightness ()
  {
    // Float reads are atomic.
    return this.zModulationMinimumBrightness;
  }

  /** Sets the minimum brightness for Z Brightness Modulation.
   * 
   * @param zModulationMinimumBrightness The new minimum brightness for Z Brightness Modulation.
   * 
   * @throws IllegalArgumentException If the number is out or range.
   * 
   * @see #MINIMUM_Z_MODULATION_MINIMUM_BRIGHTNESS
   * @see #MAXIMUM_Z_MODULATION_MINIMUM_BRIGHTNESS
   * 
   */
  public final void setZModulationMinimumBrightness (final float zModulationMinimumBrightness)
  {
    if (zModulationMinimumBrightness < MINIMUM_Z_MODULATION_MINIMUM_BRIGHTNESS
      || zModulationMinimumBrightness > MAXIMUM_Z_MODULATION_MINIMUM_BRIGHTNESS)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (zModulationMinimumBrightness != this.zModulationMinimumBrightness)
      {
        this.zModulationMinimumBrightness = zModulationMinimumBrightness;
        SwingUtilities.invokeLater (() -> repaint ());
      }
    }
  }
  
  private static Color getColorWithBrightness (final Color color, final float b)
  {
    if (color == null || b < 0 || b > 1)
      throw new IllegalArgumentException ();
    final float[] hsb = Color.RGBtoHSB (color.getRed (), color.getGreen (), color.getBlue (), null);
    return Color.getHSBColor (hsb[0], hsb[1], b);
  }
  
  private static Color getZModulationBrightnessColor (
    final Color traceColor,
    final double z, final double minZ, final double maxZ,
    final int zModulationLevels,
    final float zModulationMinimumBrightness, final float zModulationMaximumBrightness)
  {
    if (traceColor == null || minZ > maxZ)
      throw new IllegalArgumentException ();
    final int L = zModulationLevels;
    final float minB = zModulationMinimumBrightness;
    final float maxB = zModulationMaximumBrightness;
    final int colorIndex;
    if (z < minZ)
      colorIndex = 0;
    else if (z >= maxZ)
      colorIndex = L - 1;
    else
      colorIndex = Math.min (L - 1, (int) Math.floor (L * (z - minZ) / (maxZ - minZ)));
    return getColorWithBrightness (traceColor, minB + (maxB - minB) * (colorIndex + 0.5f) / L);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Z MODULATION [LINE WIDTH]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The minimum supported minimum line width for Z Line Width Modulation.
   * 
   */
  public final static float MINIMUM_Z_MODULATION_MINIMUM_LINE_WIDTH = 0f;
  
  /** The maximum supported minimum line width for Z Line Width Modulation.
   * 
   */
  public final static float MAXIMUM_Z_MODULATION_MINIMUM_LINE_WIDTH = 2f;
  
  /** The default minimum line width for Z Line Width Modulation.
   * 
   */
  public final static float DEFAULT_Z_MODULATION_MINIMUM_LINE_WIDTH = 1f;
  
  private volatile float zModulationMinimumLineWidth = DEFAULT_Z_MODULATION_MINIMUM_LINE_WIDTH;
  
  /** Returns the minimum line width for Z Line Width Modulation.
   * 
   * @return The minimum line width for Z Line Width Modulation.
   * 
   */
  public final float getZModulationMinimumLineWidth ()
  {
    return this.zModulationMinimumLineWidth;
  }

  /** Sets the minimum line width for Z Line Width Modulation.
   * 
   * <p>
   * The maximum line width may be changed as well in order to ensure {@code min <= max}.
   * 
   * @param zModulationMinimumLineWidth The new minimum line width for Z Line Width Modulation.
   * 
   * @throws IllegalArgumentException If the number is out or range.
   * 
   * @see #MINIMUM_Z_MODULATION_MINIMUM_LINE_WIDTH
   * @see #MAXIMUM_Z_MODULATION_MINIMUM_LINE_WIDTH
   * 
   */
  public final void setZModulationMinimumLineWidth (final float zModulationMinimumLineWidth)
  {
    if (zModulationMinimumLineWidth < MINIMUM_Z_MODULATION_MINIMUM_LINE_WIDTH
      || zModulationMinimumLineWidth > MAXIMUM_Z_MODULATION_MINIMUM_LINE_WIDTH)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (zModulationMinimumLineWidth != this.zModulationMinimumLineWidth)
      {
        this.zModulationMinimumLineWidth = zModulationMinimumLineWidth;
        if (this.zModulationMaximumLineWidth < this.zModulationMinimumLineWidth)
          this.zModulationMaximumLineWidth = this.zModulationMinimumLineWidth;
        SwingUtilities.invokeLater (() -> repaint ());
      }
    }
  }
  
  /** The minimum supported maximum line width for Z Line Width Modulation.
   * 
   */
  public final static float MINIMUM_Z_MODULATION_MAXIMUM_LINE_WIDTH = 1f;
  
  /** The maximum supported maximum line width for Z Line Width Modulation.
   * 
   */
  public final static float MAXIMUM_Z_MODULATION_MAXIMUM_LINE_WIDTH = 32f;
  
  /** The default maximum line width for Z Line Width Modulation.
   * 
   */
  public final static float DEFAULT_Z_MODULATION_MAXIMUM_LINE_WIDTH = 8f;
  
  private volatile float zModulationMaximumLineWidth = DEFAULT_Z_MODULATION_MAXIMUM_LINE_WIDTH;
  
  /** Returns the maximum line width for Z Line Width Modulation.
   * 
   * @return The maximum line width for Z Line Width Modulation.
   * 
   */
  public final float getZModulationMaximumLineWidth ()
  {
    return this.zModulationMaximumLineWidth;
  }

  /** Sets the maximum line width for Z Line Width Modulation.
   * 
   * <p>
   * The minimum line width may be changed as well in order to ensure {@code min <= max}.
   * 
   * @param zModulationMaximumLineWidth The new maximum line width for Z Line Width Modulation.
   * 
   * @throws IllegalArgumentException If the number is out or range.
   * 
   * @see #MINIMUM_Z_MODULATION_MAXIMUM_LINE_WIDTH
   * @see #MAXIMUM_Z_MODULATION_MAXIMUM_LINE_WIDTH
   * 
   */
  public final void setZModulationMaximumLineWidth (final float zModulationMaximumLineWidth)
  {
    if (zModulationMaximumLineWidth < MINIMUM_Z_MODULATION_MAXIMUM_LINE_WIDTH
      || zModulationMaximumLineWidth > MAXIMUM_Z_MODULATION_MAXIMUM_LINE_WIDTH)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (zModulationMaximumLineWidth != this.zModulationMaximumLineWidth)
      {
        this.zModulationMaximumLineWidth = zModulationMaximumLineWidth;
        if (this.zModulationMinimumLineWidth > this.zModulationMaximumLineWidth)
          this.zModulationMinimumLineWidth = this.zModulationMaximumLineWidth;
        SwingUtilities.invokeLater (() -> repaint ());
      }
    }
  }
  
  private static Stroke getZModulationStroke (
    final Stroke stroke,
    final double z, final double minZ, final double maxZ,
    final int zModulationLevels,
    final float zModulationMinimumLineWidth,
    final float zModulationMaximumLineWidth)
  {
    if (stroke == null || minZ > maxZ)
      throw new IllegalArgumentException ();
    final int L = zModulationLevels;
    final float minLW = zModulationMinimumLineWidth;
    final float maxLW = zModulationMaximumLineWidth;
    final int strokeIndex;
    if (z < minZ)
      strokeIndex = 0;
    else if (z >= maxZ)
      strokeIndex = L - 1;
    else
      strokeIndex = Math.min (L - 1, (int) Math.floor (L * (z - minZ) / (maxZ - minZ)));
    return L == 1 ? new BasicStroke (minLW) : new BasicStroke (minLW + (maxLW - minLW) * strokeIndex / (L - 1));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // GRATICULE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableGraticule = true;
  
  /** Returns whether to display the graticule (default {@code true}).
   * 
   * @return Whether to display the graticule.
   * 
   */
  public final boolean isEnableGraticule ()
  {
    return this.enableGraticule;
  }
  
  /** Toggles displaying the graticule.
   * 
   */
  public final void toggleEnableGraticule ()
  {
    synchronized (this)
    {
      this.enableGraticule = ! this.enableGraticule;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  /** Sets whether or not to paint the graticule.
   * 
   * @param enableGraticule Whether or not to paint the graticule.
   * 
   */
  public final void setEnableGraticule (final boolean enableGraticule)
  {
    synchronized (this)
    {
      if (this.enableGraticule != enableGraticule)
        toggleEnableGraticule ();
    }
  }
  
  private volatile boolean enableGraticuleXAxis = true;
  
  /** Returns whether or not the graticule X axis is (explicitly) painted.
   * 
   * @return Whether or not the graticule X axis is (explicitly) painted.
   * 
   */
  public final boolean isEnableGraticuleXAxis ()
  {
    return this.enableGraticuleXAxis;
  }
  
  /** Toggles painting the graticule X axis (explicitly).
   * 
   */
  public final void toggleEnableGraticuleXAxis ()
  {
    synchronized (this)
    {
      this.enableGraticuleXAxis = ! this.enableGraticuleXAxis;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  /** Sets whether or not to paint the graticule X axis (explicitly).
   * 
   * @param enableGraticuleXAxis Whether or not to paint the graticule X axis (explicitly).
   * 
   */
  public final void setEnableGraticuleXAxis (final boolean enableGraticuleXAxis)
  {
    synchronized (this)
    {
      if (this.enableGraticuleXAxis != enableGraticuleXAxis)
        toggleEnableGraticuleXAxis ();
    }
  }
  
  private volatile boolean enableGraticuleXAxisTicks = true;
  
  /** Returns whether or not the graticule X axis tick marks are painted.
   * 
   * @return Whether or not the graticule X axis tick marks are painted.
   * 
   */
  public final boolean isEnableGraticuleXAxisTicks ()
  {
    return this.enableGraticuleXAxisTicks;
  }
  
  /** Toggles painting the graticule X axis tick marks.
   * 
   */
  public final void toggleEnableGraticuleXAxisTicks ()
  {
    synchronized (this)
    {
      this.enableGraticuleXAxisTicks = ! this.enableGraticuleXAxisTicks;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  /** Sets whether or not to paint the graticule X axis tick marks.
   * 
   * @param enableGraticuleXAxisTicks  Whether or not to paint the graticule X axis tick marks.
   * 
   */
  public final void setEnableGraticuleXAxisTicks (final boolean enableGraticuleXAxisTicks)
  {
    synchronized (this)
    {
      if (this.enableGraticuleXAxisTicks != enableGraticuleXAxisTicks)
        toggleEnableGraticuleXAxisTicks ();
    }
  }
  
  private volatile boolean enableGraticuleYAxis = true;
  
  /** Returns whether or not the graticule Y axis is (explicitly) painted.
   * 
   * @return Whether or not the graticule Y axis is (explicitly) painted.
   * 
   */
  public final boolean isEnableGraticuleYAxis ()
  {
    return this.enableGraticuleYAxis;
  }
  
  /** Toggles painting the graticule Y axis (explicitly).
   * 
   */
  public final void toggleEnableGraticuleYAxis ()
  {
    synchronized (this)
    {
      this.enableGraticuleYAxis = ! this.enableGraticuleYAxis;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  /** Sets whether or not to paint the graticule Y axis (explicitly).
   * 
   * @param enableGraticuleYAxis Whether or not to paint the graticule Y axis (explicitly).
   * 
   */
  public final void setEnableGraticuleYAxis (final boolean enableGraticuleYAxis)
  {
    synchronized (this)
    {
      if (this.enableGraticuleYAxis != enableGraticuleYAxis)
        toggleEnableGraticuleYAxis ();
    }
  }
  
  private volatile boolean enableGraticuleYAxisTicks = true;
  
  /** Returns whether or not the graticule Y axis tick marks are painted.
   * 
   * @return Whether or not the graticule Y axis tick marks are painted.
   * 
   */
  public final boolean isEnableGraticuleYAxisTicks ()
  {
    return this.enableGraticuleYAxisTicks;
  }
  
  /** Toggles painting the graticule Y axis tick marks.
   * 
   */
  public final void toggleEnableGraticuleYAxisTicks ()
  {
    synchronized (this)
    {
      this.enableGraticuleYAxisTicks = ! this.enableGraticuleYAxisTicks;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  /** Sets whether or not to paint the graticule Y axis tick marks.
   * 
   * @param enableGraticuleYAxisTicks  Whether or not to paint the graticule Y axis tick marks.
   * 
   */
  public final void setEnableGraticuleYAxisTicks (final boolean enableGraticuleYAxisTicks)
  {
    synchronized (this)
    {
      if (this.enableGraticuleYAxisTicks != enableGraticuleYAxisTicks)
        toggleEnableGraticuleYAxisTicks ();
    }
  }
  
  /** The default graticule {@link Color}.
   * 
   */
  public static final Color DEFAULT_GRATICULE_COLOR = Color.green.darker ();
  
  private volatile Color graticuleColor = DEFAULT_GRATICULE_COLOR;
  
  private static final Stroke GRATICULE_STROKE =
      new BasicStroke (0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);
  
  /** Gets the graticule {@link Color}.
   * 
   * @return The graticule color.
   * 
   */
  public final Color getGraticuleColor ()
  {
    return this.graticuleColor;
  }
  
  /** Sets the graticule {@link Color}.
   * 
   * @param color The new graticule color.
   * 
   * @throws IllegalArgumentException If {@code color == null}.
   * 
   */
  public final void setGraticuleColor (final Color color)
  {
    if (color == null)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      this.graticuleColor = color;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  /** The default graticule highlight {@link Color} (used for axes).
   * 
   */
  public static final Color DEFAULT_GRATICULE_HIGHLIGHT_COLOR = Color.green;
  
  private volatile Color graticuleHighlightColor = DEFAULT_GRATICULE_HIGHLIGHT_COLOR;
  
  /** Returns the graticule highlight {@link Color} (used for axes).
   * 
   * @return The graticule highlight color.
   * 
   */
  public final Color getGraticuleHighlightColor ()
  {
    return this.graticuleHighlightColor;
  }
  
  /** Sets the graticule highlight {@link Color} (used for axes).
   * 
   * @param color The new graticule highlight color.
   * 
   * @throws IllegalArgumentException If {@code color == null}.
   * 
   */
  public final void setGraticuleHighlightColor (final Color color)
  {
    if (color == null)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      this.graticuleHighlightColor = color;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  /** The location of the origin of the graticule on the display.
   * 
   */
  public static enum GraticuleOrigin
  {
    /** Top left.
     * 
     */
    NorthWest,
    /** Top center.
     *
     */
    North,
    /** Top right.
     * 
     */
    NorthEast,
    /** Center left.
     * 
     */
    West,
    /** Center screen.
     * 
     */
    Center,
    /** Center right.
     * 
     */
    East,
    /** Bottom left.
     * 
     */
    SouthWest,
    /** Bottom center.
     * 
     */
    South,
    /** Bottom right.
     * 
     */
    SouthEast;
  }
  
  private volatile GraticuleOrigin graticuleOrigin = GraticuleOrigin.Center;
  
  /** Returns the origin of the graticule on the screen.
   * 
   * @return The origin of the graticule on the screen.
   * 
   */
  public final GraticuleOrigin getGraticuleOrigin ()
  {
    return this.graticuleOrigin;
  }
  
  /** Sets the origin of the graticule on the screen.
   * 
   * @param graticuleOrigin The new origin of the graticule on the screen.
   * 
   * @throws IllegalArgumentException If the argument is {@code null}.
   * 
   */
  public final void setGraticuleOrigin (final GraticuleOrigin graticuleOrigin)
  {
    if (graticuleOrigin == null)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (graticuleOrigin != this.graticuleOrigin)
      {
        this.graticuleOrigin = graticuleOrigin;
        SwingUtilities.invokeLater (() -> repaint ());      
      }
    }
  }
  
  private void paintGraticule (
    final Graphics2D g2d,
    final int width, final int height,
    final int xMargin, final int yMargin,
    final boolean enableGraticule,
    final int xDivisions,
    final int yDivisions,
    final GraticuleOrigin graticuleOrigin,
    final Color graticuleColor,
    final Color graticuleHighlightColor,
    final boolean enableGraticuleXAxis,
    final boolean enableGraticuleYAxis,
    final boolean enableGraticuleXAxisTicks,
    final boolean enableGraticuleYAxisTicks)
  {
    
    if (! enableGraticule)
      return;
    
    if (g2d == null)
      return;
    
    g2d.setStroke (GRATICULE_STROKE);
    
    final int i_XAxis;
    final int i_YAxis;
    switch (graticuleOrigin)
    {
      case NorthWest:
        i_XAxis = 0;
        i_YAxis = 0;
        break;
      case North:
        i_XAxis = 0;
        i_YAxis = (xDivisions % 2 == 0 ? xDivisions / 2 : -1);
        break;
      case NorthEast:
        i_XAxis = 0;
        i_YAxis = xDivisions;
        break;
      case West:
        i_XAxis = (yDivisions % 2 == 0 ? yDivisions / 2 : -1);
        i_YAxis = 0;
        break;
      case Center:
        i_XAxis = (yDivisions % 2 == 0 ? yDivisions / 2 : -1);
        i_YAxis = (xDivisions % 2 == 0 ? xDivisions / 2 : -1);
        break;
      case East:
        i_XAxis = (yDivisions % 2 == 0 ? yDivisions / 2 : -1);
        i_YAxis = xDivisions;
        break;
      case SouthWest:
        i_XAxis = yDivisions;
        i_YAxis = 0;
        break;
      case South:
        i_XAxis = yDivisions;
        i_YAxis = (xDivisions % 2 == 0 ? xDivisions / 2 : -1);
        break;
      case SouthEast:
        i_XAxis = yDivisions;
        i_YAxis = xDivisions;
        break;
      default:
        throw new IllegalArgumentException ();
    }
    
    // The Y=constant lines of the graticule with special treatment for the X axis and the tick marks on the Y axis.
    for (int i = 0; i <= yDivisions; i++)
    {
      // Switch color if we are drawing the X axis.
      if (enableGraticuleXAxis && i == i_XAxis)
        g2d.setColor (graticuleHighlightColor);
      else
        g2d.setColor (graticuleColor);
      final int y = yMargin + (int) Math.round ((i / (double) yDivisions) * (height - 2 * yMargin));
      // Horizontal line at y.
      g2d.drawLine (xMargin, y, width - xMargin, y);
      // Tick marks on the Y axis.
      if (enableGraticuleYAxis && enableGraticuleYAxisTicks && i_YAxis >= 0)
      {
        g2d.setColor (graticuleHighlightColor);
        final int xCenter = (int) Math.round (xMargin + i_YAxis * (width - 2.0 * xMargin) / ((double) xDivisions));
        for (int j = 1; j <= 9; j++)
        {
          final int yMinor = y + (int) Math.round ((height - 2 * yMargin) * j / (10.0 * yDivisions));
          if (j == 5)
            g2d.drawLine (xCenter - 4, yMinor, xCenter + 5, yMinor);
          else
            g2d.drawLine (xCenter - 2, yMinor, xCenter + 3, yMinor);
        }
      }
    }
    
    // The X=constant lines of the graticule with special treatment for the Y axis and the tick marks on the X axis.
    for (int i = 0; i <= xDivisions; i++)
    {
      // Switch color if we are drawing the Y axis.
      if (enableGraticuleYAxis && i == i_YAxis)
        g2d.setColor (graticuleHighlightColor);
      else
        g2d.setColor (graticuleColor);
      final int x = xMargin + (int) Math.round ((i / (double) xDivisions) * (width - 2 * xMargin));
      // Vertical line at x.
      g2d.drawLine (x, yMargin, x, height - yMargin);
      // Tick marks on the X axis.
      if (enableGraticuleXAxis && enableGraticuleXAxisTicks && i_XAxis >= 0)
      {
        g2d.setColor (graticuleHighlightColor);
        final int yCenter = (int) Math.round (yMargin + i_XAxis * (height - 2.0 * yMargin) / ((double) yDivisions));
        for (int j = 1; j <= 9; j++)
        {
          final int xMinor = x + (int) Math.round ((width - 2 * xMargin) * j / (10.0 * xDivisions));
          if (j == 5)
            g2d.drawLine (xMinor, yCenter - 4, xMinor, yCenter + 5);
          else
            g2d.drawLine (xMinor, yCenter - 2, xMinor, yCenter + 3);
        }
      }
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TRACES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableTraces = true;
  
  /** Returns whether or not to display traces,
   * 
   * @return Whether or not to display traces,
   * 
   */
  public final boolean isEnableTraces ()
  {
    return this.enableTraces;
  }
  
  /** Sets whether or not to display traces.
   * 
   * @param enableTraces Whether or not to display traces.
   * 
   */
  public final void setEnableTraces (final boolean enableTraces)
  {
    final boolean changed;
    synchronized (this)
    {
      changed = enableTraces != this.enableTraces;
      this.enableTraces = enableTraces;
    }
    if (changed)
      SwingUtilities.invokeLater (() -> repaint ());
  }
  
  /** The default clip {@link Color}.
   * 
   */
  public final static Color DEFAULT_CLIP_COLOR = Color.red;
  
  private volatile Color clipColor = DEFAULT_CLIP_COLOR;
  
  /** Returns the clip {@link Color}.
   * 
   * @return The clip color (non-{@code null}).
   * 
   */
  public final Color getClipColor ()
  {
    return this.clipColor;
  }
  
  /** Sets the clip {@link Color}.
   * 
   * @param color The clip color, when {@code null}, the {@link #DEFAULT_CLIP_COLOR} is taken.
   * 
   */
  public final void setClipColor (final Color color)
  {
    if (color == null)
      setClipColor (DEFAULT_CLIP_COLOR);
    else
      synchronized (this)
      {
        if (color != this.clipColor)
        {
          this.clipColor = color;
          SwingUtilities.invokeLater (this::repaint);
        }
      }
  }
  
  private void paintTraces (
    final Graphics2D g2d,
    final int width, final int height,
    final int xMargin, final int yMargin,
    final boolean enableTraces,
    final Map<K, TraceEntry> traceEntries,
    final Color clipColor,
    final float defaultTraceLineWidth,
    final ZModulationDisplayPolicy zModulationDisplayPolicy,
    final int zModulationLevels,
    final float zModulationMinimumBrightness,
    final float zModulationMaximumBrightness,
    final float zModulationMinimumLineWidth,
    final float zModulationMaximumLineWidth)
  {
    
    if (! enableTraces)
      return;
    if (g2d == null)
      return;
    if (traceEntries == null)
      return;
    
    for (final Map.Entry<K, TraceEntry> traceEntriesEntry : traceEntries.entrySet ())
    {
      if (traceEntriesEntry == null)
        continue;
      final K key = traceEntriesEntry.getKey ();
      final TraceEntry traceEntry = traceEntriesEntry.getValue ();
      if (traceEntry == null)
        continue;
      final TraceData traceData = traceEntry.getTraceData ();
      if (traceData == null)
        continue;
      final Color traceColor = traceEntry.getTraceColor ();
      if (traceColor == null)
        continue;
      final TraceData.Type traceDataType = traceData.getType ();
      switch (traceDataType)
      {
        case Yn:
        {
          final double[] yn = traceData.getYnData ();
          if (yn == null || yn.length == 0)
            continue;
          final TraceData.IntegerRange nRange = traceData.getNRange ();
          final TraceData.DoubleRange xRange = traceData.getXRange ();
          final TraceData.DoubleRange yRange = traceData.getYRange ();
          paintTrace_XYZn (
            g2d,
            width, height,
            xMargin, yMargin,
            null, yn, null,
            nRange, xRange, yRange, null,
            traceColor, clipColor,
            defaultTraceLineWidth,
            zModulationDisplayPolicy,
            zModulationLevels,
            zModulationMinimumBrightness, zModulationMaximumBrightness,
            zModulationMinimumLineWidth, zModulationMaximumLineWidth);
          break;
        }
        case YnZn:
        {
          final double[] yn = traceData.getYnData ();
          final double[] zn = traceData.getZnData ();
          if (yn == null || yn.length == 0 || zn == null || zn.length == 0)
            continue;
          final TraceData.IntegerRange nRange = traceData.getNRange ();
          final TraceData.DoubleRange xRange = traceData.getXRange ();
          final TraceData.DoubleRange yRange = traceData.getYRange ();
          final TraceData.DoubleRange zRange = traceData.getZRange ();
          paintTrace_XYZn (
            g2d,
            width, height,
            xMargin, yMargin,
            null, yn, zn,
            nRange, xRange, yRange, zRange,
            traceColor, clipColor,
            defaultTraceLineWidth,
            zModulationDisplayPolicy,
            zModulationLevels,
            zModulationMinimumBrightness, zModulationMaximumBrightness,
            zModulationMinimumLineWidth, zModulationMaximumLineWidth);
          break;          
        }
        case XnYn:
        {
          final double[] xn = traceData.getXnData ();
          final double[] yn = traceData.getYnData ();
          if (xn == null || xn.length == 0 || yn == null || yn.length == 0)
            continue;
          final TraceData.IntegerRange nRange = traceData.getNRange ();
          final TraceData.DoubleRange xRange = traceData.getXRange ();
          final TraceData.DoubleRange yRange = traceData.getYRange ();
          paintTrace_XYZn (
            g2d,
            width, height,
            xMargin, yMargin,
            xn, yn, null,
            nRange, xRange, yRange, null,
            traceColor, clipColor,
            defaultTraceLineWidth,
            zModulationDisplayPolicy,
            zModulationLevels,
            zModulationMinimumBrightness, zModulationMaximumBrightness,
            zModulationMinimumLineWidth, zModulationMaximumLineWidth);
          break;          
        }
        case XnYnZn:
        {
          final double[] xn = traceData.getXnData ();
          final double[] yn = traceData.getYnData ();
          final double[] zn = traceData.getZnData ();
          if (xn == null || xn.length == 0 || yn == null || yn.length == 0 || zn == null || zn.length == 0)
            continue;
          final TraceData.IntegerRange nRange = traceData.getNRange ();
          final TraceData.DoubleRange xRange = traceData.getXRange ();
          final TraceData.DoubleRange yRange = traceData.getYRange ();
          final TraceData.DoubleRange zRange = traceData.getZRange ();
          paintTrace_XYZn (
            g2d,
            width, height,
            xMargin, yMargin,
            xn, yn, zn,
            nRange, xRange, yRange, zRange,
            traceColor, clipColor,
            defaultTraceLineWidth,
            zModulationDisplayPolicy,
            zModulationLevels,
            zModulationMinimumBrightness, zModulationMaximumBrightness,
            zModulationMinimumLineWidth, zModulationMaximumLineWidth);
          break;          
        }
        case F_Y_vs_X:
        {
          final Function<Double, Double> f_y_vs_x = traceData.getF_Y_vs_X ();
          if (f_y_vs_x == null)
            continue;
          final TraceData.IntegerRange nRange = traceData.getNRange ();
          final TraceData.DoubleRange xRange = traceData.getXRange ();
          final TraceData.DoubleRange yRange = traceData.getYRange ();
          paintTrace_F_Y_vs_X (
            g2d,
            width, height,
            xMargin, yMargin,
            f_y_vs_x,
            nRange, xRange, yRange,
            traceColor, clipColor,
            defaultTraceLineWidth,
            zModulationDisplayPolicy,
            zModulationLevels,
            zModulationMinimumBrightness, zModulationMaximumBrightness,
            zModulationMinimumLineWidth, zModulationMaximumLineWidth);
          break;          
        }
        default:
          continue;
      }
    }    
  }

  private void paintTrace_XYZn
  (final Graphics2D g2d,
   final int width, final int height,
   final int xMargin, final int yMargin,
   final double[] xn, final double[] yn, final double[] zn,
   final TraceData.IntegerRange nRange,
   final TraceData.DoubleRange xRange,
   final TraceData.DoubleRange yRange,
   final TraceData.DoubleRange zRange,
   final Color traceColor,
   final Color clipColor,
   final float defaultTraceLineWidth,
   final ZModulationDisplayPolicy zModulationDisplayPolicy,
   final int zModulationLevels,
   final float zModulationMinimumBrightness,
   final float zModulationMaximumBrightness,
   final float zModulationMinimumLineWidth,
   final float zModulationMaximumLineWidth)
  {
    
    if (yn == null || yn.length == 0 || traceColor == null)
      return;
    if (xn != null && xn.length != yn.length)
      return;
    if (zn != null && zn.length != yn.length)
      return;
    
    final int minN;
    final int maxN;
    if (nRange != null)
    {
      minN = nRange.getMin ();
      maxN = nRange.getMax ();
    }
    else
    {
      minN = 0;
      maxN = yn.length;
    }
    
    final boolean hasXn = xn != null;
    final double minX;
    final double maxX;
    if (xRange != null)
    {
      minX = xRange.getMin ();
      maxX = xRange.getMax ();
    }
    else
    {
      minX = 0;
      maxX = yn.length;
    }
    
    final boolean hasYn = yn != null;
    final double minY;
    final double maxY;
    if (yRange != null)
    {
      minY = yRange.getMin ();
      maxY = yRange.getMax ();
    }
    else
    {
      double y_min_now = Double.POSITIVE_INFINITY;
      double y_max_now = Double.NEGATIVE_INFINITY;
      for (int n = 0; n < yn.length; n++)
      {
        if (yn[n] < y_min_now)
          y_min_now = yn[n];
        if (yn[n] > y_max_now)
          y_max_now = yn[n];
      }
      if (Double.isInfinite (y_min_now)
        || Double.isInfinite (y_max_now)
        || y_max_now - y_min_now < 1.0E-16 /* XXX SOME EPSILON */)
      {
        y_min_now = 0;
        y_max_now = 1;
      }
      minY = y_min_now;
      maxY = y_max_now;
    }
    
    final boolean hasZn = (zn != null);
    final double minZ;
    final double maxZ;
    if (hasZn)
    {
      if (zRange != null)
      {
        minZ = zRange.getMin ();
        maxZ = zRange.getMax ();
      }
      else
      {
        double z_min_now = Double.POSITIVE_INFINITY;
        double z_max_now = Double.NEGATIVE_INFINITY;
        for (int n = 0; n < zn.length; n++)
        {
          if (zn[n] < z_min_now)
            z_min_now = zn[n];
          if (zn[n] > z_max_now)
            z_max_now = zn[n];
        }
        if (Double.isInfinite (z_min_now)
          || Double.isInfinite (z_max_now)
          || z_max_now - z_min_now < 1.0E-16 /* XXX SOME EPSILON */)
        {
          z_min_now = 0;
          z_max_now = 1;
        }
        minZ = z_min_now;
        maxZ = z_max_now;
      }
    }
    else
    {
      // Dumnmy values; never used.
      minZ = Double.POSITIVE_INFINITY;
      maxZ = Double.NEGATIVE_INFINITY;      
    }
    
    final int traceLength = yn.length;
    double x_n_g2d_prev = 0;
    double y_n_g2d_prev = 0;
    boolean clipped = false;
    for (int n = 0; n < traceLength; n++) // XXX Still requires correction for the n range.
    {
      
      double x_n_g2d;
      if (xn == null)
        x_n_g2d = xMargin + ((double) width - 2 * xMargin) * n / yn.length;
      else
        x_n_g2d = xMargin + ((double) width - 2 * xMargin) * (xn[n] - minX) / (maxX - minX);
      if (x_n_g2d < xMargin)
      {
        clipped = true;
        x_n_g2d = xMargin;
        x_n_g2d_prev = xMargin;
      }
      else if (x_n_g2d > (width - xMargin))
      {
        clipped = true;
        x_n_g2d = width - xMargin;
        x_n_g2d_prev = width - xMargin;
      }
        
      double y_n_g2d = yMargin + (height - 2 * yMargin) * (maxY - yn[n]) / (maxY - minY);
      if (y_n_g2d < yMargin)
      {
        clipped = true;
        y_n_g2d = yMargin;
        y_n_g2d_prev = yMargin;
      }
      else if (y_n_g2d > (height - yMargin))
      {
        clipped = true;
        y_n_g2d = height - yMargin;
        y_n_g2d_prev = height - yMargin;
      }
      
      final Color zModulatedTraceColor;
      if (hasZn && zModulationDisplayPolicy == ZModulationDisplayPolicy.Z_BRIGHTNESS && (! clipped))
      {
        if (zn[n] < minZ || zn[n] > maxZ)
        {
          clipped = true;
          zModulatedTraceColor = traceColor; // dummy value; not used.
        }
        else
          zModulatedTraceColor = getZModulationBrightnessColor (
            traceColor,
            zn[n], minZ, maxZ,
            zModulationLevels, zModulationMinimumBrightness, zModulationMaximumBrightness);
      }
      else
        zModulatedTraceColor = traceColor;
      
      final Stroke zModulatedStroke;
      if (hasZn && zModulationDisplayPolicy == ZModulationDisplayPolicy.Z_LINE_WIDTH && (! clipped))
      {
        if (zn[n] < minZ || zn[n] > maxZ)
        {
          clipped = true;
          zModulatedStroke = new BasicStroke (defaultTraceLineWidth);
        }
        else
          zModulatedStroke = getZModulationStroke (
            g2d.getStroke (),
            zn[n], minZ, maxZ,
            zModulationLevels, zModulationMinimumLineWidth, zModulationMaximumLineWidth);
      }
      else
        zModulatedStroke = new BasicStroke (defaultTraceLineWidth);
      
      g2d.setColor (clipped ? clipColor : zModulatedTraceColor);
      g2d.setStroke (zModulatedStroke);
      
      if (n > 0)
        g2d.draw (new Line2D.Double (x_n_g2d_prev, y_n_g2d_prev, x_n_g2d, y_n_g2d));
      
      x_n_g2d_prev = x_n_g2d;
      y_n_g2d_prev = y_n_g2d;
      
      clipped = false;
      
    }
    
  }
  
  private void paintTrace_F_Y_vs_X
  (final Graphics2D g2d,
   final int width, final int height,
   final int xMargin, final int yMargin,
   final Function<Double, Double> f_y_vs_x,
   final TraceData.IntegerRange nRange,
   final TraceData.DoubleRange xRange,
   final TraceData.DoubleRange yRange,
   final Color traceColor,
   final Color clipColor,
   final float defaultTraceLineWidth,
   final ZModulationDisplayPolicy zModulationDisplayPolicy,
   final int zModulationLevels,
   final float zModulationMinimumBrightness,
   final float zModulationMaximumBrightness,
   final float zModulationMinimumLineWidth,
   final float zModulationMaximumLineWidth)   
  {
    
    if (f_y_vs_x == null || traceColor == null)
      return;
    
    if (xRange == null)
      return;

// XXX NEED TO FIGURE OUT N RANGE SEMANTICS FOR F_X_vs_Y!    
//    final int minN;
//    final int maxN;
//    if (nRange != null)
//    {
//      minN = nRange.getMin ();
//      maxN = nRange.getMax ();
//    }
//    else
//    {
//      minN = 0;
//      maxN = yn.length;
//    }

    // XXX THIS NEEDS REFINEMENT!!!
    final int N = TraceData.DEFAULT_N_RANGE_LENGTH;
    
    final double minX = xRange.getMin ();
    final double maxX = xRange.getMax ();
    
    final double minY;
    final double maxY;
    if (yRange != null)
    {
      minY = yRange.getMin ();
      maxY = yRange.getMax ();
    }
    else
    {
      // XXX THIS MAKES LITTLE SENSE DUE TO X DIGITIZATION...
      double y_min_now = Double.POSITIVE_INFINITY;
      double y_max_now = Double.NEGATIVE_INFINITY;
      for (int n = 0; n < N; n++)
      {
        final double x = xRange.getMin () + xRange.getLength () * n / N;
        final double y = f_y_vs_x.apply (x);
        if (y < y_min_now)
          y_min_now = y;
        if (y > y_max_now)
          y_max_now = y;
      }
      if (Double.isInfinite (y_min_now)
        || Double.isInfinite (y_max_now)
        || y_max_now - y_min_now < 1.0E-16 /* XXX SOME EPSILON */)
      {
        y_min_now = 0;
        y_max_now = 1;
      }
      minY = y_min_now;
      maxY = y_max_now;
    }
    
    final int traceLength = N;
    double x_n_g2d_prev = 0;
    double y_n_g2d_prev = 0;
    boolean clipped = false;
    for (int n = 0; n < traceLength; n++) // XXX Still requires correction for the n range.
    {
      
      final double x = xRange.getMin () + xRange.getLength () * n / N;
      
      double x_n_g2d = xMargin + ((double) width - 2 * xMargin) * (x - minX) / (maxX - minX);
      if (x_n_g2d < xMargin)
      {
        clipped = true;
        x_n_g2d = xMargin;
        x_n_g2d_prev = xMargin;
      }
      else if (x_n_g2d > (width - xMargin))
      {
        clipped = true;
        x_n_g2d = width - xMargin;
        x_n_g2d_prev = width - xMargin;
      }
      
      final double y = f_y_vs_x.apply (x);
      
      double y_n_g2d = yMargin + (height - 2 * yMargin) * (maxY - y) / (maxY - minY);
      if (y_n_g2d < yMargin)
      {
        clipped = true;
        y_n_g2d = yMargin;
        y_n_g2d_prev = yMargin;
      }
      else if (y_n_g2d > (height - yMargin))
      {
        clipped = true;
        y_n_g2d = height - yMargin;
        y_n_g2d_prev = height - yMargin;
      }
      
      g2d.setColor (clipped ? clipColor : traceColor);
      g2d.setStroke (new BasicStroke (defaultTraceLineWidth));
      
      if (n > 0)
        g2d.draw (new Line2D.Double (x_n_g2d_prev, y_n_g2d_prev, x_n_g2d, y_n_g2d));
      
      x_n_g2d_prev = x_n_g2d;
      y_n_g2d_prev = y_n_g2d;
      
      clipped = false;
      
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // CROSSHAIR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile boolean enableCrosshair = true;
  
  /** Returns whether or not to display the crosshair (and enable/disable its functions).
   * 
   * @return Whether or not to display the crosshair (and enable its functions).
   */
  public final boolean isEnableCrosshair ()
  {
    return this.enableCrosshair;
  }
  
  /** Toggles whether or not to display the crosshair (and enable/disable its functions).
   * 
   */
  public final void toggleEnableCrosshair ()
  {
    synchronized (this)
    {
      this.enableCrosshair = ! this.enableCrosshair;
      SwingUtilities.invokeLater (() -> repaint ());
    }
  }
  
  /** Sets whether or not to display the crosshair (and enable/disable its functions).
   * 
   * @param enableCrosshair Whether or not to display the crosshair (and enable/disable its functions).
   * 
   */
  public final void setEnableCrosshair (final boolean enableCrosshair)
  {
    synchronized (this)
    {
      if (this.enableCrosshair != enableCrosshair)
        toggleEnableCrosshair ();
    }
  }
  
  /** The default crosshair {@link Color}.
   * 
   */
  public final static Color DEFAULT_CROSSHAIR_COLOR = Color.cyan;
  
  private volatile Color crosshairColor = DEFAULT_CROSSHAIR_COLOR;
  
  /** Returns the crosshair {@link Color}.
   * 
   * @return The crosshair color.
   * 
   */
  public final Color getCrosshairColor ()
  {
    return this.crosshairColor;
  }
  
  /** Sets the crosshair {@link Color}.
   * 
   * @param color The new crosshair color, non-{@code null}.
   * 
   * @throws IllegalArgumentException If the argument is {@code null}.
   * 
   */
  public final void setCrosshairColor (final Color color)
  {
    if (color == null)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      this.crosshairColor = color;
      SwingUtilities.invokeLater (this::repaint);
    }
  }
  
  private void paintCrosshair
  (final Graphics2D g2d,
   final int width, final int height,
   final int xMargin, final int yMargin,
   final int mouseX, final int mouseY,
   final boolean enableCrosshair, final boolean mouseOverComponent,
   final Color crosshairColor)
  {
    if (! (enableCrosshair && mouseOverComponent))
      return;
    if (g2d == null)
      return;
    if (crosshairColor == null)
      return;
    if (mouseX < xMargin || mouseX > width - xMargin)
      return;
    if (mouseY < yMargin || mouseY > height - yMargin)
      return;
    g2d.setColor (crosshairColor);
    g2d.setStroke (GRATICULE_STROKE);
    // Draw the "crosshair" cursor.
    g2d.drawLine (mouseX, yMargin, mouseX, height - yMargin);
    g2d.drawLine (xMargin, mouseY, width - xMargin, mouseY);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // PAINT COMPONENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Paints the component.
   * 
   * @param g The {@link Graphics} ({@link Graphics2D}) context.
   * 
   */
  @Override
  public void paintComponent (final Graphics g)
  {
    
    super.paintComponent (g);
    
    final Graphics2D g2d = (Graphics2D) g;
    if (g2d == null)
      return;
    
    final Color oldColor = g2d.getColor ();
    final Stroke oldStroke = g2d.getStroke ();
    
    // Get Swing properties; no need to lock since we're on the Swing EDT.
    
    final int width = getWidth ();
    final int height = getHeight ();
    final int mouseX = this.mouseX;
    final int mouseY = this.mouseY;
    
    // Atomically get all required properties; avoid having to synchronize the JTraceDisplay object while painting.
    
    final int xMargin, yMargin;
    final boolean enableGraticule;
    final int xDivisions, yDivisions;
    final GraticuleOrigin graticuleOrigin;
    final Color graticuleColor, graticuleHighlightColor;
    final boolean enableGraticuleXAxis, enableGraticuleYAxis;
    final boolean enableGraticuleXAxisTicks, enableGraticuleYAxisTicks;
    final boolean enableTraces;
    final Map<K, TraceEntry> traceEntries;
    final Color clipColor;
    final float defaultTraceLineWidth;
    final ZModulationDisplayPolicy zModulationDisplayPolicy;
    final int zModulationLevels;
    final float zModulationMinimumBrightness;
    final float zModulationMaximumBrightness;
    final float zModulationMinimumLineWidth;
    final float zModulationMaximumLineWidth;
    final boolean enableCrosshair;
    final boolean mouseOverComponent;
    final Color crosshairColor;
    
    synchronized (this)
    {
      xMargin = this.xMargin;
      yMargin = this.yMargin;
      enableGraticule = this.enableGraticule;
      xDivisions = this.xDivisions;
      yDivisions = this.yDivisions;
      graticuleOrigin = this.graticuleOrigin;
      graticuleColor = this.graticuleColor;
      graticuleHighlightColor = this.graticuleHighlightColor;
      enableGraticuleXAxis = this.enableGraticuleXAxis;
      enableGraticuleYAxis = this.enableGraticuleYAxis;
      enableGraticuleXAxisTicks = this.enableGraticuleXAxisTicks;
      enableGraticuleYAxisTicks = this.enableGraticuleYAxisTicks;
      enableTraces = this.enableTraces;
      traceEntries = this.traceDB.getTraceEntries (); // The Map is atomically copied by TraceDB.
      clipColor = this.clipColor;
      defaultTraceLineWidth = this.defaultTraceLineWidth;
      zModulationDisplayPolicy = this.zModulationDisplayPolicy;
      zModulationLevels = this.zModulationLevels;
      zModulationMinimumBrightness = this.zModulationMinimumBrightness;
      zModulationMaximumBrightness = 1.0f; // this.zModulationMaximumBrightness;
      zModulationMinimumLineWidth = this.zModulationMinimumLineWidth;
      zModulationMaximumLineWidth = this.zModulationMaximumLineWidth;
      enableCrosshair = this.enableCrosshair;
      mouseOverComponent = this.mouseOverComponent;
      crosshairColor = this.crosshairColor;
    }
    
    // Paint the graticule.
    
    paintGraticule (
      g2d,
      width, height,
      xMargin, yMargin,
      enableGraticule,
      xDivisions, yDivisions,
      graticuleOrigin,
      graticuleColor, graticuleHighlightColor,
      enableGraticuleXAxis, enableGraticuleYAxis,
      enableGraticuleXAxisTicks, enableGraticuleYAxisTicks);
    
    // Paint the traces.
    
    paintTraces (
      g2d,
      width, height,
      xMargin, yMargin,
      enableTraces,
      traceEntries,
      clipColor,
      defaultTraceLineWidth,
      zModulationDisplayPolicy,
      zModulationLevels,
      zModulationMinimumBrightness, zModulationMaximumBrightness,
      zModulationMinimumLineWidth, zModulationMaximumLineWidth);
    
    // Paint the crosshair.
    
    paintCrosshair (
      g2d,
      width, height,
      xMargin, yMargin,
      mouseX, mouseY,
      enableCrosshair, mouseOverComponent,
      crosshairColor);
    
    g2d.setStroke (oldStroke);
    g2d.setColor (oldColor);
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // MOUSE HANDLING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private boolean mouseOverComponent = false;
  
  private int mouseX = -1;
  
  private int mouseY = -1;

  private final MouseAdapter mouseAdapter = new MouseAdapter ()
  {
    
    @Override
    public void mouseExited (final MouseEvent e)
    {
      JTraceDisplay.this.mouseOverComponent = false;
      JTraceDisplay.this.repaint ();
    }

    @Override
    public void mouseEntered (final MouseEvent e)
    {
      JTraceDisplay.this.mouseX = e.getX ();
      JTraceDisplay.this.mouseY = e.getY ();
      JTraceDisplay.this.mouseOverComponent = true;
      JTraceDisplay.this.repaint ();
    }

    @Override
    public void mouseMoved (final MouseEvent e)
    {
      JTraceDisplay.this.mouseX = e.getX ();
      JTraceDisplay.this.mouseY = e.getY ();
      JTraceDisplay.this.repaint ();
    }

  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
