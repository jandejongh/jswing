/* 
 * Copyright 2020-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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

/** Swing component for showing a given number of labeled {@code boolean}s as {@link JColorCheckBox.JBoolean}s.
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
  
  /** Constructs the component.
   * 
   * <p>
   * The label strings are trimmed before use internally, but not in the (displayed) label.
   * This means you can use spaces in the label strings for (rough) alignment.
   * 
   * @param colorFunction         The color function, may be {@code null} or return {@code null},
   *                                both in which cases the component appears as indicating {@code false},
   *                                i.e., not filling the square.
   * @param labelStrings          The label strings, non-{@code null}, non-empty,
   *                                not containing {@code null}, empty or duplicate strings.
   * @param horizontalOrientation Whether to outline the booleans horizontally.
   * 
   * @throws IllegalArgumentException If the {@code labelStrings} argument is {@code null} or empty,
   *                                    or contains {@code null} or empty (when trimmed) strings or duplicates.
   * 
   * @see JColorCheckBox.JBoolean
   * 
   */
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
      box.setEnabled (false);
      if (this.boxMap.containsKey (labelString.trim ()))
        throw new IllegalArgumentException ();
      this.boxMap.put (labelString.trim (), box);
      add (box);
      if (! horizontalOrientation)
      {
        final JLabel jLabel = new JLabel (labelString);
        jLabel.setHorizontalAlignment (SwingConstants.LEFT);
        jLabel.setVerticalAlignment (SwingConstants.CENTER);
        add (jLabel);
      }
    }
    if (horizontalOrientation)
      for (final String labelString : labelStrings)
      {
        final JLabel jLabel = new JLabel (labelString);
        jLabel.setHorizontalAlignment (SwingConstants.CENTER);
        jLabel.setVerticalAlignment (SwingConstants.CENTER);
        add (jLabel);
      }
  }

  /** Constructs the component.
   * 
   * <p>
   * The label strings are trimmed before use internally, but not in the (displayed) label.
   * This means you can use spaces in the label strings for (rough) alignment.
   * 
   * @param color                The color to use to indicate (filling the square) the {@code true} value,
   *                               may be {@code null} in which case the component appears as indicating {@code false},
   *                               i.e., not/never filling the square.
   * @param labelStrings          The label strings, non-{@code null}, non-empty,
   *                                not containing {@code null}, empty or duplicate strings.
   * @param horizontalOrientation Whether to outline the booleans horizontally.
   * 
   * @throws IllegalArgumentException If the {@code labelStrings} argument is {@code null} or empty,
   *                                    or contains {@code null} or empty (when trimmed) strings or duplicates.
   * 
   * @see JColorCheckBox.JBoolean
   * 
   */
  public JBooleans (
    final Color color,
    final Set<String> labelStrings,
    final boolean horizontalOrientation)
  {
    this (
      color == null ? null : ((b) -> b != null && b ? color : null),
      labelStrings,
      horizontalOrientation);
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

  /** Returns the displayed (boolean) value for given key (label string).
   * 
   * <p>
   * The key argument is trimmed before use.
   * 
   * @param key The key.
   * 
   * @return The displayed value.
   * 
   * @throws IllegalArgumentException If the key is unknown.
   * 
   */
  public final boolean getDisplayedValue (final String key)
  {
    if (! this.boxMap.containsKey (key.trim ()))
      throw new IllegalArgumentException ();
    return this.boxMap.get (key.trim ()).getDisplayedValue ();
  }
  
  /** Sets the displayed (boolean) value for given key (label string).
   * 
   * <p>
   * The key argument is trimmed before use.
   * 
   * @param key            The key.
   * @param displayedValue The value to set.
   * 
   * @throws IllegalArgumentException If the key is unknown.
   * 
   */
  public final void setDisplayedValue (final String key, final boolean displayedValue)
  {
    if (! this.boxMap.containsKey (key.trim ()))
      throw new IllegalArgumentException ();
    this.boxMap.get (key.trim ()).setDisplayedValue (displayedValue);
  }
     
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}