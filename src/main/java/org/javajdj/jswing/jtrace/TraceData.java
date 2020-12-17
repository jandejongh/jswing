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

import java.util.function.Function;
import java.util.logging.Logger;
import org.javajdj.junits.Unit;

/** The data of a trace on, e.g., an oscilloscope or spectrum analyzer, or from a function (mask, limit).
 * 
 * <p>
 * This class provides the basic means for entering trace data (as in {@code double} arrays) into
 * {@link TraceDB} and/or the display components in this package, like {@link JTrace}.
 * The class support various trace type defined by one or more {@code double} arrays or {@link Function}s.
 * See {@link Type} for more details.
 * 
 * <p>
 * Objects of this class are immutable.
 * 
 * @see Type
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class TraceData
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (TraceData.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Constructs the trace data object from its arguments (single, main constructor).
   * 
   * <p>
   * Note that if multiple {@code double} arrays are (validly) given as argument,
   * they must be of exactly the same length.
   * 
   * @param type      The {@link Type} of trace, must be non-{@code null}, see {@link Type}.
   * 
   * @param xn        The array of X values; mandatory for trace types
   *                    {@link Type#XnYn} and {@link Type#XnYnZn};
   *                    must be {@code null} for the other trace types.
   * @param yn        The array of Y values; mandatory for trace types
   *                    {@link Type#Yn}, {@link Type#YnZn},
   *                    {@link Type#XnYn}, and {@link Type#XnYnZn};
   *                    must be {@code null} for the other trace types. 
   * @param zn        The array of Z values; mandatory for trace types
   *                    {@link Type#YnZn} and {@link Type#XnYnZn};
   *                    must be {@code null} for the other trace types. 
   * @param f_y_vs_x  The {@link Function} mapping X onto Y; mandatory for trace type {@link Type#F_Y_vs_X};
   *                    must be {@code null} for the other trace types.
   * 
   * @param nUnit The (optional) {@link Unit} for N (index) values; must <i>not</i> be set for "function" {@link Type}s.
   * @param xUnit The (optional) {@link Unit} for X values (for all {@link Type}s).
   * @param yUnit The (optional) {@link Unit} for Y values (for all {@link Type}s).
   * @param zUnit The (optional) {@link Unit} for Z values; must <i>not</i> be set for {@link Type}s without Z dimension.
   * 
   * @param nRange The optional {@link DoubleRange} for N (index) values; see also {@link #getNRange}.
   * @param xRange The optional {@link DoubleRange} for X values; mandatory for {@link Type#F_Y_vs_X}. See also {@link #getXRange}.
   * @param yRange The optional {@link DoubleRange} for Y values; see also {@link #getYRange}.
   * @param zRange The optional {@link DoubleRange} for Z values; see also {@link #getZRange};
   *                 must <i>not</i> be set for {@link Type}s without Z dimension.
   * 
   * @throws IllegalArgumentException If {@code type == null},
   *                                    or, depending on {@code type}, a mandatory argument is {@code null},
   *                                    or a not-applicable argument is non-{@code null} (see argument descriptions for details),
   *                                    or an array is improperly dimensioned.
   * 
   * @see Type
   * 
   */
  protected TraceData (
    final Type type,
    final double[] xn,
    final double[] yn,
    final double[] zn,
    final Function<Double, Double> f_y_vs_x,
    final Unit nUnit,
    final Unit xUnit,
    final Unit yUnit,
    final Unit zUnit,
    final DoubleRange nRange,
    final DoubleRange xRange,
    final DoubleRange yRange,
    final DoubleRange zRange)
  {
    
    if (type == null)
      throw new IllegalArgumentException ();
    this.type = type;
    
    switch (this.type)
    {
      case Yn:
        if (xn != null || yn == null || zn != null || f_y_vs_x != null)
          throw new IllegalArgumentException ();
        break;
      case YnZn:
        if (xn != null || yn == null || zn == null || f_y_vs_x != null)
          throw new IllegalArgumentException ();
        if (yn.length != zn.length)
          throw new IllegalArgumentException ();
        break;
      case XnYn:
        if (xn == null || yn == null || zn != null || f_y_vs_x != null)
          throw new IllegalArgumentException ();
        if (xn.length != yn.length)
          throw new IllegalArgumentException ();
        break;
      case XnYnZn:
        if (xn == null || yn == null || zn == null || f_y_vs_x != null)
          throw new IllegalArgumentException ();
        if (xn.length != yn.length || xn.length != zn.length)
          throw new IllegalArgumentException ();
        break;
      case F_Y_vs_X:
        if (xn != null || yn != null || zn != null || f_y_vs_x == null)
          throw new IllegalArgumentException ();
        break;
      default:
        throw new RuntimeException ();
    }
    
    this.xn = xn;
    this.yn = yn;
    this.zn = zn;
    this.f_y_vs_x = f_y_vs_x;
    
    // Units are always optional, but should not be specified unnecessarily; likely indicates an error.
    // Note that in cases with an index (n, i.e., all the array cases), both nUnit and xUnit may be set.
    switch (this.type)
    {
      case Yn:
        if (zUnit != null)
          throw new IllegalArgumentException ();
        break;
      case YnZn:
        break;
      case XnYn:
        if (zUnit != null)
          throw new IllegalArgumentException ();
        break;
      case XnYnZn:
        break;
      case F_Y_vs_X:
        if (zUnit != null)
          throw new IllegalArgumentException ();
        break;
      default:
        throw new RuntimeException ();
    }
    
    this.nUnit = nUnit;
    this.xUnit = xUnit;
    this.yUnit = yUnit;
    this.zUnit = zUnit;
    
    // Ranges are optional for all cases except F_Y_vs_X which requires an X range.
    if (this.f_y_vs_x != null && xRange == null)
      throw new IllegalArgumentException ();
    
    this.nRange = nRange;
    this.xRange = xRange;
    this.yRange = yRange;
    this.zRange = zRange;
    
  }

  /** Constructs (factory method) a new {@link TraceData} object of type {@link Type#Yn}
   *  from a given {@code double} array.
   * 
   * <p>
   * The {@link Type#Yn} trace is probably the most common one.
   * 
   * <p>
   * The array of {@code double}s specifies the Y values as a function of X indexed by N.
   * The X value is (always) implied by the index into the Y array,
   * and the relation between N and X is linear apart from a possible offset:
   * DX/DN is constant but X(0) is not necessarily zero.
   * 
   * <p>
   * Without further specification, the X range is between zero inclusive and the array length exclusive,
   * and its {@link Unit} is {@link Unit#UNIT_NONE}, i.e., dimensionless.
   * This can be changed through {@link #withXRange} and {@link #withXUnit}, respectively.
   * 
   * <p>
   * A similar statement holds for the Y values. By default, the Y range is derived from the
   * minimum and maximum values in the {@code yn} array.
   * The Y {@link DoubleRange} can be set by using {@link #withYRange}.
   * Although this does not affect the Y values themselves (unlike with the X values),
   * it <i>does</i> provide hints to trace-display software as to which Y range to show or
   * which Y range is to be considered valid (i.e., not <i>clipped</i>).
   * A more precise interpretation depends on the (settings in the) display component.
   * The Y {@link Unit} can be set (always safely) by using {@link #withYUnit}. 
   * 
   * <p>
   * Independent of setting X, Y ranges and units, one can set the N range through {@link #withNRange},
   * which (typically) restricts the N range shown in a display component.
   * The N range may be an arbitrary double range and does not have to be restricted to the index
   * range of the {@code yn} array. It is important to note that setting the N range <i>never</i> affects the
   * relation between X and Y for {@link Type#Yn}.
   * (A similar statement with respect to the N range holds for the other non-function {@link Type}s.)
   * See also {@link #getNRange}.
   * 
   * <p>
   * Finally, one can (always safely) set the N {@link Unit} through {@link #withNUnit},
   * but it is only used in case that neither the X unit <i>or</i> range are set.
   * If the X unit (through {@link #withXUnit}) is set, it simply overrides the N unit specification.
   * If the X range is set (through {@link #withXRange}), the N-{@link Unit} specification is also
   * ignored (because it does not make sense to interpret the N {@link Unit} in this case).
   * If the X range is specified, but <i>not</i> the X unit, the X unit default to {@link Unit#UNIT_NONE}.
   * 
   * @param yn The array holding the trace's indexed Y values, non-{@code null} but may be empty (have zero length).
   * 
   * @return The newly constructed trace-data object.
   * 
   * @throws IllegalArgumentException If {@code yn == null}.
   * 
   * @see Type#Yn
   * 
   * @see #withNRange
   * @see #withNUnit
   * 
   * @see #withXRange
   * @see #withXUnit
   * 
   * @see #withYRange
   * @see #withYUnit
   * 
   */
  public static TraceData createYn (final double[] yn)
  {
    return new TraceData (
      Type.Yn,
      null,
      yn,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null);
  }
  
  /** Constructs (factory method) a new {@link TraceData} object of type {@link Type#YnZn}
   *  from two (one for Y and one for Z) given {@code double} arrays.
   * 
   * <p>
   * The {@link Type#YnZn} trace can be seen as a {@link Type#Yn} trace type
   * with an additional Z dimension.
   * 
   * <p>
   * We refer to the factory method {@link #createYn} for a more detailed description of the arguments, noting that
   * everything stated for Y also holds, with appropriate method substitutions, for Z.
   * In other words {@link #withZRange} for {@link #withYRange}
   * and {@link #withZUnit} for {@link #withYUnit}.
   * 
   * @param yn The array holding the trace's indexed Y values, non-{@code null} but may be empty (have zero length).
   * @param zn The array holding the trace's indexed Z values, non-{@code null} but may be empty (have zero length);
   *             must be equal (when non-{@code null}) in size to {@code yn}.
   * 
   * @return The newly constructed trace-data object.
   * 
   * @throws IllegalArgumentException If {@code yn == null}, {@code zn == null}, or if the arguments differ in size.
   * 
   * @see Type#YnZn
   * 
   * @see #withNRange
   * @see #withNUnit
   * 
   * @see #withXRange
   * @see #withXUnit
   * 
   * @see #withYRange
   * @see #withYUnit
   * 
   * @see #withZRange
   * @see #withZUnit
   * 
   */
  public static TraceData createYnZn (final double[] yn, final double[] zn)
  {
    return new TraceData (
      Type.YnZn,
      null,
      yn,
      zn,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null);
  }
  
  /** Constructs (factory method) a new {@link TraceData} object of type {@link Type#XnYn}
   *  from a given {@code double} array.
   * 
   * <p>
   * This trace type (XY curve) is typically used for a dual-channel oscilloscope's XY mode.
   * 
   * <p>
   * The arrays of {@code double}s specify the X and Y values as a function of N; the index used to draw the curve.
   * 
   * <p>
   * By default, the X and Y ranges are derived from the
   * minimum and maximum values in the {@code xn} and {@code yn} arrays.
   * The X and Y {@link DoubleRange} can be set by using {@link #withXRange} and {@link #withYRange}, respectively.
   * Although this does not affect the X and Y values themselves,
   * it <i>does</i> provide hints to trace-display software as to which X and Y ranges to show or
   * to consider as valid (i.e., not <i>clipped</i>).
   * A more precise interpretation depends on the (settings in the) display component.
   * 
   * <p>
   * By default, the X and Y {@link Unit}s are {@link Unit#UNIT_NONE},
   * but these can always be set with {@link #withXUnit} and {@link #withYUnit}, respectively. 
   * 
   * <p>
   * Independent of setting X, Y ranges and units, one can set the N range through {@link #withNRange},
   * which (typically) restricts the N range shown in a display component.
   * The N range may be an arbitrary double range and does not have to be restricted to the index
   * range of the {@code yn} array. It is important to note that setting the N range <i>never</i> affects the
   * relation between X and Y for {@link Type#XnYn}.
   * See also {@link #getNRange}.
   * 
   * <p>
   * Finally, one can (always safely) set the N {@link Unit} through {@link #withNUnit};
   * it defaults to {@link Unit#UNIT_NONE}.
   * Unlike the case with {@link Type#Yn} and {@link Type#YnZn},
   * the N {@code Unit} makes sense in its own right;
   * it is typically related to the time taken to acquire the curve data.
   * 
   * @param xn The array holding the trace's indexed X values, non-{@code null} but may be empty (have zero length).
   * @param yn The array holding the trace's indexed Y values, non-{@code null} but may be empty (have zero length);
   *             must be equal (when non-{@code null}) in size to {@code xn}.
   * 
   * @return The newly constructed trace-data object.
   * 
   * @throws IllegalArgumentException If {@code xn == null}, {@code yn == null}, or if the arguments differ in size.
   * 
   * @see Type#XnYn
   * 
   * @see #withNRange
   * @see #withNUnit
   * 
   * @see #withXRange
   * @see #withXUnit
   * 
   * @see #withYRange
   * @see #withYUnit
   * 
   */
  public static TraceData createXnYn (final double[] xn, final double[] yn)
  {
    return new TraceData (
      Type.XnYn,
      xn,
      yn,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null);
  }
  
  /** Constructs (factory method) a new {@link TraceData} object of type {@link Type#XnYnZn}
   *  from three (one for X, Y, and Z) given {@code double} arrays.
   * 
   * <p>
   * The {@link Type#XnYnZn} trace can be seen as a {@link Type#XnYn} trace type
   * with an additional Z dimension.
   * 
   * <p>
   * We refer to the factory method {@link #createXnYn} for a more detailed description of the arguments, noting that
   * everything stated for Y also holds, with appropriate method substitutions, for Z.
   * In other words {@link #withZRange} for {@link #withYRange}
   * and {@link #withZUnit} for {@link #withYUnit}.
   * 
   * @param xn The array holding the trace's indexed X values, non-{@code null} but may be empty (have zero length).
   * @param yn The array holding the trace's indexed Y values, non-{@code null} but may be empty (have zero length);
   *             must be equal (when non-{@code null}) in size to {@code xn}.
   * @param zn The array holding the trace's indexed Z values, non-{@code null} but may be empty (have zero length);
   *             must be equal (when non-{@code null}) in size to {@code xn}.
   * 
   * @return The newly constructed trace-data object.
   * 
   * @throws IllegalArgumentException If {@code xn == null}, {@code yn == null}, {@code zn == null},
   *                                  or if the arguments differ in size.
   * 
   * @see Type#XnYnZn
   * 
   * @see #withNRange
   * @see #withNUnit
   * 
   * @see #withXRange
   * @see #withXUnit
   * 
   * @see #withYRange
   * @see #withYUnit
   * 
   * @see #withZRange
   * @see #withZUnit
   * 
   */
  public static TraceData createXnYnZn (final double[] xn, final double[] yn, final double[] zn)
  {
    return new TraceData (
      Type.XnYnZn,
      xn,
      yn,
      zn,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null);
  }
  
  /** Constructs (factory method) a new {@link TraceData} object of type {@link Type#F_Y_vs_X}
   *  from a given {@code Function} and X boundaries.
   * 
   * <p>
   * Note that this type of trace always requires a valid X range in order to assess
   * which X range to use to acquire the Y values from the {@link Function} argument.
   * 
   * <p>
   * The X range is initially between the {@code xMin} and {@code xMax} arguments,
   * and its {@link Unit} is {@link Unit#UNIT_NONE}, i.e., dimensionless.
   * This can be changed through {@link #withXRange} and {@link #withXUnit}, respectively.
   * 
   * <p>
   * By default, the Y range is derived from the
   * minimum and maximum values obtained by invoking the {@code f_y_vs_x}.
   * The Y {@link DoubleRange} can be set by using {@link #withYRange}.
   * Although this does not affect the Y values themselves,
   * it <i>does</i> provide hints to trace-display software as to which Y range to show or
   * which Y range is to be considered valid (i.e., not <i>clipped</i>).
   * A more precise interpretation depends on the (settings in the) display component.
   * The Y {@link Unit} can be set (always safely) by using {@link #withYUnit}. 
   * 
   * <p>
   * Independent of setting X, Y ranges and units, one can set the N range through {@link #withNRange},
   * which (typically) restricts the N range shown in a display component.
   * The N range may be an arbitrary double range.
   * It is important to note that setting the N range <i>never</i> affects the
   * relation between X and Y for {@link Type#F_Y_vs_X}.
   * See also {@link #getNRange}.
   * 
   * <p>
   * The default N range for {@link Type#F_Y_vs_X} is {@link #DEFAULT_N_RANGE}.
   * 
   * <p>
   * Finally, note that it is illegal to set the N {@link Unit} through {@link #withNUnit}
   * on trace data of type {@link Type#F_Y_vs_X}.
   * 
   * @param f_y_vs_x  The function mapping X onto Y, non-{@code null}.
   * @param xMin      The minimum X (lower boundary of X range).
   * @param xMax      The maximum X (upper boundary on X range).
   * 
   * @return The newly constructed trace-data object.
   * 
   * @throws IllegalArgumentException If {@code f_y_vs_x == null} or {@code xMin > xMax}.
   * 
   * @see Type#F_Y_vs_X
   * 
   * @see #withNRange
   * 
   * @see #withXRange
   * @see #withXUnit
   * 
   * @see #withYRange
   * @see #withYUnit
   * 
   */
  public static TraceData createF_Y_vs_X (final Function<Double, Double> f_y_vs_x, final double xMin, final double xMax)
  {
    return new TraceData (
      Type.F_Y_vs_X,
      null,
      null,
      null,
      f_y_vs_x,
      null,
      null,
      null,
      null,
      null,
      new DoubleRange (xMin, xMax),
      null,
      null);
  }
  
  /** Returns a copy with given N {@link Unit}.
   * 
   * @param nUnit The N unit.
   * 
   * @return The copy.
   * 
   * @throws IllegalArgumentException If setting the N unit is illegal for given trace-data {@link Type}.
   * 
   * @see Type
   * @see Unit
   * 
   */
  public final TraceData withNUnit (final Unit nUnit)
  {
    return new TraceData (
      this.type,
      this.xn,
      this.yn,
      this.zn,
      this.f_y_vs_x,
      nUnit,
      this.xUnit,
      this.yUnit,
      this.zUnit,
      this.nRange,
      this.xRange,
      this.yRange,
      this.zRange);
  }
  
  /** Returns a copy with given X {@link Unit}.
   * 
   * @param xUnit The X unit.
   * 
   * @return The copy.
   * 
   * @throws IllegalArgumentException If setting the X unit is illegal for given trace-data {@link Type}.
   * 
   * @see Type
   * @see Unit
   * 
   */
  public final TraceData withXUnit (final Unit xUnit)
  {
    return new TraceData (
      this.type,
      this.xn,
      this.yn,
      this.zn,
      this.f_y_vs_x,
      this.nUnit,
      xUnit,
      this.yUnit,
      this.zUnit,
      this.nRange,
      this.xRange,
      this.yRange,
      this.zRange);
  }
  
  /** Returns a copy with given Y {@link Unit}.
   * 
   * @param yUnit The Y unit.
   * 
   * @return The copy.
   * 
   * @throws IllegalArgumentException If setting the Y unit is illegal for given trace-data {@link Type}.
   * 
   * @see Type
   * @see Unit
   * 
   */
  public final TraceData withYUnit (final Unit yUnit)
  {
    return new TraceData (
      this.type,
      this.xn,
      this.yn,
      this.zn,
      this.f_y_vs_x,
      this.nUnit,
      this.xUnit,
      yUnit,
      this.zUnit,
      this.nRange,
      this.xRange,
      this.yRange,
      this.zRange);
  }
  
  /** Returns a copy with given Z {@link Unit}.
   * 
   * @param zUnit The Z unit.
   * 
   * @return The copy.
   * 
   * @throws IllegalArgumentException If setting the Z unit is illegal for given trace-data {@link Type}.
   * 
   * @see Type
   * @see Unit
   * 
   */
  public final TraceData withZUnit (final Unit zUnit)
  {
    return new TraceData (
      this.type,
      this.xn,
      this.yn,
      this.zn,
      this.f_y_vs_x,
      this.nUnit,
      this.xUnit,
      this.yUnit,
      zUnit,
      this.nRange,
      this.xRange,
      this.yRange,
      this.zRange);
  }
  
  /** Returns a copy with given N {@link DoubleRange}.
   * 
   * @param nMin The minimum of the range.
   * @param nMax The maximum of the range.
   * 
   * @return The copy.
   * 
   * @throws IllegalArgumentException If {@code nMin > nMax}
   *                                  or setting the N range is illegal for given trace-data {@link Type}.
   * 
   * @see Type
   * @see #getNRange
   * @see DoubleRange
   * 
   */
  public final TraceData withNRange (final double nMin, final double nMax)
  {
    return new TraceData (
      this.type,
      this.xn,
      this.yn,
      this.zn,
      this.f_y_vs_x,
      this.nUnit,
      this.xUnit,
      this.yUnit,
      this.zUnit,
      new DoubleRange (nMin, nMax),
      this.xRange,
      this.yRange,
      this.zRange);
  }
  
  /** Returns a copy with given X {@link DoubleRange}.
   * 
   * @param xMin The minimum of the range.
   * @param xMax The maximum of the range.
   * 
   * @return The copy.
   * 
   * @throws IllegalArgumentException If {@code xMin > xMax}
   *                                  or setting the X range is illegal for given trace-data {@link Type}.
   * 
   * @see Type
   * @see #getXRange
   * @see DoubleRange
   * 
   */
  public final TraceData withXRange (final double xMin, final double xMax)
  {
    return new TraceData (
      this.type,
      this.xn,
      this.yn,
      this.zn,
      this.f_y_vs_x,
      this.nUnit,
      this.xUnit,
      this.yUnit,
      this.zUnit,
      this.nRange,
      new DoubleRange (xMin, xMax),
      this.yRange,
      this.zRange);
  }
  
  /** Returns a copy with given Y {@link DoubleRange}.
   * 
   * @param yMin The minimum of the range.
   * @param yMax The maximum of the range.
   * 
   * @return The copy.
   * 
   * @throws IllegalArgumentException If {@code yMin > yMax}
   *                                  or setting the Y range is illegal for given trace-data {@link Type}.
   * 
   * @see Type
   * @see #getYRange
   * @see DoubleRange
   * 
   */
  public final TraceData withYRange (final double yMin, final double yMax)
  {
    return new TraceData (
      this.type,
      this.xn,
      this.yn,
      this.zn,
      this.f_y_vs_x,
      this.nUnit,
      this.xUnit,
      this.yUnit,
      this.zUnit,
      this.nRange,
      this.xRange,
      new DoubleRange (yMin, yMax),
      this.zRange);
  }
  
  /** Returns a copy with given Z {@link DoubleRange}.
   * 
   * @param zMin The minimum of the range.
   * @param zMax The maximum of the range.
   * 
   * @return The copy.
   * 
   * @throws IllegalArgumentException If {@code zMin > zMax}
   *                                  or setting the Z range is illegal for given trace-data {@link Type}.
   * 
   * @see Type
   * @see #getZRange
   * @see DoubleRange
   * 
   */
  public final TraceData withZRange (final double zMin, final double zMax)
  {
    return new TraceData (
      this.type,
      this.xn,
      this.yn,
      this.zn,
      this.f_y_vs_x,
      this.nUnit,
      this.xUnit,
      this.yUnit,
      this.zUnit,
      this.nRange,
      this.xRange,
      this.yRange,
      new DoubleRange (zMin, zMax));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The trace type.
   * 
   */
  public enum Type
  {
    /** Y data as one-dimensional {@code double} array, with X value implied by array index.
     * 
     */
    Yn,
    /** Y and Z data as equal-length one-dimensional {@code double} arrays, with X value implied by array index.
     * 
     */
    YnZn,
    /** X and Y data as equal-length one-dimensional {@code double} arrays (2D-curve indexed by integer).
     * 
     */
    XnYn,
    /** X, Y, and Z data as equal-length one-dimensional {@code double} arrays (3D-curve indexed by integer).
     * 
     */    
    XnYnZn,
    /** Y as a {@link Function} of X, both {@code double}s.
     * 
     * <p>
     * This type requires specification of the X {@link Range}.
     * 
     */    
    F_Y_vs_X;
  }
  
  private final Type type;
  
  /** Returns the trace type.
   * 
   * <p>
   * The trace type is set upon construction, and since the objects of this {@code class} are immutable,
   * it cannot be changed afterwards.
   * 
   * @return The trace type, fixed upon construction and always non-{@code null}.
   * 
   */
  public final Type getType ()
  {
    return this.type;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TRACE DATA
  // TRACE FUNCTION(S)
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final double[] xn;
  
  /** Returns the X data.
   * 
   * @return The X data.
   * 
   * @throws IllegalArgumentException If this {@link Type} does not support X data,
   *                                    i.e., for all types except {@link Type#XnYn}
   *                                    and {@link Type#XnYnZn}.
   * 
   * @see Type
   * 
   */
  public final double[] getXnData ()
  {
    if (this.type == Type.XnYn || this.type == Type.XnYnZn || type == Type.F_Y_vs_X)
      return this.xn;
    else
      throw new IllegalStateException ();
  }
  
  private final double[] yn;
  
  /** Returns the Y data.
   * 
   * @return The Y data.
   * 
   * @throws IllegalArgumentException If this {@link Type} does not support Y data,
   *                                    i.e., for type {@link Type#F_Y_vs_X}.
   * 
   * @see Type
   * 
   */
  public final double[] getYnData ()
  {
    if (this.type == Type.Yn || this.type == Type.YnZn || this.type == Type.XnYn || this.type == Type.XnYnZn)
      return this.yn;
    else
      throw new IllegalStateException ();
  }
  
  private final double[] zn;
  
  /** Returns the Z data.
   * 
   * @return The Z data.
   * 
   * @throws IllegalArgumentException If this {@link Type} does not support Z data,
   *                                    i.e., for all types other than {@link Type#YnZn}
   *                                    and {@link Type#XnYnZn}.
   * 
   * @see Type
   * 
   */
  public final double[] getZnData ()
  {
    if (this.type == Type.YnZn || this.type == Type.XnYnZn)
      return this.zn;
    else
      throw new IllegalStateException ();
  }
  
  private final Function<Double, Double> f_y_vs_x;
  
  /** Returns the {@link Function} mapping X onto Y.
   * 
   * @return The {@link Function} mapping X onto Y.
   * 
   * @throws IllegalArgumentException If this {@link Type} does not support the map {@link Function},
   *                                    i.e., for all types other than {@link Type#F_Y_vs_X}.
   * 
   * @see Type
   * 
   */
  public final Function<Double, Double> getF_Y_vs_X ()
  {
    if (this.type == Type.F_Y_vs_X)
      return this.f_y_vs_x;
    else
      throw new IllegalStateException ();    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UNITS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Unit nUnit;
  
  /** Returns the N {@link Unit}.
   * 
   * @return The N unit.
   * 
   * @throws IllegalArgumentException If this {@link Type} does not support N units.
   * 
   */
  public final Unit getNUnit ()
  {
    return this.nUnit;
  }
  
  private final Unit xUnit;
  
  /** Returns the X {@link Unit}.
   * 
   * @return The X unit.
   * 
   * @throws IllegalArgumentException If this {@link Type} does not support X units.
   * 
   */
  public final Unit getXUnit ()
  {
    return this.xUnit;
  }
  
  private final Unit yUnit;
  
  /** Returns the Y {@link Unit}.
   * 
   * @return The Y unit.
   * 
   * @throws IllegalArgumentException If this {@link Type} does not support Y units.
   * 
   */
  public final Unit getYUnit ()
  {
    return this.yUnit;
  }
  
  private final Unit zUnit;
  
  /** Returns the Z {@link Unit}.
   * 
   * @return The Z unit.
   * 
   * @throws IllegalArgumentException If this {@link Type} does not support Z units.
   * 
   */
  public final Unit getZUnit ()
  {
    return this.zUnit;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RANGES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** A range on a {@link Number} line.
   * 
   * @param <E> The actual {@link Number} type; must be {@link Comparable}.
   * 
   */
  public static class Range<E extends Number & Comparable<E>>
  {
  
    /** Creates the range from given minimum and maximum.
     * 
     * @param min The minimum.
     * @param max The maximum.
     * 
     * @throws IllegalArgumentException If {@code min == null}, {@code max == null}, or {@code min > max}
     *                                    (as determined with {@link Comparable#compareTo}).
     * 
     */
    public Range (final E min, final E max)
    {
      if (min == null || max == null)
        throw new IllegalArgumentException ();
      if (min.compareTo (max) > 0)
        throw new IllegalArgumentException ();
      this.min = min;
      this.max = max;
    }
    
    /** Creates the range from given minimum and maximum.
     * 
     * 
     * @param <E> The number (generic) type.
     * @param min The minimum.
     * @param max The maximum.
     * 
     * @return The range.
     * 
     * @throws IllegalArgumentException If {@code min > max} (as determined with {@link Comparable#compareTo}).
     * 
     */
    public static <E extends Number & Comparable<E>> Range<E> fromMinMax (final E min, final E max)
    {
      return new Range<> (min, max);
    }
    
    /** Creates a copy with given minimum.
     * 
     * @param min The new minimum.
     * 
     * @return The copy.
     * 
     * @throws IllegalArgumentException If the new minimum is larger than the current maximum
     *                                    (as determined with {@link Comparable#compareTo}).
     * 
     */
    public final Range withMin (final E min)
    {
      return new Range (min, this.max);
    }
    
    /** Creates a copy with given maximum.
     * 
     * @param max The new maximum.
     * 
     * @return The copy.
     * 
     * @throws IllegalArgumentException If the new maximum is smaller than the current minimum
     *                                    (as determined with {@link Comparable#compareTo}).

     * 
     */
    public final Range withMax (final E max)
    {
      return new Range (this.min, max);
    }
    
    /** Creates a copy with given minimum and maximum.
     * 
     * @param min The new minimum.
     * @param max The new maximum.
     * 
     * @return The copy.
     * 
     * @throws IllegalArgumentException If the new maximum is smaller than the new minimum
     *                                    (as determined with {@link Comparable#compareTo}).
     * 
     */
    public final Range withMinMax (final E min, final E max)
    {
      return new Range (min, max);
    }
    
    private final E min;
    
    /** Returns the minimum.
     * 
     * @return The minimum.
     * 
     */
    public final E getMin ()
    {
      return this.min;
    }
    
    private final E max;

    /** Returns the maximum.
     * 
     * @return The maximum.
     * 
     */
    public final E getMax ()
    {
      return this.max;
    }
    
  }
  
  /** A range of {@link Integer}s.
   * 
   */
  public final static class IntegerRange
    extends Range<Integer>
  {

    /** Creates the range from given minimum and maximum.
     * 
     * @param min The minimum.
     * @param max The maximum.
     * 
     * @throws IllegalArgumentException If {@code min == null}, {@code max == null}, or {@code min > max}.
     * 
     */
    public IntegerRange (final Integer min, final Integer max)
    {
      super (min, max);
    }
    
    /** Creates the range from given minimum and maximum.
     * 
     * @param min The minimum.
     * @param max The maximum.
     * 
     * @throws IllegalArgumentException If {@code min > max}.
     * 
     */
    public IntegerRange (final int min, final int max)
    {
      super (min, max);
    }
    
    /** Returns the length of the range.
     * 
     * @return The length of the range (zero or positive).
     * 
     */
    public final int getLength ()
    {
      return getMax () - getMin ();
    }
    
  }
  
  /** A range of {@link Double}s.
   * 
   */
  public final static class DoubleRange
    extends Range<Double>
  {

    /** Creates the range from given minimum and maximum.
     * 
     * @param min The minimum.
     * @param max The maximum.
     * 
     * @throws IllegalArgumentException If {@code min == null}, {@code max == null}, or {@code min > max}.
     * 
     */
    public DoubleRange (final Double min, final Double max)
    {
      super (min, max);
    }
    
    /** Creates the range from given minimum and maximum.
     * 
     * @param min The minimum.
     * @param max The maximum.
     * 
     * @throws IllegalArgumentException If {@code min > max}.
     * 
     */
    public DoubleRange (final double min, final double max)
    {
      super (min, max);
    }
    
    /** Returns the length of the range.
     * 
     * @return The length of the range (zero or positive).
     * 
     */
    public final double getLength ()
    {
      return getMax () - getMin ();
    }
    
  }
  
  /** The default length of the N {@link DoubleRange} for function {@link Type}s.
   * 
   * @see #DEFAULT_N_RANGE
   * @see #getNRange
   * 
   */
  public final static int DEFAULT_N_RANGE_LENGTH = 1024;
  
  /** The default N {@link DoubleRange} for function {@link Type}s.
   * 
   * <p>
   * The default interval is {@code [0, 1024)}.
   * 
   * @see #DEFAULT_N_RANGE_LENGTH
   * @see #getNRange
   * 
   */
  public final static DoubleRange DEFAULT_N_RANGE = new DoubleRange (0, DEFAULT_N_RANGE_LENGTH);
  
  private final DoubleRange nRange;
  
  /** Returns the (optional) N (index) range of this trace.
   * 
   * <p>
   * For the {@link TraceData} {@code class}, the N Range is irrelevant; it does not affect the fundamental relationships
   * between X, Y and, if applicable, Z.
   * The N Range is included to support display functions like
   * panning and zooming a trace.
   * 
   * <p>
   * Although not a requirement in itself, the convention is that the N Range is half-open: The minimum
   * is included, but the maximum is <i>not</i>.
   * 
   * @return The N or index range of this trace, null when not present.
   * 
   * @throws IllegalArgumentException If requesting the N range of the trace is incompatible with its type.
   * 
   * @see #getYRange
   * 
   */
  public final DoubleRange getNRange ()
  {
    return this.nRange;
  }
  
  private final DoubleRange xRange;
  
  /** Returns the (optional) X range of this trace.
   * 
   * <p>
   * Note that a valid X range is mandatory for the {@link Type#F_Y_vs_X} trace type.
   * 
   * @return The X range of this trace, null when not present.
   * 
   * @see #getYRange
   * 
   */
  public final DoubleRange getXRange ()
  {
    return this.xRange;
  }
  
  private final DoubleRange yRange;
  
  /** Returns the (optional) Y range of this trace.
   * 
   * <p>
   * The Y range on a trace (if applicable at all) is optional; setting it <i>can</i> serve two purposes:
   * <ul>
   * <li> To define the dynamic range of the measurement data, i.e., to set the <i>clipping</i> boundaries.
   * <li> To define (limit) the Y range to be displayed on screen or other output device.
   * </ul>
   * 
   * <p>
   * The implementation of this {@code class} does not use <i>any</i> of the ranges.
   * 
   * @return The Y range of this trace, null when not present.
   * 
   */
  public final DoubleRange getYRange ()
  {
    return this.yRange;
  }
  
  private final DoubleRange zRange;
  
  /** Returns the (optional) Z range of this trace.
   * 
   * @return The Z range of this trace, null when not present.
   * 
   * @throws IllegalArgumentException If requesting the Z range of the trace is incompatible with its type.
   * 
   * @see #getYRange
   * 
   */
  public final DoubleRange getZRange ()
  {
    switch (this.type)
    {
      case Yn:
      case XnYn:
      case F_Y_vs_X:
        throw new IllegalStateException ();
      case YnZn:
      case XnYnZn:
        return this.zRange;
      default:
        throw new RuntimeException ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
