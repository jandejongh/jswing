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

/** A (Swing) package for showing <i>traces</i> or <i>functions</i> the way they are displayed on, e.g., (retro) oscilloscopes
 *  and spectrum analyzers.
 * 
 * <p>
 * The package supports various combinations of data from traces ({@code double} arrays} and {@code Function}s,
 * as well as Z-modulation (e.g., though brightness or line thickness) and <i>markers</i>.
 * 
 * <p>
 * The package features two components for displaying traces:
 * <ul>
 * <li>{@link org.javajdj.jswing.jtrace.JTraceDisplay} for displaying traces without user interaction and without side panels;
 * <li>{@link org.javajdj.jswing.jtrace.JTrace} for displaying traces with side-panel markers and controls
 *     and (corner-button) user menus. Note that {@link org.javajdj.jswing.jtrace.JTrace} actually embeds a
 *     {@link org.javajdj.jswing.jtrace.JTraceDisplay} instance.
 * </ul>
 * 
 * <p>
 * A source-annotated demo is available in {@link org.javajdj.jswing.jtrace.JTraceDemo}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 * @since 0.7
 * 
 */
package org.javajdj.jswing.jtrace;
