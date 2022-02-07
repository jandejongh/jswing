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
package org.javajdj.jswing.dialog;

import java.awt.Frame;
import java.awt.event.ItemListener;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JDialog;

/** Dialog requesting an {@link Enum} value.
 *
 * @param <E> The enum type.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see JComboBox
 * @see JArraySelectorDialog
 * @see Class#getEnumConstants
 * 
 */
public class JEnumSelectorDialog<E extends Enum<E>>
  extends JArraySelectorDialog<E>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JEnumSelectorDialog.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the dialog.
   * 
   * @param owner          The Frame from which the dialog is displayed;
   *                         see {@link JDialog#JDialog(java.awt.Frame, java.lang.String, boolean)}.
   * @param title          The String to display in the dialog's title bar;
   *                         see {@link JDialog#JDialog(java.awt.Frame, java.lang.String, boolean)}.
   * @param modal          Specifies whether dialog blocks user input to other top-level windows when shown;
   *                         see {@link JDialog#JDialog(java.awt.Frame, java.lang.String, boolean)}.
   * @param clazz          The {@code class} of the values represented; must be an {@code enum} type.
   * @param initialValue   The optional initial value to display; when {@code null} no initial value is selected.
   * @param question       An optional question string in the dialog.
   * @param itemListener   An optional {@link ItemListener} for changes in the selected value.
   * 
   * @throws IllegalArgumentException If {@code clazz == null}, or a non-{@code null initialValue} is not in {@code clazz}.
   * 
   */
  public JEnumSelectorDialog (
    final Frame owner,
    final String title,
    final boolean modal,
    final Class<E> clazz,
    final E initialValue,
    final String question,
    final ItemListener itemListener)
  {
    
    super (
      owner,
      title,
      modal,
      clazz.getEnumConstants (),
      initialValue,
      question,
      itemListener);
    
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
