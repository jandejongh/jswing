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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** Panel showing one or more traces from for instance an instrument, with side bars (for, e.g., markers) and menu buttons.
 *
 * <p>
 * This class embeds a {@link JTraceDisplay} instance, and adds border components on each size for
 * markers and (corner buttons) control and settings. Note that {@link JTraceDisplay} can be used in its own right as well,
 * for cases in which you do not want the additional {@link JTrace} features, want to inhibit user control over settings,
 * or simply do not have the room available for (e.g.) side panels.
 * 
 * <p>
 * Trace data is maintained in an in-core database, see {@link TraceDB}. The database may be user-supplied upon construction;
 * if not, an empty new database is generated. A {@link JTrace} instance and its embedded {@link JTraceDisplay} always
 * share a single (non-{@code null}) instance of the database (which itself is thread-safe in nature).
 * 
 * @param <K> The key type used for distinguishing traces.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see JTraceDisplay
 * @see TraceDB
 * 
 */
public class JTrace<K>
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTrace.class.getName ());
  
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
  public JTrace (final TraceDB<K> traceDB)
  {
    
    super ();
    
    this.traceDB = traceDB != null ? traceDB : new TraceDB<> ();
    
    setLayout (new BorderLayout ());
    
    // TRACE DISPLAY [CENTER]
    
    this.jTraceDisplay = new JTraceDisplay (this.traceDB);
    this.traceDB.addDBChangedListener ((final TraceDB dB) ->
    {
      SwingUtilities.invokeLater (this.jTraceDisplay::repaint);
    });
    add (this.jTraceDisplay, BorderLayout.CENTER);
    
    // NORTH PANEL
    
    this.nwButton = new JCornerButton ();
    this.nwButton.addActionListener ((ae) -> { new JTraceSettingsDialog (JTrace.this).setVisible (true); });
    this.nwButton.setWidth (getSidePanelWidthAsInt ());
    
    this.nwnStub = new JMarginStub ();
    this.nwnStub.setDimension (getJTraceDisplay ().getXMargin (), getSidePanelWidthAsInt ());
    
    this.jTraceNorthPanel = new JTraceSidePanel (this, JTraceSidePanel.Location.NORTH);
    this.jTraceNorthPanel.setBackground (getSidePanelColor ());
    this.jTraceNorthPanel.setPreferredSize (new Dimension (Integer.MAX_VALUE, getSidePanelWidthAsInt ()));
    this.jTraceNorthPanel.setMaximumSize (new Dimension (Integer.MAX_VALUE, getSidePanelWidthAsInt ()));
    this.traceDB.addDBChangedListener ((final TraceDB dB) -> { SwingUtilities.invokeLater (this.jTraceNorthPanel::repaint); });
    
    this.nenStub = new JMarginStub ();
    this.nenStub.setDimension (getJTraceDisplay ().getXMargin (), getSidePanelWidthAsInt ());
    
    this.neButton = new JCornerButton ();
    this.neButton.setWidth (getSidePanelWidthAsInt ());

    final JPanel nCenterPanel = new JPanel ();
    nCenterPanel.setLayout (new BorderLayout ());
    nCenterPanel.add (this.nwnStub, BorderLayout.WEST);
    nCenterPanel.add (this.jTraceNorthPanel, BorderLayout.CENTER);
    nCenterPanel.add (this.nenStub, BorderLayout.EAST);
    
    final JPanel nPanel = new JPanel ();
    nPanel.setLayout (new BorderLayout ());
    nPanel.add (this.nwButton, BorderLayout.WEST);
    nPanel.add (nCenterPanel, BorderLayout.CENTER);
    nPanel.add (this.neButton, BorderLayout.EAST);
    
    add (nPanel, BorderLayout.NORTH);
    
    // EAST PANEL
    
    this.neeStub = new JMarginStub ();
    this.neeStub.setDimension (getSidePanelWidthAsInt (), getJTraceDisplay ().getYMargin ());
    
    this.jTraceEastPanel = new JTraceSidePanel (this, JTraceSidePanel.Location.EAST);
    this.jTraceEastPanel.setBackground (getSidePanelColor ());
    this.jTraceEastPanel.setPreferredSize (new Dimension (getSidePanelWidthAsInt (), Integer.MAX_VALUE));
    this.jTraceEastPanel.setMaximumSize (new Dimension (getSidePanelWidthAsInt (), Integer.MAX_VALUE));
    this.traceDB.addDBChangedListener ((final TraceDB dB) -> { SwingUtilities.invokeLater (this.jTraceEastPanel::repaint); });
    
    this.seeStub = new JMarginStub ();
    this.seeStub.setDimension (getSidePanelWidthAsInt (), getJTraceDisplay ().getYMargin ());
    
    final JPanel ePanel = new JPanel ();
    ePanel.setLayout (new BorderLayout ());
    ePanel.add (this.neeStub, BorderLayout.NORTH);
    ePanel.add (this.jTraceEastPanel, BorderLayout.CENTER);
    ePanel.add (this.seeStub, BorderLayout.SOUTH);
    
    add (ePanel, BorderLayout.EAST);
    
    // SOUTH PANEL
    
    this.swButton = new JCornerButton ();
    this.swButton.setWidth (getSidePanelWidthAsInt ());
    
    this.swsStub = new JMarginStub ();
    this.swsStub.setDimension (getJTraceDisplay ().getXMargin (), getSidePanelWidthAsInt ());
    
    this.jTraceSouthPanel = new JTraceSidePanel (this, JTraceSidePanel.Location.SOUTH);
    this.jTraceSouthPanel.setBackground (getSidePanelColor ());
    this.jTraceSouthPanel.setPreferredSize (new Dimension (Integer.MAX_VALUE, getSidePanelWidthAsInt ()));
    this.jTraceSouthPanel.setMaximumSize (new Dimension (Integer.MAX_VALUE, getSidePanelWidthAsInt ()));
    this.traceDB.addDBChangedListener ((final TraceDB dB) -> { SwingUtilities.invokeLater (this.jTraceSouthPanel::repaint); });
    
    this.sesStub = new JMarginStub ();
    this.sesStub.setDimension (getJTraceDisplay ().getXMargin (), getSidePanelWidthAsInt ());
    
    this.seButton = new JCornerButton ();
    this.seButton.setWidth (getSidePanelWidthAsInt ());
    
    final JPanel sCenterPanel = new JPanel ();
    sCenterPanel.setLayout (new BorderLayout ());
    sCenterPanel.add (this.swsStub, BorderLayout.WEST);
    sCenterPanel.add (this.jTraceSouthPanel, BorderLayout.CENTER);
    sCenterPanel.add (this.sesStub, BorderLayout.EAST);
    
    final JPanel sPanel = new JPanel ();
    sPanel.setLayout (new BorderLayout ());
    sPanel.add (this.swButton, BorderLayout.WEST);
    sPanel.add (sCenterPanel, BorderLayout.CENTER);
    sPanel.add (this.seButton, BorderLayout.EAST);
    
    add (sPanel, BorderLayout.SOUTH);
    
    // WEST PANEL
    
    this.nwwStub = new JMarginStub ();
    this.nwwStub.setDimension (getSidePanelWidthAsInt (), getJTraceDisplay ().getYMargin ());
    
    this.jTraceWestPanel = new JTraceSidePanel (this, JTraceSidePanel.Location.WEST);
    this.jTraceWestPanel.setBackground (getSidePanelColor ());
    this.jTraceWestPanel.setPreferredSize (new Dimension (getSidePanelWidthAsInt (), Integer.MAX_VALUE));
    this.jTraceWestPanel.setMaximumSize (new Dimension (getSidePanelWidthAsInt (), Integer.MAX_VALUE));
    this.traceDB.addDBChangedListener ((final TraceDB dB) -> { SwingUtilities.invokeLater (this.jTraceWestPanel::repaint); });
    
    this.swwStub = new JMarginStub ();
    this.swwStub.setDimension (getSidePanelWidthAsInt (), getJTraceDisplay ().getYMargin ());
    
    final JPanel wPanel = new JPanel ();
    wPanel.setLayout (new BorderLayout ());
    wPanel.add (this.nwwStub, BorderLayout.NORTH);
    wPanel.add (this.jTraceWestPanel, BorderLayout.CENTER);
    wPanel.add (this.swwStub, BorderLayout.SOUTH);
    
    add (wPanel, BorderLayout.WEST);
    
  }

  /** Constructs the component with a new {@link TraceDB} instance.
   * 
   */
  public JTrace ()
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
   * <p>
   * The database is <i>not</i> copied; the actual reference is returned.
   * 
   * @return The trace database (fixed upon construction and always non-{@code null}).
   * 
   */
  public final TraceDB<K> getTraceDB ()
  {
    return this.traceDB;
  }
  
  /** Sets the trace data for given key (delegate method).
   * 
   * @param k         See {@link TraceDB#setTraceData}.
   * @param traceData See {@link TraceDB#setTraceData}.
   * 
   * @see #getTraceDB
   * 
   */
  public final void setTraceData (final K k, final TraceData traceData)
  {
    this.traceDB.setTraceData (k, traceData);
  }
  
  /** Sets the trace {@link Color} for given key (delegate method).
   * 
   * @param k          See {@link TraceDB#setTraceColor}.
   * @param traceColor See {@link TraceDB#setTraceColor}.
   * 
   * @see #getTraceDB
   * 
   */
  public final void setTraceData (final K k, final Color traceColor)
  {
    this.traceDB.setTraceColor (k, traceColor);
  }
  
  /** Sets the trace markers for given key (delegate method).
   * 
   * @param k            See {@link TraceDB#setTraceMarkers}.
   * @param traceMarkers See {@link TraceDB#setTraceMarkers}.
   * 
   * @see #getTraceDB
   * 
   */
  public final void setTraceMarkers (final K k, final Set<TraceMarker> traceMarkers)
  {
    this.traceDB.setTraceMarkers (k, traceMarkers);
  }
  
  /** Adds trace markers for given key (delegate method).
   * 
   * @param k            See {@link TraceDB#addTraceMarkers}.
   * @param traceMarkers See {@link TraceDB#addTraceMarkers}.
   * 
   * @see #getTraceDB
   * 
   */
  public final void addTraceMarkers (final K k, final Set<TraceMarker> traceMarkers)
  {
    this.traceDB.addTraceMarkers (k, traceMarkers);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE DISPLAY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTraceDisplay<K> jTraceDisplay;
  
  /** Returns the embedded {@link JTraceDisplay} instance.
   * 
   * @return The embedded {@link JTraceDisplay} instance, non-{@code null} and fixed upon construction.
   * 
   */
  public final JTraceDisplay<K> getJTraceDisplay ()
  {
    return this.jTraceDisplay;
  }
  
  /** The minimum margin (delegate field).
   * 
   * @see #getJTraceDisplay
   * @see JTraceDisplay#MINIMUM_MARGIN
   * 
   */
  public final static int MINIMUM_MARGIN = JTraceDisplay.MINIMUM_MARGIN;
  
  /** The maximum margin (delegate field).
   * 
   * @see #getJTraceDisplay
   * @see JTraceDisplay#MAXIMUM_MARGIN
   * 
   */
  public final static int MAXIMUM_MARGIN = JTraceDisplay.MAXIMUM_MARGIN;
  
  /** Gets the X margin (delegate method).
   * 
   * @return See {@link JTraceDisplay#getXMargin}.
   * 
   * @see #getJTraceDisplay
   * 
   */
  public final int getXMargin ()
  {
    return this.jTraceDisplay.getXMargin ();
  }
  
  /** Gets the Y margin (delegate method).
   * 
   * @return See {@link JTraceDisplay#getYMargin}.
   * 
   * @see #getJTraceDisplay
   * 
   */
  public final int getYMargin ()
  {
    return this.jTraceDisplay.getYMargin ();
  }
  
  /** Sets the X margin (augmented delegate method).
   * 
   * <p>
   * XXX This does not capture the case in which the embedded {@link JTraceDisplay} changes the margin on its own.
   * 
   * @param xMargin See {@link JTraceDisplay#setXMargin}.
   * 
   * @see #getJTraceDisplay
   * 
   */
  public final void setXMargin (final int xMargin)
  {
    
    this.jTraceDisplay.setXMargin (xMargin);
    
    final int newXMargin = this.jTraceDisplay.getXMargin ();
    
    this.nwnStub.setWidth (newXMargin);
    this.nenStub.setWidth (newXMargin);
    
    this.swsStub.setWidth (newXMargin);
    this.sesStub.setWidth (newXMargin);

    this.nwnStub.revalidate ();
    
  }
  
  /** Sets the Y margin (augmented delegate method).
   * 
   * <p>
   * XXX This does not capture the case in which the embedded {@link JTraceDisplay} changes the margin on its own.
   * 
   * @param yMargin See {@link JTraceDisplay#setYMargin}.
   * 
   * @see #getJTraceDisplay
   * 
   */
  public final void setYMargin (final int yMargin)
  {
    
    this.jTraceDisplay.setYMargin (yMargin);
    
    final int newYMargin = this.jTraceDisplay.getYMargin ();
    
    this.neeStub.setHeight (newYMargin);
    this.seeStub.setHeight (newYMargin);
    
    this.nwwStub.setHeight (newYMargin);
    this.swwStub.setHeight (newYMargin);

    this.neeStub.revalidate ();
    
  }
  
  /** Sets the number of X division (delegate method).
   * 
   * @param xDivisions See {@link JTraceDisplay#setXDivisions}.
   * 
   * @see #getJTraceDisplay
   * 
   */
  public final void setXDivisions (final int xDivisions)
  {
    this.jTraceDisplay.setXDivisions (xDivisions);
  }

  /** Sets the number of Y division (delegate method).
   * 
   * @param yDivisions See {@link JTraceDisplay#setYDivisions}.
   * 
   * @see #getJTraceDisplay
   * 
   */
  public final void setYDivisions (final int yDivisions)
  {
    this.jTraceDisplay.setYDivisions (yDivisions);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SIDE PANEL WIDTH
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Allowable values for the side-panel width.
   * 
   */
  public enum SidePanelWidth
  {
    
    ZERO_ABSENT (0),
    FOUR        (4),
    SIX         (6),
    EIGHT       (8),
    TEN        (10),
    TWELVE     (12),
    SIXTEEN    (16),
    TWENTY     (20),
    THIRTY_TWO (32),
    FORTY      (40),
    FIFTY      (50);
    
    private final int asInt;

    private final String asString;
    
    private SidePanelWidth (final int asInt)
    {
      this.asInt = asInt;
      this.asString = Integer.toString (asInt);
    }
    
    /** Returns this side-panel width as integer.
     * 
     * @return This side-panel width as integer.
     * 
     */
    public final int asInt ()
    {
      return this.asInt;
    }
    
    /** Returns the {@code String} representation of this side-panel width.
     * 
     * @return The {@code String} representation of this side-panel width.
     * 
     */
    @Override
    public final String toString ()
    {
      return this.asString;
    }
    
  };
  
  /** The default side-panel width.
   * 
   */
  public final static SidePanelWidth DEFAULT_SIDE_PANEL_WIDTH = SidePanelWidth.SIXTEEN;
  
  private volatile SidePanelWidth sidePanelWidth = DEFAULT_SIDE_PANEL_WIDTH;
  
  /** Returns the side panel width (as {@link SidePanelWidth}).
   * 
   * @return The side panel width.
   * 
   */
  public final SidePanelWidth getSidePanelWidth ()
  {
    return this.sidePanelWidth;
  }
  
  /** Returns the side panel width as {@code int}.
   * 
   * @return The side panel width as {@code int}.
   * 
   */
  public final int getSidePanelWidthAsInt ()
  {
    return this.sidePanelWidth.asInt ();
  }

  /** Sets the side-panel width.
   * 
   * @param sidePanelWidth The new side-panel width.
   * 
   * @throws IllegalArgumentException If the argument is {@code null}.
   * 
   */
  public final void setSidePanelWidth (final SidePanelWidth sidePanelWidth)
  {
    if (sidePanelWidth == null)
      throw new IllegalArgumentException ();
    synchronized (this)
    {
      if (sidePanelWidth != this.sidePanelWidth)
      {
        this.sidePanelWidth = sidePanelWidth;
        SwingUtilities.invokeLater (() ->
        {
          setSidePanelWidthSwing ();
          JTrace.this.invalidate ();
          JTrace.this.validate ();
        });
      }
    }
  }
  
  private void setSidePanelWidthSwing ()
  {
    
    final int w = getSidePanelWidthAsInt ();

    this.jTraceNorthPanel.setPreferredSize (new Dimension (Integer.MAX_VALUE, w));
    this.jTraceNorthPanel.setMaximumSize (new Dimension (Integer.MAX_VALUE, w));
    
    this.jTraceEastPanel.setPreferredSize (new Dimension (w, Integer.MAX_VALUE));
    this.jTraceEastPanel.setMaximumSize (new Dimension (w, Integer.MAX_VALUE));
    
    this.jTraceSouthPanel.setPreferredSize (new Dimension (Integer.MAX_VALUE, w));
    this.jTraceSouthPanel.setMaximumSize (new Dimension (Integer.MAX_VALUE, w));
    
    this.jTraceWestPanel.setPreferredSize (new Dimension (w, Integer.MAX_VALUE));
    this.jTraceWestPanel.setMaximumSize (new Dimension (w, Integer.MAX_VALUE));
    
    this.nwButton.setWidth (w);
    this.nwButton.invalidate ();
    
    this.neButton.setWidth (w);
    this.neButton.invalidate ();
    
    this.swButton.setWidth (w);
    this.swButton.invalidate ();
    
    this.seButton.setWidth (w);
    this.seButton.invalidate ();
    
    this.nwnStub.setDimension (this.jTraceDisplay.getXMargin (), w);
    this.nenStub.setDimension (this.jTraceDisplay.getXMargin (), w);
    
    this.neeStub.setDimension (w, this.jTraceDisplay.getYMargin ());
    this.seeStub.setDimension (w, this.jTraceDisplay.getYMargin ());
    
    this.swsStub.setDimension (this.jTraceDisplay.getXMargin (), w);
    this.sesStub.setDimension (this.jTraceDisplay.getXMargin (), w);

    this.nwwStub.setDimension (w, this.jTraceDisplay.getYMargin ());
    this.swwStub.setDimension (w, this.jTraceDisplay.getYMargin ());
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TRACE SIDE PANELS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTraceSidePanel jTraceNorthPanel;
  private final JTraceSidePanel jTraceEastPanel;  
  private final JTraceSidePanel jTraceSouthPanel;
  private final JTraceSidePanel jTraceWestPanel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TRACE SIDE PANEL COLOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The default side-panel {@link Color}.
   * 
   */
  public final static Color DEFAULT_SIDE_PANEL_COLOR = Color.lightGray;
  
  private volatile Color sidePanelColor = DEFAULT_SIDE_PANEL_COLOR;
  
  /** Returns the side-panel {@link Color}.
   * 
   * @return The side-panel color (non-{@code null}).
   * 
   */
  public final Color getSidePanelColor ()
  {
    return this.sidePanelColor;
  }
  
  /** Sets the side-panel {@link Color}.
   * 
   * @param color The new side-panel color; a {@code null} value is replaced with {@link #DEFAULT_SIDE_PANEL_COLOR}.
   * 
   */
  public final void setSidePanelColor (final Color color)
  {
    if (color == null)
      setSidePanelColor (DEFAULT_SIDE_PANEL_COLOR);
    else
      synchronized (this)
      {
        if (! color.equals (this.sidePanelColor))
        {
          this.sidePanelColor = color;
          this.jTraceNorthPanel.setBackground (this.sidePanelColor);
          this.jTraceEastPanel.setBackground (this.sidePanelColor);
          this.jTraceSouthPanel.setBackground (this.sidePanelColor);
          this.jTraceWestPanel.setBackground (this.sidePanelColor);
        }
      }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // CORNER BUTTON
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final class JCornerButton
    extends JButton
  {

    private JCornerButton ()
    {
      super ();
      setBackground (JTrace.this.getCornerButtonColor ());
    }
    
    private void setWidth (final int w)
    {
      setMinimumSize (new Dimension (w, w));
      setPreferredSize (new Dimension (w, w));
      setMaximumSize (new Dimension (w, w));      
    }
      
  }
  
  private final JCornerButton nwButton;
  
  private final JCornerButton neButton;
  
  private final JCornerButton swButton;
  
  private final JCornerButton seButton;
  
  /** The default corner-button {@link Color}.
   * 
   */
  public final static Color DEFAULT_CORNER_BUTTON_COLOR = DEFAULT_SIDE_PANEL_COLOR.darker ();
  
  private volatile Color cornerButtonColor = DEFAULT_CORNER_BUTTON_COLOR;
  
  /** Returns the corner-button {@link Color}.
   * 
   * @return The corner-button color.
   * 
   */
  public final Color getCornerButtonColor ()
  {
    return this.cornerButtonColor;
  }
  
  /** Sets the corner-button {@link Color}.
   * 
   * @param color The new corner-button color; a {@code null} value is replaced with {@link #DEFAULT_CORNER_BUTTON_COLOR}.
   * 
   */
  public final void setCornerButtonColor (final Color color)
  {
    if (color == null)
      setCornerButtonColor (DEFAULT_CORNER_BUTTON_COLOR);
    else if (! color.equals (this.cornerButtonColor))
    {
      this.cornerButtonColor = color;
      this.nwButton.setBackground (this.cornerButtonColor);
      this.neButton.setBackground (this.cornerButtonColor);
      this.swButton.setBackground (this.cornerButtonColor);
      this.seButton.setBackground (this.cornerButtonColor);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // MARGIN STUB
  // [COMPENSATES IN THE SIZE PANELS FOR X AND Y MARGINS OF THE DISPLAY]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final class JMarginStub
    extends JPanel
  {

    private JMarginStub ()
    {
      super ();
      setBackground (JTrace.this.getMarginStubColor ());
    }
    
    private void setDimension (final int w, final int h)
    {
      setMinimumSize (new Dimension (w, h));
      setPreferredSize (new Dimension (w, h));
      setMaximumSize (new Dimension (w, h));
      invalidate ();
    }
    
    private void setWidth (final int w)
    {
      setDimension (w, getHeight ());
    }
    
    private void setHeight (final int h)
    {
      setDimension (getWidth (), h);
    }
    
  }
  
  private final JMarginStub nwnStub;
  
  private final JMarginStub nenStub;
  
  private final JMarginStub neeStub;
  
  private final JMarginStub seeStub;

  private final JMarginStub sesStub;
  
  private final JMarginStub swsStub;
  
  private final JMarginStub swwStub;
  
  private final JMarginStub nwwStub;
  
  /** The default margin-stub {@link Color}.
   * 
   */
  public final static Color DEFAULT_MARGIN_STUB_COLOR = DEFAULT_SIDE_PANEL_COLOR.darker ().darker ();
  
  private volatile Color marginStubColor = DEFAULT_MARGIN_STUB_COLOR;
  
  /** Returns the margin-stub {@link Color}.
   * 
   * @return The margin-stub color.
   * 
   */
  public final Color getMarginStubColor ()
  {
    return this.marginStubColor;
  }
  
  /** Sets the margin-stub {@link Color}.
   * 
   * @param color The new margin-stub color; a {@code null} value is replaced with {@link #DEFAULT_MARGIN_STUB_COLOR}.
   * 
   */
  public final void setMarginStubColor (final Color color)
  {
    if (color == null)
      setMarginStubColor (DEFAULT_MARGIN_STUB_COLOR);
    else if (! color.equals (this.marginStubColor))
    {
      this.marginStubColor = color;
      this.nwnStub.setBackground (this.marginStubColor);
      this.nenStub.setBackground (this.marginStubColor);
      this.neeStub.setBackground (this.marginStubColor);
      this.seeStub.setBackground (this.marginStubColor);
      this.sesStub.setBackground (this.marginStubColor);
      this.swsStub.setBackground (this.marginStubColor);
      this.swwStub.setBackground (this.marginStubColor);
      this.nwwStub.setBackground (this.marginStubColor);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
