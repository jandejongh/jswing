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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** Swing component for showing a given number of {@code boolean}s as {@link JColorCheckBox.JBoolean}s.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JBooleans
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / FACTORIES / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JBooleans (
    final Function<Boolean, Color> colorFunction,
    final Set<String> labelStrings,
    final boolean horizontalOrientation)
  {
    super ();
    if (labelStrings == null || labelStrings.isEmpty () || labelStrings.contains (null))
      throw new IllegalArgumentException ();
    for (final String labelString : labelStrings)
      if (labelString.trim ().isEmpty ())
        throw new IllegalArgumentException ();
    this.boxMap = new LinkedHashMap ();
    setOpaque (true);
    if (horizontalOrientation)
      setLayout (new GridLayout (2, labelStrings.size ()));
    else
      setLayout (new GridLayout (labelStrings.size (), 2));
    for (final String labelString : labelStrings)
    {
      final JColorCheckBox.JBoolean box = new JColorCheckBox.JBoolean (colorFunction);
      box.setHorizontalAlignment (SwingConstants.CENTER);
      box.setVerticalAlignment (SwingConstants.CENTER);
      this.boxMap.put (labelString, box);
      add (box);      
    }
    for (final String labelString : labelStrings)
    {
      final JLabel jLabel = new JLabel (labelString);
      jLabel.setHorizontalAlignment (SwingConstants.CENTER);
      jLabel.setVerticalAlignment (SwingConstants.CENTER);
      add (jLabel);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BOX MAP
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Map<String, JColorCheckBox.JBoolean> boxMap;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAYED VALUE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final synchronized boolean getDisplayedValue (final String key)
  {
    if (! this.boxMap.containsKey (key))
      throw new IllegalArgumentException ();
    return this.boxMap.get (key).getDisplayedValue ();
  }
  
  public final synchronized void setDisplayedValue (final String key, final boolean displayedValue)
  {
    if (! this.boxMap.containsKey (key))
      throw new IllegalArgumentException ();
    this.boxMap.get (key).setDisplayedValue (displayedValue);
  }
     
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}