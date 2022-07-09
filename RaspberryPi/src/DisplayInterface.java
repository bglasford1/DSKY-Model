/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a singleton that provides the interface to the Display Arduino.  The Display
            Arduino displays the 7-segment LEDs along with the Computer Activity light.  The data
            received from the AGC is persisted in the channel 10register.  This class decodes channel 10
            data and uses this data to update the display when a DISP control pin is asserted.

  Mods:		  07/15/22  Initial Release.
*/
import java.util.BitSet;

public class DisplayInterface
{
  private static final DisplayInterface instance = new DisplayInterface();

  private final SerialInterface serialInterface;

  // Index starts with 0, but 0 is not used.
  private final BitSet channel10Register = new BitSet(16);

  // Saved display values, represented as strings.
  private int md1 = -1;
  private int md2 = -1;
  private int vd1 = -1;
  private int vd2 = -1;
  private int nd1 = -1;
  private int nd2 = -1;
  private int r1d1 = -1;
  private int r1d2 = -1;
  private int r1d3 = -1;
  private int r1d4 = -1;
  private int r1d5 = -1;
  private int r2d1 = -1;
  private int r2d2 = -1;
  private int r2d3 = -1;
  private int r2d4 = -1;
  private int r2d5 = -1;
  private int r3d1 = -1;
  private int r3d2 = -1;
  private int r3d3 = -1;
  private int r3d4 = -1;
  private int r3d5 = -1;
  private boolean r1sPlus = false;
  private boolean r1sMinus = false;
  private boolean r2sPlus = false;
  private boolean r2sMinus = false;
  private boolean r3sPlus = false;
  private boolean r3sMinus = false;
  private final BitSet indicators = new BitSet(10);

  public static DisplayInterface getInstance()
  {
    return instance;
  }

  private DisplayInterface()
  {
    serialInterface = SerialInterface.getInstance();
  }

  /**
   * Method called to set/clear a bit of the Channel 10 register.
   *
   * @param bit The bit to set/clear.
   * @param value The value to use.
   */
  public void setChannel10Bit(int bit, boolean value)
  {
    if (value)
      channel10Register.set(bit);
    else
      channel10Register.clear(bit);
  }

  /**
   * Method called to zero out the sign values.
   */
  public void resetSigns()
  {
    r1sMinus = false;
    r1sPlus = false;
    r2sMinus = false;
    r2sPlus = false;
    r3sMinus = false;
    r3sPlus = false;
  }

  /**
   * Method called to reset the display back to a power on state.
   */
  public void resetDisplay()
  {
    serialInterface.sendDisplayCommand(DisplayCommand.RESET, -1);
    channel10Register.clear();
    resetSigns();
    md1 = -1;
    md2 = -1;
    vd1 = -1;
    vd2 = -1;
    nd1 = -1;
    nd2 = -1;
    r1d1 = -1;
    r1d2 = -1;
    r1d3 = -1;
    r1d4 = -1;
    r1d5 = -1;
    r2d1 = -1;
    r2d2 = -1;
    r2d3 = -1;
    r2d4 = -1;
    r2d5 = -1;
    r3d1 = -1;
    r3d2 = -1;
    r3d3 = -1;
    r3d4 = -1;
    r3d5 = -1;
  }

