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
package org.javajdj.jswing.jcenter;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** A Swing {@link JPanel} that centers (in X, Y or both dimensions)
 *  a single {@link JComponent} passed at construction <i>without resizing it</i>.
 * 
 * <p>
 * Centering the component may be in X direction, Y direction, or both (or none, in which case the component
 * is placed top-left).
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JCenter
  extends JPanel
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JCenter (final JComponent component, final boolean centerX, final boolean centerY)
  {
    
    super ();
    
    final GroupLayout layout = new GroupLayout (this);
    setLayout (layout);

    if ((! centerX) && ! centerY)
    {
      layout.setHorizontalGroup (
        layout.createSequentialGroup ()
          .addComponent (component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
      );
      layout.setVerticalGroup (
        layout.createSequentialGroup ()
          .addComponent (component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
      );      
    }
    else if ((! centerX) && centerY)
    {
      final JLabel north = new JLabel ();
      final JLabel south = new JLabel ();
      layout.setHorizontalGroup (
        layout.createParallelGroup ()
          .addComponent (north, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent (component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent (south, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      layout.setVerticalGroup (
        layout.createSequentialGroup ()
          .addComponent (north, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent (component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent (south, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );      
    }
    else if (centerX && ! centerY)
    {
      final JLabel west = new JLabel ();
      final JLabel east = new JLabel ();
      layout.setHorizontalGroup (
        layout.createSequentialGroup ()
          .addComponent (west, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent (component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent (east, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      layout.setVerticalGroup (
        layout.createParallelGroup ()
          .addComponent (west, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent (component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent (east, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );      
    }
    else
    {
      final JLabel west = new JLabel ();
      final JLabel north = new JLabel ();
      final JLabel east = new JLabel ();
      final JLabel south = new JLabel ();
      layout.setHorizontalGroup (
        layout.createSequentialGroup ()
          .addComponent (west, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup (layout.createParallelGroup (GroupLayout.Alignment.CENTER)
            .addComponent (north, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent (component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent (south, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addComponent (east, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      layout.setVerticalGroup (
        layout.createSequentialGroup ()
          .addComponent (north, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup (layout.createParallelGroup (GroupLayout.Alignment.CENTER)
            .addComponent (west, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent (component, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent (east, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addComponent (south, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
    }

  }
  
  public JCenter (final JComponent component)
  {
    this (component, true, true);
  }

  public static JCenter X (final JComponent component)
  {
    return new JCenter (component, true, false);
  }
  
  public static JCenter Y (final JComponent component)
  {
    return new JCenter (component, false, true);
  }
  
  public static JCenter XY (final JComponent component)
  {
    return new JCenter (component, true, true);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
