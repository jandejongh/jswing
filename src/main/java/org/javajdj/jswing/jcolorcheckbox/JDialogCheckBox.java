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
package org.javajdj.jswing.jcolorcheckbox;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** A small button ({@link JColorCheckBox}) that pops up a {@link JDialog}.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see JDialog
 * 
 */
public class JDialogCheckBox
  extends JColorCheckBox<Void>
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Creates the button with given (fill) color.
   * 
   * @param color       The fill color; if {@code null}, only the outline of the box is drawn.
   * @param dialogTitle The title of the dialog (may be empty of {@code null}.
   * @param dialogSize  The dialog size (may be {@code null}).
   * @param contentPane The content pane of the dialog; if {@code null}, a new {@link JPanel} is constructed.
   * 
   * @see #getDialogContentPane
   * 
   */  
  public JDialogCheckBox (
    final Color color,
    final String dialogTitle,
    final Dimension dialogSize,
    final Container contentPane)
  {
    super ((Void t) -> color);
    final JDialog jDialog = new JOptionPane ().createDialog (dialogTitle);
    if (dialogSize != null)
      jDialog.setSize (dialogSize);
    jDialog.setLocationRelativeTo (null);
    this.dialogContentPane = contentPane != null ? contentPane : new JPanel ();
    jDialog.setContentPane (this.dialogContentPane);
    addActionListener ((ae) ->
    {
      jDialog.setVisible (true);
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DIALOG CONTENT PANE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Container dialogContentPane;
  
  /** Returns the dialog content pane.
   * 
   * <p>
   * The dialog content pane is either the one supplied upon construction, or a newly create {@link JPanel} (at construction time).
   * The content pane cannot be set, but users are free to edit is contents anytime.
   * 
   * @return The dialog content pane.
   * 
   */
  public final Container getDialogContentPane ()
  {
    return this.dialogContentPane;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}