  /**
   * Method called to process the channel 10 register after all the bits have been set.
   * The bits from MSB to LSB are as follows with R indicating the relay word, S indicating
   * the sign bits, H indicating the high display character and L indicating the low display
   * character.Note that UPACT is not used and Flash is indicated by another channel.
   *
   * RLYWD #	DSPC		DSPH		DSPL
   * 1011	11	n/a	    MD1		  MD2
   * 1010	10	Flash		VD1		  VD2
   * 1001	9	  n/a		  ND1		  ND2
   * 1000	8	  UPACT		n/a		  R1D1
   * 0111	7	  +R1S		R1D2		R1D3
   * 0110	6	  -R1S		R1D4		R1D5
   * 0101	5	  +R2S		R2D1		R2D2
   * 0100	4	  -R2S		R2D3		R2D4
   * 0011	3	  n/a		  R2D5		R3D1
   * 0010	2	  +R3S		R3D2		R3D3
   * 0001	1	  -R3S		R3D4		R3D5
   * 0000	0	  (inactive state or blank)
   * 1100	12	Lights various indicator lights
   */
  public void decodeData()
  {
    try
    {
      // Decode Relay Word.
      boolean dspcValue = channel10Register.get(11);
      BitSet relayWordBits = channel10Register.get(12, 16);
      int relayWordValue = toInt(relayWordBits);
      int lowerValue = 0;
      int upperValue = 0;

      if (relayWordValue > 0 && relayWordValue < 12)
      {
        // Decode lower value.
        BitSet lowerValueBits = channel10Register.get(1, 6);
        lowerValue = decodeValue(lowerValueBits);

        // Decode upper value.
        BitSet upperValueBits = channel10Register.get(6, 11);
        upperValue = decodeValue(upperValueBits);
      }

      // Save values based on relay word and update display.
      if (relayWordValue == 1)
      {
        // This command contains R3D4, R3D5 and -R3S
        r3d4 = upperValue;
        r3d5 = lowerValue;
        serialInterface.sendDisplayCommand(DisplayCommand.R3D4, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.R3D5, lowerValue);
        if (dspcValue)
        {
          r3sMinus = true;
          if (!r3sPlus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R3S, 1);
          }
        }
        else
        {
          r3sMinus = false;
          if (!r3sPlus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R3S, 0);
          }
        }
      }
      else if (relayWordValue == 2)
      {
        // This command contains R3D2, R3D3 and +R3S
        r3d2 = upperValue;
        r3d3 = lowerValue;
        r3sPlus = true;
        serialInterface.sendDisplayCommand(DisplayCommand.R3D2, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.R3D3, lowerValue);
        if (dspcValue)
        {
          r3sPlus = true;
          if (!r3sMinus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R3S, 2);
          }
        }
        else
        {
          r3sPlus = false;
          if (!r3sMinus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R3S, 0);
          }
        }
      }
      else if (relayWordValue == 3)
      {
        // This command contains R2D5 and R3D1
        r2d5 = upperValue;
        r3d1 = lowerValue;
        serialInterface.sendDisplayCommand(DisplayCommand.R2D5, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.R3D1, lowerValue);
      }
      else if (relayWordValue == 4)
      {
        // This command contains R2D3, R2D4 and -R2S
        r2d3 = upperValue;
        r2d4 = lowerValue;
        r2sMinus = true;
        serialInterface.sendDisplayCommand(DisplayCommand.R2D3, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.R2D4, lowerValue);
        if (dspcValue)
        {
          r2sMinus = true;
          if (!r2sPlus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R2S, 1);
          }
        }
        else
        {
          r2sMinus = false;
          if (!r2sPlus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R2S, 0);
          }
        }
      }
      else if (relayWordValue == 5)
      {
        // This command contains R2D1, R2D2 and +R2S
        r2d1 = upperValue;
        r2d2 = lowerValue;
        r2sPlus = true;
        serialInterface.sendDisplayCommand(DisplayCommand.R2D1, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.R2D2, lowerValue);
        if (dspcValue)
        {
          r2sPlus = true;
          if (!r2sMinus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R2S, 2);
          }
        }
        else
        {
          r2sPlus = false;
          if (!r2sMinus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R2S, 0);
          }
        }
      }
      else if (relayWordValue == 6)
      {
        // This command contains R1D4, R1D5 and -R1S
        r1d4 = upperValue;
        r1d5 = lowerValue;
        r1sMinus = true;
        serialInterface.sendDisplayCommand(DisplayCommand.R1D4, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.R1D5, lowerValue);
        if (dspcValue)
        {
          r1sMinus = true;
          if (!r1sPlus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R1S, 1);
          }
        }
        else
        {
          r1sMinus = false;
          if (!r1sPlus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R1S, 0);
          }
        }
      }
      else if (relayWordValue == 7)
      {
        // This command contains R1D2, R1D3 and +R1S
        r1d2 = upperValue;
        r1d3 = lowerValue;
        r1sPlus = true;
        serialInterface.sendDisplayCommand(DisplayCommand.R1D2, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.R1D3, lowerValue);
        if (dspcValue)
        {
          r1sPlus = true;
          if (!r1sMinus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R1S, 2);
          }
        }
        else
        {
          r1sPlus = false;
          if (!r1sMinus)
          {
            serialInterface.sendDisplayCommand(DisplayCommand.R1S, 0);
          }
        }
      }
      else if (relayWordValue == 8)
      {
        // This command contains R1D1
        r1d1 = lowerValue;
        serialInterface.sendDisplayCommand(DisplayCommand.R1D1, lowerValue);
      }
      else if (relayWordValue == 9)
      {
        // This command contains ND1 and ND2
        nd1 = upperValue;
        nd2 = lowerValue;
        serialInterface.sendDisplayCommand(DisplayCommand.ND1, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.ND2, lowerValue);
      }
      else if (relayWordValue == 10)
      {
        // This command contains VD1 and VD2
        vd1 = upperValue;
        vd2 = lowerValue;
        serialInterface.sendDisplayCommand(DisplayCommand.VD1, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.VD2, lowerValue);
      }
      else if (relayWordValue == 11)
      {
        // This command contains MD1 and MD2
        md1 = upperValue;
        md2 = lowerValue;
        serialInterface.sendDisplayCommand(DisplayCommand.MD1, upperValue);
        serialInterface.sendDisplayCommand(DisplayCommand.MD2, lowerValue);
      }
      else if (relayWordValue == 12)
      {
        // Note that PRIO DISP & NO DAP are not implemented.
        boolean bit3Value = channel10Register.get(3);
        boolean bit4Value = channel10Register.get(4);
        boolean bit5Value = channel10Register.get(5);
        boolean bit6Value = channel10Register.get(6);
        boolean bit8Value = channel10Register.get(8);
        boolean bit9Value = channel10Register.get(9);

        try
        {
          if (bit3Value != indicators.get(3))
          {
            indicators.set(3, bit3Value);
            serialInterface.sendIndicatorCommand(IndicatorCommand.VEL, bit3Value);
            Thread.sleep(500);
          }
          if (bit4Value != indicators.get(4))
          {
            indicators.set(4, bit4Value);
            serialInterface.sendIndicatorCommand(IndicatorCommand.NO_ATT, bit4Value);
            Thread.sleep(500);
          }
          if (bit5Value != indicators.get(5))
          {
            indicators.set(5, bit5Value);
            serialInterface.sendIndicatorCommand(IndicatorCommand.ALT, bit5Value);
            Thread.sleep(500);
          }
          if (bit6Value != indicators.get(6))
          {
            indicators.set(6, bit6Value);
            serialInterface.sendIndicatorCommand(IndicatorCommand.GIMBAL_LOCK, bit6Value);
            Thread.sleep(500);
          }
          if (bit8Value != indicators.get(8))
          {
            indicators.set(8, bit8Value);
            serialInterface.sendIndicatorCommand(IndicatorCommand.TRACKER, bit8Value);
            Thread.sleep(500);
          }
          if (bit9Value != indicators.get(9))
          {
            indicators.set(9, bit9Value);
            serialInterface.sendIndicatorCommand(IndicatorCommand.PROG, bit9Value);
            Thread.sleep(500);
          }
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
      }
    }
    catch (IllegalArgumentException e)
    {
      System.out.println("Invalid Channel Bus value for DISP: " + channel10Register);
    }
  }

  /**
   * Internal method used to decode the values of DSPH and DSPL.
   *
   * @param bits The bits to decode, there should be 5.
   * @return The integer value.
   * @throws IllegalArgumentException  An invalid set of bits was sent.
   */
  private static int decodeValue(BitSet bits) throws IllegalArgumentException
  {
    int value = toInt(bits);
    if (value == 0)
    {
      return -1;
    }
    else if (value == 21)
    {
      return 0;
    }
    else if (value == 3)
    {
      return 1;
    }
    else if (value == 25)
    {
      return 2;
    }
    else if (value == 27)
    {
      return 3;
    }
    else if (value == 15)
    {
      return 4;
    }
    else if (value == 30)
    {
      return 5;
    }
    else if (value == 28)
    {
      return 6;
    }
    else if (value == 19)
    {
      return 7;
    }
    else if (value == 29)
    {
      return 8;
    }
    else if (value == 31)
    {
      return 9;
    }
    else
      throw new IllegalArgumentException();
  }

  /**
   * Internal method to convert a bitset to an integer value.
   *
   * @param bitSet The bitset to convert.
   * @return The integer value.
   */
  private static int toInt(BitSet bitSet)
  {
    int intValue = 0;
    for (int bit = 0; bit < bitSet.length(); bit++)
    {
      if (bitSet.get(bit))
      {
        intValue |= (1 << bit);
      }
    }
    return intValue;
  }
}
