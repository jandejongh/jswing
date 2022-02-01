/*
 * Copyright 2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jswing.jbyte;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A VU-bar like representation of a number.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 * @see JColorCheckBox.JBoolean
 * 
 */
public class JVUBar
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static final Logger LOG = Logger.getLogger (JVUBar.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the component.
   * 
   * <p>
   * In the default implementation,
   * the component partitions the <i>value interval</i> {@code [minValue, maxValue]} into
   * equally-sized {@code numberOfBoxes} <i>bins</i>
   * and creates and assigns an <i>indicator</i> component or <i>box</i> ({@link JColorCheckBox.JBoolean})
   * to each bin.
   * 
   * <p>
   * Each box is assigned a {@link Color} to use when lighting up,
   * depending on the location of the bin in the value interval: The bins starting in the lower 60% (exclusive)
   * of the value interval have green color,
   * between 60% (inclusive) and 90% (exclusive) orange, and red beyond 90% (inclusive).
   * This implies that when using 10 boxes, the lowest 6 bins will be green, the next two orange, and the highest two red.
   * At the present time, the base implementation does not allow changing colors or percentages.
   * 
   * <p>
   * Each bin and associated box are assigned a <i>value limit</i> or <i>box threshold</i>.
   * If the current displayed value of the component is set ({@link #setDisplayedValue})
   * to a value higher than or equal to the value limit of a bin, the corresponding 
   * indicator lights up. The value limit is always inside the bin (including taking the value of one of the boundaries).
   * The box threshold can be queries through {@link #getBoxThreshold}
   * (boxes are indexed in positive direction starting from zero).
   * 
   * <p>
   * The {@code alignmentToMaxValue} controls whether the current implementation chooses the lower boundary ({@code false})
   * or upper boundary ({@code true}) as value limit of each bin.
   * To understand the effect, consider the case of 10 boxes on interval {@code [0, 10]}.
   * If {@code alignmentToMaxValue == false}, the value limits align to the lower boundaries of the bins,
   * constituting the sequence {@code 0, 1, ..., 9}.
   * Otherwise, they align to the higher boundaries, resulting in {@code 1, ..., 10}.
   * 
   * <p>
   * Note that much of the behavior described above can be overridden in sub-classes.
   * However, for consistency, it is highly recommended that, for instance, the value limits of the box series
   * form a strictly increasing double sequence.
   * 
   * @param minValue              The minimum value of the range to show.
   * @param maxValue              The maximum value of the range to show.
   * @param numberOfBoxes         The number of boxes (indicators) to use.
   * @param horizontalOrientation Whether the component is oriented horizontally.
   * @param alignmentToMaxValue   Whether to use the upper boundary (as opposed to the default lower boundary)
   *                                of a box's bin as value limit for the box to light up.
   * 
   * @throws IllegalArgumentException If {@code minValue >= maxValue} or {@code numberOfBoxes < 1}.
   * 
   */
  public JVUBar (
    final double minValue,
    final double maxValue,
    final int numberOfBoxes,
    final boolean horizontalOrientation,
    final boolean alignmentToMaxValue)
  {
    if (minValue >= maxValue || numberOfBoxes < 1)
      throw new IllegalArgumentException ();
    removeAll ();
    setLayout (new GridLayout (horizontalOrientation ? 1 : numberOfBoxes, horizontalOrientation ? numberOfBoxes : 1));
    for (int i = 0; i < numberOfBoxes; i++)
    {
      final double startProgress = ((double) i / numberOfBoxes);
      final double endProgress = (((double) i + 1) / numberOfBoxes);
      final Color color = (startProgress < 0.6 ? Color.green : (startProgress < 0.9 ? Color.orange : Color.red));
      final JColorCheckBox.JBoolean box = new JColorCheckBox.JBoolean (color);
      box.setBorder (null);
      final double limitValue = minValue +  (alignmentToMaxValue ? endProgress : startProgress) * (maxValue - minValue);
      box.setToolTipText (Double.toString (limitValue));
      this.boxMap.put (limitValue, box);
      if (horizontalOrientation)
        add (box);
      else
        add (box, 0);
    }
  }
  
  /** Constructs the component.
   * 
   * <p>
   * Comfort constructor.
   * 
   * <p>
   * Sets {@code alignmentToMaxValue} to {@code false}.
   * 
   * @param minValue              The minimum value of the range to show.
   * @param maxValue              The maximum value of the range to show.
   * @param numberOfBoxes         The number of boxes (indicators) to use.
   * @param horizontalOrientation Whether the component is oriented horizontally.
   * 
   */
  public JVUBar (
    final double minValue,
    final double maxValue,
    final int numberOfBoxes,
    final boolean horizontalOrientation)
  {
    this (minValue, maxValue, numberOfBoxes, horizontalOrientation, false);
  }
  
  
  /** A {@link JVUBar} optimized for integer values.
   * 
   * <p>
   * Each box represents a single integer value.
   * 
   */
  public static class Integer
    extends JVUBar
  {
    
    /** Constructs the component.
     * 
     * <p>
     * A total of {@code maxValue - minValue + 1} boxes is created,
     * each representing a single integer value.
     * 
     * @param minValue              The minimum value of the range to show.
     * @param maxValue              The maximum value of the range to show.
     * @param horizontalOrientation Whether the component is oriented horizontally.
     * 
     * @throws IllegalArgumentException If {@code minValue > maxValue}.
     * 
     */
    public Integer (
      final int minValue,
      final int maxValue,
      final boolean horizontalOrientation)
    {
      super (minValue, maxValue, maxValue - minValue + 1, horizontalOrientation);
      int i = minValue;
      final Map<Double, JColorCheckBox.JBoolean> newBoxMap = new TreeMap<> ();
      for (final Map.Entry<Double, JColorCheckBox.JBoolean> entry : this.boxMap.entrySet ())
      {
        entry.getValue ().setToolTipText (java.lang.Integer.toString (i));
        newBoxMap.put (new Double (i++), entry.getValue ());
      }
      this.boxMap.clear ();
      this.boxMap.putAll (newBoxMap);
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BOX MAP
  // NUMBER OF BOXES
  // BOX THRESHOLD
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  protected final NavigableMap<Double, JColorCheckBox.JBoolean> boxMap = new TreeMap<> ();
  
  /** Gets the number of boxes in this component.
   * 
   * @return The number of boxes in this component.
   * 
   */
  public final int getNumberOfBoxes ()
  {
    return this.boxMap.size ();
  }
  
  /** Sets the number of boxes in this component, and redraws it in order to reflect the current value.
   * 
   * <p>
   * This method is unsupported. (Implementers: note the complications with {@link Integer}.)
   * 
   * @param numberOfBoxes The new number of boxes, {@code >= 1}.
   * 
   * @throws IllegalArgumentException      If the argument is {@code <1}.
   * @throws UnsupportedOperationException Always, this method is unsupported.
   * 
   */
  public final void setNumberOfBoxes (final int numberOfBoxes)
  {
    if (numberOfBoxes < 1)
      throw new IllegalArgumentException ();
    throw new UnsupportedOperationException ();
  }
  
  /** Gets the box threshold (value limit) for the box with given index.
   * 
   * <p>
   * Boxes are indexed starting at zero until {@link #getNumberOfBoxes} - 1.
   * 
   * @param index The box index.
   * 
   * @return The threshold (value inclusive) of the displayed valued at which the box lights up.
   * 
   * @throws IndexOutOfBoundsException If the index is strictly negative or {@code > numberOfBoxes - 1}.
   * 
   */
  public final double getBoxThreshold (final int index)
  {
    if (index < 0 || index >= this.boxMap.size ())
      throw new IndexOutOfBoundsException ();
    return this.boxMap.keySet ().toArray (new Double[]{})[index];
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAYED VALUE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile Double displayedValue = null;
  
  /** Gets the displayed value of this component.
   * 
   * <p>
   * The displayed value may be {@code null},
   * in which case none of the boxes (should) light up.
   * 
   * @return The displayed value, may be {@code null}.
   * 
   */
  public final Double getDisplayedValue ()
  {
    return this.displayedValue;
  }
  
  /** Sets the displayed value.
   * 
   * <p>
   * This method should be called only from the Swing Event-Dispatching Thread.
   * 
   * @param d The new displayed value.
   * 
   */
  public void setDisplayedValue (final double d)
  {
    for (final Map.Entry<Double, JColorCheckBox.JBoolean> entry : this.boxMap.entrySet ())
    {
      final boolean set = d >= entry.getKey ();
      entry.getValue ().setDisplayedValue (d >= entry.getKey ());
    }
  }

  /** Sets the displayed value.
   * 
   * <p>
   * This method should be called only from the Swing Event-Dispatching Thread.
   * 
   * <p>
   * The displayed value may be {@code null},
   * in which case none of the boxes (should) light up.
   * 
   * @param d The new displayed value.
   * 
   */
  public final void setDisplayedValue (final Double d)
  {
    if (d != null)
      setDisplayedValue ((double) d);
    else
      setBlank ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SET BLANK
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Blanks all boxes and sets the displayed value to {@code null}.
   * 
   * <p>
   * This method should be called only from the Swing Event-Dispatching Thread.
   * 
   * <p>
   * Functionally equivalent to {@code setDisplayedValue (null)}.
   * 
   */
  public final void setBlank ()
  {
    for (final JColorCheckBox.JBoolean box : this.boxMap.values ())
      box.setDisplayedValue (false);
    this.displayedValue = null;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
