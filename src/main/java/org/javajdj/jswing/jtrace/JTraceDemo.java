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
package org.javajdj.jswing.jtrace;

import java.awt.Color;
import java.util.Collections;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/** Demo program for the {@link JTrace} and {@link JTraceDisplay} Swing components.
 *
 * <p>
 * The {@link #main} method creates a database {@link TraceDB}
 * and displays it in both a {@link JTrace} and a {@link JTraceDisplay} instance
 * in a {@link JTabbedPane}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see JTrace
 * @see JTraceDisplay
 * 
 */
public final class JTraceDemo
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JTraceDemo.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Prevents instantiation.
   * 
   */
  private JTraceDemo ()
  {
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MAIN
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs and shows the demo.
   * 
   * @param args Command-line arguments (ignored).
   * 
   */
  public static void main (final String[] args)
  {
    
    // Create the Trace DB, indexed by Integer, and populate it with example traces.
    final TraceDB<Integer> traceDB = createTraceDB ();
      
    // Create the two components, sharing a single TraceDB instance.
    final JTrace jTrace = new JTrace (traceDB);
    final JTraceDisplay jTraceDisplay = new JTraceDisplay (traceDB);
    
    // Explicitly set the number of X, Y divisions to ten on both displays,
    // and set the graticule origin at the center of the screen.
    // This way we circumvent potential (future) changes in their default values.
    jTrace.setXDivisions (10);
    jTrace.setYDivisions (10);
    jTrace.getJTraceDisplay ().setGraticuleOrigin (JTraceDisplay.GraticuleOrigin.Center);
    jTraceDisplay.setXDivisions (10);
    jTraceDisplay.setYDivisions (10);
    jTraceDisplay.setGraticuleOrigin (JTraceDisplay.GraticuleOrigin.Center);
    
    // Customize the colors on the JTrace instance.
    jTrace.setSidePanelColor (jTrace.getJTraceDisplay ().getBackground ());
    jTrace.setMarginStubColor (jTrace.getJTraceDisplay ().getBackground ());
    
    SwingUtilities.invokeLater (() ->
    {
      
      // Create the frame.
      final JFrame frame = new JFrame ("JTrace and JTraceDisplay Demo");
      frame.setExtendedState (JFrame.MAXIMIZED_BOTH);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // Create a tabbed pane with two tabs and set it as content pane.
      final JTabbedPane jTabbedPane = new JTabbedPane ();
      frame.setContentPane (jTabbedPane);
      
      // Add the trace-displaying components as tabs to the content (tabbed) pane.
      jTabbedPane.addTab ("JTrace", jTrace);
      jTabbedPane.addTab ("JTraceDisplay", jTraceDisplay);
      
      // Pack the frame and show it.
      frame.pack ();
      frame.setVisible (true);
      
    });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CREATE TRACE DB
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static TraceDB<Integer> createTraceDB ()
  {
    
    // Start with an empty TraceDB.
    
    final TraceDB<Integer> traceDB = new TraceDB<> ();
    
    // Create the array xn, yn and zn.
    // Since they are used in combinations line XnYn and XnYnZn, they have to be the same size (i.c., N).
    
    final int N = 1024;
    final double[] xn = new double[N];
    final double[] yn = new double[N];
    final double[] zn = new double[N];
    final double Ax = 1;
    final double fx = 3;
    final double Ay = 1;
    final double fy = 5;
    for (int n = 0; n < N; n++)
    {
      xn[n] = Ax * Math.cos (fx * 2 * Math.PI * ((double) n) / N);
      yn[n] = 2 + Ay * Math.sin (fy * 2 * Math.PI * ((double) n) / N);
      zn[n] = n / (N / 8);
    }
    
    // The first trace (yellow with the current default colors) consists of the yn data displayed in Yn mode.
    // In this mode, the xn are not used, but X is implied by the N range and the X range.
    // In this case, we do not supply the X range, forcing it to be equal to the N range (unlike N, X is a real number though).
    // Note that the Y range is not symmetric around zero, which is reflected by the Y zero marker on the side.
    // It should be exactly one division below the Y center position.
    //
    // In addition, yn itself is not symmetric around zero, just to make the display even more incomprehensible :-).
    // The average value of yn is 2, so yn should be displayed around the center + 1 division vertical position.
    //
    // Anyway, a total number of fy periods should be visible in the display.
    
    final TraceData test1 = new TraceData (
      TraceData.Type.Yn,
      null, yn, null, null,
      null, null, null, null,
      null, null, new TraceData.DoubleRange (-4, 6), null);
    traceDB.setTraceData (1, test1);
    traceDB.addTraceMarkers (1, Collections.singleton (TraceMarker.ZERO_Y_SIDE_MARKER));
    
    // The second trace (cyan with the current deafult colors) is pretty similar to the first one,
    // except that we now show the xn values.
    // We will use the xn for X in later traces, but here we just want to show the xn data in Yn display mode.
    // The xn data is a cosine signal around zero, and the display (Y!) range is also centered around zero.
    // We should thus see fx periods of a nice cosine.
    // Note that the zero (Y) marker in JTrace is at the Y center of the display.
    
    final TraceData test2 = new TraceData (
      TraceData.Type.Yn,
      null, xn, null, null,
      null, null, null, null,
      null, null, new TraceData.DoubleRange (-2, 2), null);
    traceDB.setTraceData (2, test2);
    traceDB.addTraceMarkers (2, Collections.singleton (TraceMarker.ZERO_Y_SIDE_MARKER));

    // The third trace (pink with the current deafult colors) shows the xn and yn data in XnYn display mode.
    // The result is a Lissajous curve.
    // Note the (easy) placement and scaling of the figure through the X and Y range settings.
    // At the same time, note the difficulty of relating the X and Y values in the curve with those in the first two traces.
    // X and Y zero markers are present in the side bars of the JTrace display.
    
    final TraceData test3 = new TraceData (
      TraceData.Type.XnYn,
      xn, yn, null, null,
      null, null, null, null,
      null, new TraceData.DoubleRange (-6, 2), new TraceData.DoubleRange (-2, 8), null);
    traceDB.setTraceData (3, test3);
    traceDB.addTraceMarkers (3, Collections.singleton (TraceMarker.ZERO_X_SIDE_MARKER));
    traceDB.addTraceMarkers (3, Collections.singleton (TraceMarker.ZERO_Y_SIDE_MARKER));
    
    // The fourth trace (blue with the current deafult colors) is identical to the first trace with the addition of Z modulation,
    // i.e., in YnZn display mode.
    // Lesson learned here is that Z modulation (through brightness) really requires careful tuning of the Z range,
    // and the (discrete) number of Z steps.
    
    final TraceData test4 = new TraceData (
      TraceData.Type.YnZn,
      null, yn, zn, null,
      null, null, null, null,
      null, null, new TraceData.DoubleRange (-5, 5), new TraceData.DoubleRange (0, 7));
    traceDB.setTraceData (4, test4);
    traceDB.addTraceMarkers (4, Collections.singleton (TraceMarker.ZERO_Y_SIDE_MARKER));
    
    // The fifth trace (with forced orange color) combines the xn, yn, and zn data into an XnYnZn trace.
    // Again, placement and scaling of the curve is easily controlled with the X and Y range settings.
    // The fifth trace also shows the impact of clipping (red parts at the top of the figure).
    
    final TraceData test5 = new TraceData (
      TraceData.Type.XnYnZn,
      xn, yn, zn, null,
      null, null, null, null,
      null, new TraceData.DoubleRange (-2, 6), new TraceData.DoubleRange (-7.01, 2.99), new TraceData.DoubleRange (0, 7));
    traceDB.setTraceData (5, test5);
    traceDB.setTraceColor (5, Color.orange);
    traceDB.addTraceMarkers (5, Collections.singleton (TraceMarker.ZERO_X_SIDE_MARKER));
    traceDB.addTraceMarkers (5, Collections.singleton (TraceMarker.ZERO_Y_SIDE_MARKER));

    // The sixth trace uses a Function to specify the trace data.
    // Note that with the F_Y_vs_X display mode, setting the X range is mandatory.
    // We set the Y range such that the trace appears at the bottom of the display with 2 divisions peak-to-peak value.
    
    final TraceData test6 = new TraceData (
      TraceData.Type.F_Y_vs_X,
      null, null, null, REFERENCE_SAW_TOOTH,
      null, null, null, null,
      null, new TraceData.DoubleRange (-3, 7), new TraceData.DoubleRange (-1, 9), null);
    traceDB.setTraceData (6, test6);
    traceDB.setTraceColor (6, Color.WHITE);
    traceDB.addTraceMarkers (6, Collections.singleton (TraceMarker.ZERO_X_SIDE_MARKER));
    traceDB.addTraceMarkers (6, Collections.singleton (TraceMarker.ZERO_Y_SIDE_MARKER));

    return traceDB;
    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // REFERENCE SAWTOOTH FUNCTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** A sawtooth function with period 1, average value 0, peak-to-peak value 2 and {@code f(0+) = -1}.
   * 
   */
  private final static Function<Double, Double> REFERENCE_SAW_TOOTH = (t) -> 2 * Math.IEEEremainder (t - 0.5, 1);

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
