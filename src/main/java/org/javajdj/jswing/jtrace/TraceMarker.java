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

import java.util.logging.Logger;

/** A marker (type) for a trace (or multiple) for displaying in a {@link JTrace} panel.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @see TraceEntry
 * @see JTrace
 * 
 * @see TraceEntry#withTraceMarkers
 * @see TraceEntry#withTraceMarkersAdded
 * 
 * @see JTrace#setTraceMarkers
 * @see JTrace#addTraceMarkers
 * 
 * @see TraceDB#getTraceMarkers
 * @see TraceDB#setTraceMarkers
 * @see TraceDB#addTraceMarkers
 * 
 */
public enum TraceMarker
{

  /** A marker on the side of the trace display showing the value zero of X.
   * 
   */
  ZERO_X_SIDE_MARKER,
  /** A marker on the side of the trace display showing the value zero of Y.
   * 
   */
  ZERO_Y_SIDE_MARKER;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (TraceMarker.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
