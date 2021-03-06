/*
 * Copyright 2010-2021 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jswing.jsevensegment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.EnumMap;
import java.util.logging.Logger;
import javax.swing.JLabel;

/** A single seven-segment LED/LCD display (digit), with support for a dot (so, an eight-segment display).
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see JSevenSegmentNumber
 * 
 */
public class JSevenSegmentDigit
  extends JLabel
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static final Logger LOG = Logger.getLogger (JSevenSegmentDigit.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates the component with (medium) given color for the segments.
   * 
   * <p>
   * The component uses darker versions of the medium color for segments that are not lit,
   * and brighter ones for those that are.
   * This mimics the behavior of the physical device in which you often still see
   * all segments, even if they are not all lit.
   * 
   * <p>
   * The background color is set to {@link Color#black},
   * but this is easily changed after construction.
   * 
   * @param mediumColor The medium color of the segments.
   * 
   */
  public JSevenSegmentDigit (final Color mediumColor)
  {
    if (mediumColor != null)
      setOnOffColorsFromMediumColor (mediumColor);
    setPreferredSize (JSevenSegmentDigit.REFERENCE_DIMENSION);
    setOpaque (true);
    setBackground (Color.black);
    setBlank ();
  }
  
  public JSevenSegmentDigit ()
  {
    this (null);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // REFERENCE DIMENSION
  // SEGMENT
  // SEGMENT PAINT MAP
  //
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The dimension against which the segments are (internally) defined.
   * 
   * <p>
   * The paint method will compensate for (likely) other dimensions.
   * 
   * @see Segment
   * 
   */
  private final static Dimension REFERENCE_DIMENSION = new Dimension (160, 180);
  
  /** The segments in the display, with their {@link Shape}s.
   * 
   * @see #REFERENCE_DIMENSION
   * 
   */
  public static enum Segment
  {
     
    a   (new Polygon (new int[]{20, 90,  98,  90, 20, 12}, new int[]{  8,   8,  15,  22,  22,  15}, 6)),
    b   (new Polygon (new int[]{91, 98, 105, 105, 98, 91}, new int[]{ 23,  18,  23,  81,  89,  81}, 6)),
    c   (new Polygon (new int[]{91, 98, 105, 105, 98, 91}, new int[]{ 97,  89,  97, 154, 159, 154}, 6)),
    d   (new Polygon (new int[]{20, 90,  98,  90, 20, 12}, new int[]{155, 155, 162, 169, 169, 162}, 6)),
    e   (new Polygon (new int[]{ 5, 12,  19,  19, 12,  5}, new int[]{ 97,  89,  97, 154, 159, 154}, 6)),
    f   (new Polygon (new int[]{ 5, 12,  19,  19, 12,  5}, new int[]{ 23,  18,  23,  81,  89,  81}, 6)),
    g   (new Polygon (new int[]{20, 90,  95,  90, 20, 15}, new int[]{ 82,  82,  89,  96,  96,  89}, 6)),
    dot (new Ellipse2D.Double (125,150,20,20))
    ;
      
    Segment (final Shape shape)
    {
      this.shape = shape;
    }
      
    private final Shape shape;
    
    /** Returns the {@link Shape} of this segment, relative to {@link #REFERENCE_DIMENSION}.
     * 
     * @return The shape of this segment.
     * 
     */
    public final Shape getShape ()
    {
      return this.shape;
    }
      
  }
  
  /** Internal map dictating which segments are to be (actively) displayed.
   * 
   */
  private final EnumMap<Segment, Boolean> segmentPaintMap = new EnumMap<> (Segment.class);
 
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // METHODS SETTING THE SEGMENT PAINT MAP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Blanks all segments, including the decimal point.
   * 
   */
  public final void setBlank ()
  {
    for (Segment s : Segment.values ())
      this.segmentPaintMap.put (s, false);
  }
  
  /** Shows the minus sign, blanking the decimal point.
   * 
   */
  public final void setMinus ()
  {
    setBlank ();
    this.segmentPaintMap.put (Segment.g, true);
  }
  
  private final static boolean NUMBERS[][] = new boolean[][]
  {
    /* 0 */ { true,  true,  true,  true,  true,  true, false },
    /* 1 */ {false,  true,  true, false, false, false, false },
    /* 2 */ { true,  true, false,  true,  true, false,  true },
    /* 3 */ { true,  true,  true,  true, false, false,  true },
    /* 4 */ {false,  true,  true, false, false,  true,  true },
    /* 5 */ { true, false,  true,  true, false,  true,  true },
    /* 6 */ { true, false,  true,  true,  true,  true,  true },
    /* 7 */ { true,  true,  true, false, false, false, false },
    /* 8 */ { true,  true,  true,  true,  true,  true,  true },
    /* 9 */ { true,  true,  true,  true, false,  true,  true }
  };
  
  private final static boolean SMALL_O[] = new boolean[] { false,  false,  true,  true,  true,  false,  true };
  
  /** Shows the given number, which must be between 0 and 9 inclusive.
   * 
   * <p>
   * This method does <i>not</i> affect the decimal point.
   * 
   * @param n The number.
   * 
   * @throws IllegalArgumentException If the number is out of range.
   * 
   */
  public final void setNumber (final int n)
  {
    if (n < 0 || n > 9)
      throw new IllegalArgumentException ();
    for (final Segment s : Segment.values ())
      if (s.ordinal () >= Segment.a.ordinal () && s.ordinal () <= Segment.g.ordinal ())
        this.segmentPaintMap.put (s, JSevenSegmentDigit.NUMBERS[n][s.ordinal ()]);
  }

  /** Controls visibility of the decimal point.
   * 
   * @param decimalPoint Whether the decimal point is to be shown.
   * 
   */
  public void setDecimalPoint (final boolean decimalPoint)
  {
    this.segmentPaintMap.put (Segment.dot, decimalPoint);      
  }
  
  /** Shows a small letter 'o' (not affecting the decimal point).
   * 
   * <p>
   * The small letter 'o' is typically used to indicate an overflow condition,
   * but this is by no means enforced by this class.
   * 
   */
  public void setSmallO ()
  {
    for (final Segment s : Segment.values ())
      if (s.ordinal () >= Segment.a.ordinal () && s.ordinal () <= Segment.g.ordinal ())
        this.segmentPaintMap.put (s, JSevenSegmentDigit.SMALL_O[s.ordinal ()]);    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT MEDIUM COLOR
  // OFF AND ON COLORS
  // MEDIUM COLOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public static final Color DEFAULT_MEDIUM_COLOR = Color.red;
  
  private Color offColor = JSevenSegmentDigit.DEFAULT_MEDIUM_COLOR.darker ().darker ().darker ().darker ();
  
  public final Color getOffColor ()
  {
    return this.offColor;
  }
  
  public final void setOffColor (final Color offColor)
  {
    if (offColor == null)
      throw new IllegalArgumentException ();
    this.offColor = offColor;
  }
  
  private Color onColor = JSevenSegmentDigit.DEFAULT_MEDIUM_COLOR.brighter ().brighter ().brighter ().brighter ().brighter ();
    
  public final Color getOnColor ()
  {
    return this.onColor;
  }
  
  public final void setOnColor (final Color onColor)
  {
    if (onColor == null)
      throw new IllegalArgumentException ();
    this.onColor = onColor;
  }
  
  public final void setOnOffColorsFromMediumColor (final Color mediumColor)
  {
    if (mediumColor == null)
      throw new IllegalArgumentException ();
    this.offColor = mediumColor.darker ().darker ().darker ().darker ();
    this.onColor = mediumColor.brighter ().brighter ().brighter ().brighter ().brighter ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // paintComponent
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public void paintComponent (final Graphics g)
  {
    super.paintComponent (g); // This is needed to set the background color.
    final Color origColor = g.getColor ();
    final int w = getWidth ();
    final int h = getHeight ();
    final double scaleX = w / 160.0;
    final double scaleY = h / 180.0;
    final Graphics2D g2 = (Graphics2D) g;
    final AffineTransform origTransform = g2.getTransform ();
    final AffineTransform af = new AffineTransform (origTransform);
    af.translate (-20.0, -8.0);
    af.scale (scaleX, scaleY);
    af.translate (20.0 / scaleX, 8.0 / scaleY);
    // af.rotate(0.025);
    g2.setTransform (af);
    for (final Segment s: Segment.values ())
    {
      g2.setColor (this.segmentPaintMap.get (s) ? this.onColor : this.offColor);
      g2.draw (s.getShape ());
      g2.fill (s.getShape ());
    }
    g2.setTransform (origTransform);
    g.setColor (origColor);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
}
