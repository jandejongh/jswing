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
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

/** Trace data (as entry in a trace database) augmented with markers and graphics information.
 * 
 * <p>
 * This class is designed for internal use in {@link JTrace}, {@link JTraceDisplay}, and {@link TraceDB},
 * but is exposed as it may be useful in other use cases as well.
 * 
 * <p>
 * The class is final because it is used internally in {@link JTraceDisplay};
 * often by recreating {@link TraceEntry} objects with (slightly) different properties.
 * 
 * <p>
 * Objects of this class are immutable.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public final class TraceEntry
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (TraceEntry.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the trace entry (main constructor).
   * 
   * @param traceData    The trace data, may be {@code null}.
   * @param traceColor   The trace color, may be {@code null}.
   * @param traceMarkers The (initial) {@link Set} of trace markers, may be {@code null} or empty
   *                       but must <i>not</i> contain {@code null}.
   * 
   * @throws IllegalArgumentException If the set of trace markers contains {@code null}.
   * 
   */
  public TraceEntry (
    final TraceData traceData,
    final Color traceColor,
    final Set<TraceMarker> traceMarkers)
  {
    this.traceData = traceData;
    this.traceColor = traceColor;
    if (traceMarkers != null)
    {
      if (traceMarkers.contains (null))
        throw new IllegalArgumentException ();
      this.traceMarkers.addAll (traceMarkers);
    }
  }


  /** Constructs the trace entry (auxiliary constructor).
   * 
   * <p>
   * The initial {@link Set} of {@link TraceMarker}s is empty.
   * 
   * @param traceData  The trace data, may be {@code null}.
   * @param traceColor The trace color, may be {@code null}.
   * 
   */
  public TraceEntry (
    final TraceData traceData,
    final Color traceColor)
  {
    this (traceData, traceColor, null);
  }

  /** The empty trace entry (no data, color, markers, etc.).
   * 
   */
  public static TraceEntry EMPTY = new TraceEntry (null, null, null);
  
  /** Creates a copy with given {@link TraceData}.
   * 
   * @param traceData The new trace data, may be {@code null}.
   * 
   * @return The new {@link TraceEntry}.
   * 
   */
  public final TraceEntry withTraceData (final TraceData traceData)
  {
    return new TraceEntry (traceData, this.traceColor, this.traceMarkers);
  }
  
  /** Creates a copy with given trace {@link Color}.
   * 
   * @param traceColor The new trace {@link Color}, may be {@code null}.
   * 
   * @return The new {@link TraceEntry}.
   * 
   */
  public final TraceEntry withTraceColor (final Color traceColor)
  {
    return new TraceEntry (this.traceData, traceColor, this.traceMarkers);
  }
  
  /** Creates a copy with given trace markers ({@link TraceMarker}s).
   * 
   * <p>
   * Existing active trace markers are cleared (unless present in the argument).
   * 
   * @param traceMarkers A set (may be {@code null} but <i>not</i> contain {@code null}) holding the new {@link TraceMarker}s.
   * 
   * @return The new {@link TraceEntry}.
   * 
   * @throws IllegalArgumentException If the set of trace markers contains {@code null}.
   * 
   */
  public final TraceEntry withTraceMarkers (final Set<TraceMarker> traceMarkers)
  {
    return new TraceEntry (this.traceData, this.traceColor, traceMarkers);
  }
  
  /** Creates a copy with added given trace markers ({@link TraceMarker}s).
   * 
   * <p>
   * Existing active trace markers are maintained.
   * 
   * @param traceMarkers A set (may be {@code null} but <i>not</i> contain {@code null}) holding the {@link TraceMarker}s to add.
   * 
   * @return The new {@link TraceEntry}.
   * 
   * @throws IllegalArgumentException If the set of trace markers contains {@code null}.
   * 
   */
  public final TraceEntry withTraceMarkersAdded (final Set<TraceMarker> traceMarkers)
  {
    final EnumSet<TraceMarker> newTraceMarkers = EnumSet.copyOf (this.traceMarkers);
    if (traceMarkers != null)
      newTraceMarkers.addAll (traceMarkers);
    return new TraceEntry (this.traceData, this.traceColor, newTraceMarkers);
  }
  
  /** Creates a copy without trace markers ({@link TraceMarker}s).
   * 
   * @return The new {@link TraceEntry}.
   * 
   */
  public final TraceEntry withoutTraceMarkers ()
  {
    return new TraceEntry (this.traceData, this.traceColor, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE DATA
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final TraceData traceData;
  
  /** Returns the trace data.
   * 
   * @return The trace data, may be {@code null}.
   * 
   */
  public final TraceData getTraceData ()
  {
    return this.traceData;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE COLOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Color traceColor;
  
  /** Returns the trace {@link Color}.
   * 
   * @return The trace {@link Color}, may be {@code null}.
   * 
   */
  public final Color getTraceColor ()
  {
    return this.traceColor;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE MARKERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final EnumSet<TraceMarker> traceMarkers = EnumSet.noneOf (TraceMarker.class);
  
  /** Returns the set of active {@link TraceMarker}s.
   * 
   * <p>
   * The result is an unmodifiable collection.
   * 
   * @return A {@link Set} (view) holding the active trace markers.
   * 
   * @see Collections#unmodifiableSet
   * 
   */
  public final Set<TraceMarker> getTraceMarkers ()
  {
    return Collections.unmodifiableSet (this.traceMarkers);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
