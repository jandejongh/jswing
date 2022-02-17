/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jswing.jsevensegment;

import java.awt.Color;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/** A multi-digit seven-segment display capable of showing a number in fixed-point decimal notation
 *  (with leading-zero suppression support).
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 * @see JSevenSegmentDigit
 * 
 */
public class JSevenSegmentNumber
  extends JPanel
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static final Logger LOG = Logger.getLogger (JSevenSegmentNumber.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates the component (main constructor).
   * 
   * <p>
   * Main constructor.
   * 
   * @param mediumColor       The color of the digits, may be {@code null}; {@link JSevenSegmentDigit}.
   * @param sign              Whether or not to include a digit for the sign.
   * @param numberOfDigits    The number of digits to display (excluding the sign digit).
   * @param decimalPointIndex The index of the decimal point, -1 if no decimal point is to be displayed.
   * 
   * @see JSevenSegmentDigit
   * 
   */
  public JSevenSegmentNumber (
    final Color mediumColor,
    final boolean sign,
    final int numberOfDigits,
    final int decimalPointIndex)
  {
    this.minus = sign ? new JSevenSegmentDigit (mediumColor) : null;
    setLayout (new GridLayout (1, this.minus != null ? (numberOfDigits + 1) : numberOfDigits));
    if (this.minus != null)
      add (this.minus);
    this.digits = new JSevenSegmentDigit[numberOfDigits];
    for (int i = 0; i < numberOfDigits; i++)
    {
      this.digits[i] = new JSevenSegmentDigit (mediumColor);
      this.digits[i].setBorder (BorderFactory.createMatteBorder (2, 2, 2, 2, Color.black));
      add (this.digits[i]);
    }
    char[] formatArray = new char[numberOfDigits];
    Arrays.fill (formatArray, '0');
    this.decimalFormat = new DecimalFormat (new String (formatArray));
    this.defaultDecimalPointIndex = decimalPointIndex;
    this.decimalPointIndex = decimalPointIndex;
    this.number = null;
  }
  
  /** Creates the component with default digit color.
   * 
   * @param sign              See main constructor.
   * @param numberOfDigits    See main constructor.
   * @param decimalPointIndex See main constructor.
   * 
   * @see JSevenSegmentNumber#JSevenSegmentNumber(java.awt.Color, boolean, int, int)
   * 
   */
  public JSevenSegmentNumber (
    final boolean sign,
    final int numberOfDigits,
    final int decimalPointIndex)
  {
    this (null, sign, numberOfDigits, decimalPointIndex);
  }
  
  /** Creates the component with given range and resolution.
   * 
   * @param mediumColor See main constructor.
   * @param minValue    The minimum value to represent.
   * @param maxValue    The maximum value to represent.
   * @param resolution  The highest resolution of the value to represent.
   * 
   * @see JSevenSegmentNumber#JSevenSegmentNumber(java.awt.Color, boolean, int, int)
   * 
   */
  public JSevenSegmentNumber (
    final Color mediumColor,
    final double minValue,
    final double maxValue,
    final double resolution)
  {
    this (mediumColor,
      minValue < 0 || maxValue < 0,
      JSevenSegmentNumber.numberOfDigits (minValue, maxValue, resolution),
      JSevenSegmentNumber.decimalPointIndex (minValue, maxValue, resolution));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MINUS DIGIT
  // NUMBER DIGITS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final JSevenSegmentDigit minus;
  
  private final JSevenSegmentDigit[] digits;
  
  /** Returns the total number of digits (including fractional ones) in this component, excluding the minus-sign.
   * 
   * @return The total number of digits (including fractional ones) in this component, excluding the minus-sign.
   * 
   */
  public final int getNumberOfDigits ()
  {
    return this.digits.length;
  }
  
  /** Returns the number of fractional digits in this component.
   * 
   * @return The number of fractional digits in this component.
   * 
   */
  public final int getNumberOfFractionalDigits ()
  {
    if (getDecimalPointIndex () >= 0)
      return getNumberOfDigits () - getDecimalPointIndex () - 1;
    else
      return 0;
  }
  
  private static int numberOfDigits (final double minValue, final double maxValue, final double resolution)
  {
    if (resolution <= 0)
      throw new IllegalArgumentException ();
    if (minValue > maxValue)
      throw new IllegalArgumentException ();
    return Math.max (
        (int) Math.ceil (Math.log10 (Math.max (1.0, Math.abs (minValue / resolution)))),
        (int) Math.ceil (Math.log10 (Math.max (1.0, Math.abs (maxValue / resolution))))
      );
  }
  
  private static int numberOfFractionalDigits (final double resolution)
  {
    if (resolution <= 0)
      throw new IllegalArgumentException ();
    if (resolution >= 1.0)
      return 0;
    return (int) - Math.ceil (Math.log10 (resolution));
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DECIMAL FORMAT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final DecimalFormat decimalFormat;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DECIMAL POINT INDEX
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final int defaultDecimalPointIndex;
  
  /** Returns the default decimal point index (set upon construction).
   * 
   * <p>
   * Note that the actual decimal point index can be changed through {@link #setDecimalPointIndex(int)}.
   * 
   * @return The default decimal point index (set upon construction), -1 if no default decimal point is present.
   * 
   * @see #getDefaultDecimalPointIndex
   * 
   */
  public final int getDefaultDecimalPointIndex ()
  {
    return this.defaultDecimalPointIndex;
  }
  
  private int decimalPointIndex;
  
  /** Returns the (actual) decimal point index.
   * 
   * <p>
   * Note that the actual decimal point index can be changed through {@link #setDecimalPointIndex(int)}.
   * 
   * @return The decimal point index, -1 if no actual decimal point is present.
   * 
   * @see #getDefaultDecimalPointIndex
   * @see #setDecimalPointIndex(int)
   * 
   */
  public final int getDecimalPointIndex ()
  {
    return this.decimalPointIndex;
  }
  
  /** Sets the actual decimal point index.
   * 
   * @param decimalPointIndex The new decimal point index.
   * 
   * @see #getDecimalPointIndex
   * 
   */
  public final void setDecimalPointIndex (final int decimalPointIndex)
  {
    if (decimalPointIndex != this.decimalPointIndex)
    {
      this.decimalPointIndex = decimalPointIndex;
      if (this.number != null)
        setNumber (this.number);
    }
  }
  
  /** Sets the actual decimal point index to its default value.
   * 
   * @see #getDefaultDecimalPointIndex
   * 
   */
  public final void setDecimalPointIndex ()
  {
    setDecimalPointIndex (this.defaultDecimalPointIndex);
  }
  
  private static int decimalPointIndex (final double minValue, final double maxValue, final double resolution)
  {
    final int numberOfFractionalDigits = JSevenSegmentNumber.numberOfFractionalDigits (resolution);
    final int numberOfDigits = JSevenSegmentNumber.numberOfDigits (minValue, maxValue, resolution);
    if (numberOfFractionalDigits == 0)
      return -1;
    else
      return (numberOfDigits - 1) - numberOfFractionalDigits;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // MINIMUM/MAXIMUM REPRESENTABLE NUMBER
  // IN RANGE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns the minimum (most negative) number representable in this component.
   * 
   * <p>
   * Note that zero is returned if no minus (sign) digit is present.
   * 
   * @return The minimum (most negative) number representable in this component.
   * 
   */
  public final double getMinimumRepresentableNumber ()
  {
    if (this.minus == null)
      return 0;
    return -getMaximumRepresentableNumber ();
  }
  
  /** Returns the maximum (most positive) number representable in this component.
   * 
   * @return The maximum (most positive) number representable in this component.
   * 
   */
  public final double getMaximumRepresentableNumber ()
  {
    return
      Math.pow (10.0, getNumberOfDigits () - getNumberOfFractionalDigits ())
      - Math.pow (10.0, -getNumberOfFractionalDigits ());
  }
  
  /** Checks whether a given number is in representation range of this component.
   * 
   * @param d The number.
   * 
   * @return True if and only if the number
   *           is not a {@code NaN},
   *           is finite,
   *           and is within the representation range of this component.
   * 
   * @see #getMinimumRepresentableNumber
   * @see #getMaximumRepresentableNumber
   * 
   */
  public final boolean isInRange (final double d)
  {
    return Double.isFinite (d) && d >= getMinimumRepresentableNumber () && d <= getMaximumRepresentableNumber ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NUMBER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private Double number = null;
  
  /** Returns the number being displayed.
   * 
   * @return The number being displayed; may be {@code null} in which case a blank display is shown.
   * 
   */
  public final Double getNumber ()
  {
    return this.number;
  }
  
  /** Sets the number being displayed.
   * 
   * <p>
   * Main display routine for non-{code null} numbers; sets all digits.
   * 
   * @param d The number to display.
   * 
   * @see #setNumber(java.lang.Double)
   * @see #setBlank
   * 
   */
  public void setNumber (final double d)
  {
    this.number = d;
    if (! isInRange (d))
    {
      setOverflow (this.minus != null && d < 0);
      return;
    }
    if (this.minus != null)
    {
      if (d < 0)
        this.minus.setMinus ();
      else
        this.minus.setBlank ();
      this.minus.repaint ();
    }
    else if (d < 0)
    {
      LOG.log (Level.WARNING, "Supplied number ({0}) is negative (out of range)!", d);
      setBlank ();
      return;
    }
    final long N;
    if (this.decimalPointIndex >= 0)
      N = (long) Math.round (d * Math.pow (10, this.digits.length - this.decimalPointIndex - 1));
    else
      N = (long) Math.round (d);
    final String s = this.decimalFormat.format (Math.abs (N));
    if (s.length () != this.digits.length)
    {
      LOG.log (Level.SEVERE, "Formatted string size ({0}) does not match number of digits ({1}); String={2}!",
        new Object[]{s.length (), this.digits.length, s});
      throw new RuntimeException ();
    }
    for (int i = 0; i < this.digits.length; i++)
    {
      boolean leadingZero = true;
      int digit = 0;
      try
      {
        digit = Integer.parseInt (new String (new char[] {s.charAt (i)}));
      }
      catch (NumberFormatException nfs)
      {
        LOG.log (Level.SEVERE, "NumberFormatException, char = {0}.", s.charAt (i));
        throw new RuntimeException (nfs);
      }
      leadingZero &= (digit == 0);
      if (this.suppressLeadingZeroes
        && leadingZero
        && (i < this.digits.length - 1)
        && (this.decimalPointIndex < 0 || i < this.decimalPointIndex))
        this.digits[i].setBlank ();
      else
        this.digits[i].setNumber (digit);
      this.digits[i].setDecimalPoint (i == this.decimalPointIndex);
      this.digits[i].repaint ();
    }  
  }

  /** Sets the number displayed.
   * 
   * @param number The number displayed; may be {@code null} in which case a blank display is shown.
   * 
   * @see #setBlank
   * 
   */
  public final void setNumber (final Double number)
  {
    if (number != null)
      setNumber ((double) number);
    else
      setBlank ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SET BLANK
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Blanks all digits, including (if present) sign and decimal points.
   * 
   * <p>
   * Note that this method also erases (sets the {@code null}) the internally stored number.
   * 
   * @see #getNumber
   * @see #setNumber(java.lang.Double)
   * 
   */
  public final void setBlank ()
  {
    if (this.minus != null)
    {
      this.minus.setBlank ();
      this.minus.repaint ();
    }
    for (final JSevenSegmentDigit digit: this.digits)
    {
      digit.setBlank ();
      digit.repaint ();
    }
    this.number = null;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DISPLAY OVERFLOW
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Displays an overflow condition on the digits.
   * 
   * @param displayMinusSign Whether or not to display the minus sign (if available).
   * 
   */
  private void setOverflow (final boolean showMinusSign)
  {
    if (this.minus != null)
    {
      if (showMinusSign)
        this.minus.setMinus ();
      else
        this.minus.setBlank ();
      this.minus.repaint ();      
    }
    for (final JSevenSegmentDigit digit: this.digits)
    {
      digit.setSmallO ();
      digit.repaint ();
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SUPPRESS LEADING ZEROES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private boolean suppressLeadingZeroes = false;
  
  /** Returns whether this object suppresses leading zeroes (default {@code false}).
   * 
   * @return Whether this object suppresses leading zeroes.
   * 
   * @see #setSuppressLeadingZeroes
   * 
   */
  public final boolean isSuppressLeadingZeroes ()
  {
    return this.suppressLeadingZeroes;
  }

  /** Sets whether this object suppresses leading zeroes.
   * 
   * <p>
   * When set, a consecutive sequence of zeroes starting at the first digit
   * is suppressed up to but not including the digit with the decimal point.
   * If no decimal point is present, a consecutive sequence of zeroes  starting at the first digit
   * is suppressed up to but not including the last digit.
   * The last digit as well as zero-digits following the decimal point are never suppressed.
   * 
   * <p>
   * If needed, the current readout is modified to reflect the new setting.
   * 
   * @param suppressLeadingZeroes Whether this object suppresses leading zeroes (from now on).
   * 
   * @see #isSuppressLeadingZeroes
   * @see #getDecimalPointIndex
   * 
   */
  public final void setSuppressLeadingZeroes (final boolean suppressLeadingZeroes)
  {
    if (suppressLeadingZeroes != this.suppressLeadingZeroes)
    {
      this.suppressLeadingZeroes = suppressLeadingZeroes;
      if (this.number != null)
        setNumber (this.number);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
