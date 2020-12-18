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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/** An in-core database of traces and their properties.
 *
 * <p>
 * The database is thread-safe and supports listener notification of changes.
 * 
 * <p>
 * Note that the {@code null} key is allowed (and bears no special meaning in this {@code class}).
 * 
 * @param <K> The key type used for distinguishing traces.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see JTrace
 * @see JTraceDisplay
 * 
 */
public class TraceDB<K>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (TraceDB.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the (empty) database.
   * 
   */
  public TraceDB ()
  {
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE DB LISTENERS AND NOTIFICATIONS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** A listener to database changes.
   * 
   */
  @FunctionalInterface
  public interface TraceDBListener
  {
    
    /** Notification of a change in the database.
     * 
     * @param dB The non-{@code null} database to which the change applies.
     * 
     */
    void dbChanged (final TraceDB dB);
    
  }
  
  private final Set<TraceDBListener> listeners = new LinkedHashSet<> ();

  private final Object listenersLock = new Object ();
  
  /** Adds a listener for changes in the database.
   * 
   * @param l The listener to add.
   * 
   */
  public void addDBChangedListener (final TraceDBListener l)
  {
    synchronized (this.listenersLock)
    {
      if (l != null && ! this.listeners.contains (l))
        this.listeners.add (l);
    }
  }
  
  /** Removes a listener for changes in the database.
   * 
   * @param l The listener to remove; ignored when {@code null} or absent (as listener).
   * 
   */
  public void removeDBChangedListener (final TraceDBListener l)
  {
    synchronized (this.listenersLock)
    {
      if (l != null)
        this.listeners.remove (l);
    }
  }
  
  private void fireDBChanged ()
  {
    final Set<TraceDBListener> listenersCopy;
    synchronized (this.listenersLock)
    {
      listenersCopy = new LinkedHashSet<> (this.listeners);
    }
    for (final TraceDBListener l : listenersCopy)
      l.dbChanged (this);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE ENTRIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Map<K, TraceEntry> traceEntries = new LinkedHashMap<> ();
  
  private final Object traceEntriesLock = new Object ();
  
  /** Returns the trace description, including its data, for given trace key (as {@link TraceEntry}).
   * 
   * @param k The key.
   * 
   * @return The trace description as {@link TraceEntry}.
   * 
   * @see #getTraceData
   * 
   */
  public final TraceEntry getTraceEntry (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      return this.traceEntries.get (k);
    }
  }
  
  /** Sets the trace description, including its data, for given trace key (as {@link TraceEntry}).
   * 
   * @param k          The key, may be {@code null}.
   * @param traceEntry The trace entry; when {@code null} the key and all related data is removed.
   * 
   * @see #setTraceData
   * 
   */
  public final void setTraceEntry (final K k, final TraceEntry traceEntry)
  {
    synchronized (this.traceEntriesLock)
    {
      if (traceEntry == null)
        this.traceEntries.remove (k);
      else
        this.traceEntries.put (k, traceEntry);
    }
    fireDBChanged ();
  }
  
  /** Returns an unmodifiable (and temporary) view on the database (entries).
   * 
   * <p>
   * A copy is created as return value; subsequent changes to the trace entries or the map are not
   * reflected in the returned {@link Map}.
   * 
   * @return The trace entries (as a copied {@link Map}); cannot (should not) be modified.
   * 
   * @see Collections#unmodifiableMap
   * 
   */
  public final Map<K, TraceEntry> getTraceEntries ()
  {
    synchronized (this.traceEntriesLock)
    {
      return Collections.unmodifiableMap (new LinkedHashMap<> (this.traceEntries));
    }
  }
  
  /** Puts in the database all (trace description) entries from given {@code Map}.
   * 
   * @param traceEntries The {@code Map} of keys onto {@link TraceEntry}s, when {@code null}, the entire database is cleared.
   * 
   */
  public final void putTraceEntries (final Map<K, TraceEntry> traceEntries)
  {
    synchronized (this.traceEntriesLock)
    {
      if (traceEntries == null)
        this.traceEntries.clear ();
      else
        this.traceEntries.putAll (traceEntries);
    }
    fireDBChanged ();
  }
  
  private TraceEntry getOrCreateTraceEntry (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      final TraceEntry entry;
      if (this.traceEntries.containsKey (k))
        entry = this.traceEntries.get (k);
      else
      {
        entry = TraceEntry.EMPTY.withTraceColor (nextDefaultTraceColor ());
        this.traceEntries.put (k, entry);
        // NOTE: DOES NOT REPAINT -> THEREFORE private!
      }
      return entry;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE DATA
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Gets the trace data for given key.
   * 
   * @param k The key, may be {@code null}.
   * 
   * @return The trace data, {@code null} when a {@link TraceEntry} for the given key is not present.
   * 
   */
  public final TraceData getTraceData (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getTraceData ();
      else
        return null;
    }
  }
  
  /** Sets the trace data for given key.
   * 
   * <p>
   * This method creates a new {@link TraceEntry} for given key if that key is not yet present in the database.
   * 
   * @param k         The key, may be {@code null}.
   * @param traceData The new trace data for given key.
   * 
   */
  public final void setTraceData (final K k, final TraceData traceData)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withTraceData (traceData));
    }
    fireDBChanged ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT TRACE COLORS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Default trace colors.
   * 
   * <p>
   * These trace colors are assigned to newly created ({@link TraceEntry}s that have no {@link Color} set.
   * 
   */
  public final static Color[] DEFAULT_TRACE_COLORS =
  {
    Color.yellow,
    Color.cyan,
    Color.pink,
    Color.blue
  };
  
  private volatile int nextDefaultTraceColorIndex = 0;
  
  private final Object nextDefaultColorIndexLock = new Object ();
  
  private Color nextDefaultTraceColor ()
  {
    final Color traceColor;
    synchronized (this.nextDefaultColorIndexLock)
    {
      traceColor = DEFAULT_TRACE_COLORS[this.nextDefaultTraceColorIndex];
      this.nextDefaultTraceColorIndex = (this.nextDefaultTraceColorIndex + 1) % DEFAULT_TRACE_COLORS.length;
    }
    return traceColor;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE COLOR
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Gets the {@link Color} of the trace (and its, e.g., markers) with given key.
   * 
   * @param k The key, may be {@code null}.
   * 
   * @return The trace's {@link Color}, {@code null} if the key is unknown or the entry's color is not set.
   * 
   */
  public final Color getTraceColor (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getTraceColor ();
      else
        return null;
    }
  }
  
  /** Sets the trace {@link Color} for given key.
   * 
   * <p>
   * This method creates a new {@link TraceEntry} for given key if that key is not yet present in the database.
   * 
   * @param k          The key, may be {@code null}.
   * @param traceColor The new trace {@link Color} for given key.
   * 
   */
  public final void setTraceColor (final K k, final Color traceColor)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withTraceColor (traceColor));
    }
    fireDBChanged ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE MARKERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Gets the (active) {@link TraceMarker}s of the trace with given key.
   * 
   * <p>
   * An unmodifiable {@link Set} (view) is returned!
   * 
   * @param k The key, may be {@code null}.
   * 
   * @return The trace's {@link TraceMarker}s; {@code null} if the key is unknown, and empty {@link Set} if no markers are set.
   * 
   * @see Collections#unmodifiableSet
   * 
   */
  public final Set<TraceMarker> getTraceMarkers (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getTraceMarkers ();
      else
        return null;
    }
  }
  
  /** Sets the {@link TraceMarker}s for given key.
   * 
   * <p>
   * This method creates a new {@link TraceEntry} for given key if that key is not yet present in the database.
   * 
   * <p>
   * Note that the a priori contents of the set of trace markers for the key is <i>lost</i>.
   * 
   * @param k            The key, may be {@code null}.
   * @param traceMarkers The new {@link TraceMarker}s for given key.
   * 
   */
  public final void setTraceMarkers (final K k, final Set<TraceMarker> traceMarkers)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withTraceMarkers (traceMarkers));
    }
    fireDBChanged ();
  }
  
  /** Adds {@link TraceMarker}s for given key.
   * 
   * <p>
   * This method creates a new {@link TraceEntry} for given key if that key is not yet present in the database.
   * 
   * <p>
   * Note that the a priori contents of the set of trace markers is <i>preserved</i>.
   * 
   * @param k            The key, may be {@code null}.
   * @param traceMarkers The new {@link TraceMarker}s for given key.
   * 
   */
  public final void addTraceMarkers (final K k, final Set<TraceMarker> traceMarkers)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withTraceMarkersAdded (traceMarkers));
    }
    fireDBChanged ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE X, Y, Z OFFSETS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Gets the X Offset of the trace with given key.
   * 
   * @param k The key, may be {@code null}.
   * 
   * @return The trace's X Offset, {@link Double#NaN} if the key is unknown.
   * 
   * @see TraceEntry#getXOffset
   * 
   */
  public final double getXOffset (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getXOffset ();
      else
        return Double.NaN;
    }
  }
  
  /** Sets the X Offset for given key.
   * 
   * <p>
   * This method creates a new {@link TraceEntry} for given key if that key is not yet present in the database.
   * 
   * @param k       The key, may be {@code null}.
   * @param xOffset The new X Offset for given key.
   * 
   * @see TraceEntry#getXOffset
   * 
   */
  public final void setXOffset (final K k, final double xOffset)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withXOffset (xOffset));
    }
    fireDBChanged ();
  }
  
  /** Gets the Y Offset of the trace with given key.
   * 
   * @param k The key, may be {@code null}.
   * 
   * @return The trace's Y Offset, {@link Double#NaN} if the key is unknown.
   * 
   * @see TraceEntry#getYOffset
   * 
   */
  public final double getYOffset (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getYOffset ();
      else
        return Double.NaN;
    }
  }
  
  /** Sets the Y Offset for given key.
   * 
   * <p>
   * This method creates a new {@link TraceEntry} for given key if that key is not yet present in the database.
   * 
   * @param k       The key, may be {@code null}.
   * @param yOffset The new Y Offset for given key.
   * 
   * @see TraceEntry#getYOffset
   * 
   */
  public final void setYOffset (final K k, final double yOffset)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withYOffset (yOffset));
    }
    fireDBChanged ();
  }
  
  /** Gets the Z Offset of the trace with given key.
   * 
   * @param k The key, may be {@code null}.
   * 
   * @return The trace's Z Offset, {@link Double#NaN} if the key is unknown.
   * 
   * @see TraceEntry#getZOffset
   * 
   */
  public final double getZOffset (final K k)
  {
    synchronized (this.traceEntriesLock)
    {
      if (this.traceEntries.containsKey (k))
        return this.traceEntries.get (k).getZOffset ();
      else
        return Double.NaN;
    }
  }
  
  /** Sets the Z Offset for given key.
   * 
   * <p>
   * This method creates a new {@link TraceEntry} for given key if that key is not yet present in the database.
   * 
   * @param k       The key, may be {@code null}.
   * @param zOffset The new Z Offset for given key.
   * 
   * @see TraceEntry#getZOffset
   * 
   */
  public final void setZOffset (final K k, final double zOffset)
  {
    synchronized (this.traceEntriesLock)
    {
      this.traceEntries.put (k, getOrCreateTraceEntry (k).withZOffset (zOffset));
    }
    fireDBChanged ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
