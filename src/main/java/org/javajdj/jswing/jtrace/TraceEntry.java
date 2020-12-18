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
   * @param traceData               The trace data, may be {@code null}.
   * @param rescaleDisplayXToNRange Whether or not to rescale the display X range in case of a partial N range.
   * @param traceColor              The trace color, may be {@code null}.
   * @param traceMarkers            The (initial) {@link Set} of trace markers, may be {@code null} or empty
   *                                  but must <i>not</i> contain {@code null}.
   * @param xOffset                 The X Offset, see {@link #getXOffset};
   *                                  may be {@code null} in which case the offset defaults to zero.
   * @param yOffset                 The Y Offset, see {@link #getYOffset};
   *                                  may be {@code null} in which case the offset defaults to zero.
   * @param zOffset                 The Z Offset, see {@link #getZOffset};
   *                                  may be {@code null} in which case the offset defaults to zero.
   * 
   * @throws IllegalArgumentException If the set of trace markers contains {@code null}.
   * 
   */
  public TraceEntry (
    final TraceData traceData,
    final boolean rescaleDisplayXToNRange,
    final Color traceColor,
    final Set<TraceMarker> traceMarkers,
    final Double xOffset,
    final Double yOffset,
    final Double zOffset)
  {
    this.traceData = traceData;
    this.rescaleDisplayXToNRange = rescaleDisplayXToNRange;
    this.traceColor = traceColor;
    if (traceMarkers != null)
    {
      if (traceMarkers.contains (null))
        throw new IllegalArgumentException ();
      this.traceMarkers.addAll (traceMarkers);
    }
    this.xOffset = (xOffset != null) ? xOffset : 0;
    this.yOffset = (yOffset != null) ? yOffset : 0;
    this.zOffset = (zOffset != null) ? zOffset : 0;
  }

  /** Constructs the trace entry (auxiliary constructor).
   * 
   * <p>
   * The initial {@link Set} of {@link TraceMarker}s is empty.
   * 
   * <p>
   * X, Y and Z Offsets are set to zero.
   * 
   * @param traceData               The trace data, may be {@code null}.
   * @param rescaleDisplayXToNRange Whether or not to rescale the display X range in case of a partial N range.
   * @param traceColor              The trace color, may be {@code null}.
   * 
   */
  public TraceEntry (
    final TraceData traceData,
    final boolean rescaleDisplayXToNRange,
    final Color traceColor)
  {
    this (traceData, rescaleDisplayXToNRange, traceColor, null, null, null, null);
  }

  /** Constructs the trace entry (auxiliary constructor).
   * 
   * <p>
   * Traces with partial N ranges are expanded in the display by default
   * (the argument is given the value {@link #DEFAULT_RESCALE_DISPLAY_X_TO_N_RANGE}).
   * 
   * <p>
   * The initial {@link Set} of {@link TraceMarker}s is empty.
   * 
   * <p>
   * X, Y and Z Offsets are set to zero.
   * 
   * @param traceData               The trace data, may be {@code null}.
   * @param traceColor              The trace color, may be {@code null}.
   * 
   * @see #DEFAULT_RESCALE_DISPLAY_X_TO_N_RANGE
   * 
   */
  public TraceEntry (
    final TraceData traceData,
    final Color traceColor)
  {
    this (traceData, TraceEntry.DEFAULT_RESCALE_DISPLAY_X_TO_N_RANGE, traceColor, null, null, null, null);
  }

  /** The empty trace entry (no data, color, markers, etc.).
   * 
   * <p>
   * Traces (necessarily added later) with partial N ranges are expanded in the display by default
   * (the argument is given the value {@link #DEFAULT_RESCALE_DISPLAY_X_TO_N_RANGE}).
   * 
   * <p>
   * X, Y and Z Offsets are set to zero.
   * 
   * @see #DEFAULT_RESCALE_DISPLAY_X_TO_N_RANGE
   * 
   */
  public static TraceEntry EMPTY = new TraceEntry (
    null,
    TraceEntry.DEFAULT_RESCALE_DISPLAY_X_TO_N_RANGE,
    null,
    null,
    null,
    null,
    null);
  
  /** Creates a copy with given {@link TraceData}.
   * 
   * @param traceData The new trace data, may be {@code null}.
   * 
   * @return The new {@link TraceEntry}.
   * 
   */
  public final TraceEntry withTraceData (final TraceData traceData)
  {
    return new TraceEntry (
      traceData,
      this.rescaleDisplayXToNRange,
      this.traceColor,
      this.traceMarkers,
      this.xOffset,
      this.yOffset,
      this.zOffset);
  }
  
  /** Creates a copy with given setting whether or not to rescale the display X range in case of a partial N range.
   * 
   * @param rescaleDisplayXToNRange The new setting whether or not to rescale the display X range in case of a partial N range.
   * 
   * @return The new {@link TraceEntry}.
   * 
   */
  public final TraceEntry withRescaleDisplayXToNRange (final boolean rescaleDisplayXToNRange)
  {
    return new TraceEntry (
      this.traceData,
      rescaleDisplayXToNRange,
      this.traceColor,
      this.traceMarkers,
      this.xOffset,
      this.yOffset,
      this.zOffset);
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
    return new TraceEntry (
      this.traceData,
      this.rescaleDisplayXToNRange,
      traceColor,
      this.traceMarkers,
      this.xOffset,
      this.yOffset,
      this.zOffset);
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
    return new TraceEntry (
      this.traceData,
      this.rescaleDisplayXToNRange,
      this.traceColor,
      traceMarkers,
      this.xOffset,
      this.yOffset,
      this.zOffset);
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
    return new TraceEntry (
      this.traceData,
      this.rescaleDisplayXToNRange,
      this.traceColor,
      newTraceMarkers,
      this.xOffset,
      this.yOffset,
      this.zOffset);
  }
  
  /** Creates a copy without trace markers ({@link TraceMarker}s).
   * 
   * @return The new {@link TraceEntry}.
   * 
   */
  public final TraceEntry withoutTraceMarkers ()
  {
    return new TraceEntry (
      this.traceData,
      this.rescaleDisplayXToNRange,
      this.traceColor,
      null,
      this.xOffset,
      this.yOffset,
      this.zOffset);
  }
  
  /** Creates a copy with given X Offset.
   * 
   * @param xOffset The new X Offset.
   * 
   * @return The new {@link TraceEntry}.
   * 
   * @see #getXOffset
   * 
   */
  public final TraceEntry withXOffset (final double xOffset)
  {
    return new TraceEntry (
      this.traceData,
      this.rescaleDisplayXToNRange,
      this.traceColor,
      this.traceMarkers,
      xOffset,
      this.yOffset,
      this.zOffset);
  }
  
  /** Creates a copy with given Y Offset.
   * 
   * @param yOffset The new Y Offset.
   * 
   * @return The new {@link TraceEntry}.
   * 
   * @see #getYOffset
   * 
   */
  public final TraceEntry withYOffset (final double yOffset)
  {
    return new TraceEntry (
      this.traceData,
      this.rescaleDisplayXToNRange,
      this.traceColor,
      this.traceMarkers,
      this.xOffset,
      yOffset,
      this.zOffset);
  }
  
  /** Creates a copy with given Z Offset.
   * 
   * @param zOffset The new Z Offset.
   * 
   * @return The new {@link TraceEntry}.
   * 
   * @see #getZOffset
   * 
   */
  public final TraceEntry withZOffset (final double zOffset)
  {
    return new TraceEntry (
      this.traceData,
      this.rescaleDisplayXToNRange,
      this.traceColor,
      this.traceMarkers,
      this.xOffset,
      this.yOffset,
      zOffset);
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
  // RESCALE DISPLAY X TO N RANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static boolean DEFAULT_RESCALE_DISPLAY_X_TO_N_RANGE = true;
  
  private final boolean rescaleDisplayXToNRange;
  
  /** Returns whether or not to rescale the display X range in case of a partial N range.
   * 
   * @return Whether or not to rescale the display X range in case of a partial N range.
   * 
   */
  public final boolean isRescaleDisplayXToNRange ()
  {
    return this.rescaleDisplayXToNRange;
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
  // X, Y, Z OFFSETS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double xOffset;
  
  /** Returns the X Offset.
   * 
   * <p>
   * The X offset is used when the X values and ranges do not reflect the real "physical" values of the data,
   * but are instead <i>to be</i> shifted by this offset.
   * In other words, the real value is the value from the data <i>plus</i> the offset.
   * 
   * <p>
   * Offset data present here <i>never</i> affect the way the trace is displayed on the screen, but hey
   * are important hints to display components so they show "real" physical values when needed/requested.
   * 
   * @return The X offset (default zero).
   * 
   */
  public final double getXOffset ()
  {
    return this.xOffset;
  }
  
  private final double yOffset;
  
  /** Returns the Y Offset.
   * 
   * <p>
   * The Y offset is used when the Y values and ranges do not reflect the real "physical" values of the data,
   * but are instead <i>to be</i> shifted by this offset.
   * In other words, the real value is the value from the data <i>plus</i> the offset.
   * 
   * <p>
   * Offset data present here <i>never</i> affect the way the trace is displayed on the screen, but hey
   * are important hints to display components so they show "real" physical values when needed/requested.
   * 
   * @return The Y offset (default zero).
   * 
   */
  public final double getYOffset ()
  {
    return this.yOffset;
  }
  
  private final double zOffset;
  
  /** Returns the Z Offset.
   * 
   * <p>
   * The Z offset is used when the Z values and ranges do not reflect the real "physical" values of the data,
   * but are instead <i>to be</i> shifted by this offset.
   * In other words, the real value is the value from the data <i>plus</i> the offset.
   * 
   * <p>
   * Offset data present here <i>never</i> affect the way the trace is displayed on the screen, but hey
   * are important hints to display components so they show "real" physical values when needed/requested.
   * 
   * @return The Z offset (default zero).
   * 
   */
  public final double getZOffset ()
  {
    return this.zOffset;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
