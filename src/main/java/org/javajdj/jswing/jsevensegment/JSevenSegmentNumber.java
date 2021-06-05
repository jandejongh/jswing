/*
 * Copyright 2010-2021 Jan de Jongh <jfcmdejongh@gmail.com>.
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

/** A multi-digit seven-segment display capable of showing a number in fixed-point decimal notation.
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
  
  public JSevenSegmentNumber (
    final boolean sign,
    final int numberOfDigits,
    final int decimalPointIndex)
  {
    this (null, sign, numberOfDigits, decimalPointIndex);
  }
  
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
  
  public final int getDefaultDecimalPointIndex ()
  {
    return this.defaultDecimalPointIndex;
  }
  
  private int decimalPointIndex;
  
  public final int getDecimalPointIndex ()
  {
    return this.decimalPointIndex;
  }
  
  public final void setDecimalPointIndex (final int decimalPointIndex)
  {
    if (decimalPointIndex != this.decimalPointIndex)
    {
      this.decimalPointIndex = decimalPointIndex;
      if (this.number != null)
        setNumber (this.number);
    }
  }
  
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
  
  public final Double getNumber ()
  {
    return this.number;
  }
  
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
      this.digits[i].setNumber (digit);
      this.digits[i].setDecimalPoint (i == this.decimalPointIndex);
      this.digits[i].repaint ();
    }  
  }

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
   * Note that this method also erases the internally stored number.
   * This may change in future releases...
   * 
   * @see #getNumber
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
    // XXX This really does not feel right, but there many dependencies using this, so we cannot blindly change it...
    // Why are we changing the number here?
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
