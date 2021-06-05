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
package org.javajdj.jswing.jenumsquare;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** Panel displaying an {@link Enum}'s values in a (horizontal or vertical) line with optional editing (user selection) support.
 *
 * @param <E> The enum type.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JEnumLine<E extends Enum<E>>
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JEnumLine.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The maximum number of constant {@code enum} values supported.
   * 
   * @see Class#getEnumConstants
   * 
   */
  public final static int MAX_ENUM_CONSTANTS = 100;
  
  /** Constructs the component.
   * 
   * <p>
   * A line of buttons is constructed to represent all the possible constant {@code enum} values.
   * 
   * @param orientation    The component (button) orientation,
   *                         must be either {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}.
   * @param clazz          The {@code class} of the values represented; must be an {@code enum} type.
   * @param initialValue   The optional initial value to display; when {@code null} no initial value is selected.
   * @param changeListener The optional listener for changes in the selected value.
   * @param selectedColor  The optional {@link Color} for the button corresponding with the selected value;
   *                         when {@code null}, the default {@link JToggleButton} behavior is inherited.
   * @param cellDimension  The optional {@link Dimension} of each value cell.
   * @param writeOnly      Whether or not the buttons serve as action initiators only, and are not meant to actually
   *                         represent the {@code enum} value.
   * 
   * @throws IllegalArgumentException If the number of values in the {@link Enum} class exceeds {@link #MAX_ENUM_CONSTANTS},
   *                                    or if the {@code orientation} argument has an illegal value.
   * 
   */
  public JEnumLine (
    final int orientation,
    final Class<E> clazz,
    final E initialValue,
    final Consumer<E> changeListener,
    final Color selectedColor,
    final Dimension cellDimension,
    final boolean writeOnly)
  {
   
    if (orientation != SwingConstants.HORIZONTAL && orientation != SwingConstants.VERTICAL)
      throw new IllegalArgumentException ();
    if (clazz == null)
      throw new IllegalArgumentException ();
    if (clazz.getEnumConstants ().length > JEnumLine.MAX_ENUM_CONSTANTS)
      throw new IllegalArgumentException ();
    
    final int enumSize = clazz.getEnumConstants ().length;
    
    setOpaque (true);
    
    switch (orientation)
    {
      case SwingConstants.HORIZONTAL:
        setLayout (new GridLayout (1, enumSize, 0, 0));
        break;
      case SwingConstants.VERTICAL:
        setLayout (new GridLayout (enumSize, 1, 0, 0));
        break;
      default:
        throw new RuntimeException ();
    }
    
    this.selectedItem = initialValue;
    this.writeOnly = writeOnly;
    
    this.buttonMap = new EnumMap (clazz);
    
    final ButtonGroup bg = new ButtonGroup ();
    for (final E e : clazz.getEnumConstants ())
    {
      final JToggleButton jb = writeOnly ? new JColorCheckBox () : new JToggleButton ();
      this.buttonMap.put (e, jb);
      if (! writeOnly)
      {
        bg.add (jb);
        jb.setUI (new MetalToggleButtonUI ()
        {
          @Override
          protected Color getSelectColor ()
          {
            return selectedColor;
          }
        });
      }
      if (cellDimension != null)
      {
        jb.setPreferredSize (cellDimension);
        jb.setMinimumSize (cellDimension);
        jb.setMaximumSize (cellDimension);
      }
      jb.setToolTipText (e.toString ());
      jb.setEnabled (changeListener != null);
      if (changeListener != null)
        jb.addActionListener ((final ActionEvent ae) ->
        {
          this.selectedItem = e;
          changeListener.accept (e);
          // XXX Is this safe; don't we shoot ourselves in the foot?
//          if (writeOnly)
//          {
//            // XXX THIS IS ALREADY OVERKILL BUT STILL DOES NOT WORK...
//            bg.setSelected (jb.getModel (), false);
//            jb.setSelected (false);
//            jb.repaint ();
//          }
        });
      jb.setSelected (e == this.selectedItem);
      add (JCenter.XY (jb));
    }
    
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BUTTON MAP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final EnumMap<E, JToggleButton> buttonMap;
  
  /** Gets the button for given key.
   * 
   * <p>
   * Buttons are created at construction time and fixed thereafter.
   * 
   * @param e The key (enum value or {@code null}).
   * 
   * @return The {@link JToggleButton} corresponding to given key, {@code null} if {@code key == null} (or if the key is unknown).
   * 
   */
  public final JToggleButton getButton (final E e)
  {
    if (e == null)
      return null;
    else
      return this.buttonMap.get (e);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // WRITE ONLY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final boolean writeOnly;
  
  /** Returns whether or not this component is write only.
   * 
   * <p>
   * The write-only setting (property) is determined at construction time, and cannot be changed afterwards.
   * 
   * <p>
   * In write-only mode, the component will maintain the currently selected item ({@code enum} value}) as normal
   * but is will <i>not</i> display the selected value.
   * In other words, the buttons generated merely act as triggers for setting the {@code enum} value.
   * 
   * @return Whether or not this component is write only.
   * 
   */
  public final boolean isWriteOnly ()
  {
    return this.writeOnly;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SELECTED ITEM
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private volatile E selectedItem;
  
  /** Returns the currently selected item (enum value), if any.
   * 
   * @return The currently selected item (enum value), if any, {@code null} otherwise.
   */
  public final E getSelectedItem ()
  {
    return this.selectedItem;
  }
  
  /** Sets the currently selected item (enum value).
   * 
   * <p>
   * Sets the selected item and updates the {@code selected} status of the buttons.
   * Note that in {@code writeOnly} mode, all buttons will only represent the initial value, if any.
   * 
   * <p>
   * If this method is <i>not</i> invoked from the Swing Event Dispath Thread,
   * it is scheduled on the EDT event list.
   * 
   * @param item The new enum value, may be {@code null}
   *             in which case all buttons are "unselected" and the represented value is {code null}.
   * 
   */
  public void setSelectedItem (final E item)
  {
    if (! SwingUtilities.isEventDispatchThread ())
      SwingUtilities.invokeLater (() -> setSelectedItem (item));
    if (this.selectedItem != item)
    {
      this.selectedItem = item;
      for (final Map.Entry<E, JToggleButton> e : this.buttonMap.entrySet ())
        e.getValue ().setSelected ((! this.writeOnly) && e.getKey () == this.selectedItem);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
