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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JPanel;

/** A side panel of {@link JTrace} (package private).
 *
 * <p>
 * This panel shows the side-panel markers.
 * 
 * <p>
 * Note that this panel's geometry is closely tied to that of the {@link JTrace} instance it is part of
 * (and on which of its four sides).
 * 
 * @param <K> The key type used for distinguishing traces.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see JTrace
 * 
 */
class JTraceSidePanel<K>
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTraceSidePanel.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  JTraceSidePanel (final JTrace<K> jTrace, final Location location)
  {
    super ();
    if (jTrace == null)
      throw new IllegalArgumentException ();
    this.jTrace = jTrace;
    this.traceDB = this.jTrace.getTraceDB ();
    this.jTraceDisplay = this.jTrace.getJTraceDisplay ();
    if (this.traceDB == null || this.jTraceDisplay == null)
      throw new IllegalArgumentException ();
    if (location == null)
      throw new IllegalArgumentException ();
    this.location = location;
    this.myPath = getTrianglePath (location, null, getSidePanelThickness ());
    this.position = 0;
    setOpaque (true);
    addMouseListener (this.mouseAdapter);
    addMouseMotionListener (this.mouseAdapter);
    addComponentListener (this.componentAdapter);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // JTrace COMPONENT
  // TRACE DB
  // TRACE DISPLAY  
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JTrace<K> jTrace;
  
  private final TraceDB<K> traceDB;
  
  private final JTraceDisplay<K> jTraceDisplay;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOCATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  enum Location
  {
    NORTH,
    EAST,
    SOUTH,
    WEST;
  }
  
  private final Location location;
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // PATH / POSITION / CLIPPED
  // XXX: MARKERS!
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Path2D myPath;

  // position is [0,1[ across the width or height, depending on orientation.
  private double position;

  private boolean clipped = false;
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // SIDE PANEL LENGTH
  // SIDE PANEL THICKNESS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private int getSidePanelLength ()
  {
    switch (this.location)
    {
      case NORTH: return getWidth ();
      case EAST:  return getHeight ();
      case SOUTH: return getWidth ();
      case WEST:  return getHeight ();
      default: throw new RuntimeException ();
    }
  }
  
  private int getSidePanelThickness ()
  {
    switch (this.location)
    {
      case NORTH: return getHeight ();
      case EAST:  return getWidth ();
      case SOUTH: return getHeight ();
      case WEST:  return getWidth ();
      default: throw new RuntimeException ();
    }    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // paintComponent
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected void paintComponent (final Graphics g)
  {

    super.paintComponent (g);

    final Graphics2D g2d = (Graphics2D) g;

    final Color oldColor = g2d.getColor ();
    final Paint oldPaint = g2d.getPaint ();
    final Stroke oldStroke = g2d.getStroke ();
    final AffineTransform oldTransform = g2d.getTransform ();

    // XXX Just a test marker in black; shows how to interact with the MouseAdapter...
    // XXX Remove me later...
    g2d.setPaint (this.clipped ? JTraceSidePanel.this.jTraceDisplay.getClipColor () : Color.black);
    g2d.fill (this.myPath);

    // Iterate over (main) traces.
    for (final TraceEntry te : this.traceDB.getTraceEntries ().values ())
    {
      if (te == null)
        continue;
      if (te.getTraceData () == null)
        continue;
      final Set<TraceMarker> markers = te.getTraceMarkers ();
      if (markers == null)
        continue;
      final Color traceColor = te.getTraceColor ();
      if (traceColor == null)
        continue;
      // Iterate over the trace's markers.
      for (final TraceMarker tm : markers)
        if (tm != null)
          switch (tm)
          {
            case ZERO_X_SIDE_MARKER:
              switch (this.location)
              {
                case NORTH:
                case SOUTH:
                {
                  // Note that we can always safely ask the X range on the Trace Data.
                  final TraceData.DoubleRange xRange = te.getTraceData ().getXRange ();
                  if (xRange == null)
                    continue;
                  final double relPosition;
                  final boolean isClipped;
                  if (xRange.getMax () < 0)
                  {
                    // Zero is "right of us".
                    relPosition = 1;
                    isClipped = true;
                  }
                  else if (xRange.getMin () > 0)
                  {
                    // Zero is "left of us".
                    relPosition = 0;
                    isClipped = true;
                  }
                  else if (xRange.getLength () < 1.0e-12) // XXX SOME EPSILON
                  {
                    relPosition = 0.5;
                    isClipped = true;
                  }
                  else
                  {
                    // x = xmin + p (xmax-xmin)
                    // x = 0 -> p(xmax - xmin) = -xmin
                    // p = - xmin / (xmax - xmin)
                    relPosition = - xRange.getMin () / xRange.getLength ();
                    isClipped = false;
                  }
                  final Path2D path = getTrianglePath (this.location, null, getSidePanelThickness ());
                  final double tx = relPosition* getWidth ();
                  g2d.setPaint (isClipped ? this.jTraceDisplay.getClipColor () : traceColor);
                  path.transform (AffineTransform.getTranslateInstance (tx, 0));
                  g2d.fill (path);                  
                }
                break;
              }
              break;
            case ZERO_Y_SIDE_MARKER:
              switch (this.location)
              {
                case EAST:
                case WEST:
                {
                  // Note that we can always safely ask the Y range on the Trace Data.
                  final TraceData.DoubleRange yRange = te.getTraceData ().getYRange ();
                  if (yRange == null)
                    continue;
                  final double relPosition;
                  final boolean isClipped;
                  if (yRange.getMax () < 0)
                  {
                    // Zero is "above".
                    relPosition = 1;
                    isClipped = true;
                  }
                  else if (yRange.getMin () > 0)
                  {
                    // Zero is "below".
                    relPosition = 0;
                    isClipped = true;
                  }
                  else if (yRange.getLength () < 1.0e-12) // XXX SOME EPSILON
                  {
                    relPosition = 0.5;
                    isClipped = true;
                  }
                  else
                  {
                    // y = ymin + p (ymax-ymin)
                    // y = 0 -> p(ymax - ymin) = -ymin
                    // p = - ymin / (ymax - ymin)
                    relPosition = - yRange.getMin () / yRange.getLength ();
                    isClipped = false;
                  }
                  final Path2D path = getTrianglePath (this.location, null, getSidePanelThickness ());
                  // Note that we have to correct for the positive Y direction, and this is the place we do that!
                  final double ty = (1 - relPosition) * getHeight ();
                  g2d.setPaint (isClipped ? this.jTraceDisplay.getClipColor () : traceColor);
                  path.transform (AffineTransform.getTranslateInstance (0, ty));
                  g2d.fill (path);
                }
                break;
              }
              break;
          }
    }
    
    // Restore save Graphics2D settings.
    g2d.setTransform (oldTransform);
    g2d.setStroke (oldStroke);
    g2d.setPaint (oldPaint);
    g2d.setColor (oldColor);

  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ComponentAdapter
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final ComponentAdapter componentAdapter = new ComponentAdapter ()
  {
      
    @Override
    public void componentResized (final ComponentEvent ce)
    {
      super.componentResized (ce);
      final int w;
      switch (JTraceSidePanel.this.location)
      {
        case NORTH:
        case SOUTH:
          w = JTraceSidePanel.this.getHeight ();
          break;
        case EAST:
        case WEST:
          w = JTraceSidePanel.this.getWidth ();
          break;
        default:
          throw new RuntimeException ();
      }
      // Correct the size of the triangle.
      getTrianglePath (JTraceSidePanel.this.location, JTraceSidePanel.this.myPath, w);
      // Correct the (relative) position of the triangle.
      final AffineTransform at;
      switch (JTraceSidePanel.this.location)
      {
        case NORTH:
        case SOUTH:
          final int tx = (int) Math.round (JTraceSidePanel.this.position * JTraceSidePanel.this.getWidth ());
          at = AffineTransform.getTranslateInstance (tx, 0);
          break;
        case EAST:
        case WEST:
          final int ty = (int) Math.round (JTraceSidePanel.this.position * JTraceSidePanel.this.getHeight ());
          at = AffineTransform.getTranslateInstance (0, ty);
          break;
        default:
          throw new RuntimeException ();
      }
      JTraceSidePanel.this.myPath.transform (at);
      // No need to cancel any pending triangle drag operation.
      // IMO, an ongoing drag should not be possible.
      // So, just do the repaint...
      JTraceSidePanel.this.repaint ();
    }
      
  };
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MouseAdapter
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final MouseAdapter mouseAdapter = new MouseAdapter () // Shameless Internet Rip!
  {

    private Point pPressed = null;

    @Override
    public void mousePressed (final MouseEvent e)
    {
      if (e.getButton () != MouseEvent.BUTTON1)
        return;
      if (JTraceSidePanel.this.myPath.contains (e.getPoint ()))
        this.pPressed = e.getPoint ();
    }

    @Override
    public void mouseDragged (final MouseEvent e)
    {
      drag (e);
    }

    @Override
    public void mouseReleased (final MouseEvent e)
    {
      drag (e);
      this.pPressed = null;
      clipped = false;
    }

    private void drag (final MouseEvent e)
    {
      if (this.pPressed == null)
      {
        return;
      }
      final Point p = e.getPoint ();
      final int tx = p.x - this.pPressed.x;
      final int ty = p.y - this.pPressed.y;
      switch (JTraceSidePanel.this.location)
      {
        case NORTH:
        case SOUTH:
          if (tx == 0)
          {
            return;
          }
          break;
        case EAST:
        case WEST:
          if (ty == 0)
          {
            return;
          }
          break;
        default:
          throw new RuntimeException ();
      }
      final AffineTransform at;
      switch (JTraceSidePanel.this.location)
      {
        case NORTH:
        case SOUTH:
          // XXX Probably better to get position directly from at...
          JTraceSidePanel.this.position += (tx / (double) JTraceSidePanel.this.getWidth ());
          at = AffineTransform.getTranslateInstance (tx, 0);
          break;
        case EAST:
        case WEST:
          // XXX Probably better to get position directly from at...
          JTraceSidePanel.this.position += (ty / (double) JTraceSidePanel.this.getHeight ());
          at = AffineTransform.getTranslateInstance (0, ty);
          break;
        default:
          throw new RuntimeException ();
      }
      if (position < 0)
      {
        getTrianglePath (JTraceSidePanel.this.location, JTraceSidePanel.this.myPath, getSidePanelThickness ());
        clipped = true;
        position = 0;
        this.pPressed = new Point (0, 0);
      }
      else if (position >= 1)
      {
        getTrianglePath (JTraceSidePanel.this.location, JTraceSidePanel.this.myPath, getSidePanelThickness ());
        clipped = true;
        switch (location)
        {
          case NORTH:
          case SOUTH:
            JTraceSidePanel.this.myPath.transform (AffineTransform.getTranslateInstance (getWidth (), 0));
            position = 1;
            this.pPressed = new Point (getWidth (), 0);
            break;
          case EAST:
          case WEST:
            JTraceSidePanel.this.myPath.transform (AffineTransform.getTranslateInstance (0, getHeight ()));
            position = 1;
            this.pPressed = new Point (0, getHeight ());
            break;
          default:
            throw new RuntimeException ();
        }
      }
      else
      {
        JTraceSidePanel.this.myPath.transform (at);
        clipped = false;
        this.pPressed = p;
      }
      repaint ();
    }

  };

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SWING
  // TRIANGLE [MARKER SYMBOL]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static Path2D getTrianglePath (final Location location, final Path2D path, final int t)
  {

    final Path2D myPath = (path == null) ? new Path2D.Double () : path;

    myPath.reset ();

    switch (location)
    {
      case NORTH:
        myPath.moveTo (-t / 2, 1);
        myPath.lineTo (t / 2, 1);
        myPath.lineTo (0, t - 1);
        myPath.closePath ();
        break;
      case EAST:
        myPath.moveTo (t - 1, -t / 2);
        myPath.lineTo (t - 1, t / 2);
        myPath.lineTo (1, 0);
        myPath.closePath ();
        break;
      case SOUTH:
        myPath.moveTo (-t / 2, t - 1);
        myPath.lineTo (t / 2, t - 1);
        myPath.lineTo (0, 1);
        myPath.closePath ();
        break;
      case WEST:
        myPath.moveTo (1, -t / 2);
        myPath.lineTo (1, t / 2);
        myPath.lineTo (t - 1, 0);
        myPath.closePath ();
        break;
      default:
        throw new RuntimeException ();
    }

    return myPath;

  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
