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
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.plaf.metal.MetalToggleButtonUI;
import org.javajdj.jswing.jcenter.JCenter;

/** Panel displaying an {@link Enum} value in a square grid with optional editing (user selection) support.
 *
 * <p>
 * Currently, the implementation does not allow changing the selected {@code enum} value other than through the
 * component itself.
 * 
 * @param <E> The enum type.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JEnumSquare<E extends Enum<E>>
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JEnumSquare.class.getName ());
  
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
  public final static int MAX_ENUM_CONSTANTS = 10000; // 100 x 100
  
  /** Constructs the component.
   * 
   * <p>
   * A square grid of buttons is constructed just enough to hold all the constant {@code enum} values,
   * with dummy (disabled) buttons added if needed. The button representation of the {@code enum} values are
   * added to the {@link GridLayout} on a row-first basis.
   * 
   * @param clazz          The {@code class} of the values represented; must be an {@code enum} type.
   * @param initialValue   The optional initial value to display; when {@code null} no initial value is selected.
   * @param changeListener The optional listener for changes in the selected value.
   * @param selectedColor  The optional {@link Color} for the button corresponding with the selected value;
   *                         when {@code null}, the default {@link JToggleButton} behavior is inherited.
   * @param cellDimension  The optional {@link Dimension} of each value cell.
   * 
   * @throws IllegalArgumentException If the number of values in the {@link Enum} class exceeds {@link #MAX_ENUM_CONSTANTS}.
   * 
   */
  public JEnumSquare (
    final Class<E> clazz,
    final E initialValue,
    final Consumer<E> changeListener,
    final Color selectedColor,
    final Dimension cellDimension)
  {
    
    if (clazz == null)
      throw new IllegalArgumentException ();
    if (clazz.getEnumConstants ().length > JEnumSquare.MAX_ENUM_CONSTANTS)
      throw new IllegalArgumentException ();
    
    final int squareSideSize = (int) Math.ceil (Math.sqrt (clazz.getEnumConstants ().length));
    
    setOpaque (true);
    
    setLayout (new GridLayout (squareSideSize, squareSideSize, 0, 0));
    
    final ButtonGroup bg = new ButtonGroup ();
    for (final E e : clazz.getEnumConstants ())
    {
      final JToggleButton jb = new JToggleButton ();
      bg.add (jb);
      jb.setUI (new MetalToggleButtonUI ()
      {
        @Override
        protected Color getSelectColor ()
        {
          return selectedColor;
        }
      });
      if (cellDimension != null)
      {
        jb.setPreferredSize (cellDimension);
        jb.setMinimumSize (cellDimension);
        jb.setMaximumSize (cellDimension);
      }
      jb.setToolTipText (e.toString ());
      jb.setSelected (e == initialValue);
      jb.setEnabled (changeListener != null);
      if (changeListener != null)
        jb.addActionListener ((final ActionEvent ae) -> changeListener.accept (e));
      add (JCenter.XY (jb));
    }
    
    for (int m = clazz.getEnumConstants ().length; m < squareSideSize * squareSideSize; m++)
    {
      final JToggleButton jb = new JToggleButton ();
      jb.setEnabled (false);
      add (JCenter.XY (jb));
    }
    
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
