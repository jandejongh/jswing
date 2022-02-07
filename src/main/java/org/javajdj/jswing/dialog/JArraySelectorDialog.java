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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.javajdj.jswing.jcenter.JCenter;

/** Dialog requesting a single value out of a fixed array.
 * 
 * @param <E> The type of the elements in the array.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see JComboBox
 * @see JEnumSelectorDialog
 * 
 */
public class JArraySelectorDialog<E>
  extends JDialog
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JArraySelectorDialog.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the dialog.
   * 
   * <p>
   * Null values and duplicates in the array supplied are allowed, but both will lead to ambiguous interpretation of the value
   * returned by the dialog through {@link #getSelectedItem}.
   *
   * @param owner          The Frame from which the dialog is displayed;
   *                         see {@link JDialog#JDialog(java.awt.Frame, java.lang.String, boolean)}.
   * @param title          The String to display in the dialog's title bar;
   *                         see {@link JDialog#JDialog(java.awt.Frame, java.lang.String, boolean)}.
   * @param modal          Specifies whether dialog blocks user input to other top-level windows when shown;
   *                         see {@link JDialog#JDialog(java.awt.Frame, java.lang.String, boolean)}.
   * @param values         The values from which to choose.
   * @param initialValue   The optional initial value to display; when {@code null} no initial value is selected.
   * @param question       An optional question string in the dialog.
   * @param itemListener   An optional {@link ItemListener} for changes in the selected value.
   * 
   * @throws IllegalArgumentException If {@code values == null} or a non-{@code null initialValue} is not in {@code values}.
   * 
   */
  public JArraySelectorDialog (
    final Frame owner,
    final String title,
    final boolean modal,
    final E[] values,
    final E initialValue,
    final String question,
    final ItemListener itemListener)
  {
    
    super (owner, title, modal);
    
    if (values == null)
      throw new IllegalArgumentException ();
    
    if (initialValue != null && ! Arrays.asList (values).contains (initialValue))
      throw new IllegalArgumentException ();
    
    setLocationRelativeTo (owner);
    
    //
    // XXX For some reason, including me, the layout of this component has turned WAY too complex.
    // Yet, it works, for now...
    //
    
    getContentPane ().setLayout (new BoxLayout (getContentPane (), BoxLayout.Y_AXIS));
    
    getContentPane ().add (new JLabel (" "));
    getContentPane ().add (new JLabel (" "));
    
    if (question != null)
    {
      getContentPane ().add (JCenter.XY (new JLabel ("   " + question + "   ")));
      getContentPane ().add (new JLabel (" "));
      getContentPane ().add (new JLabel (" "));
    }
    
    final JPanel jComboBoxPane = new JPanel ();
    getContentPane ().add (jComboBoxPane);
    jComboBoxPane.setLayout (new BoxLayout (jComboBoxPane, BoxLayout.X_AXIS));
    jComboBoxPane.add (new JLabel ("    "));
    this.jComboBox = new JComboBox (values)
    {
      @Override
      public final Dimension getMaximumSize ()
      {
        final Dimension dim = super.getMaximumSize ();
        dim.width = getPreferredSize ().width;
        dim.height = getPreferredSize ().height;
        return dim;
      }
    };
    this.jComboBox.setEditable (false);
    jComboBoxPane.add (JCenter.XY (this.jComboBox));
    this.jComboBox.setSelectedItem (initialValue);
    if (itemListener != null)
      this.jComboBox.addItemListener (itemListener);
    jComboBoxPane.add (new JLabel ("    "));
    
    getContentPane ().add (new JLabel (" "));
    getContentPane ().add (new JLabel (" "));
    
    final JPanel southPanel = new JPanel ();
    getContentPane ().add (southPanel);
    southPanel.setLayout (new BoxLayout (southPanel, BoxLayout.X_AXIS));
    southPanel.add (new JLabel ("    "));
    final JButton okButton = new JButton ("  OK  ");
    okButton.addActionListener ((final ActionEvent ae) ->
    {
      this.okPressed = true;
      JArraySelectorDialog.this.setVisible (false);
    });
    southPanel.add (JCenter.XY (okButton));
    southPanel.add (new JLabel ("            "));
    final JButton cancelButton = new JButton ("Cancel");
    cancelButton.addActionListener ((final ActionEvent ae) ->
    {
      this.okPressed = false;
      JArraySelectorDialog.this.setVisible (false);
    });
    southPanel.add (JCenter.XY (cancelButton));
    southPanel.add (new JLabel ("    "));
    
    getContentPane ().add (new JLabel (" "));
    
    pack ();
    
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMBO BOX
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JComboBox<E> jComboBox;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // OK PRESSED
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private boolean okPressed = false;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SELECTED ITEM
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns the selected item, if any, once the dialog has closed.
   * 
   * <p>
   * Note: The selected item returned here is always {@code null} until the user hits the OK or Cancel buttons
   * or closes the dialog.
   * Only if OK was pressed, can this method return a non-{@code null} value.
   * 
   * @return The selected item, {@code null} if none selected or if the user hit the cancel button.
   * 
   */
  public final E getSelectedItem ()
  {
    return this.okPressed ? (E) this.jComboBox.getSelectedItem () : null;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
