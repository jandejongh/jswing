/* 
 * Copyright 2020 Jan de Jongh <jfcmdejongh@gmail.com>.
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** Swing component for showing a given number of (least significant) bit values in a long.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JBitsLong
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JBitsLong (
    final Color color,
    final int length,
    final List<String> labelStrings,
    final Consumer<Long> listener,
    final boolean autoUpdate)
  {
    super ();
    this.color = (color != null ? color : JBitsLong.DEFAULT_COLOR);
    if (length < 0 || length > 64)
      throw new IllegalArgumentException ();
    if (labelStrings != null && labelStrings.size () != length)
      throw new IllegalArgumentException ();
    this.listener = listener;
    this.autoUpdate = autoUpdate;
    setOpaque (true);
    setLayout (new GridLayout (labelStrings == null ? 1 : 2, length));
    this.box = new JColorCheckBox.JBoolean[length];
    for (int i = 0; i < length; i++)
    {
      this.box[i] = new JColorCheckBox.JBoolean ((Function<Boolean, Color>) (Boolean t) -> (t != null && t) ? this.color : null);
      if (listener != null || autoUpdate)
        this.box[i].addActionListener (new BoxListener (length - i - 1));        
      add (this.box[i]);
    }
    if (labelStrings != null)
      for (final String labelString : labelStrings)
        if (labelString != null)
          add (new JLabel (labelString));
        else
          add (new JLabel ());
  }
  
  public JBitsLong (
    final Color color,
    final int length,
    final List<String> labelStrings)
  {
    this (color, length, labelStrings, null, false);
  }
  
  public JBitsLong (final Color color, final int length)
  {
    this (color, length, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BOX - THE (FIXED IN SIZE AND CONTENTS) ARRAY OF SWING SUB-COMPONENTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JColorCheckBox.JBoolean[] box;
  
  public final JColorCheckBox.JBoolean[] getBoxes ()
  {
    return Arrays.copyOf (this.box, this.box.length);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAYED VALUE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private volatile long displayedValue = 0;
  
  public final synchronized long getDisplayedValue ()
  {
    return this.displayedValue;
  }
  
  public final synchronized void setDisplayedValue (final long displayedValue)
  {
    this.displayedValue = displayedValue;
    for (int i = 0; i < this.box.length; i++)
      this.box[i].setDisplayedValue ((this.displayedValue & (0x8000000000000000L >>> (64 - this.box.length + i))) != 0);
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
  // LISTENER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Consumer<Long> listener;
  
  private final boolean autoUpdate;
  
  private final class BoxListener
    implements ActionListener
  {

    public BoxListener (final int bit)
    {
      this.bit = bit;
    }
    
    
    final int bit;
    
    @Override
    public void actionPerformed (ActionEvent ae)
    {
      final long newValue;
      synchronized (JBitsLong.this)
      {
        final long oldValue = JBitsLong.this.getDisplayedValue ();
        newValue = oldValue ^ (1L << this.bit);
        if (JBitsLong.this.autoUpdate)
          JBitsLong.this.setDisplayedValue (newValue);
      }
      if (JBitsLong.this.listener != null)
        JBitsLong.this.listener.accept (newValue);
    }
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}