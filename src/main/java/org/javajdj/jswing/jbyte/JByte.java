/* 
 * Copyright 2019 Jan de Jongh <jfcmdejongh@gmail.com>.
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
import java.util.List;
import java.util.function.Function;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** Swing component for showing the bit values in a byte.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JByte
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JByte (final Color color, final List<String> labelStrings)
  {
    super ();
    this.color = (color != null ? color : JByte.DEFAULT_COLOR);
    if (labelStrings != null && labelStrings.size () != 8)
      throw new IllegalArgumentException ();
    setOpaque (true);
    setLayout (new GridLayout (labelStrings == null ? 1 : 2, 8));
    this.box = new JColorCheckBox.JBoolean[8];
    for (int i = 0; i < 8; i++)
    {
      this.box[i] = new JColorCheckBox.JBoolean ((Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? this.color : null);
      add (this.box[i]);
    }
    if (labelStrings != null)
      for (final String labelString : labelStrings)
        if (labelString != null)
          add (new JLabel (labelString));
        else
          add (new JLabel ());
  }
  
  public JByte (final Color color)
  {
    this (color, null);
  }
  
  public JByte ()
  {
    this (JByte.DEFAULT_COLOR, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BOX - THE (FIXED IN SIZE AND CONTENTS) ARRAY OF SWING SUB-COMPONENTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JColorCheckBox.JBoolean[] box;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAYED VALUE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private volatile byte displayedValue = 0;
  
  public final synchronized byte getDisplayedValue ()
  {
    return this.displayedValue;
  }
  
  public final synchronized void setDisplayedValue (final byte displayedValue)
  {
    this.displayedValue = displayedValue;
    for (int i = 0; i < 8; i++)
      this.box[i].setDisplayedValue ((this.displayedValue & (0x80 >>> i)) != 0);
  }
     
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COLOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static Color DEFAULT_COLOR = Color.red;
  
  private final Color color;
  
  public final Color getColor ()
  {
    return this.color;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}