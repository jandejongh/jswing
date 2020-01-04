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
  
  public JSevenSegmentNumber (final Color mediumColor, final boolean sign, final int numberOfDigits, final int decimalPointIndex)
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
    this.decimalPointIndex = decimalPointIndex;
  }
  
  public JSevenSegmentNumber (final boolean sign, final int numberOfDigits, final int decimalPointIndex)
  {
    this (null, sign, numberOfDigits, decimalPointIndex);
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
  
  private static int decimalPointIndex (final double minValue, final double maxValue, final double resolution)
  {
    final int numberOfFractionalDigits = JSevenSegmentNumber.numberOfFractionalDigits (resolution);
    final int numberOfDigits = JSevenSegmentNumber.numberOfDigits (minValue, maxValue, resolution);
    if (numberOfFractionalDigits == 0)
      return -1;
    else
      return (numberOfDigits - 1) - numberOfFractionalDigits;
  }
  
  public JSevenSegmentNumber (final Color mediumColor, final double minValue, final double maxValue, final double resolution)
  {
    this (mediumColor,
      minValue < 0 || maxValue < 0,
      JSevenSegmentNumber.numberOfDigits (minValue, maxValue, resolution),
      JSevenSegmentNumber.decimalPointIndex (minValue, maxValue, resolution));
  }
  
  private final JSevenSegmentDigit minus;
  
  private final JSevenSegmentDigit[] digits;
  
  private final DecimalFormat decimalFormat;
  
  private final int decimalPointIndex;
  
  public void setNumber (final double d)
  {
    if (this.minus != null)
    {
      if (d < 0)
        this.minus.setMinus ();
      else
        this.minus.setBlank ();
      this.minus.repaint ();
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

  /** Blanks all digits, including (if present) sign and decimal points.
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
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
